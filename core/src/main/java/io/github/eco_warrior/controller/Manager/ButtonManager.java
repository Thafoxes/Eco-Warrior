package io.github.eco_warrior.controller.Manager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.enums.ButtonEnums;
import io.github.eco_warrior.sprite.buttons.PurchaseButton;
import io.github.eco_warrior.sprite.UI.Currency;

import java.util.HashMap;
import java.util.Map;

public class ButtonManager {
    private Map<ButtonEnums, PurchaseButton> buttons = new HashMap<>();

    public void addButton(ButtonEnums type, PurchaseButton button) {
        buttons.put(type, button);
    }

    public void update(float delta) {
        for (PurchaseButton button : buttons.values()) {
            button.update(delta);
        }

    }

    public void render(SpriteBatch batch) {

        for (PurchaseButton button : buttons.values()) {
            button.render(batch);
        }
    }

    public void click(Vector2 touchPos, Currency currency) {
        for (PurchaseButton button : buttons.values()) {

            if(button.getCollisionRect().contains(touchPos)) {
                button.click();
                currency.spendMoney(button.price);
            }

        }


    }

    public void dispose() {
        for (PurchaseButton button : buttons.values()) {
            button.dispose();
        }
    }


    public void drawDebug(ShapeRenderer shapeRenderer) {
        for(PurchaseButton button : buttons.values()) {
            button.debug(shapeRenderer);
        }
    }
}
