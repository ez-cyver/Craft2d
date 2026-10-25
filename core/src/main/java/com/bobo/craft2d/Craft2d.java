package com.bobo.craft2d;
import javax.xml.stream.EventFilter;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Craft2d extends ApplicationAdapter {
    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;

    private static final int WORLD_WIDTH = 600; // Width of the game world in Blocks
    private static final int WORLD_HEIGHT = 40; // Height of the game world in Blocks
    private static final int VIEWPORT_WIDTH_BLOCKS = 60; // Width of the viewport in Blocks
    private static final int VIEWPORT_HEIGHT_BLOCKS = 40; // Height of the viewport in Blocks
    private int [][] worldGrid;

    float velocityY = 0; //vertical speed of the player
    float gravity =  -900f; //gravity strength (negative value to pull down)
    final int originalTileSize = 8; // Size of the player square in pixels
    final int scale = 3; // Scale factor for the player square
    final int tileSize = originalTileSize * scale; // Size of the player square
    private static final int GROUND_HEIGHT_BLOCKS = 10; // Height of the ground in Blocks                                                           <-Wolrd height is 40, so the ground will be at 10 blocks from the bottom
    float groundLevel = GROUND_HEIGHT_BLOCKS * tileSize; // Calculate the ground level based on the floor position and height

    float playerX = (WORLD_WIDTH / 2) * tileSize;
    float playerY = groundLevel + tileSize; // Position the player on the ground
    float playerSpeed = 200f; // Speed in pixels per second


    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        worldGrid = new int[WORLD_WIDTH][WORLD_HEIGHT];
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < GROUND_HEIGHT_BLOCKS; y++){
                worldGrid[x][y] = 1;// here is a block, 1 means there is a block, 0 means empty space
            }
        }
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT_WIDTH_BLOCKS * tileSize, VIEWPORT_HEIGHT_BLOCKS * tileSize);
        camera.zoom = 0.5f;
    }

