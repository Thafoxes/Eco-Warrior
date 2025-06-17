package io.github.eco_warrior.controller.Manager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.entity.Tool;
import io.github.eco_warrior.enums.GardeningEnums;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToolManager {
    private Map<GardeningEnums, Tool> tools = new HashMap<>();
    private ArrayList<BaseSaplingController> saplingControllers = new ArrayList<>();
    private static int saplingIndex = 0;

    public void addTool(GardeningEnums type, Tool tool) {
        tools.put(type, tool);
    }

    public void addSaplingController(BaseSaplingController saplingController) {
        saplingControllers.add(saplingController);
    }

    public void update(float delta) {
        for (Tool tool : tools.values()) {
            tool.update(delta);
        }

        for(BaseSaplingController saplingController : saplingControllers) {
            saplingController.update(delta);
        }
    }

    public boolean isWaterCansCollide(GameSprite waterPool) {
        if(tools.get(GardeningEnums.WATERING_CAN).getCollisionRect().overlaps(waterPool.getCollisionRect())){
            System.out.println("Watering can collided with water pool");
            WateringCan wateringCan = (WateringCan) tools.get(GardeningEnums.WATERING_CAN);
            wateringCan.updateWateringCan();
            return true;
        }
        return false;
    }

    public void handleSaplingPlanting(GameSprite sapling) {
        for (int i = 0; i < saplingControllers.size(); i++) {
            BaseSaplingController saplingController = saplingControllers.get(i);


            if(saplingController.getSprite().equals(sapling.getSprite()))  {
                //If sapling is already planted, move to next sapling
                if(saplingIndex < saplingControllers.size() - 1) {
                    saplingIndex = i + 1;
                }
            }
        }

    }

    public void render(SpriteBatch batch) {
        saplingControllers.get(saplingIndex).render(batch);

        for (Tool tool : tools.values()) {
            tool.render(batch);
        }
    }

    public GameSprite getToolAt(Vector2 position) {
        GameSprite sapling = getSaplingAt(position);
        if (sapling != null) {
            return sapling;
        }

        for (GameSprite tool : tools.values()) {
            if (tool.getCollisionRect().contains(position)) {
                return tool;
            }
        }
        return null;
    }

    private GameSprite getSaplingAt(Vector2 position) {

        if(saplingControllers.get(saplingIndex).getCollisionRect().contains(position)) {

            return saplingControllers.get(saplingIndex);
        }else{
        }

        return null;
    }

    public void dispose() {
        for (GameSprite tool : tools.values()) {
            tool.dispose();
        }
        for (BaseSaplingController saplingController : saplingControllers) {
            saplingController.dispose();
        }
    }


    public void drawDebug(ShapeRenderer shapeRenderer) {
        for( Tool tool : tools.values()) {
            tool.debug(shapeRenderer);
        }
        for( BaseSaplingController saplingController : saplingControllers) {
            saplingController.drawDebug(shapeRenderer);
        }
    }

    public ArrayList<BaseSaplingController> getSaplingSize() {
        return saplingControllers;

    }
}
