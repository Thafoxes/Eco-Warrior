package io.github.eco_warrior.sprite.Characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.eco_warrior.controller.MapController;
import io.github.eco_warrior.enums.PlayerDirection;

public abstract class GameCharacter extends Sprite {
    protected String name;

    // Common fields and methods from Goblin and Adventurer_Girl
    protected Vector2 position;
    protected Vector2 velocity;
    protected Vector2 oldPosition;
    protected PlayerDirection currentDirection;
    protected float stateTime;
    protected boolean isMoving;
    protected TiledMapTileLayer collisionLayer;
    protected MapObjects collisionObjects;
    protected Rectangle boundingBox; // For collision detection
    protected int tileWidth, tileHeight;
    protected Texture texture;
    protected TextureRegion textureRegion;
    protected MapController mapController;
    protected boolean isBlocking = true;
    protected boolean isNPC = false;


    // Abstract methods that must be implemented
    public abstract void draw(SpriteBatch batch);
    public abstract void move(Vector2 direction);
    public abstract TextureRegion getCurrentFrame();

    // Dimensions for collision box
    protected final float COLLISION_WIDTH = 16;
    protected final float COLLISION_HEIGHT = 16;


    public void setVelocity(float x, float y){

        velocity.set(x, y);

        // Update direction based on velocity
        if (x > 0 && Math.abs(x) > Math.abs(y)) {
            currentDirection = PlayerDirection.RIGHT;
        } else if (x < 0 && Math.abs(x) > Math.abs(y)) {
            currentDirection = PlayerDirection.LEFT;
        } else if (y > 0) {
            currentDirection = PlayerDirection.UP;
        } else if (y < 0) {
            currentDirection = PlayerDirection.DOWN;
        }

    }
    public abstract void update(float delta, TiledMap tiledMap);

    // Add collision check method
    protected boolean checkCollision(float newX, float newY) {
        // Update bounding box to potential new position
        Rectangle tempBounds = new Rectangle(
            newX - boundingBox.width/2,
            newY - boundingBox.height/2,
            boundingBox.width,
            boundingBox.height
        );

        // Check collision with map objects
        for (MapObject object : collisionObjects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (tempBounds.overlaps(rect)) {
                    return true; // Collision detected
                }
            }
        }
        return false; // No collision
    }

    // Update position with collision check
    protected void updatePosition(float deltaX, float deltaY) {
        float newX = position.x + deltaX;
        float newY = position.y + deltaY;

        // Only update position if no collision
        if (!checkCollision(newX, newY)) {
            position.x = newX;
            position.y = newY;
            // Update bounding box position
            boundingBox.setPosition(
                position.x - boundingBox.width/2,
                position.y - boundingBox.height/2
            );
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
        oldPosition.set(position.x, position.y);
    }

    public PlayerDirection getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(PlayerDirection currentDirection) {
        this.currentDirection = currentDirection;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void onInteract() {
        System.out.println("Character interaction!");
        // Default implementation - override in subclasses
    }

    protected void updateBoundingBox() {
        // To be implemented by subclasses if needed
    }

    public void dispose() {

    }

    public void setNPC(boolean isNPC) {
        this.isNPC = isNPC;
    }

    public boolean isNPC() {
        return isNPC;
    }

    public void setBlocking(boolean blocking) {
        isBlocking = blocking;
    }

    public boolean getBlocking() {
        return isBlocking;
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public String getName(){
        if(name == null){
            return "Unnamed Character";
        } else {
            return name;
        }
    }
    public void setMapController(MapController mapController) {
    }
}
