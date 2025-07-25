package io.github.eco_warrior.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.sprite.Bins.BinBase;
import io.github.eco_warrior.sprite.Characters.Racoon;
import io.github.eco_warrior.sprite.tools.FlipFlop;

import java.util.HashMap;
import java.util.Map;

public class BinController {

    // Layers for bins and raccoons
    private Array<BinBase> backgroundBins;
    private Array<BinBase> foregroundBins;
    private Array<Racoon> raccoons;

    // Spawn control
    private float spawnTimer;
    private float spawnInterval;
    private float minSpawnInterval = 4f;
    private float maxSpawnInterval = 6f;

    //temp raccoon
    private Racoon tempRacoon;
    //fontGenerator
    private Map<BinBase, FontGenerator> binLabels;


    //track raccoon spawn locations
    private Map<Integer, Racoon> binToRaccoon = new HashMap<>();

    public BinController() {
        backgroundBins = new Array<>();
        foregroundBins = new Array<>();
        raccoons = new Array<>();
        binLabels = new HashMap<>();
        tempRacoon = new Racoon(new Vector2(0,0));

        // Initialize with random spawn interval
        resetSpawnTimer();
    }

    public Array<BinBase> getAllBins(){
        Array<BinBase> bins = new Array<>();
        bins.addAll(backgroundBins);
        bins.addAll(foregroundBins);
        return bins;
    }

    public Array<BinBase> getForegroundBins(){
        Array<BinBase> bins = new Array<>();
        bins.addAll(foregroundBins);
        return bins;
    }

    private void resetSpawnTimer() {
        spawnInterval = MathUtils.random(minSpawnInterval, maxSpawnInterval);
        spawnTimer = spawnInterval;
    }

    public void addBin(BinBase bin) {
        backgroundBins.add(bin);
        foregroundBins.add(bin);

        // Create font generator for the bin label
        binLabels.put(bin, new FontGenerator());
    }

    public void update(float delta){
        for(BinBase bin: backgroundBins){
            bin.update(delta);
        }

        updateRacoon(delta);

        for(BinBase bin: foregroundBins){
            bin.update(delta);
        }

    }

    private void updateRacoon(float delta) {
        spawnTimer -= delta;

        if(spawnTimer <= 0f && backgroundBins.size > 0){
            spawnRacoon();
            resetSpawnTimer();
        }

        for(int i = raccoons.size - 1; i >= 0; i--){
            Racoon raccoon = raccoons.get(i);
            raccoon.update(delta);

            if(raccoon.shouldRemove()){
                //remove the key here
                for(Map.Entry<Integer, Racoon> entry : binToRaccoon.entrySet()) {
                    if(entry.getValue() == raccoon) {
                        binToRaccoon.remove(entry.getKey());
                        break;
                    }
                }
                raccoons.removeIndex(i);
            }
        }
    }

    private void spawnRacoon() {
        if(backgroundBins.size == 0){
            return; // No bins to spawn from
        }

        // First, create a list of bins that don't already have raccoons on them
        Array<Integer> availableBinIndices = new Array<>();
        for(int i = 0; i < backgroundBins.size; i++) {
            if(!binToRaccoon.containsKey(i) || binToRaccoon.get(i).shouldRemove()) {
                availableBinIndices.add(i);
            }
        }

        // If no bins are available, just return without spawning
        if(availableBinIndices.size == 0) {
            return;
        }

        // Choose a random bin from available bins
        int randomIndex = MathUtils.random(availableBinIndices.size - 1);
        int binIndex = availableBinIndices.get(randomIndex);
        GameSprite bin = backgroundBins.get(binIndex);

        // Calculate spawn position (center of the bin)
        Vector2 spawnPos = new Vector2(
            bin.getCollisionRect().getX() + 40f, // Adjust x position as needed
            bin.getCollisionRect().y + bin.getCollisionRect().height - 25 // Adjust height as needed
        );

        // Create raccoon
        try {
            Racoon raccoon = new Racoon(spawnPos, 0.20f); // Scale down the raccoon a bit
            raccoon.resetFrame(); // Start from first frame
            raccoons.add(raccoon);
            binToRaccoon.put(binIndex, raccoon);

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean checkRacoonHit(FlipFlop flipFlop) {
        for (Racoon raccoon : raccoons) {
            if (!raccoon.isHit()
                && !raccoon.isDying()
                && raccoon.getCollisionRect().overlaps(flipFlop.getCollisionRect())) {
                raccoon.setHit();
                return true; // Hit detected
            }
        }
        return false; // No hit detected
    }

    public void drawLabels(SpriteBatch batch, OrthographicCamera camera) {

        for(BinBase bin: foregroundBins){
            String binType = bin.getBinType();

            // Calculate position for label (centered at bottom of bin)
            float labelX = bin.getSprite().getX() + (bin.getSprite().getWidth() / 2f) ; // Centered horizontally
            float labelY = bin.getSprite().getY() - 10f; // 20 pixels below the bin

            // Use the bin as the key, not binType
            FontGenerator font = binLabels.get(bin);
            if (font != null) {
                font.objFontDraw(
                    batch,
                    binType.toUpperCase() + " BIN",
                    24,
                    camera,
                    new Vector2(labelX, labelY)
                );
            }

        }
    }
    public void draw(SpriteBatch batch) {
        // Draw in layers: background bins, raccoons, foreground bins
        for (GameSprite bin : backgroundBins) {
            bin.draw(batch);
        }

        for (Racoon raccoon : raccoons) {
            raccoon.draw(batch);
        }

        for (GameSprite bin : foregroundBins) {
            bin.draw(batch);
        }

        // Draw explosion effects last so they appear on top of everything
        for(Racoon raccoon : raccoons){
            if (raccoon.getExplosionEffect() != null && !raccoon.getExplosionEffect().isFinished()) {
                raccoon.getExplosionEffect().draw(batch);
            }
        }

    }

    /**
     * Checks if any raccoon is currently active (idle or spawning).
     * @return true if any raccoon is active, false otherwise.
     */
    public boolean hasBinRaccoon(BinBase bin) {
        int binIndex = -1;

        // Find the bin index
        for (int i = 0; i < backgroundBins.size; i++) {
            if (backgroundBins.get(i) == bin) {
                binIndex = i;
                break;
            }
        }

        // Check if this bin has a raccoon
        if (binIndex != -1) {
            Racoon raccoon = binToRaccoon.get(binIndex);
            return raccoon != null && !raccoon.shouldRemove() && raccoon.isIdleOrSpawning();
        }

        return false;
    }

    /***
     * Check if any raccoon is currently active (idle or spawning)
     * @return true if any raccoon is active, false otherwise
     */
    public boolean isAnyRaccoonActive() {
        for (Racoon raccoon : raccoons) {
            if (raccoon.isIdleOrSpawning()) {
                return true;
            }
        }
        return false;
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        for (BinBase bin : backgroundBins) {
            bin.drawDebug(shapeRenderer);
        }

        for (Racoon raccoon : raccoons) {
            raccoon.drawDebug(shapeRenderer, Color.RED);
        }

        for (BinBase bin : foregroundBins) {
            bin.drawDebug(shapeRenderer);
        }
    }

    public void dispose() {
        for (GameSprite bin : backgroundBins) {
            bin.dispose();
        }

        for (GameSprite bin : foregroundBins) {
            bin.dispose();
        }

        for (Racoon raccoon : raccoons) {
            raccoon.dispose();
        }

        for(FontGenerator label : binLabels.values()) {
            label.dispose();
        }
        backgroundBins.clear();
        foregroundBins.clear();
        raccoons.clear();
        binLabels.clear();
    }

}
