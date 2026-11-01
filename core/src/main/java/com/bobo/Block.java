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
    

    private static Texture[] textures;

    //once at the beginning 
    public static  void load(){
        textures = new Texture[4];
        textures[GRASS] = new Texture("grass.png");
        textures[DIRT] = new Texture("dirt.png");
        textures[STONE] = new Texture("stone.png");

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

    public static void handleMouse(OrthographicCamera camera, int[][] worldGrid, int tileSize, int selectedBlock){
        int WORLD_WIDTH = GameData.WORLD_WIDTH;
        int WORLD_HEIGHT = GameData.WORLD_HEIGHT;
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);

            int gridX = (int) (mousePos.x / tileSize);
            int gridY = (int) (mousePos.y / tileSize);

            if (gridX >= 0 && gridX < WORLD_WIDTH && gridY >= 0 && gridY < WORLD_HEIGHT) {
                worldGrid[gridX][gridY] = AIR; // breaks the block at the clicked position
            }
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);

            int gridX = (int) (mousePos.x / tileSize);
            int gridY = (int) (mousePos.y / tileSize);

            if (gridX >= 0 && gridX < WORLD_WIDTH && gridY >= 0 && gridY < WORLD_HEIGHT) {
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
