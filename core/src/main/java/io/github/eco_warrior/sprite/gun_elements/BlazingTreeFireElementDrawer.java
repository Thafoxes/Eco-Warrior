package io.github.eco_warrior.sprite.gun_elements;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.eco_warrior.controller.Manager.TreeControllerManager;
import io.github.eco_warrior.controller.Trees.TreeController;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.sprite.UI.GunElementUI;
import io.github.eco_warrior.sprite.gardening_equipments.RayGun;
import io.github.eco_warrior.sprite.tree_variant.BlazingTree;

public class BlazingTreeFireElementDrawer {

    public static class ElementIconState {
        public float x, y, width, height;
        public boolean visible = true;
        public long hiddenUntil = 0L;

        public void updatePosition(float x, float y, float w, float h) {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
        }

        public boolean isPointInside(float px, float py) {
            return visible && px >= x && px <= x + width && py >= y && py <= y + height;
        }
    }

    private final TreeControllerManager treeControllerManager;
    private final GunElementUI gunElementUI;
    private final ElementIconState fireIconState = new ElementIconState();
    private long hideMs;
    private boolean lastClicked = false;

    public BlazingTreeFireElementDrawer(TreeControllerManager treeControllerManager, GunElementUI gunElementUI, long hideMs) {
        this.treeControllerManager = treeControllerManager;
        this.gunElementUI = gunElementUI;
        this.hideMs = hideMs;
    }

    // You can adjust the hideMs value to control how long the icon stays hidden after buying the cooldown potion
    // Example: Decrease by 2 seconds (2000 ms)
    //long newHideMs = blazingTreeFireElementDrawer.getHideMs() - 2000;
    //blazingTreeFireElementDrawer.setHideMs(newHideMs);

    public void setHideMs(long hideMs) {
        this.hideMs = Math.max(1000, hideMs); // never below 1s, adjust as you like
    }

    public long getHideMs() {
        return hideMs;
    }

    public void draw(SpriteBatch batch, float delta/*, RayGun.RayGunMode currentMode*/) {
        for (TreeController<?> controller : treeControllerManager.getTreeControllers()) {
            Trees tree = controller.getTree();
            if (tree instanceof BlazingTree) {
                boolean isMature = tree.getStage() == BlazingTree.TreeStage.MATURED_TREE;
                boolean isDead = tree.getStage() == BlazingTree.TreeStage.DEAD_MATURE_TREE
                    || tree.getStage() == BlazingTree.TreeStage.DEAD_YOUNG_TREE
                    || tree.getStage() == BlazingTree.TreeStage.DEAD_SAPLING;

                long now = System.currentTimeMillis();
                if (!isMature || isDead /*|| currentMode == RayGun.RayGunMode.BLAZING*/) {
                    fireIconState.visible = false;
                    return;
                }
                if (!fireIconState.visible && now > fireIconState.hiddenUntil) {
                    fireIconState.visible = true;
                }
                float iconX = tree.getSprite().getX() + tree.getSprite().getWidth() / 2f + 13;
                float iconY = tree.getSprite().getY() + tree.getSprite().getHeight() - 20;
                fireIconState.updatePosition(iconX, iconY, 40, 40);
                if (fireIconState.visible) {
                    gunElementUI.renderElementIcon(batch, GunElementUI.ElementType.FIRE, iconX, iconY, 40, 40, delta);
                }
            }
        }
    }

    public void handleClick(float screenX, float screenY) {
        long now = System.currentTimeMillis();
        if (fireIconState.isPointInside(screenX, screenY)) {
            fireIconState.visible = false;
            fireIconState.hiddenUntil = now + hideMs;
            lastClicked = true;
        } else {
            lastClicked = false;
        }
    }

    public boolean wasLastIconClicked() {
        return lastClicked;
    }

}
