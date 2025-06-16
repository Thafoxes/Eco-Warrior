package io.github.eco_warrior.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.controller.Trees.*;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;
import io.github.eco_warrior.sprite.gardening_equipments.sapling_variant.BreezingSapling;
import io.github.eco_warrior.sprite.gardening_equipments.sapling_variant.OrdinarySapling;
import io.github.eco_warrior.sprite.tree_variant.BreezingTree;
import io.github.eco_warrior.sprite.tree_variant.IceTree;
import io.github.eco_warrior.sprite.tree_variant.OrdinaryTree;
import io.github.eco_warrior.sprite.tree_variant.VoltaicTree;

public class TreeControllerTest implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private TreeController treeController;
    private WateringCan wateringCan;
    private BaseSaplingController sapling;
    private Vector2 wateringCanPosition;
    private Vector2 saplingPosition;

    private ShapeRenderer shapeRenderer;



    public TreeControllerTest() {
        this.batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Initialize game objects
        OrdinaryTree tree = new OrdinaryTree(
            new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f),
            0.5f
        );

        wateringCan = new WateringCan(
            new Vector2(100, 100),
            1f
        );

        sapling = new OrdinarySapling(
            new Vector2(200, 200),
            1f
        );

        treeController = new OrdinaryTreeController(tree, wateringCan);

        wateringCanPosition = wateringCan.getPosition();
        saplingPosition = sapling.getPosition();
        this.shapeRenderer = new ShapeRenderer();
    }

    private void handleInput(float delta) {
        float moveSpeed = 200f * delta;

        // Move watering can with WASD
        if (Gdx.input.isKeyPressed(Input.Keys.W)) wateringCanPosition.y += moveSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) wateringCanPosition.y -= moveSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) wateringCanPosition.x -= moveSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) wateringCanPosition.x += moveSpeed;

        // Move sapling with arrow keys
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) saplingPosition.y += moveSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) saplingPosition.y -= moveSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) saplingPosition.x -= moveSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) saplingPosition.x += moveSpeed;

        // Update positions
        wateringCan.setPosition(wateringCanPosition);
        sapling.setPosition(saplingPosition);

        // Test controls
        if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            System.out.println("Current Tree Stage: " + treeController.getTree().getStage());
            treeController.handleWatering();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            System.out.println("Planting sapling at position: " + sapling.getPosition());
            treeController.handleSaplingPlanting(sapling);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            System.out.println("Reviving the tree");
            treeController.reviveTree();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            System.out.println("Reseting");
            treeController.setMaturedStateDebug();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            System.out.println("Filling watering can");
            wateringCan.waterLevel = WateringCan.WateringCanState.FILLED;
            wateringCan.setFrame(wateringCan.waterLevel.ordinal());
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)){
            //should have a check for shovel
            treeController.digHole();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            System.out.println("Handling damage to the tree");
            treeController.takeDamage(1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            System.out.println("Current Tree Stage: " + treeController.getTree().getStage());
        }

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0.2f, 0.3f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update logic
        handleInput(delta);
        treeController.update(delta);

        // Draw
        batch.begin();
        treeController.draw(batch);
        wateringCan.draw(batch);
        sapling.draw(batch);
        batch.end();

        drawDebug();
    }

    private void drawDebug() {
        // Draw debug outlines
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        // Draw tree bounds
        GameSprite tree = treeController.getTree();
        shapeRenderer.rect(tree.getPosition().x, tree.getPosition().y,
            tree.getSprite().getWidth(), tree.getSprite().getHeight());

        // Draw watering can bounds
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(wateringCan.getPosition().x, wateringCan.getPosition().y,
            wateringCan.getSprite().getWidth(), wateringCan.getSprite().getHeight());

        // Draw sapling bounds
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(sapling.getPosition().x, sapling.getPosition().y,
            sapling.getSprite().getWidth(), sapling.getSprite().getHeight());

        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        treeController.dispose();
        shapeRenderer.dispose();
    }
}
