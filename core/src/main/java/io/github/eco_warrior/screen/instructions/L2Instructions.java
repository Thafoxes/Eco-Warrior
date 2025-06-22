package io.github.eco_warrior.screen.instructions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.LevelTwoScreen;
import io.github.eco_warrior.Main;
import io.github.eco_warrior.controller.buttonGenerator;
import io.github.eco_warrior.controller.FontGenerator;
import io.github.eco_warrior.enums.textEnum;

import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_HEIGHT;
import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_WIDTH;

public class L2Instructions implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Stage stage;

    // Fonts
    private FontGenerator titleFont;
    private FontGenerator instructionFont;

    // Images
    private Texture instructionImage;
    private Texture backgroundImage;

    // Button
    private buttonGenerator buttonGen;
    private TextButton acknowledgeButton;

    public L2Instructions(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);
        batch = new SpriteBatch();
        stage = new Stage(viewport);

        // Set camera position to center of screen
        camera.position.set(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2, 0);
        camera.update();

        // Initialize fonts
        titleFont = new FontGenerator(36, Color.WHITE, Color.BLACK);
        instructionFont = new FontGenerator(24, Color.WHITE, Color.BLACK);

        // Load images
        instructionImage = new Texture(Gdx.files.internal("ui/instructions/L3instructions.png"));
        backgroundImage = new Texture(Gdx.files.internal("Image/girl.png"));

        // Create acknowledge button
        buttonGen = new buttonGenerator();
        buttonGen.createCustomButton("acknowledge-button", Color.BLUE, Color.CYAN, Color.SKY);
        acknowledgeButton = buttonGen.createNewButton("Got it!", "acknowledge-button",
                WINDOW_WIDTH, WINDOW_HEIGHT, textEnum.X_CENTER, textEnum.BOTTOM);

        // Add button click listener
        acknowledgeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Play sound effect
                Gdx.audio.newSound(Gdx.files.internal("sound_effects/Button_Click.mp3")).play(0.5f);

                // Proceed to Level Three Screen
                game.setScreen(new LevelTwoScreen(game));
            }
        });

        // Add button to stage
        stage.addActor(acknowledgeButton);

        // Set input processor
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Draw title
        titleFont.fontDraw(batch, "Level 2: Forest in Peril ", camera,
            new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT),
            textEnum.X_CENTER, textEnum.TOP);

        batch.begin();

        // Draw background image (scaled to fit screen)
        float scaleBGM = 3;
        batch.draw(backgroundImage, 0, 0, backgroundImage.getWidth() * scaleBGM, backgroundImage.getHeight() * scaleBGM);


        // Draw the instruction image centered
        float scale = 0.6f; // Scale factor for image
        float imageWidth = instructionImage.getWidth() * scale;
        float imageHeight = instructionImage.getHeight() * scale;

        batch.draw(instructionImage,
                WINDOW_WIDTH / 2 - imageWidth / 2,
                WINDOW_HEIGHT / 2 - 40f,
                imageWidth, imageHeight);

        // Draw instruction text
        String[] instructions = {
            "1. Use shovel to dig the hole accodring to the flag and sapling",
            "2. Use the shovel to hit the monsters!",
            "3. Water the plants to grow them",
            "4. Use fertilizer to revive the plants",
            "5. Plant all the trees to complete the level",
        };

        float textY = WINDOW_HEIGHT / (instructions.length * 4); // Position text below the title
        for (int i = 0; i < instructions.length; i++) {
            instructionFont.getFont().draw(batch, instructions[i],
                    WINDOW_WIDTH / 2 - 300, (WINDOW_HEIGHT / 3) + 50f - textY * i);
        }

        batch.end();

        // Draw stage (for button)
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        titleFont.dispose();
        instructionFont.dispose();
        instructionImage.dispose();
        backgroundImage.dispose();
    }
}
