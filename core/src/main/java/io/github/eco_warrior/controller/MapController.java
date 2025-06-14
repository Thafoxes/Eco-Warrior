package io.github.eco_warrior.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.List;


public class MapController {
    public final int tileWidth;
    public final int tileHeight;
    public final Vector2 spawnPosition;
    public final MapObjects allCollisionObjects;
    private static final float PIXELS_PER_METER = 32f; // Adjust based on your scale

    private static final String COLLISION_LAYER_NAME = "objects1";
    private final List<Polygon> collisionPolygons;
    private final List<Rectangle> collisionRectangles;
    private final List<Circle> collisionCircles;
    private final List<Ellipse> collisionEllipses;


    public MapController(int tileWidth, int tileHeight, Vector2 spawnPosition) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.spawnPosition = spawnPosition;
        this.allCollisionObjects = new MapObjects();
        this.collisionPolygons = new ArrayList<>();
        this.collisionRectangles = new ArrayList<>();
        this.collisionCircles = new ArrayList<>();
        this.collisionEllipses = new ArrayList<>();



    }

    /**
     * Loads collision objects from a specified layer in the TiledMap.
     *
     * @param map       The TiledMap to load collision objects from.
     * @param layerName The name of the layer containing collision objects.
     */
    public void loadCollisionObjects(TiledMap map, String layerName) {
        MapLayer layer = map.getLayers().get(layerName);
        if (layer == null) {
            System.out.println("Collision object layer not found: " + layerName);
            return;
        }

        for(MapObject object : layer.getObjects()){
            FilterCollisionObj(object, 0, 0);

        }
    }

    private void FilterCollisionObj(MapObject object, float worldX, float worldY) {

            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                rect.x += worldX;
                rect.y += worldY;
                collisionRectangles.add(rect);
            }
            else if (object instanceof CircleMapObject) {
                // Store ellipse data for circle collision
                CircleMapObject circleObj = (CircleMapObject) object;
                // We'll store as a rectangle for now (bounding box)
                Circle circle = circleObj.getCircle();
                circle.x += worldX + circle.radius;
                circle.y += worldY + circle.radius;
                collisionCircles.add(circle);
            }
            else if (object instanceof EllipseMapObject) {
                Ellipse ellipse = ((EllipseMapObject) object).getEllipse();
                ellipse.x += worldX;
                ellipse.y += worldY;
                collisionEllipses.add(ellipse);
            }
            else if (object instanceof PolygonMapObject) {
                // Store polygon for polygon collision
                PolygonMapObject polyObj = (PolygonMapObject) object;
                Polygon poly = polyObj.getPolygon();
                float[] vertices = poly.getTransformedVertices();
                for (int i = 0; i < vertices.length; i += 2) {
                    vertices[i] += worldX;
                    vertices[i + 1] += worldY;
                }
                collisionPolygons.add(poly);
            }

    }


    public boolean checkCollision(Rectangle boundingBox){
        // Broad phase: Check rectangles
        for (Rectangle rect : collisionRectangles) {
            if (boundingBox.overlaps(rect)) {
                return true;
            }
        }

        // Broad phase: Check circles
        for (Circle circle : collisionCircles) {
            if (Intersector.overlaps(circle, boundingBox)) {
                return true;
            }
        }

        // Check ellipses
        for (Ellipse ellipse : collisionEllipses) {
            // Convert ellipse to a polygon representation
            if (ellipseRectangleCollision(ellipse, boundingBox)) {
                return true;
            }
        }

        // Narrow phase: Check polygons
        float[] rectVertices = new float[] {
            boundingBox.x, boundingBox.y,
            boundingBox.x + boundingBox.width, boundingBox.y,
            boundingBox.x + boundingBox.width, boundingBox.y + boundingBox.height,
            boundingBox.x, boundingBox.y + boundingBox.height
        };
        Polygon boundingPoly = new Polygon(rectVertices);

        for (Object poly : collisionPolygons) {
            if(poly instanceof Polygon){
                if (Intersector.overlapConvexPolygons(boundingPoly, (Polygon) poly)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean ellipseRectangleCollision(Ellipse ellipse, Rectangle rect) {
        // Check if rectangle center is inside ellipse (simplification)
        float rectCenterX = rect.x + rect.width / 2;
        float rectCenterY = rect.y + rect.height / 2;

        float ellipseCenterX = ellipse.x + ellipse.width / 2;
        float ellipseCenterY = ellipse.y + ellipse.height / 2;

        // Quick rejection test - bounding boxes
        if (!new Rectangle(ellipse.x, ellipse.y, ellipse.width, ellipse.height).overlaps(rect)) {
            return false;
        }

        // More accurate test for ellipse-rectangle collision
        // Check if any corner of the rectangle is inside the ellipse
        return isPointInEllipse(rect.x, rect.y, ellipse) ||
            isPointInEllipse(rect.x + rect.width, rect.y, ellipse) ||
            isPointInEllipse(rect.x, rect.y + rect.height, ellipse) ||
            isPointInEllipse(rect.x + rect.width, rect.y + rect.height, ellipse) ||
            // Additional checks for rectangle edges intersecting with ellipse
            lineEllipseIntersection(rect.x, rect.y, rect.x + rect.width, rect.y, ellipse) ||
            lineEllipseIntersection(rect.x, rect.y, rect.x, rect.y + rect.height, ellipse) ||
            lineEllipseIntersection(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height, ellipse) ||
            lineEllipseIntersection(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height, ellipse);
    }

    private boolean lineEllipseIntersection(float x1, float y1, float x2, float y2, Ellipse ellipse) {
        // Simplified approach - check several points along the line
        final int STEPS = 10;

        for (int i = 0; i <= STEPS; i++) {
            float t = i / (float)STEPS;
            float x = x1 + t * (x2 - x1);
            float y = y1 + t * (y2 - y1);

            if (isPointInEllipse(x, y, ellipse)) {
                return true;
            }
        }

        return false;
    }

    private boolean isPointInEllipse(float x, float y, Ellipse ellipse) {
        float centerX = ellipse.x + ellipse.width / 2;
        float centerY = ellipse.y + ellipse.height / 2;
        float a = ellipse.width / 2;
        float b = ellipse.height / 2;

        // Check if point (x,y) is inside the ellipse using the standard equation
        float normalizedX = (x - centerX) / a;
        float normalizedY = (y - centerY) / b;

        return normalizedX * normalizedX + normalizedY * normalizedY <= 1.0f;
    }

    /**
     * Loads collision tiles from the default set layer in the TiledMap.
     * To test cause is using object3
     *
     * @param map The TiledMap to load collision tiles from.
     */
    public void loadCollisionTiles(TiledMap map) {
        loadCollisionTiles(map, COLLISION_LAYER_NAME);
    }


    /**
     * Loads collision tiles from a specified layer in the TiledMap.
     *
     * @param map  The TiledMap to load collision tiles from.
     * @param name The name of the layer containing collision tiles.
     */
    public void loadCollisionTiles(TiledMap map, String name){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(name);
        if(layer == null){
            System.out.println("Collision layer not found: " + name);
            return;
        }

        for(int y = 0; y < layer.getHeight(); y++){
            for(int x = 0; x < layer.getWidth(); x++){
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);

                // Check if the cell is not null and has a tile
                if(cell != null && cell.getTile() != null){
                    float worldX = x * layer.getTileWidth();
                    float worldY = y * layer.getTileHeight();
                    // Convert tile position to world coordinates
                    MapObjects objects = cell.getTile().getObjects();

                    for(MapObject object : objects){
                        FilterCollisionObj(object, worldX, worldY);

                    }

                }
            }
        }
    }

    public void drawDebug(ShapeRenderer shapeRenderer, Color color){

        shapeRenderer.setColor(color);

        // Draw rectangles
        for (Rectangle rect : collisionRectangles) {
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }

        // Draw circles
        for (Circle circle : collisionCircles) {
            shapeRenderer.circle(circle.x, circle.y, circle.radius);
        }

        // Draw ellipses
        for (Ellipse ellipse : collisionEllipses) {
            shapeRenderer.ellipse(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
        }



        for(Polygon shape: collisionPolygons){
            if(shape instanceof Polygon){
                float[] vertices = shape.getTransformedVertices();
                for (int i = 0; i < vertices.length; i += 2) {
                    int nextIndex = (i + 2) % vertices.length;
                    shapeRenderer.line(vertices[i], vertices[i + 1], vertices[nextIndex], vertices[nextIndex + 1]);
                }
            }
        }
    }

    public List<Ellipse> getCollisionEllipses() {
        return collisionEllipses;
    }

    public List<Rectangle> getCollisionRectangles() {
        return collisionRectangles;
    }

    public List<Circle> getCollisionCircles() {
        return collisionCircles;
    }

    public List<Polygon> getCollisionPolygons() {
        return collisionPolygons;
    }


    //please override this method in the class that extends MapInfo
    public MapController getMapInfo() throws Exception{
        return null;
    }

    /**
     * Adds all collision objects from a given MapLayer to the list of all collision objects.
     *
     * @param layer The MapLayer from which to add collision objects.
     */
    public void addCollisionObjectsFromLayer(MapLayer layer) {
        if (layer != null) {
            for (MapObject object : layer.getObjects()) {
                allCollisionObjects.add(object);
            }
        }
    }

    /***
     * Retrieves map information from a TiledMap.
     * This this just a guide how to extract information from a TiledMap. Not encourage to use it directly.
     * Refer to WorldTestsV2 for how to use this method.
     *
     * @param map The TiledMap to extract information from.
     * @return A MapInfo object containing the map's tile dimensions, spawn position, and collision layer.
     * @throws Exception If the spawn point or collision layer is not found in the map.
     */
    public static MapController getMapInfo(TiledMap map, World world) throws Exception {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("main_space");
        int tileWidth = layer.getTileWidth();
        int tileHeight = layer.getTileHeight();

        Vector2 spawnPosition = new Vector2(0, 0);
        MapLayer objectLayer = map.getLayers().get("spawn_layer");
        Rectangle spawnPoint = null;

        boolean foundSpawn = false;
        if (objectLayer != null) {
            for (MapObject object : objectLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectangleObject = (RectangleMapObject) object;
                    if ("spawn_point".equals(object.getName())) {
                        spawnPoint = rectangleObject.getRectangle();
                        spawnPosition.x = rectangleObject.getProperties().get("x", Float.class);
                        spawnPosition.y = rectangleObject.getProperties().get("y", Float.class);
                        foundSpawn = true;
                        break;
                    }
                }
            }
        }
        if (!foundSpawn) {
            throw new Exception("Spawn point not found in the map. Please ensure the map has a spawn point defined.");
        }

        MapLayer collisionObjectLayer = map.getLayers().get("Collision");
        if (collisionObjectLayer == null) {
            throw new Exception("Collision object layer not found in the map.");
        }

        // Create MapInfo with the standard collision layer
        MapController mapController = new MapController(tileWidth, tileHeight, spawnPosition);

        // Add collision objects from top layers - fixed to use proper variable access
        // Get total layers count directly
        int numLayers = map.getLayers().getCount();
        // Use a constant value or define one here
        final int LAYER_MAP = 4; // Matching WorldTestsV2.LAYER_MAP

        // Add collision objects from top layers
        for (int i = numLayers - LAYER_MAP; i < numLayers; i++) {
            String layerName = map.getLayers().get(i).getName();
            MapLayer topLayer = map.getLayers().get(layerName);
            if (topLayer != null) {
                mapController.addCollisionObjectsFromLayer(topLayer);
            }
        }

        return mapController;
    }






}

