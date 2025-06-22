package io.github.eco_warrior.controller;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.sprite.Recyables.*;

public class recyclablesController {
    private Array<GameSprite> recyclables;
    private Recyclables draggingItem;

    // Spawn parameters
    private float spawnTimer = 0f;
    private float spawnInterval = 1.0f;
    private float startX;
    private float startY;

    // Movement control
    private boolean isDragging = false;
    private boolean isReturning = false;

    // Recyclable types
    private final Class<? extends GameSprite>[] recyclableClasses = new Class[] {
        PlasticBottle.class,
        Newspaper.class,
        TinCans.class,
        GlassBottle.class,
    };

    public recyclablesController(float screenWidth, float screenHeight) {
        recyclables = new Array<>();
        startX = screenWidth + 50f;
        startY = screenHeight / 8;
    }

    public void update(float delta) {
        // Update spawn timer
        spawnTimer += delta;
        if(spawnTimer > spawnInterval) {
            spawnTimer = 0f;
            spawnRecyclable();
        }

        // Update recyclables
        for(int i = recyclables.size - 1; i >= 0; i--) {
            GameSprite item = recyclables.get(i);
            item.update(delta);

            if(item.isOffScreen()) {
                recyclables.removeIndex(i);
            }
        }

        // Handle returning items
        if(isReturning) {
            returnOriginalPosition(delta);
        }
    }

    private void spawnRecyclable() {
        int index = MathUtils.random(recyclableClasses.length - 1);

        Class<?> selectedClass = recyclableClasses[index];
        GameSprite newItem = null;

        try {
            newItem = (GameSprite) selectedClass.getConstructor(Vector2.class)
                .newInstance(new Vector2(startX, startY));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(newItem != null) {
            recyclables.add(newItem);
        }
    }

    public boolean checkItemTouched(Vector2 touchPos) {
        for(GameSprite item : recyclables) {
            if(item.getCollisionRect().contains(touchPos)) {
                draggingItem = (Recyclables) item;
                isDragging = true;
                return true;
            }
        }
        return false;
    }

    public void dragItem(Vector2 touchPos) {
        if(isDragging && draggingItem != null) {
            float dx = touchPos.x - draggingItem.getMidX();
            float dy = touchPos.y - draggingItem.getMidY();

            draggingItem.getSprite().setPosition(dx, dy);
            draggingItem.getCollisionRect().setPosition(
                draggingItem.getSprite().getX(),
                draggingItem.getSprite().getY()
            );
        }
    }

    public Recyclables releaseItem() {
        isDragging = false;
        if(draggingItem != null) {
            Recyclables item = draggingItem;
            isReturning = true;
            return item;
        }
        return null;
    }

    public void removeItem(Recyclables item) {
        recyclables.removeValue(item, true);
        if(draggingItem == item) {
            draggingItem = null;
            isReturning = false;
        }
    }

    private void returnOriginalPosition(float delta) {
        if(draggingItem != null) {
            float dy = startY;
            Vector2 current = new Vector2(draggingItem.getSprite().getX(), draggingItem.getSprite().getY());
            Vector2 target = new Vector2(current.x, dy);

            Vector2 lerped = current.lerp(target, 5f * delta);

            draggingItem.getSprite().setPosition(lerped.x, lerped.y);
            draggingItem.getCollisionRect().setPosition(lerped.x, lerped.y);

            if(current.dst(target) < 2f) {
                draggingItem.getSprite().setPosition(target.x, target.y);
                draggingItem.getCollisionRect().setPosition(target.x, target.y);
                isReturning = false;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for(GameSprite item : recyclables) {
            item.draw(batch);
        }
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        for(GameSprite item : recyclables) {
            item.drawDebug(shapeRenderer);
        }
    }

    public void dispose() {
        for(GameSprite item : recyclables) {
            item.dispose();
        }
        recyclables.clear();
    }

    public Recyclables getDraggingItem() {
        return draggingItem;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    public boolean isReturning() {
        return isReturning;
    }
}
