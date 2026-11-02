package com.bobo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class Block {
    public static final int AIR = 0;
    public static final int GRASS = 1;
    public static final int DIRT = 2;
    public static final int STONE = 3;
    public static final int BEDROCK = 4;
    public static final int WOOD = 5;
    public static final int LEAVES = 6;
    

    private static Texture[] textures;

    //once at the beginning 
    public static  void load(){
        textures = new Texture[7];
        textures[GRASS] = new Texture("grass.png");
        textures[DIRT] = new Texture("dirt.png");
        textures[STONE] = new Texture("stone.png");
        textures[BEDROCK] = new Texture("bedrock.png");
        textures[WOOD] = new Texture("wood.png");
        textures[LEAVES] = new Texture("leaves.png");

        for (int i = 1; i < textures.length; i++){
            textures[i].setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    public static void draw(SpriteBatch batch, int id, float x, float y, int size){
        if (id == AIR){
            return; // air will not be drawn
        }
        batch.draw(textures[id], x, y, size, size);
    }

    // checks if the block is close enough to the player
    public static boolean isInReach(Player player, int gridX, int gridY, int tileSize){
    float playerCenterX = player.playerX + tileSize / 2f;
    float playerCenterY = player.playerY + tileSize;   // the player is 2 blocks high
    float blockCenterX = (gridX + 0.5f) * tileSize;
    float blockCenterY = (gridY + 0.5f) * tileSize;

    float dx = blockCenterX - playerCenterX;
    float dy = blockCenterY - playerCenterY;
    float reach = GameData.REACH_BLOCKS * tileSize;
    return dx * dx + dy * dy <= reach * reach;
        }

    // checks if the block would be placed inside of the player
    private static boolean overlapsPlayer(Player player, int gridX, int gridY, int tileSize){
    return gridX * tileSize < player.playerX + tileSize
    && (gridX + 1) * tileSize > player.playerX
    && gridY * tileSize < player.playerY + 2 * tileSize
    && (gridY + 1) * tileSize > player.playerY;
    }

    public static void handleMouse(OrthographicCamera camera, int[][] worldGrid, int tileSize, int selectedBlock, Player player){
    int WORLD_WIDTH = GameData.WORLD_WIDTH;
    int WORLD_HEIGHT = GameData.WORLD_HEIGHT;
    if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        int gridX = (int) Math.floor(mousePos.x / tileSize);
        int gridY = (int) Math.floor(mousePos.y / tileSize);

        if (gridX >= 0 && gridX < WORLD_WIDTH && gridY >= 0 && gridY < WORLD_HEIGHT
            && isInReach(player, gridX, gridY, tileSize)
            && worldGrid[gridX][gridY] != BEDROCK) {
            worldGrid[gridX][gridY] = AIR; // breaks the block at the clicked position
        }
    }

    if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        int gridX = (int) Math.floor(mousePos.x / tileSize);
        int gridY = (int) Math.floor(mousePos.y / tileSize);

        if (gridX >= 0 && gridX < WORLD_WIDTH && gridY >= 0 && gridY < WORLD_HEIGHT
            && isInReach(player, gridX, gridY, tileSize)
            && worldGrid[gridX][gridY] == AIR
            && !overlapsPlayer(player, gridX, gridY, tileSize)) {
            worldGrid[gridX][gridY] = selectedBlock; // place Block
        }
    }
}

    public static void dispose(){
        for (int i = 1; i < textures.length; i++){
            if (textures[i] != null){
                textures[i].dispose();
            }

        }
    }

    
}
