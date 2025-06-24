package io.github.eco_warrior.controller.Manager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.entity.Tool;
import io.github.eco_warrior.enums.GardeningEnums;
import io.github.eco_warrior.sprite.gardening_equipments.Shovel;
import io.github.eco_warrior.controller.FertilizerController;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;

import java.util.*;

public class ToolManager {
    private Map<GardeningEnums, Tool> tools = new HashMap<>();
    private ArrayList<BaseSaplingController> saplingControllers = new ArrayList<>();
    private Set<BaseSaplingController> availableSaplings = new HashSet<>();
    public static ArrayList<FertilizerController> fertilizerControllers = new ArrayList<>();
    private int saplingIndex = 0;
    private int fertilizerIndex = 0;
    private boolean isPlanting = false;

//    private boolean isFertilizerUsed = false;

    public void addTool(GardeningEnums type, Tool tool) {
        tools.put(type, tool);
    }

    public void addSaplingController(BaseSaplingController saplingController) {
        saplingControllers.add(saplingController);
        availableSaplings.add(saplingController);
    }

    // Add to ToolManager class
    public void unlockNextSapling() {
        // Find the next unavailable sapling in the list and make it available
        for (BaseSaplingController sapling : saplingControllers) {
            if (!availableSaplings.contains(sapling)) {
                availableSaplings.add(sapling);
                System.out.println("Unlocked new sapling: " + sapling.getClass().getSimpleName());
                return;
            }
        }
    }


    // Add method to set a sapling as unavailable
    public void setSaplingAvailable(BaseSaplingController sapling, boolean available) {
        if (available) {
            availableSaplings.add(sapling);
        } else {
            availableSaplings.remove(sapling);
        }
    }


    public void addFertilizerController(FertilizerController fertilizerController) {
        fertilizerControllers.add(fertilizerController);
    }

    public void update(float delta) {
        for (Tool tool : tools.values()) {
            tool.update(delta);
        }

        if(!isPlanting){
            for(BaseSaplingController saplingController : saplingControllers) {
                saplingController.update(delta);
            }
        }

        if (!fertilizerControllers.isEmpty()) {
            for(FertilizerController fertilizerController : fertilizerControllers) {
                fertilizerController.update(delta);
            }
        }

    }

    public void render(SpriteBatch batch) {
//            System.out.println("Tool Manager: will not print");
            // Only draw saplings if there are any left
        if (!saplingControllers.isEmpty() && !isPlanting) {
            // Draw current sapling at the tool position
            BaseSaplingController currentSapling = saplingControllers.get(saplingIndex);
            if (currentSapling != null) {
                currentSapling.draw(batch);
            }
        }

        // Draw only available saplings
        for (BaseSaplingController sapling : saplingControllers) {
            if (availableSaplings.contains(sapling)) {
                sapling.draw(batch);
            }
        }
            // draw fertilizer when it is not empty
        if (!fertilizerControllers.isEmpty()) {

            FertilizerController fertilizerController = fertilizerControllers.get(fertilizerIndex);
            if (fertilizerController != null) {
                fertilizerController.draw(batch);
            }
        }

        for (Tool tool : tools.values()) {
            tool.render(batch);
        }
    }

    // Only allow dragging if the sapling is the current one
    public boolean canDragSaplingAt(Vector2 position) {
        if (!saplingControllers.isEmpty() && !isPlanting) {
            BaseSaplingController currentSapling = saplingControllers.get(saplingIndex);
            return currentSapling.getCollisionRect().contains(position);
        }
        return false;
    }

    public void setIsPlanting(boolean isPlanting) {
        this.isPlanting = isPlanting;
    }

    public boolean canBuyFertilizer() {
        return !fertilizerControllers.isEmpty();
    }

//    public void setIsFertilizerUsed(boolean isFertilizerUsed) {this.isFertilizerUsed = isFertilizerUsed;}

