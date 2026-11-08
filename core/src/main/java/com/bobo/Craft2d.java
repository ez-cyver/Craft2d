package com.bobo;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Craft2d extends ApplicationAdapter {
    ShapeRenderer shapeRenderer;
    SpriteBatch batch;
    OrthographicCamera camera;
    Player player;
    Hotbar hotbar;
    Inventory inventory;
    BitmapFont font;

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

    @Override //says this is a LibGDX method
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        Block.load();
        font = new BitmapFont();
        inventory = new Inventory();
        hotbar = new Hotbar();
        worldGrid = new int[WORLD_WIDTH][WORLD_HEIGHT];
        WorldGenerator.generate(worldGrid, System.currentTimeMillis()); // every start a new world

        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT_WIDTH_BLOCKS * tileSize, VIEWPORT_HEIGHT_BLOCKS * tileSize);
        camera.zoom = 0.5f;


        //serching where to spawn the player
        int spawnX = WORLD_WIDTH / 2;//the spawnposition in the middle of the world
        player = new Player(
            spawnX * tileSize,
            WorldGenerator.getSurfaceHeight(spawnX) * tileSize //looks how heigt the floor ist
        );
    }

@Override
public void render() {
    float deltaTime = Gdx.graphics.getDeltaTime();// Get time since the last frame for frame-rate independent movement
    player.update(deltaTime, worldGrid, WORLD_HEIGHT, tileSize);//update player movment, physiks, and colisions of the world
    //centers the camera on the middle of the player
    camera.position.set(player.playerX, player.playerY, 0);
    camera.update();
    inventory.update();
    hotbar.update();
    if (!inventory.isOpen()) {
        Block.handleMouse(camera, worldGrid, tileSize, hotbar, inventory, player);
    }
    //the blue background(sky) 
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
    for (int x = startX; x <= endX; x++) {//looks the all x cordinates, that the camera sees
        for (int y = startY; y <= endY; y++) {//looks the all y cordinates, that the camera sees
            Block.draw(batch, worldGrid[x][y], x * tileSize, y * tileSize, tileSize);//thakes out of the array, the cordinates of the blocks that have to be drawn 
        }
    }
    player.render(shapeRenderer, batch);//draws the player
    batch.end(); //stops the drawing of the pictures

    //outline of the block under the mouse
    Vector3 hoverPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);//vectore3 = cordinates of the mouse 
    camera.unproject(hoverPos);//calculates the cordinates, so that they fit in the game window
    // Convert world pixel coordinates into block grid coordinates (floored to integers)
    int hoverX = (int) Math.floor(hoverPos.x / tileSize);
    int hoverY = (int) Math.floor(hoverPos.y / tileSize);

    //looks if the mouse is infide of the world and in the players reach
    if (hoverX >= 0 && hoverX < WORLD_WIDTH && hoverY >= 0 && hoverY < WORLD_HEIGHT&& Block.isInReach(player, hoverX, hoverY, tileSize)) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);//begins drawing
        shapeRenderer.setColor(Color.BLACK);//seting color black
        shapeRenderer.rect(hoverX * tileSize, hoverY * tileSize, tileSize, tileSize);//draws a square arround the block
        shapeRenderer.end();//finishes drawing 
    } 
    if (inventory.isOpen()) {
        inventory.render(batch, shapeRenderer, font); // the inventory contains the hotbar row
    } else {
        hotbar.render(batch, shapeRenderer, inventory, font); // Render the hotbar UI at the bottom of the screen
    }
}

@Override
public void dispose() {//deletes all, that was createt in the create method when stopping the game
    shapeRenderer.dispose();
    player.dispose();
    batch.dispose();
    Block.dispose();
    font.dispose();
    }

}