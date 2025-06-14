package io.github.eco_warrior.controller;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.List;


public class MapController {
    public final int tileWidth;
    public final int tileHeight;
    public final Vector2 spawnPosition;
    public final MapLayer collisionObjectLayer;
    public final com.badlogic.gdx.maps.MapObjects allCollisionObjects;
    private static final float PIXELS_PER_METER = 32f; // Adjust based on your scale

    private static final String COLLISION_LAYER_NAME = "objects1";
    private final List<Rectangle> collisionRects;


    public MapController(int tileWidth, int tileHeight, Vector2 spawnPosition, MapLayer collisionObjectLayer) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.spawnPosition = spawnPosition;
        this.collisionObjectLayer = collisionObjectLayer;
        this.allCollisionObjects = new MapObjects();
        this.collisionRects = new ArrayList<>();



        if(collisionObjectLayer != null) {
            // Add all objects from the collision layer
            for (MapObject object : collisionObjectLayer.getObjects()) {
                allCollisionObjects.add(object);
            }
        }
        // Add collision rectangles from the collision layer
        loadCollisionObjects();

    }

    public void loadCollisionObjects() {
        if (collisionObjectLayer != null) {
            for (MapObject object : collisionObjectLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    collisionRects.add(rect);
                }
            }
        }
    }


    public void loadCollisionTiles(TiledMap map) {
        loadCollisionTiles(map, COLLISION_LAYER_NAME);
    }
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
                            collisionRects.add(rect);
                        }
                    }

                }
            }
        }
    }

    // Add method to check collision with tile rectangles
    public boolean checkCollision(Rectangle boundingBox) {
        for (Rectangle rect : collisionRects) {
            if (boundingBox.overlaps(rect)) {
                return true; // Collision detected
            }
        }
        return false;
    }

    public List<Rectangle> getCollisionRects() {
        return collisionRects;
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
        MapController mapController = new MapController(tileWidth, tileHeight, spawnPosition, collisionObjectLayer);

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

