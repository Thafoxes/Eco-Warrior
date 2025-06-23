package io.github.eco_warrior.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.sprite.Characters.GameCharacter;
import io.github.eco_warrior.sprite.Characters.Goblin;
import io.github.eco_warrior.sprite.Characters.Goblin2;
import io.github.eco_warrior.sprite.Characters.Goblin3;

import java.util.ArrayList;
import java.util.List;

public class NPCManager {
    private List<GameCharacter> NPCs = new ArrayList<>();
    private MapController mapController;
    private boolean isInteracting = false;
    private FontGenerator fontGenerator;


    public NPCManager(TiledMap map, int tileWidth, int tileHeight) {
        MapLayer npcLayer = map.getLayers().get("NPC_area");

        for (MapObject obj : npcLayer.getObjects()) {
            if ("Level_1".equals(obj.getName()) && obj instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                NPCs.add(new Goblin(new Vector2(rect.x, rect.y), tileWidth, tileHeight, true));
            }
            if ("Level_2".equals(obj.getName()) && obj instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                NPCs.add(new Goblin2(new Vector2(rect.x, rect.y), tileWidth, tileHeight, true));
            }
            if ("Level_3".equals(obj.getName()) && obj instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                NPCs.add(new Goblin3(new Vector2(rect.x, rect.y), tileWidth, tileHeight, true));
            }
        }

        fontGenerator = new FontGenerator(16, Color.WHITE, Color.BLACK);
    }

    /**
     * Set the map controller for collision detection
     */
    public void setMapController(MapController mapController) {
        this.mapController = mapController;

        // Set map controller for all NPCs
        for (GameCharacter npc : NPCs) {
            npc.setMapController(mapController);
        }
    }

    public void update(float deltaTime, TiledMap tileMap) {
        for (GameCharacter npc : NPCs) {
            npc.update(deltaTime, tileMap);
        }
    }

    /**
     * Returns all NPC collision boxes for external collision checking
     * @return List of Rectangle objects representing NPC collision areas
     */
    public List<Rectangle> getNpcCollisionBoxes() {
        List<Rectangle> boxes = new ArrayList<>();
        for (GameCharacter npc : NPCs) {
            if (npc.getBoundingBox() != null) {
                boxes.add(npc.getBoundingBox());
            }
        }
        return boxes;
    }

    public void setInteracting(boolean interacting) {
        isInteracting = interacting;
    }

    public GameCharacter checkInteraction(Rectangle playerBox) {

        for (GameCharacter character : NPCs) {

            Rectangle characterBox = character.getBoundingBox();
            if (characterBox == null) {
                continue; // Skip this character if it has no texture
            }
            Rectangle interactionBox = new Rectangle(
                characterBox.x - 35f,
                characterBox.y - 35f,
                characterBox.width + 50f,
                characterBox.height + 50f
            );
            if (interactionBox.overlaps(playerBox)) {
                System.out.println("Interaction detected with: " + character.getName());
                return character; // Interaction detected!
            }
        }
        return null;
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        for(GameCharacter npc : NPCs) {
            npc.draw(batch);

            // Draw character name above the sprite
            Vector2 textPosition = new Vector2(
                npc.getPosition().x,
                npc.getPosition().y + 40 // 10 pixels above the sprite
            );

            batch.end();
            fontGenerator.objFontDraw(
                batch,
                "Press F to " + npc.getName(),
                5, // font size
                camera,
                textPosition
            );
            batch.begin();

        }
    }

    public void drawDebug(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(Color.GREEN);
        for (GameCharacter npc : NPCs) {
            if (npc.getBoundingBox() != null) {
                shapeRenderer.rect(
                    npc.getBoundingBox().x,
                    npc.getBoundingBox().y,
                    npc.getBoundingBox().width,
                    npc.getBoundingBox().height
                );

            }
        }

    }


    /**
     * Check if a rectangle (usually player's next position) would be blocked by any NPC
     */
    public boolean isBlocked(Rectangle playerNextPos) {
        for (GameCharacter npc : NPCs) {
            if (npc.getBlocking() && playerNextPos.overlaps(npc.getBoundingBox())) {
                return true; // Collision detected!
            }
        }
        return false; // No collision
    }

    public void dispose() {

        for (GameCharacter npc : NPCs) {
            npc.dispose();
        }
        fontGenerator.dispose();
    }


}
