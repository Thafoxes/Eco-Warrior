package io.github.eco_warrior.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
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
//    private final List<Rectangle> collisionRects;
    private final List<Object> collisionPolygons;


    public MapController(int tileWidth, int tileHeight, Vector2 spawnPosition) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.spawnPosition = spawnPosition;
        this.allCollisionObjects = new MapObjects();
        this.collisionPolygons = new ArrayList<>();



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

        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                collisionPolygons.add(rect);
            }
            else if (object instanceof EllipseMapObject) {
                // Store ellipse data for circle collision
                EllipseMapObject ellipseObj = (EllipseMapObject) object;
                // We'll store as a rectangle for now (bounding box)
                Ellipse ellipse = ellipseObj.getEllipse();
                Rectangle rect = new Rectangle(
                    ellipse.x, ellipse.y,
                    ellipse.width, ellipse.height
                );
                collisionPolygons.add(rect);
            }
            else if (object instanceof PolygonMapObject) {
                // Store polygon for polygon collision
                PolygonMapObject polyObj = (PolygonMapObject) object;
                Polygon poly = polyObj.getPolygon();
                collisionPolygons.add(poly);
            }
            else if (object instanceof PolylineMapObject) {
                // Convert polyline to polygon for collision
                PolylineMapObject lineObj = (PolylineMapObject) object;
                Polyline polyline = lineObj.getPolyline();
                float[] vertices = polyline.getTransformedVertices();

                // Create a polygon from the polyline
                Polygon poly = new Polygon(vertices);
                collisionPolygons.add(poly);
            }
        }
    }

    /**
     * Loads collision tiles from the default set layer in the TiledMap.
     *
     * @param map The TiledMap to load collision tiles from.
     */
    public void loadCollisionTiles(TiledMap map) {
        loadCollisionTiles(map, COLLISION_LAYER_NAME);
    }


    public boolean checkCollision(Rectangle boundingBox){
        for(Object rect : collisionPolygons){
            if(rect instanceof Rectangle){
                // Check if the bounding box overlaps with the rectangle
                if(boundingBox.overlaps((Rectangle)rect)){
                    return true; // Collision detected
                }
        }

        // Check against polygons
        float[] rectVertices = new float[] {
            boundingBox.x, boundingBox.y,
            boundingBox.x + boundingBox.width, boundingBox.y,
            boundingBox.x + boundingBox.width, boundingBox.y + boundingBox.height,
            boundingBox.x, boundingBox.y + boundingBox.height
        };
        Polygon boundingPoly = new Polygon(rectVertices);


        for (Object shape : collisionPolygons) {
            if (shape instanceof Polygon) {
                if (Intersector.overlapConvexPolygons(boundingPoly, (Polygon) shape)) return true;
                // Collision detected
            } else if (shape instanceof Rectangle) {
                if (boundingBox.overlaps((Rectangle) shape)) {
                    return true; // Collision detected
                }
            }
        }
        }

        return false;
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
                if(cell != null && cell.getTile() != null){
                    // Convert tile position to world coordinates
                    float worldX = x * layer.getTileWidth();
                    float worldY = y * layer.getTileHeight();
                    MapObjects objects = cell.getTile().getObjects();

                    for(MapObject object : objects) {
                        if(object instanceof RectangleMapObject){
                            RectangleMapObject rectObj = (RectangleMapObject) object;
                            Rectangle rect = new Rectangle(rectObj.getRectangle());
                            rect.x += worldX;
                            rect.y += worldY;
                            collisionPolygons.add(rect);
                        }
                    }

                }
            }
        }
    }

    public void drawDebug(ShapeRenderer shapeRenderer, Color color){

        shapeRenderer.setColor(Color.RED);
        for (Object obj : collisionPolygons) {
            if(obj instanceof Rectangle){
                Rectangle rect = (Rectangle) obj;
                shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
            }
        }

        for(Object shape: collisionPolygons){
            if(shape instanceof Polygon){
                Polygon polygon = (Polygon) shape;
                float[] vertices = polygon.getTransformedVertices();
                for (int i = 0; i < vertices.length; i += 2) {
                    int nextIndex = (i + 2) % vertices.length;
                    shapeRenderer.line(vertices[i], vertices[i + 1], vertices[nextIndex], vertices[nextIndex + 1]);
                }
            }else if(shape instanceof Rectangle){
                Rectangle rect = (Rectangle) shape;
                shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
            }

        }
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