@Override
public void render() {
    float deltaTime = Gdx.graphics.getDeltaTime();
    camera.position.set(playerX, playerY, 0);
    camera.update();

    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
        playerSpeed = 100f;
    } else {
        playerSpeed = 200f;
    }

    if (Gdx.input.isKeyPressed(Input.Keys.A)) {
        playerX -= playerSpeed * deltaTime;
        int leftGridX = (int) (playerX / tileSize);
        int bottomGridY = (int) (playerY / tileSize);
        int topGridY = (int) ((playerY + 2 * tileSize - 1) / tileSize);
        if ((bottomGridY >= 0 && bottomGridY < WORLD_HEIGHT && worldGrid[leftGridX][bottomGridY] == 1) ||
            (topGridY >= 0 && topGridY < WORLD_HEIGHT && worldGrid[leftGridX][topGridY] == 1)) {
            playerX = (leftGridX + 1) * tileSize;
        }
    }


    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
        playerX += playerSpeed * deltaTime;
        int rightGridX = (int) ((playerX + tileSize) / tileSize);
        int bottomY = (int) (playerY / tileSize);
        int topY = (int) ((playerY + 2 * tileSize - 1) / tileSize);
        if (rightGridX >= 0 && rightGridX < WORLD_WIDTH) {
            if ((bottomY >= 0 && bottomY < WORLD_HEIGHT && worldGrid[rightGridX][bottomY] == 1) ||
                (topY >= 0 && topY < WORLD_HEIGHT && worldGrid[rightGridX][topY] == 1)) {
                playerX = rightGridX * tileSize - tileSize;
            }
        }
    }

    if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        int gridX = (int) (mousePos.x / tileSize);
        int gridY = (int) (mousePos.y / tileSize);

        if (gridX >= 0 && gridX < WORLD_WIDTH && gridY >= 0 && gridY < WORLD_HEIGHT) {
            worldGrid[gridX][gridY] = 0; // breaks the block at the clicked position
        }
    }

    if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        int gridX = (int) (mousePos.x / tileSize);
        int gridY = (int) (mousePos.y / tileSize);

        if (gridX >= 0 && gridX < WORLD_WIDTH && gridY >= 0 && gridY < WORLD_HEIGHT) {
            worldGrid[gridX][gridY] = 1; // Block wird platziert
        }
    }
    velocityY += gravity * deltaTime;
    playerY += velocityY * deltaTime;

    // 2. Boden-Kollision (das "- 1" zwingt den Check in das Block-Tile unter den Füßen)
    int footGridX1 = (int) (playerX / tileSize);
    int footGridX2 = (int) ((playerX + tileSize - 1) / tileSize);
    int footGridY = (int) ((playerY - 1) / tileSize);

    if (footGridY >= 0 && footGridY < WORLD_HEIGHT) {
        if ((footGridX1 >= 0 && footGridX1 < WORLD_WIDTH && worldGrid[footGridX1][footGridY] == 1) ||
            (footGridX2 >= 0 && footGridX2 < WORLD_WIDTH && worldGrid[footGridX2][footGridY] == 1)) {
            playerY = (footGridY + 1) * tileSize; 
            velocityY = 0; 
        }
    }

    if (velocityY > 0) {
        int headGridX1 = (int) (playerX / tileSize);
        int headGridX2 = (int) ((playerX + tileSize - 1) / tileSize);
        int headGridY = (int) ((playerY + 2 * tileSize - 1) / tileSize);

        if (headGridY >= 0 && headGridY < WORLD_HEIGHT) {
            if ((headGridX1 >= 0 && headGridX1 < WORLD_WIDTH && worldGrid[headGridX1][headGridY] == 1) ||
                (headGridX2 >= 0 && headGridX2 < WORLD_WIDTH && worldGrid[headGridX2][headGridY] == 1)) {
                playerY = headGridY * tileSize - 2 * tileSize;
                velocityY = 0;
            }
        }
    }


    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
        int groundCheckY = (int) ((playerY - 1) / tileSize);
        if (footGridX1 >= 0 && footGridX1 < WORLD_WIDTH && groundCheckY >= 0 && groundCheckY < WORLD_HEIGHT) {
            if (worldGrid[footGridX1][groundCheckY] == 1) {
                velocityY = (float) Math.sqrt(2 * Math.abs(gravity) * (1.4f * tileSize));
            }
        }
    }

    int footGridX = (int) (playerX / tileSize);

    if (footGridX >= 0 && footGridX < WORLD_WIDTH && footGridY >= 0 && footGridY < WORLD_HEIGHT) {
        if (worldGrid[footGridX][footGridY] == 1) {
            playerY = footGridY * tileSize + tileSize; 
            velocityY = 0; 
        }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
        if (footGridX >= 0 && footGridX < WORLD_WIDTH && footGridY >= 0 && footGridY < WORLD_HEIGHT) {
            if (worldGrid[footGridX][footGridY] == 1) {
                velocityY = (float) Math.sqrt(2 * Math.abs(gravity) * (1.4f * tileSize));
            }
        }
    }
    Gdx.gl.glClearColor(0.5f, 0.8f, 1.0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer.setProjectionMatrix(camera.combined);
    shapeRenderer.setColor(0.3f, 0.7f, 0.3f, 1f);
    for (int x = 0; x < WORLD_WIDTH; x++) {
    for (int y = 0; y < WORLD_HEIGHT; y++) {
        if (worldGrid[x][y] == 1) {
            shapeRenderer.rect(x * tileSize, y * tileSize, tileSize, tileSize);
        }
    }
    }


    shapeRenderer.setColor(Color.BLACK);
    shapeRenderer.rect(playerX, playerY, tileSize,2 * tileSize);

    shapeRenderer.end();

    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    shapeRenderer.setProjectionMatrix(camera.combined);
    shapeRenderer.setColor(0.6f, 0.85f, 1.0f, 0.4f);

    for (int x = 0; x <= WORLD_WIDTH; x++) {
        shapeRenderer.line(x * tileSize, 0, x * tileSize, GROUND_HEIGHT_BLOCKS * tileSize);
    }
    for (int y = 0; y <= GROUND_HEIGHT_BLOCKS; y++) {
        shapeRenderer.line(0, y * tileSize, WORLD_WIDTH * tileSize, y * tileSize);
    }
    shapeRenderer.end();
    }


@Override
public void dispose() {
    shapeRenderer.dispose();
    }

}