package io.github.eco_warrior.controller.Manager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.controller.Trees.TreeController;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.sprite.DebugStick;
import io.github.eco_warrior.sprite.gardening_equipments.Fertilizer;
import io.github.eco_warrior.sprite.gardening_equipments.Shovel;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;

import java.util.ArrayList;

public class TreeControllerManager {
    ArrayList<TreeController> treeControllers = new ArrayList<>();
    private TreeController currentTreeController;

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

        for (TreeController<?> treeController : treeControllers) {
            if(treeController.getTree().getCollisionRect().overlaps(draggingTool.getCollisionRect())){


                if (draggingTool instanceof WateringCan) {
                    treeController.handleWatering();
                }
                if(draggingTool instanceof Fertilizer){
                    treeController.resetHealth();
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
                        //TODO - check this
                        currentTreeController = treeController;
                        if(planted) {
                            return true; // Return true if a sapling was successfully planted
                        }
                    }
                }
            }
        }
        return planted;
    }

    public boolean isCurrentTreeMatured(){
        if(currentTreeController == null) {
            return false; // No current tree controller set
        }
        boolean matured =  currentTreeController.isMatured();
        return matured;
    }


}

