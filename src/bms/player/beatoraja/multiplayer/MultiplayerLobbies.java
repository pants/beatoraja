package bms.player.beatoraja.multiplayer;

import bms.player.beatoraja.MainController;
import bms.player.beatoraja.MainState;
import bms.player.beatoraja.PlayerConfig;
import bms.player.beatoraja.Resolution;
import bms.player.beatoraja.config.KeyConfigurationSkin;
import bms.player.beatoraja.input.BMSPlayerInputProcessor;
import bms.player.beatoraja.input.KeyBoardInputProcesseor;
import bms.player.beatoraja.skin.SkinType;
import com.badlogic.gdx.Gdx;
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

    public MultiplayerLobbies(MainController main) {
        super(main);
    }

    @Override
    public void create() {
        loadSkin(SkinType.MULTIPLAYER_LOBBIES);
        if(getSkin() == null) {
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
    }

    @Override
    public void render() {
        final SpriteBatch sprite = main.getSpriteBatch();
        final float scaleX = (float) getSkin().getScaleX();
        final float scaleY = (float) getSkin().getScaleY();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        final Rectangle buttonDimensions = new Rectangle(80 * scaleX, 40, 200 * scaleX, 40 * scaleY);
        Color createButtonColor;
        if(buttonDimensions.contains(input.getMouseX(), input.getMouseY())){
            createButtonColor = input.getMouseButton() != 1 ? Color.valueOf("00dd00") : Color.valueOf("55ff55");
        } else {
            createButtonColor = Color.GREEN;
        }

        sprite.begin();
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.WHITE);
        shape.rect(80 * scaleX, 100, getSkin().getWidth() - 160, getSkin().getHeight() - 170);
        shape.end();


        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(createButtonColor);
        shape.rect(80 * scaleX, 40, 200 * scaleX, 40 * scaleY);
        shape.end();
        sprite.end();


        sprite.begin();
        titlefont.setColor(Color.CYAN);
        titlefont.draw(sprite, "Available Lobbies ()", 80 * scaleX, 680 * scaleY);

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
