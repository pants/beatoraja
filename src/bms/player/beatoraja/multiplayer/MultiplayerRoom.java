package bms.player.beatoraja.multiplayer;

import bms.player.beatoraja.*;
import bms.player.beatoraja.input.BMSPlayerInputProcessor;
import bms.player.beatoraja.input.KeyBoardInputProcesseor;
import bms.player.beatoraja.multiplayer.packets.out.RoomSetSong;
import bms.player.beatoraja.multiplayer.packets.out.UserNoMap;
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

import static bms.player.beatoraja.skin.SkinProperty.*;

public class MultiplayerRoom extends MainState {

    private BitmapFont titlefont;
    private BitmapFont chartTitleFont;
    private ShapeRenderer shape;

    private BMSPlayerInputProcessor input;
    private KeyBoardInputProcesseor keyboard;
    private PlayerConfig config;

    private MPServerConnection server;
    private String lastChartHash = null;

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

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("skin/default/VL-Gothic-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) (20 * getSkin().getScaleY());
        titlefont = generator.generateFont(parameter);


        shape = new ShapeRenderer();

        input = main.getInputProcessor();
        keyboard = input.getKeyBoardInputProcesseor();
        config = main.getPlayerResource().getPlayerConfig();

        server = main.getMultiplayerServer();

        //todo: find a better way of handling this
        final SongData songData = main.getPlayerResource().getSongdata();
        if (songData != null) {
            updateChart(songData, true);
        }
    }

    private void updateChart(SongData songData, boolean setChart) {
        final String chartHash = songData.getCharthash();

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
                server.sendPacket(new RoomSetSong(songData.getTitle(), songData.getDifficulty(), songData.getLevel(), songData.getCharthash()));
            }
        }

        lastChartHash = main.getPlayerResource().getSongdata().getCharthash();
    }

    @Override
    public void input() {
        if (input.getKeyBoardInputProcesseor().getLastPressedKey() != -1) {
            int lastPressedKey = keyboard.getLastPressedKey();
            if (lastPressedKey == Input.Keys.NUM_2) {
                main.changeState(MainStateType.MUSICSELECT);
            } else if (lastPressedKey == Input.Keys.NUM_3) {
                main.changeState(MainStateType.DECIDE);
            }
        }
    }

    @Override
    public void render() {
        final SpriteBatch sprite = main.getSpriteBatch();
        final float scaleX = (float) getSkin().getScaleX();
        final float scaleY = (float) getSkin().getScaleY();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        TextureRegion textureRegion = getImage(IMAGE_STAGEFILE);
        if (lastChartHash != null && getImage(IMAGE_STAGEFILE) != null) {
//            sprite.begin();
//
//            float spriteX = 0, spriteY = 0, spriteW = , spriteH;
//            float differenceX = textureRegion.getRegionWidth() - getSkin().getWidth();
//            float differenceY = textureRegion.getRegionHeight() - getSkin().getHeight();
//
//            if (differenceX > 0 || differenceY > 0) {
//                if (differenceX > differenceY) {
//                    spriteX = 0;
//                    spriteW =
//                } else {
//
//                }
//            }
//
//            if (spri)
//                int spriteX = textureRegion.getRegionWidth();
//            sprite.draw(getImage(IMAGE_STAGEFILE).getTexture(), getSkin().getWidth() - getI, 0, 500, 500);
//            sprite.end();
        }

        sprite.begin();

        //Rectangle surrounding player list
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.WHITE);
        shape.rect(80 * scaleX, 100, 400, getSkin().getHeight() - 170);
        shape.end();

        //Rectangle surrounding current chart
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.WHITE);
        shape.rect(getSkin().getWidth() - 480, getSkin().getHeight() - 190, 400, 120);
        shape.end();

        sprite.end();


        sprite.begin();
        titlefont.setColor(Color.CYAN);

        final RoomData roomData = server.getRoomData();
        if (roomData.getRoomInfo() != null && roomData.getProperties() != null) {
            String line = String.format("%s (%s/%s)",
                    roomData.getRoomInfo().getName(),
                    roomData.getProperties().getUsers().length, roomData.getRoomInfo().getMax());

            titlefont.draw(sprite, line, 80 * scaleX, 680 * scaleY);

            for (int i = 0; i < roomData.getProperties().getUsers().length; i++) {
                UserType userType = roomData.getProperties().getUsers()[i];
                titlefont.draw(sprite, userType.getName(), 80 * scaleX, (640 + 26 * i) * scaleY);
            }

            //chart title:
            if (lastChartHash != null) {
                SongData data = main.getPlayerResource().getSongdata();
                chartTitleFont.draw(sprite, data.getTitle() + " " + data.getSubtitle(), getSkin().getWidth() - 470, getSkin().getHeight() - 80);
                chartTitleFont.draw(sprite, data.getFullArtist(), getSkin().getWidth() - 470, getSkin().getHeight() - 110);
                titlefont.draw(sprite, "lv" +
                        "" + data.getLevel(), getSkin().getWidth() - 470, getSkin().getHeight() - 140);
            }
        } else {
            titlefont.draw(sprite, "Loading..", 80 * scaleX, 680 * scaleY);
        }
        sprite.end();
    }

    public void findAndUpdateChart(String hash) {
        final SongData[] songs = main.getSongDatabase().getSongDatas(new String[]{hash});

        if (songs.length > 0) {
            final SongData data = songs[0];
            updateChart(data, false);
            missingChart = false;
        } else {
            missingChart = true;
            server.sendPacket(new UserNoMap());
        }
    }

    public void startChart() {
        final SongData songData = main.getPlayerResource().getSongdata();
        if (main.getPlayerResource().setBMSFile(Paths.get(songData.getPath()), BMSPlayerMode.PLAY)) {
            main.changeState(MainStateType.DECIDE);
        }
    }
}
