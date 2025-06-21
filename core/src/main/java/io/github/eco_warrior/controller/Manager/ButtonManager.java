package io.github.eco_warrior.controller.Manager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.enums.ButtonEnums;
import io.github.eco_warrior.sprite.buttons.FertilizerButton;
import io.github.eco_warrior.sprite.buttons.PurchaseButton;
import io.github.eco_warrior.sprite.UI.Currency;
import io.github.eco_warrior.sprite.buttons.UpgradePotionButton;

import java.util.HashMap;
import java.util.Map;

public class ButtonManager {

    public enum ButtonType {
        FERTILIZER_BUTTON,
        UPGRADE_POTION_BUTTON
    }

    public ButtonType buttonType;

    private Map<ButtonEnums, PurchaseButton> buttons = new HashMap<>();

    public void addButton(ButtonEnums type, PurchaseButton button) {
        buttons.put(type, button);
    }

    public void update(float delta) {
        for (PurchaseButton button : buttons.values()) {
            button.update(delta);
        }

    }

    public void draw(SpriteBatch batch) {

        for (PurchaseButton button : buttons.values()) {
            button.render(batch);
        }
    }

    // Purchase successful if button is clicked and money is sufficient
    // Identify the button clicked
    //Cannot purchase fertilizer if fertilizer is available
    public boolean purchase(Vector2 touchPos, Currency currency) {
        for (PurchaseButton button : buttons.values()) {

            if(button.getCollisionRect().contains(touchPos)
                && currency.getMoneyAmount() >= button.price) {

                if(ToolManager.fertilizerControllers.isEmpty() &&
                    button instanceof FertilizerButton) {
                    button.click();
                    currency.spendMoney(button.price);

                    buttonType = ButtonType.FERTILIZER_BUTTON;

                    return true;
                }

                if (button instanceof UpgradePotionButton) {
                    button.click();
                    currency.spendMoney(button.price);

                    buttonType = ButtonType.UPGRADE_POTION_BUTTON;

                    return true;
                }

                return false;
            }

        }

        return false;
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
