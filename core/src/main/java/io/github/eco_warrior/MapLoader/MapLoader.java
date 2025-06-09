package io.github.eco_warrior.MapLoader;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MapLoader {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    /***
     * This map loader uses TiledMap and OrthogonalTiledMapRenderer to load and render maps.
     */
    public MapLoader() {
    }

    public void loadMap(String mapPath) throws Exception {
        map = new TmxMapLoader().load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map);
    }
    public TiledMap getMap() {
        return map;
    }

    public OrthogonalTiledMapRenderer getRenderer() {
        return renderer;
    }

    public TiledMapTileLayer getLayer(String layerName) {
        return (TiledMapTileLayer) map.getLayers().get(layerName);
    }

    public void dispose() {
        if (map != null) {
            map.dispose();
        }
        if (renderer != null) {
            renderer.dispose();
        }
    }
}
