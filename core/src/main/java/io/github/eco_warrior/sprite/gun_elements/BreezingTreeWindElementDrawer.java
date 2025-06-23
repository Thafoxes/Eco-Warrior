package io.github.eco_warrior.sprite.gun_elements;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.eco_warrior.controller.Manager.TreeControllerManager;
import io.github.eco_warrior.controller.Trees.TreeController;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.sprite.UI.GunElementUI;
import io.github.eco_warrior.sprite.gardening_equipments.RayGun;
import io.github.eco_warrior.sprite.tree_variant.BreezingTree;

public class BreezingTreeWindElementDrawer extends GunElementDrawer {

    public BreezingTreeWindElementDrawer(TreeControllerManager treeControllerManager, GunElementUI gunElementUI, long hideMs) {
        super(treeControllerManager, gunElementUI, hideMs);
    }

    @Override
    public RayGun.RayGunMode getElementMode() {
        return RayGun.RayGunMode.BREEZING;
    }


    @Override
    public boolean isCorrectType(Trees tree) {
        return tree instanceof BreezingTree;
    }

    @Override
    public void renderElementIcon(SpriteBatch batch, float iconX, float iconY, float width, float height, float delta) {
        gunElementUI.renderElementIcon(batch, GunElementUI.ElementType.WIND, iconX, iconY, 65, 65, delta);
    }



}
