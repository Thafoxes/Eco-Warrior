package io.github.eco_warrior.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

import java.util.List;

public class DialogBox {
    private List<String> dialogLines;
    private String speaker;
    private int currentLine;
    private boolean visible;

    private Rectangle boxRect;
    private BitmapFont font;
    private BitmapFont speakerFont;
    private float padding = 18f;
    private float minWidth = 120f;
    private float minHeight = 40f;
    private ShapeRenderer shapeRenderer;

    private String advancePrompt = "[Tab]";
    private int advanceKey = Input.Keys.TAB;
    private Runnable onCompleteCallback = null;


    public DialogBox(BitmapFont font, BitmapFont speakerFont) {
        this.font = font;
        this.speakerFont = speakerFont;
        this.boxRect = new Rectangle(0, 0, minWidth, minHeight);
        this.visible = false;
        this.currentLine = 0;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void setAdvancePrompt(String prompt) {
        this.advancePrompt = prompt;
        updateBoxRect();
    }

    /**
     * Starts a dialog with the given speaker and lines.
     * After done, perform the onComplete callback if provided.
     * @param speaker
     * @param lines
     * @param onComplete
     */
    public void startDialog(String speaker, List<String> lines, Runnable onComplete) {
        if (lines.isEmpty()) return;
        this.onCompleteCallback = onComplete;
        this.speaker = speaker;
        this.dialogLines = lines;
        this.currentLine = 0;
        this.visible = true;
        updateBoxRect();
    }

    // In your closeDialog or advanceDialog method:
    private void closeDialog() {
        visible = false;
        if (currentLine >= dialogLines.size()) {
            if (onCompleteCallback != null) {
                onCompleteCallback.run();
                onCompleteCallback = null;
            }
        }
    }

    public void update() {
        if (!visible) return;
        if (Gdx.input.isKeyJustPressed(advanceKey)) {
            currentLine++;
            if (currentLine >= dialogLines.size()) {
               closeDialog();
            } else {
                updateBoxRect();
            }
        }
    }

    public void updateBoxRect() {
        if (!visible) return;
        String text = dialogLines.get(currentLine);
        float maxTextWidth = Gdx.graphics.getWidth() * 0.7f; // wrap text if too long

        // Measure dialog text
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, text, Color.WHITE, maxTextWidth, Align.left, true);
        float textWidth = layout.width;
        float textHeight = layout.height;

        // Measure speaker text properly
        GlyphLayout speakerLayout = new GlyphLayout(speakerFont, speaker);
        float speakerWidth = speakerLayout.width;
        float speakerHeight = speakerLayout.height;


        // Measure prompt text (only if it will show)
        float promptWidth = 0f;
        float promptHeight = 0f;
        if (currentLine < dialogLines.size() - 1 && advancePrompt != null && !advancePrompt.isEmpty()) {
            GlyphLayout promptLayout = new GlyphLayout(font, advancePrompt);
            promptWidth = promptLayout.width;
            promptHeight = promptLayout.height;
        }

        // Use max width needed - consider speaker name width too
        float boxWidth = Math.max(
            Math.max(
                Math.max(textWidth, promptWidth),
                speakerWidth) + 2 * padding,
            minWidth);
        // Account for speaker height properly
        float boxHeight = Math.max(textHeight + speakerHeight + 3 * padding, minHeight);

        // Center horizontally, place at bottom with margin
        float margin = 18f;
        float boxX = (Gdx.graphics.getWidth() - boxWidth) / 2f;
        float boxY = margin;

        boxRect.set(boxX, boxY, boxWidth, boxHeight);
    }

    public void render(SpriteBatch batch) {
        if (!visible) return;

        batch.end();

        // Draw background
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.85f));
        shapeRenderer.rect(boxRect.x, boxRect.y, boxRect.width, boxRect.height);
        shapeRenderer.end();

        batch.begin();

        // Draw speaker name
        float namePad = padding / 2f;
        speakerFont.setColor(Color.WHITE);
        speakerFont.draw(batch, speaker,
            boxRect.x + namePad + 6,
            boxRect.y + boxRect.height - namePad
        );

        // Draw dialog text
        font.setColor(Color.WHITE);
        font.draw(batch, dialogLines.get(currentLine),
            boxRect.x + padding, boxRect.y + boxRect.height - speakerFont.getCapHeight() - padding,
            boxRect.width - 2 * padding, Align.left, true);

        // Draw prompt
        if (currentLine < dialogLines.size() - 1 && advancePrompt != null && !advancePrompt.isEmpty()) {
            GlyphLayout promptLayout = new GlyphLayout(font, advancePrompt);
            float rightMargin = padding;
            float bottomMargin = padding / 2f;
            float fX = boxRect.x + boxRect.width - promptLayout.width - rightMargin;
            float fY = boxRect.y + promptLayout.height + bottomMargin;
            font.draw(batch, advancePrompt, fX, fY);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
