package io.github.eco_warrior.sprite.Characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.World;
import io.github.eco_warrior.controller.MapController;
import io.github.eco_warrior.enums.PlayerDirection;

public class Goblin extends GameCharacter {

    public enum State{
        IDLE,
        WALKING,
        ATTACKING,
        DYING
    }

    private String name;

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

    // Object Layer (for arbitrary collision shapes)
    private MapObjects collisionObjects;

    // Speed of the goblin
    private float speed = 100f;

    private boolean isNPC = false;
    private Texture texture;
    private TextureRegion textureRegion;
    private MapController mapController;

    public Goblin(Vector2 position, int tileWidth, int tileHeight, TiledMapTileLayer collisionLayer, MapObjects objects) {
        super();
        this.position = position;
        this.oldPosition = new Vector2(position);
        this.velocity = new Vector2(0, 0);
        this.currentDirection = PlayerDirection.DOWN;
        this.stateTime = 0.0f;
        this.isMoving = false;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.collisionLayer = collisionLayer;
        this.collisionObjects =  objects;

        loadAnimations();
    }

    /**
     * For NPC uses idle
     * @param position
     * @param tileWidth
     * @param tileHeight
     */
    public Goblin(Vector2 position, int tileWidth, int tileHeight, boolean isNPC) {
        this(position, tileWidth, tileHeight);
        this.texture = new Texture("character/goblins/Globlins.png");
        if(isNPC){
            this.isNPC = true;
        }
        // Add this to ensure we have a bounding box for collision
        if (this.boundingBox == null) {
            this.boundingBox = new Rectangle(
                position.x - COLLISION_WIDTH / 2,
                position.y - COLLISION_HEIGHT / 2,
                COLLISION_WIDTH,
                COLLISION_HEIGHT
            );
        }
        // Set default texture region if not loaded from animations
        if (this.textureRegion == null) {
            this.textureRegion = new TextureRegion(texture, 0, 0, 32, 32);
        }
    }
    /**
     * For user uses only
     * @param position
     * @param tileWidth
     * @param tileHeight
     */
    public Goblin(Vector2 position, int tileWidth, int tileHeight) {
        super();
        this.position = position;
        this.oldPosition = new Vector2(position);
        this.velocity = new Vector2(0, 0);
        this.currentDirection = PlayerDirection.DOWN;
        this.stateTime = 0.0f;
        this.isMoving = false;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.name = "Goblin Warrior";

        // Create a bounding box centered on the goblin's position
        this.boundingBox = new Rectangle(
            position.x - COLLISION_WIDTH / 2,
            position.y - COLLISION_HEIGHT / 2,
            COLLISION_WIDTH,
            COLLISION_HEIGHT
        );

        // Set default direction to face down
        this.setCurrentDirection(PlayerDirection.DOWN);

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

        Vector2 nextPosition = new Vector2(
            position.x + velocity.x * delta,
            position.y + velocity.y * delta
        );

        Rectangle nextBoundingBox;
        if(isNPC){

            // Update the textureRegion with the current animation frame
            textureRegion = getCurrentSprite();


            float goblinWidth = textureRegion.getRegionWidth();
            float goblinHeight = textureRegion.getRegionHeight();
            nextBoundingBox = new Rectangle(
                nextPosition.x - goblinWidth / 2f,
                nextPosition.y - goblinHeight / 2f,
                goblinWidth,
                goblinHeight
            );

        }else{
            TextureRegion currentSprite = getCurrentSprite();
            float goblinWidth = currentSprite.getRegionWidth();
            float goblinHeight = currentSprite.getRegionHeight();
            nextBoundingBox= new Rectangle(
                nextPosition.x - goblinWidth / 2f,
                nextPosition.y - goblinHeight / 2f,
                goblinWidth,
                goblinHeight
            );
        }
        

        // Check collision with objects and tile layer
        boolean blocked = false;
        if (collisionObjects != null && isCollidingWithObjects(nextBoundingBox)) {
            blocked = true;
        }
        if (!blocked && collisionLayer != null && isCollidingWithTiles(nextBoundingBox)) {
            blocked = true;
        }

        if (!blocked) {
            position.set(nextPosition);
        } else {
            position.set(oldPosition);
            velocity.set(0, 0);
        }

        isMoving = velocity.len2() > 0.0001f;

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


    public void move(Vector2 direction){
        setVelocity(direction.x * CHARACTER_SPEED, direction.y * CHARACTER_SPEED);
    }


    public float getSpeed(){
        return speed;
    }

    /**
     * Set map controller for collision detection
     */
    public void setMapController(MapController mapController) {
        this.mapController = mapController;
    }


    @Override
    protected void updateBoundingBox() {
        boundingBox.setPosition(
            position.x - COLLISION_WIDTH / 2,
            position.y - COLLISION_HEIGHT / 2
        );
    }

    public void draw(SpriteBatch batch) {
        batch.draw(textureRegion,
            position.x - textureRegion.getRegionWidth()/2f,
            position.y - textureRegion.getRegionHeight()/2f
        );
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public TextureRegion getCurrentFrame() {
        return textureRegion;
    }


    /** Checks if the goblin's bounding box collides with any collidable object in the collisionObjects */
    private boolean isCollidingWithObjects(Rectangle boundingBox) {
        if (collisionObjects == null) return false;
        for (MapObject object : collisionObjects) {
            if (!object.isVisible()) continue;
            // Only check objects with "collidable" property set to true
            Object collidableProp = object.getProperties().get("collidable");
            boolean collidable = collidableProp != null &&
                (collidableProp.equals(true) || "true".equalsIgnoreCase(collidableProp.toString()));
            if (!collidable) continue;

            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (boundingBox.overlaps(rect)) return true;
            } else if (object instanceof EllipseMapObject) {
                Ellipse ellipse = ((EllipseMapObject) object).getEllipse();
                if (ellipseIntersectsRectangle(ellipse, boundingBox)) return true;
            } else if (object instanceof PolygonMapObject) {
                Polygon poly = ((PolygonMapObject) object).getPolygon();
                if (polygonIntersectsRectangle(poly, boundingBox)) return true;
            }
        }
        return false;
    }

    // Helper for ellipse-rectangle collision (approximate by bounding box)
    private boolean ellipseIntersectsRectangle(Ellipse ellipse, Rectangle rect) {
        Rectangle ellipseRect = new Rectangle(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
        return ellipseRect.overlaps(rect);
    }

    // Helper for polygon-rectangle collision (using bounding box for performance, for more accurate testing use Intersector)
    private boolean polygonIntersectsRectangle(Polygon poly, Rectangle rect) {
        Rectangle polyRect = poly.getBoundingRectangle();
        return polyRect.overlaps(rect);
    }

    /** Checks if the goblin's bounding box collides with any blocked tile */
    private boolean isCollidingWithTiles(Rectangle boundingBox) {
        int startX = (int) (boundingBox.x / tileWidth);
        int startY = (int) (boundingBox.y / tileHeight);
        int endX = (int) ((boundingBox.x + boundingBox.width) / tileWidth);
        int endY = (int) ((boundingBox.y + boundingBox.height) / tileHeight);

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (isTileBlocked(x, y)) return true;
            }
        }
        return false;
    }