    public boolean isWaterCansCollideRefillWater(GameSprite waterPool) {
        if(tools.get(GardeningEnums.WATERING_CAN).getCollisionRect().overlaps(waterPool.getCollisionRect())){
            WateringCan wateringCan = (WateringCan) tools.get(GardeningEnums.WATERING_CAN);
            wateringCan.updateWateringCan();
            return true;
        }
        return false;
    }

    public void emptyWaterCan() {
        WateringCan wateringCan = (WateringCan) tools.get(GardeningEnums.WATERING_CAN);
        wateringCan.emptyWateringCan();
    }

    public void handleSaplingPlanting(GameSprite sapling) {
        if(sapling instanceof BaseSaplingController){
            saplingControllers.remove(sapling);
            //to fix the issue flash spawning issue
            isPlanting = true;

            if(!saplingControllers.isEmpty()) {
                // Update current index if needed
                saplingIndex = Math.min(saplingIndex, saplingControllers.size() - 1);
            }
        }

    }

    public void handleFertilizerUsing(GameSprite fertilizer) {
        if(fertilizer instanceof FertilizerController){
            fertilizerControllers.remove(fertilizer);
            //to fix the issue flash spawning issue
//            isFertilizerUsed = true;

            if(!fertilizerControllers.isEmpty()) {
                // Update current index if needed
                fertilizerIndex = Math.min(fertilizerIndex, fertilizerControllers.size() - 1);
            }
        }

    }

    // This method is special for Shovel as it has hit sound custom made.
    public void shovelHitSound(){
        for(Tool tool : tools.values()) {
            if (tool instanceof Shovel) {
                ((Shovel) tool).playSound();
            }
        }
    }



    public GameSprite getToolAt(Vector2 position) {
        // Check if the position is within the bounds of any tool
        GameSprite sapling = getSaplingAt(position);
        if (sapling != null && sapling instanceof BaseSaplingController) {
            if (availableSaplings.contains(sapling)) {
                // If the sapling is available, return it
                return sapling;
            } else {
                // If the sapling is not available, return null
                return null;
            }
        }

        GameSprite fertilizer = getFertilizerAt(position);
        if (fertilizer != null) {
            return fertilizer;
        }

        for (GameSprite tool : tools.values()) {
            if (tool.getCollisionRect().contains(position)) {
                return tool;
            }
        }
        return null;
    }

    private GameSprite getSaplingAt(Vector2 position) {

        // Check if there are any saplings
        if(saplingControllers.isEmpty()) {
            return null;
        }

        if(saplingControllers.get(saplingIndex).getCollisionRect().contains(position)) {
            return saplingControllers.get(saplingIndex);
        }else{
        }

        return null;
    }

    private GameSprite getFertilizerAt(Vector2 position) {

        // Check if there are any saplings
        if(fertilizerControllers.isEmpty()) {
            return null;
        }

        if(fertilizerControllers.get(fertilizerIndex).getCollisionRect().contains(position)) {
            return fertilizerControllers.get(fertilizerIndex);
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
        for (FertilizerController fertilizerController : fertilizerControllers) {
            fertilizerController.dispose();
        }
    }


    public void drawDebug(ShapeRenderer shapeRenderer) {
        for( Tool tool : tools.values()) {
            tool.debug(shapeRenderer);
        }
        for( BaseSaplingController saplingController : saplingControllers) {
            saplingController.drawDebug(shapeRenderer);
        }
        for (FertilizerController fertilizerController : fertilizerControllers) {
            fertilizerController.drawDebug(shapeRenderer);
        }
    }

    public boolean hasSaplingsRemaining() {
        return saplingControllers.size() > 0;
    }

    public void cycleNextSapling() {
        if (!saplingControllers.isEmpty()) {
            saplingIndex = (saplingIndex + 1) % saplingControllers.size();
        }
    }

    public ArrayList<BaseSaplingController> getSaplingSize() {
        return saplingControllers;
    }
}
