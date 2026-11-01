package com.bobo;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    SpriteBatch batch;
    OrthographicCamera camera;
    Player player;
    Hotbar hotbar;

    int WORLD_WIDTH = GameData.WORLD_WIDTH;    // Width of the game world in Blocks
    int WORLD_HEIGHT = GameData.WORLD_HEIGHT; // Height of the game world in Blocks

    int VIEWPORT_WIDTH_BLOCKS = GameData.VIEWPORT_WIDTH_BLOCKS;// Width of the viewport in Blocks
    int VIEWPORT_HEIGHT_BLOCKS = GameData.VIEWPORT_HEIGHT_BLOCKS; // Height of the viewport in Blocks
    private int [][] worldGrid;

    int originalTileSize = GameData.originalTileSize; // Size of the player square in pixels
    int scale = GameData.scale; // Scale factor for the player square
    int tileSize = GameData.tileSize; // Size of the player square
    int GROUND_HEIGHT_BLOCKS = GameData.GROUND_HEIGHT_BLOCKS; // Height of the ground in Blocks
    float groundLevel = GameData.groundLevel; // Calculate the ground level based on the floor position and height

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        Block.load();
        hotbar = new Hotbar();
        worldGrid = new int[WORLD_WIDTH][WORLD_HEIGHT];
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < GROUND_HEIGHT_BLOCKS; y++){
                if (y == GROUND_HEIGHT_BLOCKS - 1) {
                    worldGrid[x][y] = Block.GRASS;   // top layer
                } else if (y >= GROUND_HEIGHT_BLOCKS - 5) {
                    worldGrid[x][y] = Block.DIRT;    // 4 layers of dirt below
                } else {
                    worldGrid[x][y] = Block.STONE;   // everything below
                }
            }
        }
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT_WIDTH_BLOCKS * tileSize, VIEWPORT_HEIGHT_BLOCKS * tileSize);
        camera.zoom = 0.5f;
        
        player = new Player(
            (GameData.WORLD_WIDTH / 2) * tileSize,
            GameData.groundLevel + tileSize
        );
    }

@Override
public void render() {
    float deltaTime = Gdx.graphics.getDeltaTime();
    player.update(deltaTime, worldGrid, WORLD_HEIGHT, tileSize);
    camera.position.set(player.playerX, player.playerY, 0);
    camera.update();
    hotbar.update();
    Block.handleMouse(camera, worldGrid, tileSize, hotbar.getSelectedBlock());
    
    Gdx.gl.glClearColor(0.5f, 0.8f, 1.0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    //only draw the blocks, that are visable on the screen
    float halfW = camera.viewportWidth * camera.zoom / 2;
    float halfH = camera.viewportHeight * camera.zoom / 2;
    int endX = Math.min(WORLD_WIDTH - 1, (int) ((camera.position.x + halfW) / tileSize) + 1);
    int startX = Math.max(0, (int) ((camera.position.x - halfW) / tileSize));    int startY = Math.max(0, (int) ((camera.position.y - halfH) / tileSize));
    int endY = Math.min(WORLD_HEIGHT - 1, (int) ((camera.position.y + halfH) / tileSize) + 1);
    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    for (int x = startX; x <= endX; x++) {
        for (int y = startY; y <= endY; y++) {
            Block.draw(batch, worldGrid[x][y], x * tileSize, y * tileSize, tileSize);
        }
    }
    player.render(shapeRenderer, batch);
    batch.end();

    //outline of the block under the mouse

    Vector3 hoverPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
    camera.unproject(hoverPos);
    int hoverX = (int) Math.floor(hoverPos.x / tileSize);
    int hoverY = (int) Math.floor(hoverPos.y / tileSize);

    if (hoverX >= 0 && hoverX < WORLD_WIDTH && hoverY >= 0 && hoverY < WORLD_HEIGHT) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(hoverX * tileSize, hoverY * tileSize, tileSize, tileSize);
        shapeRenderer.end();
    }
    hotbar.render(batch, shapeRenderer);
}

@Override
public void dispose() {
    shapeRenderer.dispose();
    player.dispose();
    batch.dispose();
    Block.dispose();
    }

}