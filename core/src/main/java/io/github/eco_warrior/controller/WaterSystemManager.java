package io.github.eco_warrior.controller;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.sprite.CrackSprite;
import io.github.eco_warrior.sprite.PipeCrack;
import io.github.eco_warrior.sprite.UI.WaterWasteBarUI;
import io.github.eco_warrior.sprite.WaterDrop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static io.github.eco_warrior.constant.ConstantsVar.WATER_DROP_MAX_SPAWN_INTERVAL;
import static io.github.eco_warrior.constant.ConstantsVar.WATER_DROP_MIN_SPAWN_INTERVAL;


public class WaterSystemManager {
    private List<WaterDrop> activeWaterDrops;
    private float dropSpawnTimer = 0f;

    private float currentSpawnInterval;
    private WaterWasteBarUI waterMeter;
    private int dropsLost = 0;
    private static final float DROP_VOLUME = 0.2f; // Amount each drop adds to meter

    public WaterSystemManager(WaterWasteBarUI waterMeter) {
        activeWaterDrops = new ArrayList<>();
        resetSpawnInterval();
        this.waterMeter = waterMeter;
    }

    private void resetSpawnInterval() {
        currentSpawnInterval = WATER_DROP_MIN_SPAWN_INTERVAL + (float) Math.random() * (WATER_DROP_MAX_SPAWN_INTERVAL - WATER_DROP_MIN_SPAWN_INTERVAL);
    }

    public void update(float delta, List<CrackSprite> cracks) {
        dropSpawnTimer += delta;


        // Spawn new drops
        if (dropSpawnTimer >= currentSpawnInterval) {
            for (CrackSprite crack : cracks) {
                if (crack.isVisible()) {
//                    System.out.println("WaterSystemManager: update called with delta = " + delta);
                    spawnWaterDrop(crack.getWaterDropPosition());
                }
            }
            dropSpawnTimer = 0;
            resetSpawnInterval();
        }

        // Update existing drops
        Iterator<WaterDrop> iterator = activeWaterDrops.iterator();
        while (iterator.hasNext()) {
            WaterDrop drop = iterator.next();
            drop.update(delta);

            // Remove drops that have fallen below screen
            if (drop.getY() < 0 || !drop.isActive()) {
                if(drop.getY() < 0) {
                    // If drop is below screen, increment drops lost
                    waterMeter.addWater(DROP_VOLUME);
                    dropsLost++;
                }
                iterator.remove();
            }
        }
    }

    private void spawnWaterDrop(Vector2 waterDropPosition) {
        WaterDrop waterDrop = new WaterDrop(waterDropPosition);
        activeWaterDrops.add(waterDrop);
    }

    public void draw(SpriteBatch batchSprite) {
        for (WaterDrop drop : activeWaterDrops) {
            drop.draw(batchSprite);
        }
    }

    public void dispose() {
        activeWaterDrops.clear();
    }

    public int getDropsLost() {
        return dropsLost;
    }

    public List<WaterDrop> getActiveWaterDrops() {
        return activeWaterDrops;
    }

    public boolean isWaterMeterFull() {
        return waterMeter.isFull();
    }



}
