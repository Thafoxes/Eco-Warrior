package io.github.eco_warrior.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import io.github.eco_warrior.Main;
import io.github.eco_warrior.constant.ConstantsVar;
import io.github.eco_warrior.mainmenu.MainMenuScreen;

public class EndScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private boolean fadeToBlackMode = false;
    private float fadeAlpha = 0f;
    private float fadeSpeed = .8f;
    private boolean showMovingImage = false;
    private float imageY;
    private float imageTargetY;
    private float imageSpeed = 100f; // Positive: moves up
    private float timer = 0f;
    private float displayDuration = 2.5f;
    private Texture movingImage;
    private Texture blackPixel;
    private float imageScale = 2.0f; // Adjust as needed
    private float imageOffsetX = 500f; // Positive: move right, Negative: move left
    private Music endScreenMusic;

    public EndScreen(Main game, Texture movingImage, float displayDuration) {
        this.game = game;
        this.fadeToBlackMode = true;
        this.movingImage = movingImage;
        this.displayDuration = displayDuration;
        
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        blackPixel = new Texture(Gdx.files.internal("Image/anotherblack.jpg"));
        endScreenMusic = Gdx.audio.newMusic(Gdx.files.internal("sound_effects/speech2.mp3"));
        endScreenMusic.setLooping(false);
        endScreenMusic.play();
        if (fadeToBlackMode && movingImage != null) {
            float scaledHeight = movingImage.getHeight() * imageScale;
            imageY = -scaledHeight; // Start below the screen
            imageTargetY = ConstantsVar.WINDOW_HEIGHT * 2; // Center vertically
        }
    }

    @Override
    public void render(float delta) {
        float scaledWidth = movingImage.getWidth() * imageScale;
        float scaledHeight = movingImage.getHeight() * imageScale;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (fadeToBlackMode) {
            // Fade to black
            if (fadeAlpha < 1f) {
                fadeAlpha += fadeSpeed * delta;
                if (fadeAlpha > 1f) fadeAlpha = 1f;
            } else {
                showMovingImage = true;
            }

            // Draw black overlay
            batch.begin();
            batch.setColor(0, 0, 0, fadeAlpha);
            batch.draw(blackPixel, 0, 0, ConstantsVar.BUTTON_WIDTH, ConstantsVar.WINDOW_HEIGHT);
            batch.setColor(1, 1, 1, 1);
            batch.end();

            // Show moving image after fade
            if (showMovingImage && movingImage != null) {
                batch.begin();
                if (imageY < imageTargetY) {
                    imageY += imageSpeed * delta; // Move up
                    if (imageY > imageTargetY) imageY = imageTargetY;
                }
                float imageX = (ConstantsVar.BUTTON_WIDTH - scaledWidth) / 2f + imageOffsetX; // Center + offset
                batch.draw(movingImage, imageX, imageY, scaledWidth, scaledHeight);
                batch.end();

                timer += delta;
                if (timer > displayDuration) {
                    game.setScreen(new MainMenuScreen(game));
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (blackPixel != null) blackPixel.dispose();
        if (endScreenMusic != null) endScreenMusic.dispose();
        // Do not dispose movingImage if managed elsewhere
    }

    // Optional: Setter for imageOffsetX
    public void setImageOffsetX(float offsetX) {
        this.imageOffsetX = offsetX;
    }
}
