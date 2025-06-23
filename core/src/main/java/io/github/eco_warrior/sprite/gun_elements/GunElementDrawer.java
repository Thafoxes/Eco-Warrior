package io.github.eco_warrior.sprite.gun_elements;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.eco_warrior.controller.Manager.TreeControllerManager;
import io.github.eco_warrior.controller.Trees.TreeController;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.sprite.UI.GunElementUI;
import io.github.eco_warrior.sprite.gardening_equipments.RayGun;

public abstract class GunElementDrawer {
    protected TreeControllerManager treeControllerManager;
    protected GunElementUI gunElementUI;
    protected long hideMs;
    protected boolean lastIconClicked;
    protected ElementIconState iconState;


    public GunElementDrawer(TreeControllerManager treeControllerManager, GunElementUI gunElementUI, long hideMs) {
        this.treeControllerManager = treeControllerManager;
        this.gunElementUI = gunElementUI;
        this.hideMs = hideMs;
        this.lastIconClicked = false;
        this.iconState = new ElementIconState();
    }

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

    public void draw(SpriteBatch batch, float delta){
        for (TreeController<?> controller : treeControllerManager.getTreeControllers()) {
            Trees tree = controller.getTree();
            if(isCorrectType(tree)){
                boolean isMature = controller.isMaturedAliveTree();
                boolean isDead = controller.isDead();

                long now = System.currentTimeMillis();
                if(!isMature || isDead) {
                    iconState.visible = false;
                    return;
                }

                if(!iconState.visible && now > iconState.hiddenUntil) {
                    iconState.visible = true;
                }


                float iconX = tree.getSprite().getX() + tree.getSprite().getWidth() / 2f;
                float iconY = tree.getSprite().getY() + tree.getSprite().getHeight() - 20;
                iconState.updatePosition(iconX, iconY, 40, 40);
                if (iconState.visible) {
                    renderElementIcon(batch, iconX, iconY, 40, 40, delta);
                }
            }
        }
    }

    public abstract boolean isCorrectType(Trees tree);

    public abstract void renderElementIcon(SpriteBatch batch, float x, float y, float width, float height, float delta);

    public boolean wasLastIconClicked() {
        return lastIconClicked;
    }

    public void setLastIconClicked(boolean lastIconClicked) {
        this.lastIconClicked = lastIconClicked;
    }

    public long getHideMs() {
        return hideMs;
    }

    public void setHideMs(long hideMs) {
        this.hideMs = hideMs;
    }

    public void dispose(){
        gunElementUI.dispose();
        treeControllerManager.dispose();
    }

    public void handleClick(float screenX, float screenY) {
        long now = System.currentTimeMillis();
        if (iconState.isPointInside(screenX, screenY)) {
            iconState.visible = false;
            iconState.hiddenUntil = now + hideMs;
            lastIconClicked = true;
        } else {
            lastIconClicked = false;
        }
    }

    public abstract RayGun.RayGunMode getElementMode();
}
