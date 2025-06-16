package io.github.eco_warrior.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.Trees.IceTreeController;
import io.github.eco_warrior.controller.Trees.OrdinaryTreeController;
import io.github.eco_warrior.controller.Trees.VoltaicTreeController;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;
import io.github.eco_warrior.sprite.tree_variant.IceTree;
import io.github.eco_warrior.sprite.tree_variant.OrdinaryTree;
import io.github.eco_warrior.sprite.tree_variant.VoltaicTree;

public class TreeControllerTest implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private VoltaicTreeController treeController;
    private WateringCan wateringCan;
    private GameSprite sapling;
    private Vector2 wateringCanPosition;
    private Vector2 saplingPosition;



    public TreeControllerTest() {
        this.batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Initialize game objects
        VoltaicTree tree = new VoltaicTree(
            new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f),
            0.5f
        );

        wateringCan = new WateringCan(
            new Vector2(100, 100),
            1f
        );

        sapling = new GameSprite(
            "atlas/saplings/saplings.atlas",
            "blazing_sapling",
            new Vector2(200, 100),
            1f
        );

        treeController = new VoltaicTreeController(tree, wateringCan);

        wateringCanPosition = wateringCan.getPosition();
        saplingPosition = sapling.getPosition();
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
            treeController.reset();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            System.out.println("Filling watering can");
            wateringCan.waterLevel = WateringCan.WateringCanState.FILLED.ordinal();
            wateringCan.setFrame(wateringCan.waterLevel);
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
//            treeController.getBlazingTree().getStage();
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
    }
}
