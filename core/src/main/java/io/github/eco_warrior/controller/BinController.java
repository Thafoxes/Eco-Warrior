package io.github.eco_warrior.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.sprite.Bins.BinBase;
import io.github.eco_warrior.sprite.Characters.Racoon;

public class BinController {

    // Layers for bins and raccoons
    private Array<BinBase> backgroundBins;
    private Array<BinBase> foregroundBins;
    private Array<Racoon> raccoons;

    // Spawn control
    private float spawnTimer;
    private float spawnInterval;
    private float minSpawnInterval = 2f;
    private float maxSpawnInterval = 5f;

    public BinController() {
        backgroundBins = new Array<>();
        foregroundBins = new Array<>();
        raccoons = new Array<>();

        // Initialize with random spawn interval
        resetSpawnTimer();
    }

    public Array<BinBase> getAllBins(){
        Array<BinBase> bins = new Array<>();
        bins.addAll(backgroundBins);
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
                raccoons.removeIndex(i);
            }
        }
    }

    private void spawnRacoon() {
        if(backgroundBins.size == 0){
            return; // No bins to spawn from
        }

        int binIndex = MathUtils.random(backgroundBins.size - 1);
        gameSprite bin = backgroundBins.get(binIndex);

        // Calculate spawn position (center of the bin)
        Vector2 spawnPos = new Vector2(
            bin.getCollisionRect().x + bin.getCollisionRect().width / 2 - bin.getMidX(),
            bin.getCollisionRect().y + bin.getCollisionRect().height - 20 // Adjust height as needed
        );

        // Create raccoon
        Racoon raccoon = new Racoon(spawnPos, 1f); // Scale down the raccoon a bit
        raccoon.resetFrame(); // Start from first frame

        raccoons.add(raccoon);
    }

    public boolean checkRacoonHit(Vector2 touchPosition) {
        for (Racoon raccoon : raccoons) {
            if (!raccoon.isHit()
                && !raccoon.isDying()
                && raccoon.getCollisionRect().contains(touchPosition)) {
                raccoon.setHit();
                return true; // Hit detected
            }
        }
        return false; // No hit detected
    }


    public void draw(SpriteBatch batch) {
        // Draw in layers: background bins, raccoons, foreground bins
        for (gameSprite bin : backgroundBins) {
            bin.draw(batch);
        }

        for (Racoon raccoon : raccoons) {
            raccoon.draw(batch);
        }

        for (gameSprite bin : foregroundBins) {
            bin.draw(batch);
        }
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
        for (gameSprite bin : backgroundBins) {
            bin.dispose();
        }

        for (gameSprite bin : foregroundBins) {
            bin.dispose();
        }

        for (Racoon raccoon : raccoons) {
            raccoon.dispose();
        }

        backgroundBins.clear();
        foregroundBins.clear();
        raccoons.clear();
    }

}
