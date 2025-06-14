package io.github.eco_warrior.sprite.Characters;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import io.github.eco_warrior.controller.MapController;
import io.github.eco_warrior.enums.PlayerDirection;

public class Adventurer_Girl extends GameCharacter {


    private TextureAtlas character;
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
    private int tileX, tileY;

    // Object Layer (for arbitrary collision shapes)
    private MapObjects collisionObjects;

    // Speed of the character
    private float speed = 100f;

    private static int PIXELS_PER_X_METER = 48;
    private static int PIXELS_PER_Y_METER = 64;

    private MapController mapController;


    private Body body;

    public Adventurer_Girl(Vector2 position, int tileWidth, int tileHeight, TiledMapTileLayer collisionLayer,
                           MapObjects objects, MapController mapController) {
        super();
        try {
            this.position = position;
            this.oldPosition = new Vector2(position);
            this.velocity = new Vector2(0, 0);
            this.currentDirection = PlayerDirection.DOWN;
            this.stateTime = 0.0f;
            this.isMoving = false;
            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;
            this.collisionLayer = collisionLayer;
            this.collisionObjects = objects;
            this.mapController = mapController;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error initializing");
        }
        loadAnimations();
    }

    public void createPhysicsBody(World world){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getPosition().x / PIXELS_PER_X_METER, getPosition().y / PIXELS_PER_Y_METER);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            getCurrentFrame().getRegionWidth() / 2f / PIXELS_PER_X_METER,
            getCurrentFrame().getRegionHeight() / 2f / PIXELS_PER_Y_METER
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }
    private void loadAnimations() {
        character = new TextureAtlas("character/npc/adventurer_girl.atlas");

        // Create animations for each direction and state
        idleFrontAnimation = new Animation<>(0.15f, character.findRegions("Idle_Up"));
        idleBackAnimation = new Animation<>(0.15f, character.findRegions("Idle_Down"));
        idleLeftAnimation = new Animation<>(0.15f, character.findRegions("Idle_Left_Down"));
        idleRightAnimation = new Animation<>(0.15f, character.findRegions("Idle_Right_Down"));

        walkFrontAnimation = new Animation<>(0.1f, character.findRegions("walk_Up"));
        walkBackAnimation = new Animation<>(0.1f, character.findRegions("walk_Down"));
        walkLeftAnimation = new Animation<>(0.1f, character.findRegions("walk_Left_Down"));
        walkRightAnimation = new Animation<>(0.1f, character.findRegions("walk_Right_Down"));
    }

    public void update(float delta, TiledMap tiledMap) {
        stateTime += delta;
        oldPosition.set(position);

        // Calculate next position
        Vector2 nextPosition = new Vector2(
            position.x + velocity.x * delta,
            position.y + velocity.y * delta
        );


        TextureRegion currentSprite = getCurrentSprite();
        Rectangle nextBoundingBox = new Rectangle(
            nextPosition.x - currentSprite.getRegionWidth() / 2f,
            nextPosition.y - currentSprite.getRegionHeight() / 2f,
            currentSprite.getRegionWidth(),
            currentSprite.getRegionHeight()
        );

        // Check collision with objects and tile layer
        boolean blocked = false;
        if (collisionObjects != null && isCollidingWithObjects(nextBoundingBox)) {
            blocked = true;
        }


        // Add check for tile-based collision rectangles
        if (!blocked && mapController.checkCollision(nextBoundingBox)) {
            blocked = true;
        }

        if (!blocked) {
            position.set(nextPosition);
        } else {
            position.set(oldPosition);
            velocity.set(0, 0);
        }

        isMoving = velocity.len2() > 0.0001f;

        tileX = (int) (position.x / tileWidth);
        tileY = (int) (position.y / tileHeight);

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

    public void draw(SpriteBatch batch) {
        TextureRegion currentSprite = getCurrentSprite();
        batch.draw(currentSprite,
            position.x - currentSprite.getRegionWidth()/2f,
            position.y - currentSprite.getRegionHeight()/2f
        );
    }

    /** Checks if the character's bounding box collides with any collidable object in the collisionObjects */
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

    /** Checks if the character's bounding box collides with any blocked tile */
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
        if (character != null) {
            character.dispose();
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
