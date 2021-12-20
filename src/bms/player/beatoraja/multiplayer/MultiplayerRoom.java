package bms.player.beatoraja.multiplayer;

import bms.player.beatoraja.MainController;
import bms.player.beatoraja.MainState;
import bms.player.beatoraja.PlayerConfig;
import bms.player.beatoraja.Resolution;
import bms.player.beatoraja.input.BMSPlayerInputProcessor;
import bms.player.beatoraja.input.KeyBoardInputProcesseor;
import bms.player.beatoraja.multiplayer.packets.Packet;
import bms.player.beatoraja.multiplayer.packets.in.RoomUpdate;
import bms.player.beatoraja.multiplayer.packets.in.ServerRoomJoined;
import bms.player.beatoraja.multiplayer.packets.out.ServerRoomJoin;
import bms.player.beatoraja.multiplayer.types.RoomType;
import bms.player.beatoraja.multiplayer.types.UserType;
import bms.player.beatoraja.skin.SkinType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class MultiplayerRoom extends MainState {

    private BitmapFont titlefont;
    private ShapeRenderer shape;

    private BMSPlayerInputProcessor input;
    private KeyBoardInputProcesseor keyboard;
    private PlayerConfig config;

    private MPServerConnection server;

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
    }



    @Override
    public void render() {
        final SpriteBatch sprite = main.getSpriteBatch();
        final float scaleX = (float) getSkin().getScaleX();
        final float scaleY = (float) getSkin().getScaleY();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sprite.begin();
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.WHITE);
        shape.rect(80 * scaleX, 100, 400, getSkin().getHeight() - 170);
        shape.rect(80 * scaleX, 100, 400, getSkin().getHeight() - 170);
        shape.end();

        sprite.end();


        sprite.begin();
        titlefont.setColor(Color.CYAN);
        if(server.getRoomInfo() != null && server.getLastRoomUpdate() != null){
            String line = String.format("%s (%s/%s)",
                    server.getRoomInfo().getName(),
                    server.getLastRoomUpdate().getUsers().length, server.getRoomInfo().getMax());

            titlefont.draw(sprite, line, 80 * scaleX, 680 * scaleY);

            for (int i = 0; i < server.getLastRoomUpdate().getUsers().length; i++) {
                UserType userType = server.getLastRoomUpdate().getUsers()[i];
                titlefont.draw(sprite, userType.getName(), 80 * scaleX, (640 + 26 * i) * scaleY);
            }
        } else {
            titlefont.draw(sprite, "Loading..", 80 * scaleX, 680 * scaleY);
        }
        sprite.end();
    }
}
