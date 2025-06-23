package io.github.eco_warrior.controller.Manager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.eco_warrior.sprite.UI.GunElementUI;
import io.github.eco_warrior.sprite.gardening_equipments.RayGun;
import io.github.eco_warrior.sprite.gun_elements.BlazingTreeFireElementDrawer;
import io.github.eco_warrior.sprite.gun_elements.BreezingTreeWindElementDrawer;
import io.github.eco_warrior.sprite.gun_elements.IceTreeIceElementDrawer;
import io.github.eco_warrior.sprite.gun_elements.VoltaicTreeLightningElementDrawer;

public class GunManager {
    private BlazingTreeFireElementDrawer blazingTreeFireElementDrawer;
    private BreezingTreeWindElementDrawer breezingTreeWindElementDrawer;
    private IceTreeIceElementDrawer iceTreeIceElementDrawer;
    private VoltaicTreeLightningElementDrawer voltaicTreeLightningDrawer;
    private GunElementUI gunElementUI;
    private RayGun rayGun;

    public GunManager(TreeControllerManager treeControllerManager, RayGun rayGun, long hideTimeMs) {
        this.rayGun = rayGun;
        this.gunElementUI = new GunElementUI("atlas/gun_element/GunElement.atlas",
            "atlas/gun_element/Lighting.atlas",
            "atlas/gun_element/Fire.atlas",
            "atlas/gun_element/Wind.atlas",
            "atlas/gun_element/Ice.atlas");
        blazingTreeFireElementDrawer = new BlazingTreeFireElementDrawer(treeControllerManager, gunElementUI, hideTimeMs);
        breezingTreeWindElementDrawer = new BreezingTreeWindElementDrawer(treeControllerManager, gunElementUI, hideTimeMs);
        iceTreeIceElementDrawer = new IceTreeIceElementDrawer(treeControllerManager, gunElementUI, hideTimeMs);
        voltaicTreeLightningDrawer = new VoltaicTreeLightningElementDrawer(treeControllerManager, gunElementUI, hideTimeMs);
    }

    public void draw(SpriteBatch batch, float stateTime) {
        blazingTreeFireElementDrawer.draw(batch, stateTime);
        voltaicTreeLightningDrawer.draw(batch, stateTime);
        breezingTreeWindElementDrawer.draw(batch, stateTime);
        iceTreeIceElementDrawer.draw(batch, stateTime);
    }

    public void handleClick(float x, float y) {

        blazingTreeFireElementDrawer.handleClick(x, y);
        breezingTreeWindElementDrawer.handleClick(x, y);
        iceTreeIceElementDrawer.handleClick(x, y);
        voltaicTreeLightningDrawer.handleClick(x, y);

        updateGunMode();
    }

    private void updateGunMode() {
        if (blazingTreeFireElementDrawer.wasLastIconClicked()) {
            rayGun.setMode(RayGun.RayGunMode.BLAZING);
            rayGun.playModeSound();
        }
        if (breezingTreeWindElementDrawer.wasLastIconClicked()) {
            rayGun.setMode(RayGun.RayGunMode.BREEZING);
            rayGun.playModeSound();
        }
        if (iceTreeIceElementDrawer.wasLastIconClicked()) {
            rayGun.setMode(RayGun.RayGunMode.ICE);
            rayGun.playModeSound();
        }
        if (voltaicTreeLightningDrawer.wasLastIconClicked()) {
            rayGun.setMode(RayGun.RayGunMode.VOLTAIC);
            rayGun.playModeSound();
        }
    }

    public void decreaseHideTime(long decreaseMs) {
        blazingTreeFireElementDrawer.setHideMs(blazingTreeFireElementDrawer.getHideMs() - decreaseMs);
        breezingTreeWindElementDrawer.setHideMs(breezingTreeWindElementDrawer.getHideMs() - decreaseMs);
        iceTreeIceElementDrawer.setHideMs(iceTreeIceElementDrawer.getHideMs() - decreaseMs);
        voltaicTreeLightningDrawer.setHideMs(voltaicTreeLightningDrawer.getHideMs() - decreaseMs);
    }

    public void dispose() {
        blazingTreeFireElementDrawer.dispose();
        breezingTreeWindElementDrawer.dispose();
        iceTreeIceElementDrawer.dispose();
        voltaicTreeLightningDrawer.dispose();
        gunElementUI.dispose();
    }


}
