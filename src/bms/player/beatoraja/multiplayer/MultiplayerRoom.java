package bms.player.beatoraja.multiplayer;

import bms.player.beatoraja.*;
import bms.player.beatoraja.input.BMSPlayerInputProcessor;
import bms.player.beatoraja.input.KeyBoardInputProcesseor;
import bms.player.beatoraja.multiplayer.packets.out.*;
import bms.player.beatoraja.multiplayer.skinning.MultiplayerLobbiesSkin;
import bms.player.beatoraja.multiplayer.types.UserType;
import bms.player.beatoraja.skin.SkinType;
import bms.player.beatoraja.song.SongData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static bms.player.beatoraja.skin.SkinProperty.*;

public class MultiplayerRoom extends MainState {

    private final static Color WHITE_TEXT = Color.valueOf("#e2e2e2");
    private final static Color LIGHTER_GRAY = Color.valueOf("#1e1e1e");
    private final static Color OUTLINE_BLACK = Color.valueOf("#0f0f0f");

    private final static Color ACCENT_PURPLE = Color.valueOf("bb86fc");

    private final static Color PASTEL_RED = Color.valueOf("#d32f2f");
    private final static Color PASTEL_GREEN = Color.valueOf("#4caf50");

    /**
     * Control everything with controller
     * Start starts game/ready up
     * BT7 enable host passing
     * - TT to scroll users
     * - BT1 Pass
     * - BT2 Cancel
     * BT6 start kick
     * - Same as above
     * BT1 Select chart
     * BT5 Enable Auto Host Rotate
     */

    private BitmapFont titlefont;
    private BitmapFont largeText;
    private BitmapFont chartTitleFont;
    private ShapeRenderer shape;

    private BMSPlayerInputProcessor input;
    private KeyBoardInputProcesseor keyboard;
    private PlayerConfig config;

    private MPServerConnection server;
    private String lastChartHash = null;
    private SongData selectedChart = null;

    private enum HostModes {
        PASS_HOST, KICK_PLAYER, NORMAL
    }

    private HostModes currentMode = HostModes.NORMAL;
    private int scrollIndex = 0;
    private String highlightedUser = "";

    private boolean missingChart = false;

    public MultiplayerRoom(MainController main) {
        super(main);
    }

    @Override
    public void create() {
        loadSkin(SkinType.MULTIPLAYER_LOBBY);
        if (getSkin() == null) {
            this.setSkin(new MultiplayerLobbiesSkin(Resolution.HD, main.getConfig().getResolution()));
        }

        initializeFonts();

        shape = new ShapeRenderer();

        //todo: can these be final and initialized in the constructor?
        input = main.getInputProcessor();
        keyboard = input.getKeyBoardInputProcesseor();
        config = main.getPlayerResource().getPlayerConfig();

        server = main.getMultiplayerServer();

        //todo: find a better way of handling this
        final SongData songData = main.getPlayerResource().getSongdata();
        if (songData != null) {
            updateChart(songData, true);
        }

        server.syncedReady = false;
        server.setGameStarted(false);
        server.sendPacket(new RoomUpdateGet());
    }

    private void initializeFonts() {
        final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skin/default/VL-Gothic-Regular.ttf"));
        final FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) (20 * getSkin().getScaleY());
        titlefont = generator.generateFont(parameter);

