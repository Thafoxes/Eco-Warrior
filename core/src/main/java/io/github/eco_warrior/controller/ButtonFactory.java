package io.github.eco_warrior.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import static io.github.eco_warrior.constant.ConstantsVar.BUTTON_HEIGHT;
import static io.github.eco_warrior.constant.ConstantsVar.BUTTON_WIDTH;

public class ButtonFactory {

    private Stage stage;
    private Skin skin;
    private fontGenerator fontGen;
    private float horizontalPadding = 20f;

    public ButtonFactory() {
        fontGen = new fontGenerator();
        skin = new Skin();
        //get font from font Generator
        skin.add("default-font", fontGen.getFont());

        designDefaultButton();

    }

    /**
     *  Create a new custom button based on user preference
     *  buttonStyleName: the button you want to call
     *  borderColor: the button border colour
     *  backgroundColor: the button background colour
     *  hoveredButtonName: the hovered button call,
     *  hoveredBackgroundColor: hovered background button colour
     *
     * */
    public void createCustomButton(String buttonStyleName, Color borderColor, Color backgroundColor,
                                   Color hoveredBackgroundColor
                                   ) {
        String hoveredButtonName = buttonStyleName+"-hovered";

        designDefaultButton(buttonStyleName, borderColor, backgroundColor);
        designHoverDefaultButton(hoveredButtonName, borderColor, hoveredBackgroundColor);
        styleButton(buttonStyleName, hoveredButtonName, buttonStyleName);

    }

    private void designDefaultButton() {
        Pixmap defaultPixmap = new Pixmap(BUTTON_WIDTH , BUTTON_HEIGHT, Pixmap.Format.RGBA8888);
        //border circle
        defaultPixmap.setColor(Color.BLACK);
        defaultPixmap.fillRectangle(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT);
        //background
        defaultPixmap.setColor(Color.LIGHT_GRAY);
        defaultPixmap.fillRectangle(2,2, BUTTON_WIDTH - 4, BUTTON_HEIGHT - 4);

        TextureRegion defaultRegion = new TextureRegion(
            new Texture(defaultPixmap)
        );

        skin.add("default-button", defaultRegion);

        defaultPixmap.dispose();

        designHoverDefaultButton();
    }

    /**
     *  function overload, I want to split different instruction for future design
     */
    private void designDefaultButton(String buttonName, Color borderColor, Color backgroundColor) {
        Pixmap defaultPixmap = new Pixmap(BUTTON_WIDTH , BUTTON_HEIGHT, Pixmap.Format.RGBA8888);
        //border circle
        defaultPixmap.setColor(borderColor);
        defaultPixmap.fillRectangle(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT);
        //background
        defaultPixmap.setColor(backgroundColor);
        defaultPixmap.fillRectangle(2,2, BUTTON_WIDTH - 4, BUTTON_HEIGHT - 4);

        TextureRegion defaultRegion = new TextureRegion(
            new Texture(defaultPixmap)
        );

        skin.add(buttonName, defaultRegion);

        defaultPixmap.dispose();

    }


    private void designHoverDefaultButton() {
        Pixmap defaultHoverPixmap = new Pixmap(BUTTON_WIDTH , BUTTON_HEIGHT, Pixmap.Format.RGBA8888);
        //border circle
        defaultHoverPixmap.setColor(Color.BLACK);
        defaultHoverPixmap.fillRectangle(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT);
        //background
        defaultHoverPixmap.setColor(Color.DARK_GRAY);
        defaultHoverPixmap.fillRectangle(2,2, BUTTON_WIDTH - 4, BUTTON_HEIGHT - 4);

        TextureRegion defaultHoverButton = new TextureRegion(
                new Texture(defaultHoverPixmap)
            );

        skin.add("default-hover-button", defaultHoverButton);

        defaultHoverPixmap.dispose();

        styleButton();
    }

    private void designHoverDefaultButton(String hoveredButtonName, Color borderColor, Color backgroundColor) {
        Pixmap defaultHoverPixmap = new Pixmap(BUTTON_WIDTH , BUTTON_HEIGHT, Pixmap.Format.RGBA8888);
        //border circle
        defaultHoverPixmap.setColor(borderColor);
        defaultHoverPixmap.fillRectangle(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT);
        //background
        defaultHoverPixmap.setColor(backgroundColor);
        defaultHoverPixmap.fillRectangle(2,2, BUTTON_WIDTH - 4, BUTTON_HEIGHT - 4);

        TextureRegion defaultHoverButton = new TextureRegion(
            new Texture(defaultHoverPixmap)
        );

        skin.add(hoveredButtonName, defaultHoverButton);

        defaultHoverPixmap.dispose();

    }

    private void styleButton() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = fontGen.getFont();
        style.up = skin.getDrawable("default-button");
        style.over = skin.getDrawable("default-hover-button");
        style.checked = skin.getDrawable("default-button");

        skin.add("button-style", style);

    }

    private void styleButton(String initName, String hoverName, String finalStyleName) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = fontGen.getFont();
        style.up = skin.getDrawable(initName);
        style.over = skin.getDrawable(hoverName);
        style.checked = skin.getDrawable(initName);

        skin.add(finalStyleName, style);

    }

    public TextButton createDefaultButton(String text, float width, float height) {
        TextButton button = new TextButton(text, skin, "button-style");
        button.pack();
        button.setSize(button.getWidth() + horizontalPadding, button.getHeight() + horizontalPadding);
        button.setPosition((width - button.getWidth())/2, (height - button.getHeight())/2);

        return button;
    }


    /**
    *  the position is measured starting from the bottom left. If you want to measure from the mid point you have to - the width and height
    *
    * */
    public TextButton createNewButton(String text, String buttonStyle , float width, float height) {

        TextButton button = new TextButton(text, skin, buttonStyle);
        button.pack();
        button.setSize(button.getWidth() + horizontalPadding, button.getHeight() + horizontalPadding);
        button.setPosition((width - button.getWidth())/2, (height - button.getHeight())/2);

        return button;
    }

}
