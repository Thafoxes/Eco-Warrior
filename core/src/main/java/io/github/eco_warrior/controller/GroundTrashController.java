package io.github.eco_warrior.controller;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import io.github.eco_warrior.sprite.GroundTrash.*;

public class GroundTrashController {
    private final Array<GroundTrash> groundTrash;
    private final Pool<GroundTrash> trashPool = new Pool<GroundTrash>() {
        @Override
        protected GroundTrash newObject() {
            // Default to PlasticBottle, but will be replaced in spawnGroundTrash
            return new GroundGlass(new Vector2(0, 0));
        }
    };

    // Spawn trash parameters
    private float spawnTimer = 0f;
    private float spawnInterval = 5f;

    // Maximum number of trash items on screen
    private static final int MAX_TRASH = 10;

    // Define a spawn rectangle
    private final float minX;
    private final float maxX;
    private final float minY;
    private final float maxY;

    // Ground trash types
    private final Class<? extends GroundTrash>[] trashClasses = new Class[] {
        GroundGlass.class,
        GroundPlastic.class,
        GroundMetal.class,
        GroundPaper.class,
    };

    public GroundTrashController(float minX, float maxX, float minY, float maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;

        groundTrash = new Array<>();
    }

    public void update(float delta) {
        // Update spawn timer
        spawnTimer += delta;
        if(spawnTimer > spawnInterval) {
            spawnTimer = 0f;

            long startTime = System.nanoTime();

            spawnGroundTrash();

            long endTime = System.nanoTime();
            float elapsedMs = (endTime - startTime) / 1_000_000f;
            System.out.println("spawnGroundTrash() execution time: " + elapsedMs + " ms");
        }

        // Update trash items
        for(int i = groundTrash.size - 1; i >= 0; i--) {
            GroundTrash item = groundTrash.get(i);
            item.update(delta);

        }
    }

    private void spawnGroundTrash() {
        if (groundTrash.size >= MAX_TRASH) return;

        float positionX = MathUtils.random(minX, maxX);
        float positionY = MathUtils.random(minY, maxY);

        int index = MathUtils.random(trashClasses.length - 1);

        Class<?> selectedClass = trashClasses[index];
        GroundTrash newItem = null;

        try {
            newItem = (GroundTrash) selectedClass.getConstructor(Vector2.class)
                .newInstance(new Vector2(positionX, positionY));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(newItem != null) {
            groundTrash.add(newItem);
        }
    }

    public boolean removeItem(Vector2 touchPos) {
        for(GroundTrash item : groundTrash) {

            if(item.getCollisionRect().contains(touchPos)) {
                groundTrash.removeValue(item, true);
                trashPool.free(item);
                return true;
            }
        }
        return false;
    }

    public void draw(SpriteBatch batch) {
        for(GroundTrash item : groundTrash) {
            item.draw(batch);
        }
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        for(GroundTrash item : groundTrash) {
            item.drawDebug(shapeRenderer);

        }

        spawnRectangleDebug(shapeRenderer);
    }

    public void spawnRectangleDebug (ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(0, 0, 1, 1); // Red color
        shapeRenderer.rect(minX, minY, maxX - minX, maxY - minY);

    }

    public void dispose() {
        for(GroundTrash item : groundTrash) {
            item.dispose();
        }
        groundTrash.clear();
    }
}