        parameter.size = (int) (32 * getSkin().getScaleY());
        largeText = generator.generateFont(parameter);
    }

    private void updateChart(SongData songData, boolean setChart) {
        final String chartHash = songData.getSha256();

        if (!chartHash.equals(lastChartHash)) {
            if (chartTitleFont != null) {
                chartTitleFont.dispose();
                chartTitleFont = null;
            }

            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                    Gdx.files.internal("skin/default/VL-Gothic-Regular.ttf"));

            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = (int) (20 * getSkin().getScaleY());
            parameter.characters = songData.getFullTitle() + songData.getFullArtist();
            chartTitleFont = generator.generateFont(parameter);

            if (setChart) {
                server.sendPacket(new RoomSetSong(songData.getTitle(), songData.getDifficulty(), songData.getLevel(), songData.getSha256()));
            }
        }

        selectedChart = songData;
        lastChartHash = songData.getSha256();
    }

    private void handleHighlightingInputMode(int lastPressedKey) {
        if(lastPressedKey == getScratchKey(true)) scrollIndex ++;
        else if(lastPressedKey == getScratchKey(false)) scrollIndex --;

        final String hostId = Optional.ofNullable(server.getRoomData().getProperties().getHost()).orElse("");
        final List<String> userIds = Arrays.stream(server.getRoomData().getProperties().getUsers())
                .map(UserType::getId)
                .filter(hostId::equals)
                .collect(Collectors.toList());

        if(scrollIndex > userIds.size()) scrollIndex = 0;
        if(scrollIndex < 0) scrollIndex = userIds.size() - 1;

        //todo: user remainder %
        this.highlightedUser = userIds.get(scrollIndex);

        if(lastPressedKey == getButtonKey(0)) {
            server.sendPacket(currentMode == HostModes.PASS_HOST ? new RoomHostSet(highlightedUser) : new RoomKick(highlightedUser));
        } else if(lastPressedKey == getButtonKey(6)) {
            this.currentMode = HostModes.NORMAL;
        }
    }

    @Override
    public void input() {
        if (input.getKeyBoardInputProcesseor().getLastPressedKey() != -1) {
            final int lastPressedKey = keyboard.getLastPressedKey();
            keyboard.setLastPressedKey(-1);

            if(server.isHost() && currentMode != HostModes.NORMAL) {
                handleHighlightingInputMode(lastPressedKey);
                return;
            }

            if(server.isHost()) {
                if(lastPressedKey == getButtonKey(0)) {
                    main.changeState(MainStateType.MUSICSELECT);
                } else if(lastPressedKey == getButtonKey(5)) {
                    server.sendPacket(new RoomOptionRotationToggle());
                } else if(lastPressedKey == getStart() && selectedChart != null) {
                    server.getRoomData().startGame();
                } else if(lastPressedKey == getButtonKey(6)) {
                    currentMode = HostModes.PASS_HOST;
                    scrollIndex = 0;
                }
            } else {
                if(lastPressedKey == getButtonKey(2)) {
                    server.sendPacket(new RoomUpdateGet());
                } else if(lastPressedKey == getStart()) {
                    server.getRoomData().toggleReady();
                }
            }
        }

        if (input.isControlKeyPressed(KeyBoardInputProcesseor.ControlKeys.ESCAPE)) {
            server.leaveRoom();
            main.changeState(MainStateType.MULTIPLAYER_LOBBIES);
        }
    }

    @Override
    public void render() {
        //todo: this needs to be skinnable.

        if (server.pendingGameStart) {
            server.pendingGameStart = false;
            startChart();
            return;
        }

        if (server.pendingChartUpdateHash != null) {
            findAndUpdateChart(server.pendingChartUpdateHash);
            server.pendingChartUpdateHash = null;
        }

        final SpriteBatch sprite = main.getSpriteBatch();
        final float scaleX = (float) getSkin().getScaleX();
        final float scaleY = (float) getSkin().getScaleY();
        final float width = getSkin().getWidth();
        final float height = getSkin().getHeight();

        Gdx.gl.glClearColor(0.07f, 0.07f, 0.07f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        TextureRegion textureRegion = getImage(IMAGE_STAGEFILE);
        if (lastChartHash != null && textureRegion != null) {
            sprite.begin();
            sprite.draw(getImage(IMAGE_STAGEFILE).getTexture(), 0, 0, 500, 500);
            sprite.end();
        }
        //0.886

        final float roomNameBoxX = scaleX + 15;
        final float roomNameBoxY = height - 93;
        final float roomNameBoxWidth = 400f;
        final float roomNameBoxHeight = 32;

        sprite.begin();
        {
            //Room name inner
            drawBox(roomNameBoxX, roomNameBoxY, roomNameBoxWidth, roomNameBoxHeight, OUTLINE_BLACK, LIGHTER_GRAY);

            //Rectangle surrounding player list
            drawOutlineRect(roomNameBoxX, roomNameBoxY - 400, roomNameBoxWidth, 395, currentMode != HostModes.NORMAL ? PASTEL_RED : WHITE_TEXT);
        }
        sprite.end();

        sprite.begin();
        {
            //Text elements
            largeText.setColor(WHITE_TEXT);
            largeText.draw(sprite, "Multiplayer", scaleX + 20, getSkin().getHeight() - 20);

            titlefont.setColor(WHITE_TEXT);
        }
        sprite.end();

        drawChartInfo(sprite, scaleX, scaleY);
        drawHelpText(sprite, scaleX, scaleY);


        final RoomData roomData = server.getRoomData();
        drawRoomTitle(sprite, scaleX, scaleY);

        if (roomData.getRoomInfo() != null && roomData.getProperties() != null) {
            //Box indicating if auto host-rotate is enabled.
            sprite.begin();
            {
                drawBox(roomNameBoxX, roomNameBoxY - 440, roomNameBoxWidth, 32, roomData.getProperties().isRotatingHost() ? PASTEL_GREEN : PASTEL_RED, LIGHTER_GRAY);
            }
            sprite.end();

            renderUsers(sprite, roomData);
        } else {
            titlefont.draw(sprite, "Loading..", 80 * scaleX, 680 * scaleY);
        }
    }

    private void draw(SpriteBatch sprite, Runnable runnable) {
        sprite.begin();
        runnable.run();
        sprite.end();
    }

    private void drawRoomTitle(SpriteBatch sprite, float scaleX, float scaleY) {
        final RoomData roomData = server.getRoomData();

        String line = String.format("%s (%s/%s)",
                roomData.getRoomInfo().getName(),
                roomData.getProperties().getUsers().length, roomData.getRoomInfo().getMax()
        );

        draw(sprite, () -> {
            titlefont.draw(sprite, line, scaleX + 20, getSkin().getHeight() - 70);
        });
    }

    private void drawChartInfo(SpriteBatch sprite, float scaleX, float scaleY) {
        sprite.begin();
        {
            //Rectangle surrounding current chart
            drawOutlineRect(scaleX + 600, getSkin().getHeight() - 190, 400, 120, WHITE_TEXT);
        }
        sprite.end();

        sprite.begin();
        {
            //chart title:
            if (lastChartHash != null && selectedChart != null) {
                SongData data = selectedChart;
                chartTitleFont.draw(sprite, data.getTitle() + " " + data.getSubtitle(), scaleX + 600, getSkin().getHeight() - 80);
                chartTitleFont.draw(sprite, data.getFullArtist(), scaleX + 600, getSkin().getHeight() - 110);
                titlefont.setColor(Color.CYAN);
                titlefont.draw(sprite, "lv" + "" + data.getLevel(), getSkin().getWidth() - 470, getSkin().getHeight() - 140);
            }
        }
        sprite.end();
    }

    private void drawHelpText(SpriteBatch sprite, float scaleX, float scaleY) {
        sprite.begin();
        titlefont.draw(sprite, (server.getRoomData().isReady() ? "Unready" : "Ready") + " [6]", 90 * scaleX, 68 * scaleY);

        titlefont.draw(sprite, String.format("Start Game / Toggle Ready [START / %s]", Input.Keys.toString(getStart())), 230 * scaleX, 60);
        titlefont.draw(sprite, String.format("SELECT CHART [BT1 / %s]", Input.Keys.toString(getButtonKey(0))), 230 * scaleX, 80);
        titlefont.draw(sprite,
                String.format("Pass Host [BT7 / %s] (TT Scroll / %s / %s)",
                        Input.Keys.toString(getButtonKey(6)),
                        Input.Keys.toString(getScratchKey(true)),
                        Input.Keys.toString(getScratchKey(false))),
                230 * scaleX, 100);
        titlefont.draw(sprite, String.format("Enable Host Rotate [BT6 / %s]", Input.Keys.toString(getButtonKey(5))), 230 * scaleX, 120);
        titlefont.draw(sprite, String.format("Force Lobby Refresh [BT3 / %s]", Input.Keys.toString(getButtonKey(2))), 230 * scaleX, 140);
        sprite.end();
    }

    private void renderUsers(SpriteBatch sprite, RoomData roomData) {
        for (int i = 0; i < roomData.getProperties().getUsers().length; i++) {
            final UserType userType = roomData.getProperties().getUsers()[i];

            //host is null when in-game
            final String hostId = Optional.ofNullable(roomData.getProperties().getHost()).orElse("");

            final boolean isHost = hostId.equals(userType.getId());
            final boolean isHighlighted = highlightedUser.equals(userType.getId()) && currentMode != HostModes.NORMAL;

            final float roomNameBoxX = (float)getSkin().getScaleX() + 15;
            final float roomNameBoxY = getSkin().getHeight() - 93;
            final float roomNameBoxWidth = 310f;
            final float roomNameBoxHeight = 32;

            sprite.begin();

            drawBox(roomNameBoxX + 6, roomNameBoxY - 42 - (i * 36), roomNameBoxWidth - 12, roomNameBoxHeight, isHost ? ACCENT_PURPLE : isHighlighted ? WHITE_TEXT : OUTLINE_BLACK, LIGHTER_GRAY);
            drawBox(roomNameBoxX + roomNameBoxWidth, roomNameBoxY - 42 - (i * 36), 84, roomNameBoxHeight, PASTEL_GREEN, LIGHTER_GRAY);
            sprite.end();

            if(userType.isMissingMap()){
                titlefont.setColor(PASTEL_RED);
            } else if(userType.isReady() && !isHost){
                titlefont.setColor(PASTEL_GREEN);
            } else {
                titlefont.setColor(WHITE_TEXT);
            }

            final String scoreText;

            StringBuilder stringBuilder = new StringBuilder();
            if (userType.getScore() != null) {
                scoreText = String.valueOf(userType.getScore());
            } else {
                scoreText = "00000";
            }
            stringBuilder.append(userType.getName());

            sprite.begin();
            titlefont.draw(sprite, stringBuilder.toString(), roomNameBoxX + 10, roomNameBoxY - 45 - (i * 36) + (titlefont.getLineHeight()));
            titlefont.setColor(WHITE_TEXT);
            titlefont.draw(sprite, scoreText, roomNameBoxX + 313, roomNameBoxY - 45 - (i * 36) + titlefont.getLineHeight());
            sprite.end();
        }
    }

    public void findAndUpdateChart(String hash) {
        final SongData[] songs = main.getSongDatabase().getSongDatas(new String[]{hash});

        if (songs.length > 0) {
            final SongData data = songs[0];
            updateChart(data, false);
            missingChart = false;
        } else {
            missingChart = true;
            server.sendPacket(new UserNomap());
        }
    }

    @Override
    public void dispose() {
        if (titlefont != null) {
            titlefont.dispose();
            titlefont = null;
        }
        super.dispose();
    }

    public void startChart() {
        final SongData songData = selectedChart;
        if (main.getPlayerResource().setBMSFile(Paths.get(songData.getPath()), BMSPlayerMode.PLAY)) {
            main.changeState(MainStateType.DECIDE);
        }
    }

    private void drawBox(float x, float y, float w, float h, Color outline, Color fill) {
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(fill);
        shape.rect(x, y, w, h);
        shape.end();

        //Room Name outline
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(outline);
        Gdx.gl.glLineWidth(2.0f);
        shape.rect(x, y, w, h);
        shape.end();
    }

    private void drawOutlineRect(float x, float y, float w, float h, Color color) {
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(color);
        shape.rect(x, y, w, h);
        shape.end();
    }

    private int getButtonKey(int i) {
        return main.getPlayerConfig().getMode7().getKeyboardConfig().getKeyAssign()[i];
    }

    private int getScratchKey(boolean up) {
        return main.getPlayerConfig().getMode7().getKeyboardConfig().getKeyAssign()[up ? 7 : 8];
    }

    private int getStart() {
        return main.getPlayerConfig().getMode7().getKeyboardConfig().getStart();
    }

    private int getSelect() {
        return main.getPlayerConfig().getMode7().getKeyboardConfig().getSelect();
    }
}