    // Checks if a tile is blocked based on tile properties (for completeness, in case you still use tile collision)
    private boolean isTileBlocked(int tileX, int tileY) {
        if (tileX < 0 || tileY < 0 ||
            tileX >= collisionLayer.getWidth() ||
            tileY >= collisionLayer.getHeight()) {
            return true; // Out of bounds, block movement
        }

        Cell cell = collisionLayer.getCell(tileX, tileY);
        if (cell != null && cell.getTile() != null) {
            Object blocked = cell.getTile().getProperties().get("blocked");
            if (blocked != null) {
                return blocked.equals(true) || "true".equalsIgnoreCase(blocked.toString());
            }
        }
        return false;
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

    @Override
    public void dispose() {
        if (goblinsAtlas != null) {
            goblinsAtlas.dispose();
        }
        if (texture != null) {
            texture.dispose();
        }
    }

    public float getHeight() {
        TextureRegion currentSprite = getCurrentSprite();
        return currentSprite.getRegionHeight();
    }

    public String getName() {
        return name;
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

    public boolean isNPC() {
        return isNPC;
    }

    public void setNPCState(boolean controllable) {
        this.isNPC = controllable;
    }

    public float getCollisionWidth() {
        return COLLISION_WIDTH;
    }

    public float getCollisionHeight() {
        return COLLISION_HEIGHT;
    }
}

