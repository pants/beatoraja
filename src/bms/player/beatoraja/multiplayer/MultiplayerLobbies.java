package bms.player.beatoraja.multiplayer;

import bms.player.beatoraja.MainController;
import bms.player.beatoraja.MainState;
import bms.player.beatoraja.PlayerConfig;
import bms.player.beatoraja.input.BMSPlayerInputProcessor;
import bms.player.beatoraja.input.KeyBoardInputProcesseor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MultiplayerLobbies extends MainState {

    private BitmapFont titlefont;
    private ShapeRenderer shape;

    private BMSPlayerInputProcessor input;
    private KeyBoardInputProcesseor keyboard;
    private PlayerConfig config;

    public MultiplayerLobbies(MainController main) {
        super(main);
    }

    @Override
    public void create() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("skin/default/VL-Gothic-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) (20 * getSkin().getScaleY());
        titlefont = generator.generateFont(parameter);
        shape = new ShapeRenderer();

        input = main.getInputProcessor();
        keyboard = input.getKeyBoardInputProcesseor();
    }

    @Override
    public void render() {
        final SpriteBatch sprite = main.getSpriteBatch();
        final float scaleX = (float) getSkin().getScaleX();
        final float scaleY = (float) getSkin().getScaleY();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sprite.begin();
        titlefont.setColor(Color.CYAN);
        titlefont.draw(sprite, "Available Lobbies ()", 80 * scaleX, 650 * scaleY);
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.WHITE);
        shape.rect(80 * scaleX, 690 * scaleY, 200, 30);
        shape.end();

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
