package io.github.eco_warrior.sprite.Characters;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.enums.PlayerDirection;

public class Goblin extends Sprite {


    public enum State{
        IDLE,
        WALKING,
        ATTACKING,
        DYING
    }

    private TextureAtlas goblinsAtlas;
    // Animations
    private Animation<TextureRegion> idleFrontAnimation;
    private Animation<TextureRegion> idleBackAnimation;
    private Animation<TextureRegion> idleLeftAnimation;
    private Animation<TextureRegion> idleRightAnimation;
    private Animation<TextureRegion> walkFrontAnimation;
    private Animation<TextureRegion> walkBackAnimation;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;

    // Character state variables
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 oldPosition;
    private PlayerDirection currentDirection;
    private float stateTime;
    private boolean isMoving;
    private static final float CHARACTER_SPEED = 120.0f;

    //Tile information
    private TiledMapTileLayer collisionLayer;
    private int tileWidth, tileHeight;
    private int goblinTileX, goblinTileY;

    // Speed of the goblin
    private float speed = 100f;

    public Goblin(Vector2 position, int tileWidth, int tileHeight, TiledMapTileLayer collisionLayer) {
        this.position = position;
        this.oldPosition = new Vector2(position);
        this.velocity = new Vector2(0, 0);
        this.currentDirection = PlayerDirection.DOWN;
        this.stateTime = 0.0f;
        this.isMoving = false;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.collisionLayer = collisionLayer;

        loadAnimations();
    }

    private void loadAnimations() {
        goblinsAtlas = new TextureAtlas("character/goblins/Globlins.atlas");

        // Create animations for each direction and state
        idleFrontAnimation = new Animation<>(0.15f, goblinsAtlas.findRegions("Idle_front"));
        idleBackAnimation = new Animation<>(0.15f, goblinsAtlas.findRegions("Idle_back"));
        idleLeftAnimation = new Animation<>(0.15f, goblinsAtlas.findRegions("Idle_left"));
        idleRightAnimation = new Animation<>(0.15f, goblinsAtlas.findRegions("Idle_right"));

        walkFrontAnimation = new Animation<>(0.1f, goblinsAtlas.findRegions("Walk_front"));
        walkBackAnimation = new Animation<>(0.1f, goblinsAtlas.findRegions("Walk_back"));
        walkLeftAnimation = new Animation<>(0.1f, goblinsAtlas.findRegions("Walk_left"));
        walkRightAnimation = new Animation<>(0.1f, goblinsAtlas.findRegions("Walk_right"));
    }

    public void update(float delta, TiledMap tiledMap) {
        stateTime += delta;

        oldPosition.set(position);

        // Update position based on velocity
        position.x  += velocity.x * delta;
        position.y  += velocity.y * delta;

        //check if the goblin is moving
        isMoving = velocity.len2() > 0.0001f;

        checkCollision();

        goblinTileX = (int) (position.x / tileWidth);
        goblinTileY = (int) (position.y / tileHeight);

        // Clamp position to map bounds if tiledMap is provided
        if(tiledMap != null) {
            MapProperties mapProps = tiledMap.getProperties();
            int mapWidth = mapProps.get("width", Integer.class) * tileWidth;
            int mapHeight = mapProps.get("height", Integer.class) * tileHeight;
            position.x = MathUtils.clamp(position.x, 0, mapWidth);
            position.y = MathUtils.clamp(position.y, 0, mapHeight);
        }
    }

    public void move(Vector2 position){
        setVelocity(position.x * CHARACTER_SPEED, position.y * CHARACTER_SPEED);
    }

    public float getSpeed(){
        return speed;
    }


    public void draw(SpriteBatch batch) {
        TextureRegion currentSprite = getCurrentSprite();
        batch.draw(currentSprite,
            position.x - currentSprite.getRegionWidth()/2f,
            position.y - currentSprite.getRegionHeight()/2f
        );
    }

