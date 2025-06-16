package io.github.eco_warrior.entity;

import com.badlogic.gdx.math.Vector2;

public class BaseTreeHealth extends GameSprite {

    private Trees parentTree;

    public BaseTreeHealth(String atlasPath, String regionBaseName, Trees tree) {
        super(atlasPath,
            regionBaseName,
            5, // Assuming 4 frames for health bar
            tree.getPosition(),
            .1f);

        this.parentTree = tree;
        updatePosition();
    }

    private void updatePosition() {
        float xPos = parentTree.getPosition().x + (parentTree.getSprite().getWidth() * parentTree.getScale() / 4f);
        float yPos = parentTree.getPosition().y - 30f;

        setPosition(new Vector2(xPos, yPos));
    }

    /**
     * Updates the health bar frame based on the current health.
     * Assumes that the health is represented as a percentage of the maximum health.
     * @param health The current health value.
     */
    public void updateHealth(int health) {
        if(health < 0) {
            health = getFrameCount() - 1;
        }else if(health >= getFrameCount()){
            health = 0;
        }
        /***
         Wah Hin is not sure why the health is inverted, but this is how it is in the original code.
         Please when you are reading this, if you know why the health is inverted, please let me know.
         If you want to change it, you can change the line below to:
            setFrame(health);
         and change your atlas
         */
       setFrame(getFrameCount() - health - 1);

    }
}
