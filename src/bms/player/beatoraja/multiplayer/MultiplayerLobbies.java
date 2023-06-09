package bms.player.beatoraja.multiplayer;

import bms.player.beatoraja.MainController;
import bms.player.beatoraja.MainState;
import bms.player.beatoraja.PlayerConfig;
import bms.player.beatoraja.Resolution;
import bms.player.beatoraja.input.BMSPlayerInputProcessor;
import bms.player.beatoraja.input.KeyBoardInputProcesseor;
import bms.player.beatoraja.multiplayer.packets.out.ServerRoomJoin;
import bms.player.beatoraja.multiplayer.packets.out.ServerRoomNew;
import bms.player.beatoraja.multiplayer.skinning.MultiplayerLobbiesSkin;
import bms.player.beatoraja.multiplayer.types.RoomType;
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

public class MultiplayerLobbies extends MainState {

    private BitmapFont titlefont;
    private ShapeRenderer shape;

    private BMSPlayerInputProcessor input;
    private KeyBoardInputProcesseor keyboard;
    private PlayerConfig config;

    private MPServerConnection server;
    private boolean failedToConnect = false;

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

        server = main.getMultiplayerServer();

        if (!server.isConnected()) {
            final String serverAddress = main.getConfig().getMultiplayerServer();
            final String host = serverAddress.split(":")[0];
            final int port = serverAddress.contains(":") ? Integer.parseInt(serverAddress.split(":")[1]) : 39079;

            if (server.connect(host, port)) {
                server.authenticate(config.getName(), "", "v0.19");
            } else {
                failedToConnect = true;
            }
        }
    }

    @Override
    public void input() {
        if (input.getKeyBoardInputProcesseor().getLastPressedKey() != -1) {
            int lastPressedKey = keyboard.getLastPressedKey();
            if (lastPressedKey == Input.Keys.NUM_1) {
                server.sendPacket(new ServerRoomNew(config.getName() + "'s Room!", null));
                keyboard.setLastPressedKey(-1);
            }
        }

        if (input.isControlKeyPressed(KeyBoardInputProcesseor.ControlKeys.ESCAPE)) {
            if (server.isConnected()) {
                server.disconnect();
            }
            main.changeState(MainStateType.MUSICSELECT);
        }
    }

    @Override
    public void render() {
        if (server.joinRoom) {
            server.joinRoom = false;
            main.changeState(MainStateType.MULTIPLAYER_LOBBY);
            return;
        }

        final SpriteBatch sprite = main.getSpriteBatch();
        final float scaleX = (float) getSkin().getScaleX();
        final float scaleY = (float) getSkin().getScaleY();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        final Rectangle createRoomDimensions = new Rectangle(80 * scaleX, 40, 200 * scaleX, 40 * scaleY);
        Color createButtonColor;
        if (createRoomDimensions.contains(input.getMouseX(), input.getMouseY())) {
            createButtonColor = input.getMouseButton() != 1 ? Color.valueOf("00dd00") : Color.valueOf("55ff55");
        } else {
            createButtonColor = Color.GREEN;
        }
        float baseRoomY = main.getConfig().getWindowHeight() - 120;

        sprite.begin();

        //White background rectangle to surround the boxes of individual rooms
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.WHITE);
        shape.rect(80 * scaleX, 100, getSkin().getWidth() - 160, getSkin().getHeight() - 170);
        shape.end();

        //Green rectangle surrounding for the "create room" button
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(createButtonColor);
        shape.rect(80 * scaleX, 40, 200 * scaleX, 40 * scaleY);
        shape.end();

        //Stacked boxes which will contain the name of each room
        for (int i = 0; i < server.getAvailableRooms().size(); i++) {
            shape.begin(ShapeRenderer.ShapeType.Line);
            shape.setColor(Color.CYAN);
            final Rectangle roomButton = new Rectangle(90 * scaleX, baseRoomY * scaleY - (50 * i), 300, 40);
            shape.rect(roomButton.x, roomButton.y, roomButton.width, roomButton.height);

            //Check if the button is being pressed
            if (roomButton.contains(input.getMouseX(), input.getMouseY())) {
                createButtonColor = Color.valueOf("55ffff");
                if (input.getMouseButton() == 0 && input.isMousePressed()) {
                    RoomType room = server.getAvailableRooms().get(i);
                    server.sendPacket(new ServerRoomJoin(room.getId(), null, null));
                }
            } else {
                createButtonColor = Color.GREEN;
            }

            shape.end();
        }

        sprite.end();


        sprite.begin();
        //Title
        titlefont.setColor(Color.CYAN);
        titlefont.draw(sprite, "Available Lobbies (" + server.getAvailableRooms().size() + ")", 80 * scaleX, 680 * scaleY);

        if (failedToConnect) {
            titlefont.setColor(Color.RED);
            titlefont.draw(sprite, "Failed to connect to multiplayer server", 90 * scaleX, 380 * scaleY);
        } else if (!server.isConnected()) {
            titlefont.setColor(Color.WHITE);
            titlefont.draw(sprite, "Connecting to server...", 90 * scaleX, 380 * scaleY);
        } else if (server.getAvailableRooms().isEmpty()) {
            titlefont.draw(sprite, "No rooms found!", 90 * scaleX, 380 * scaleY);
        } else {
            //Room name inside previously stacked boxes
            for (int i = 0; i < server.getAvailableRooms().size(); i++) {
                RoomType room = server.getAvailableRooms().get(i);
                titlefont.setColor(Color.CYAN);
                titlefont.draw(sprite, room.getName(), 100 * scaleX, (baseRoomY + 28) * scaleY - (50 * i));
            }
        }

        //Text inside of green rectangle to create a game
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
}