    private void checkCollision() {
        if(collisionLayer == null) {
            return;
        }

        TextureRegion currentSprite = getCurrentSprite();
        float goblinWidth = currentSprite.getRegionWidth();
        float goblinHeight = currentSprite.getRegionHeight();

        if(velocity.x > 0) {
            //moving right
            int tileX = (int) ((position.x + goblinWidth/3) / tileWidth);
            int topTileY = (int) ((position.y + goblinHeight/3) / tileHeight);
            int bottomTileY = (int) ((position.y - goblinHeight/3) / tileHeight);

            if(isTileBlocked(tileX, topTileY) || isTileBlocked(tileX, bottomTileY)) {
                System.out.println("Collision detected at tile: " + tileX + ", " + topTileY);
                position.x = oldPosition.x;
            }

        }else if(velocity.x < 0) {
            //moving left
            int tileX = (int) ((position.x - goblinWidth/3) / tileWidth);
            int topTileY = (int) ((position.y + goblinHeight/3) / tileHeight);
            int bottomTileY = (int) ((position.y - goblinHeight/3) / tileHeight);

            if (isTileBlocked(tileX, topTileY) || isTileBlocked(tileX, bottomTileY)) {
                System.out.println("Collision detected at tile: " + tileX + ", " + topTileY);

                position.x = oldPosition.x;
            }

        }
        if (velocity.y > 0) { // Moving up
            // Check top-left and top-right corners
            int tileY = (int)((position.y + goblinHeight/3) / tileHeight);
            int leftTileX = (int)((position.x - goblinWidth/3) / tileWidth);
            int rightTileX = (int)((position.x + goblinWidth/3) / tileWidth);

            if (isTileBlocked(leftTileX, tileY) || isTileBlocked(rightTileX, tileY)) {
                position.y = oldPosition.y;
            }
        } else if (velocity.y < 0) { // Moving down
            // Check bottom-left and bottom-right corners
            int tileY = (int)((position.y - goblinHeight/3) / tileHeight);
            int leftTileX = (int)((position.x - goblinWidth/3) / tileWidth);
            int rightTileX = (int)((position.x + goblinWidth/3) / tileWidth);

            if (isTileBlocked(leftTileX, tileY) || isTileBlocked(rightTileX, tileY)) {
                position.y = oldPosition.y;
            }
        }
    }

    private TextureRegion getCurrentSprite() {
        TextureRegion region;

        if(isMoving) {
            // Select walking animation based on direction
            switch (currentDirection) {
                case UP:
                    region = walkBackAnimation.getKeyFrame(stateTime, true);
                    break;
                case DOWN:
                    region = walkFrontAnimation.getKeyFrame(stateTime, true);
                    break;
                case LEFT:
                    region = walkLeftAnimation.getKeyFrame(stateTime, true);
                    break;
                case RIGHT:
                    region = walkRightAnimation.getKeyFrame(stateTime, true);
                    break;
                default:
                    region = walkFrontAnimation.getKeyFrame(stateTime, true);
                    break;
            }
        }else{
            // Select idle animation based on direction
            switch (currentDirection) {
                case UP:
                    region = idleBackAnimation.getKeyFrame(stateTime, true);
                    break;
                case DOWN:
                    region = idleFrontAnimation.getKeyFrame(stateTime, true);
                    break;
                case LEFT:
                    region = idleLeftAnimation.getKeyFrame(stateTime, true);
                    break;
                case RIGHT:
                    region = idleRightAnimation.getKeyFrame(stateTime, true);
                    break;
                default:
                    region = idleFrontAnimation.getKeyFrame(stateTime, true);
                    break;
            }
        }

        return region;
    }

    public void setVelocity(float x, float y) {
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

    private boolean isTileBlocked(int tileX, int tileY) {
//        if(true) return false; //debug debug
        if(tileX < 0 || tileY < 0 ||
            tileX >= collisionLayer.getWidth() ||
            tileY >= collisionLayer.getHeight()) {
            return true; // Out of bounds, no collision
        }

        // Get the cell at the specified coordinates
        TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);

        // Check if the cell exists and has a tile
        if (cell != null && cell.getTile() != null) {
            // Check for a "blocked" property in the tile
            Object blocked = cell.getTile().getObjects();
            if (blocked != null) {
//                return Boolean.parseBoolean(blocked.toString());
                return true;
            }
            // this one is causing issue
            return false; // By default, assume solid tiles block movement
        }

        // If there's no cell, allow movement
        if (cell == null) {
            return false;
        }

        return false; // No tile means no collision
    }

    public void dispose() {
        if (goblinsAtlas != null) {
            goblinsAtlas.dispose();
        }
    }

    public float getHeight() {
        TextureRegion currentSprite = getCurrentSprite();
        return currentSprite.getRegionHeight();
    }


    // Getters and setters
    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        oldPosition.set(position);
    }

    public PlayerDirection getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(PlayerDirection direction) {
        this.currentDirection = direction;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public TextureRegion getCurrentFrame() {
        return getCurrentSprite();
    }
}
