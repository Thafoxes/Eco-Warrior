package io.github.eco_warrior.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.sun.tools.javac.code.Attribute;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.sprite.Bins.BinBase;
import io.github.eco_warrior.sprite.Characters.Racoon;

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
    private float minSpawnInterval = 2f;
    private float maxSpawnInterval = 5f;

    //temp raccoon
    private Racoon tempRacoon;
    //fontGenerator
    private Map<BinBase, fontGenerator> binLabels;

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
        binLabels.put(bin, new fontGenerator());
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

        Array<Integer> availableIndexes = new Array<>();


        int binIndex = MathUtils.random(backgroundBins.size - 1);
        if(availableIndexes.contains(binIndex, true)){
            System.out.println("Raccoon already spawned at bin: " + binIndex);
            return; // Already spawned a raccoon at this bin
        }
        gameSprite bin = backgroundBins.get(binIndex);
        System.out.println("Spawning raccoon at bin: " + binIndex);
        System.out.println("Bin x: " + bin.getCollisionRect().getX() + ", width: " + bin.getCollisionRect().getWidth());
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
            availableIndexes.add(binIndex);

        }catch (Exception e) {
            e.printStackTrace();
            return; // Skip spawning if there's an error
        }

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

    public void drawLabels(SpriteBatch batch, OrthographicCamera camera) {

        for(BinBase bin: foregroundBins){
            String binType = bin.getBinType();

            // Calculate position for label (centered at bottom of bin)
            float labelX = bin.getSprite().getX() + (bin.getSprite().getWidth() / 2f) ; // Centered horizontally
            float labelY = bin.getSprite().getY() - 10f; // 20 pixels below the bin

            // Use the bin as the key, not binType
            fontGenerator font = binLabels.get(bin);
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

        // For raccoons
        for (Racoon raccoon : raccoons) {
            Gdx.gl.glLineWidth(3.0f);
            float x = raccoon.getSprite().getX();
            float width = raccoon.getSprite().getWidth();
//            System.out.println("Raccoon position: " + x + ", width: " + width);

            // Draw marker at raccoon position
            shapeRenderer.setColor(Color.MAGENTA);
            shapeRenderer.line(x, 0, x, 30);
            shapeRenderer.line(x + width, 0, x + width, 30);
            Gdx.gl.glLineWidth(1.0f);
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

        for(fontGenerator label : binLabels.values()) {
            label.dispose();
        }
        backgroundBins.clear();
        foregroundBins.clear();
        raccoons.clear();
        binLabels.clear();
    }

}
