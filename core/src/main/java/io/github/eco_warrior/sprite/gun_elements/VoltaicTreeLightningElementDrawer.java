package io.github.eco_warrior.sprite.gun_elements;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.eco_warrior.controller.Manager.TreeControllerManager;
import io.github.eco_warrior.controller.Trees.TreeController;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.sprite.UI.GunElementUI;
import io.github.eco_warrior.sprite.gardening_equipments.RayGun;
import io.github.eco_warrior.sprite.tree_variant.VoltaicTree;

public class VoltaicTreeLightningElementDrawer extends GunElementDrawer {


    public VoltaicTreeLightningElementDrawer(TreeControllerManager treeControllerManager, GunElementUI gunElementUI, long hideMs) {
       super(treeControllerManager, gunElementUI, hideMs);
    }

    @Override
    public RayGun.RayGunMode getElementMode() {
        return RayGun.RayGunMode.VOLTAIC;
    }

    @Override
    public boolean isCorrectType(Trees tree) {
        return tree instanceof VoltaicTree;
    }

    @Override
    public void renderElementIcon(SpriteBatch batch, float x, float y, float width, float height, float delta) {
        gunElementUI.renderElementIcon(batch, GunElementUI.ElementType.LIGHTING, x, y, 40, 40, delta);
    }




}
