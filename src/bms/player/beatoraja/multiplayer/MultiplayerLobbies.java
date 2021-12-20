package bms.player.beatoraja.multiplayer;

import bms.player.beatoraja.MainController;
import bms.player.beatoraja.MainState;
import bms.player.beatoraja.PlayerConfig;
import bms.player.beatoraja.Resolution;
import bms.player.beatoraja.config.KeyConfigurationSkin;
import bms.player.beatoraja.input.BMSPlayerInputProcessor;
import bms.player.beatoraja.input.KeyBoardInputProcesseor;
import bms.player.beatoraja.multiplayer.packets.Packet;
import bms.player.beatoraja.multiplayer.packets.in.ServerRooms;
import bms.player.beatoraja.multiplayer.packets.out.ServerRoomNew;
import bms.player.beatoraja.skin.SkinType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class MultiplayerLobbies extends MainState {

    private BitmapFont titlefont;
    private ShapeRenderer shape;

    private BMSPlayerInputProcessor input;
    private KeyBoardInputProcesseor keyboard;
    private PlayerConfig config;
    private MPServerConnection serverConnection;

    private List<ServerRoom> multiplayerRooms;

    public MultiplayerLobbies(MainController main) {
        super(main);
    }

    @Override
    public void create() {
        loadSkin(SkinType.MULTIPLAYER_LOBBIES);
        if (getSkin() == null) {
            this.setSkin(new MultiplayerLobbiesSkin(Resolution.HD, main.getConfig().getResolution()));
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("skin/default/VL-Gothic-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) (20 * getSkin().getScaleY());
        titlefont = generator.generateFont(parameter);
        shape = new ShapeRenderer();

        input = main.getInputProcessor();
        keyboard = input.getKeyBoardInputProcesseor();
        config = main.getPlayerResource().getPlayerConfig();

        multiplayerRooms = new ArrayList<>();
        serverConnection = new MPServerConnection(this);
        serverConnection.connect("45.56.99.79", 39079);

        serverConnection.subscribeToPacket("server.rooms", this::onServerRooms);

        serverConnection.auth(config.getName(), "", "v0.19");
    }

    @Override
    public void render() {
        final SpriteBatch sprite = main.getSpriteBatch();
        final float scaleX = (float) getSkin().getScaleX();
        final float scaleY = (float) getSkin().getScaleY();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (input.getKeyBoardInputProcesseor().getLastPressedKey() != -1) {
            int lastPressedKey = keyboard.getLastPressedKey();
            if (lastPressedKey == Input.Keys.NUM_1) {
                serverConnection.sendPacket(new ServerRoomNew(config.getName()  + "'s Room!", null));
                keyboard.setLastPressedKey(-1);
            }
        }

        final Rectangle buttonDimensions = new Rectangle(80 * scaleX, 40, 200 * scaleX, 40 * scaleY);
        Color createButtonColor;
        if (buttonDimensions.contains(input.getMouseX(), input.getMouseY())) {
            createButtonColor = input.getMouseButton() != 1 ? Color.valueOf("00dd00") : Color.valueOf("55ff55");
        } else {
            createButtonColor = Color.GREEN;
        }
        float baseRoomY = getSkin().getHeight() - 120;

        sprite.begin();
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.WHITE);
        shape.rect(80 * scaleX, 100, getSkin().getWidth() - 160, getSkin().getHeight() - 170);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(createButtonColor);
        shape.rect(80 * scaleX, 40, 200 * scaleX, 40 * scaleY);
        shape.end();

        for (int i = 0; i < multiplayerRooms.size(); i++) {
            shape.begin(ShapeRenderer.ShapeType.Line);
            shape.setColor(Color.CYAN);
            shape.rect(90 * scaleX, baseRoomY * scaleY - (50 * i), 300, 40);
            shape.end();
        }

        sprite.end();


        sprite.begin();
        titlefont.setColor(Color.CYAN);
        titlefont.draw(sprite, "Available Lobbies (" + multiplayerRooms.size() + ")", 80 * scaleX, 680 * scaleY);

        if (!serverConnection.isConnected()) {
            titlefont.setColor(Color.WHITE);
            titlefont.draw(sprite, "Connecting to server...", 90 * scaleX, 380 * scaleY);
        } else if (multiplayerRooms.isEmpty()) {
            titlefont.draw(sprite, "No rooms found!", 90 * scaleX, 380 * scaleY);
        } else {
            for (int i = 0; i < multiplayerRooms.size(); i++) {
                ServerRoom room = multiplayerRooms.get(i);
                titlefont.setColor(Color.CYAN);
                titlefont.draw(sprite, room.name, 100 * scaleX, (baseRoomY + 28) * scaleY - (50 * i));
            }
        }

        titlefont.setColor(createButtonColor);
        titlefont.draw(sprite, "Create Game [1]", 90 * scaleX, 68 * scaleY);
        sprite.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (titlefont != null) {
            titlefont.dispose();
            titlefont = null;
        }
        if (shape != null) {
            shape.dispose();
            shape = null;
        }
    }

    public void onServerRooms(Packet packet) {
        final ServerRooms rooms = ((ServerRooms) packet);

        if (rooms.getRooms() == null) {
            System.out.println("No rooms lol");
            return;
        }

        multiplayerRooms.addAll(Arrays.asList(rooms.getRooms()));
    }
}
