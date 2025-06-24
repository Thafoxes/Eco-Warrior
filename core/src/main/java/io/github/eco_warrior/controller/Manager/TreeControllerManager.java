package io.github.eco_warrior.controller.Manager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.controller.Trees.TreeController;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.enums.TreeType;
import io.github.eco_warrior.sprite.DebugStick;
import io.github.eco_warrior.controller.FertilizerController;
import io.github.eco_warrior.sprite.gardening_equipments.Shovel;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;

import java.util.ArrayList;

public class TreeControllerManager {
    ArrayList<TreeController> treeControllers = new ArrayList<>();
    private TreeController currentTreeController;
    private boolean wateringSuccessful = false;

    public void addTreeController(TreeController treeController) {
        treeControllers.add(treeController);
    }

    public void update(float delta) {
        for (TreeController treeController : treeControllers) {
            treeController.update(delta);
        }
    }

    public void draw(SpriteBatch batch) {
        for (TreeController treeController : treeControllers) {
            treeController.draw(batch);

            if(!(treeController.getStage() == Trees.TreeStage.FLAG
                || treeController.getStage() == Trees.TreeStage.HOLE)) {
                treeController.getTreeHealth().draw(batch);
            }
        }
    }

    public void dispose() {
        for (TreeController treeController : treeControllers) {
            treeController.dispose();
        }
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        for (TreeController treeController : treeControllers) {
            treeController.drawDebug(shapeRenderer);
        }
    }

    /**
     * Interacts with trees based on the type of tool being dragged.
     * return true if planted a sapling.
     * @param draggingTool The tool being dragged, which can be a sapling, watering can, fertilizer, or shovel.
     */
    public boolean interactWithTrees(GameSprite draggingTool) {
        boolean planted = false;
        boolean isFertilizerUsed = false;

        for (TreeController<?> treeController : treeControllers) {
            if(treeController.getTree().getCollisionRect().overlaps(draggingTool.getCollisionRect())){


                if (draggingTool instanceof WateringCan) {
                    WateringCan wateringCan = (WateringCan) draggingTool;
                    if(treeController.isPlanting() && wateringCan.waterLevel == WateringCan.WateringCanState.FILLED){
                        treeController.handleWatering();
                        wateringSuccessful = true; // Set the flag to true if watering was successful
                    }
                }
                if(draggingTool instanceof Shovel){
                    treeController.digHole();
                }
                if(draggingTool instanceof DebugStick) {
                    treeController.takeDamage(1);
                }
                //if the tree is not hole Condition, return
                if(treeController.getStage() == Trees.TreeStage.HOLE) {
                    if(draggingTool instanceof BaseSaplingController){
                        // If the dragging tool is a sapling, handle planting
                        BaseSaplingController sapling = (BaseSaplingController) draggingTool;
                        planted = treeController.handleSaplingPlanting(sapling);
                        // If a sapling was successfully planted, remove it from the tool manager.
                        currentTreeController = treeController;
                        if(planted) {
                            return true; // Return true if a sapling was successfully planted
                        }
                    }
                }
                //Fertilizer can only be used if the right condition is met
                if ( (treeController.isPlanted() && treeController.getHealth() != 4)) {
                    //!(treeController.getStage() == Trees.TreeStage.HOLE
                    //                    || treeController.getStage() == Trees.TreeStage.FLAG
                    //                    || treeController.getHealth() == 4)
                    //reference
                    if (draggingTool instanceof FertilizerController) {
                        treeController.resetHealth();

                        FertilizerController fertilizerController = (FertilizerController) draggingTool;
                        isFertilizerUsed = treeController.handleFertilizerUsing(fertilizerController);

                        currentTreeController = treeController;
                        if(isFertilizerUsed) {
                            return true;
                        }
                    }
                }
            }
        }
        return planted;
    }

    /**
     * Temporary not in use
     * @return
     */
    public boolean isPlanting(){
        if(currentTreeController == null) {
            return false; // No current tree controller set
        }
        boolean growing = !currentTreeController.isMaturedTree();
        return growing;
    }


    public ArrayList<TreeController> getTreeControllers(){
        return treeControllers;
    }

    public TreeController<?> getTreeController(TreeType type) {
        for (TreeController<?> controller : getTreeControllers()) {
            if (controller.getTreeType() == type) {
                return controller;
            }
        }
        return null;
    }

    public boolean wasWateringSuccessful() {
        boolean result = wateringSuccessful;
        wateringSuccessful = false; // Reset after checking
        return result;
    }
}

