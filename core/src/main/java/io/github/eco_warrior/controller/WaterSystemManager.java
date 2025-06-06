package io.github.eco_warrior.controller;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.sprite.CrackSprite;
import io.github.eco_warrior.sprite.PipeCrack;
import io.github.eco_warrior.sprite.UI.WaterWasteBarUI;
import io.github.eco_warrior.sprite.WaterDrop;

import java.util.*;

import static io.github.eco_warrior.constant.ConstantsVar.WATER_DROP_MAX_SPAWN_INTERVAL;
import static io.github.eco_warrior.constant.ConstantsVar.WATER_DROP_MIN_SPAWN_INTERVAL;


public class WaterSystemManager {
    private List<WaterDrop> activeWaterDrops;
    private float dropSpawnTimer = 0f;

    private float currentSpawnInterval;
    private WaterWasteBarUI waterMeter;
    private int dropsLost = 0;
    private static final float DROP_VOLUME = 0.5f; // Amount each drop adds to meter
    private float dropVolume;

    private Map<CrackSprite, Float> crackTimers = new HashMap<>();
    private Map<CrackSprite, Float> crackIntervals = new HashMap<>();


    public WaterSystemManager(WaterWasteBarUI waterMeter) {
        activeWaterDrops = new ArrayList<>();
        this.waterMeter = waterMeter;
        this.dropVolume = DROP_VOLUME;

    }

    public WaterSystemManager(WaterWasteBarUI waterMeter, float dropVolume) {
        activeWaterDrops = new ArrayList<>();
        this.waterMeter = waterMeter;
        this.dropVolume = dropVolume;
    }

    private void resetSpawnInterval() {
        currentSpawnInterval =  (float) Math.random() * (WATER_DROP_MAX_SPAWN_INTERVAL - WATER_DROP_MIN_SPAWN_INTERVAL);
    }

    public void update(float delta, List<CrackSprite> cracks) {

        for(CrackSprite crack: cracks){
            if(crack.isVisible()){
                if(!crackTimers.containsKey(crack)){
                    crackTimers.put(crack, 0f);
                    crackIntervals.put(crack, MathUtils.random(WATER_DROP_MIN_SPAWN_INTERVAL, WATER_DROP_MAX_SPAWN_INTERVAL));
                }

                float timer = crackTimers.get(crack);
                timer += delta;

                if(timer >= crackIntervals.get(crack)) {
                   spawnWaterDrop(crack.getWaterDropPosition());
                   timer = 0f;
                   crackIntervals.put(crack, MathUtils.random(WATER_DROP_MIN_SPAWN_INTERVAL, WATER_DROP_MAX_SPAWN_INTERVAL));
                }
                crackTimers.put(crack, timer);
            }
        }

        updateExistingDrops(delta);
    }

    private void updateExistingDrops(float delta) {
        // Update existing drops
        Iterator<WaterDrop> iterator = activeWaterDrops.iterator();
        while (iterator.hasNext()) {
            WaterDrop drop = iterator.next();
            drop.update(delta);

            // Remove drops that have fallen below screen
            if (drop.getY() < 0 || !drop.isActive()) {
                if(drop.getY() < 0) {
                    // If drop is below screen, increment drops lost
                    waterMeter.addWater(dropVolume);
                    dropsLost++;
                }
                iterator.remove();
            }
        }
    }

    // Clean up timers for removed cracks
    public void cleanupRemovedCracks(List<CrackSprite> currentCracks) {
        crackTimers.keySet().retainAll(currentCracks);
        crackIntervals.keySet().retainAll(currentCracks);
    }

    public void spawnWaterDrop(Vector2 waterDropPosition) {
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
