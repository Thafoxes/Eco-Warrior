package io.github.eco_warrior.sprite.gun_elements;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.eco_warrior.controller.Manager.TreeControllerManager;
import io.github.eco_warrior.controller.Trees.TreeController;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.sprite.UI.GunElementUI;
import io.github.eco_warrior.sprite.gardening_equipments.RayGun;
import io.github.eco_warrior.sprite.tree_variant.BlazingTree;

public class BlazingTreeFireElementDrawer extends GunElementDrawer {


    public BlazingTreeFireElementDrawer(TreeControllerManager treeControllerManager, GunElementUI gunElementUI, long hideMs) {
        super(treeControllerManager, gunElementUI, hideMs);
    }


    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public boolean isCorrectType(Trees tree) {
        return tree instanceof BlazingTree;
    }

    @Override
    public void renderElementIcon(SpriteBatch batch, float x, float y, float width, float height, float delta) {
        gunElementUI.renderElementIcon(batch, GunElementUI.ElementType.FIRE, x, y, 40, 40, delta);
    }

    @Override
    public RayGun.RayGunMode getElementMode() {
        return RayGun.RayGunMode.BLAZING;
    }
}
