package com.bobo;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Player {

    float playerSpeed = 200f; // Speed in pixels per second

    boolean facingRight = true; // true = looks right


    float velocityY = GameData.velocityY; //vertical speed of the player
    float gravity = GameData.gravity; //gravity strength (negative value to pull down)

    int WORLD_HEIGHT = GameData.WORLD_HEIGHT;
    int WORLD_WIDTH = GameData.WORLD_WIDTH;

    int tileSize = GameData.tileSize;
    public float playerX = (GameData.WORLD_WIDTH / 2) * tileSize;
    public float playerY = GameData.groundLevel + tileSize; // Position the player on the ground

    //size of the Player:
    public int width = 24;
    public int height = 48;

    private Texture texture;

    public Player(float playerX, float playerY) {
        this.playerX = playerX;
        this.playerY = playerY;
        
        texture = new Texture("Player.png"); //picture of the player
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); 
    }

    //movement
    public void update(float deltaTime, int[][] worldGrid, int worldHeight, int tileSize) {

        // key binds for moving left and right

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerX -= playerSpeed * deltaTime;
            facingRight = false;
            int leftGridX = (int) (playerX / tileSize);
            int bottomGridY = (int) (playerY / tileSize);
            int topGridY = (int) ((playerY + 2 * tileSize - 1) / tileSize);
            if (leftGridX >= 0 && leftGridX < WORLD_WIDTH) {
                if ((bottomGridY >= 0 && bottomGridY < WORLD_HEIGHT && worldGrid[leftGridX][bottomGridY] != 0) ||
                    (topGridY >= 0 && topGridY < WORLD_HEIGHT && worldGrid[leftGridX][topGridY] != 0)) {
                    playerX = (leftGridX + 1) * tileSize;
                }
            }
        }


        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerX += playerSpeed * deltaTime;
            facingRight = true;
            int rightGridX = (int) ((playerX + tileSize) / tileSize);
            int bottomY = (int) (playerY / tileSize);
            int topY = (int) ((playerY + 2 * tileSize - 1) / tileSize);
            if (rightGridX >= 0 && rightGridX < WORLD_WIDTH) {
                if ((bottomY >= 0 && bottomY < WORLD_HEIGHT && worldGrid[rightGridX][bottomY] != 0) ||
                    (topY >= 0 && topY < WORLD_HEIGHT && worldGrid[rightGridX][topY] != 0)) {
                    playerX = rightGridX * tileSize - tileSize;
                }
            }
        }


        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            playerSpeed = 100f;
        }
        else {
            playerSpeed = 200f;
        }

        int footGridX1 = (int) (playerX / tileSize);
        int footGridX2 = (int) ((playerX + tileSize - 1) / tileSize);
        int footGridY = (int) ((playerY - 1) / tileSize);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
        int groundCheckY = (int) ((playerY - 1) / tileSize);
            if (footGridX1 >= 0 && footGridX1 < WORLD_WIDTH && groundCheckY >= 0 && groundCheckY < WORLD_HEIGHT) {
                if (worldGrid[footGridX1][groundCheckY] != 0) {
                    velocityY = (float) Math.sqrt(2 * Math.abs(gravity) * (1.4f * tileSize));
                }
            }
        }

        velocityY += gravity * deltaTime;
        playerY += velocityY * deltaTime;

        if (velocityY <= 0 && footGridY >= 0 && footGridY < WORLD_HEIGHT) {
            if ((footGridX1 >= 0 && footGridX1 < WORLD_WIDTH && worldGrid[footGridX1][footGridY] != 0) ||
                (footGridX2 >= 0 && footGridX2 < WORLD_WIDTH && worldGrid[footGridX2][footGridY] != 0)) {
                playerY = (footGridY + 1) * tileSize; 
                velocityY = 0; 
            }
        }
            


        if (velocityY > 0) {
            int headGridX1 = (int) (playerX / tileSize);
            int headGridX2 = (int) ((playerX + tileSize - 1) / tileSize);
            int headGridY = (int) ((playerY + 2 * tileSize - 1) / tileSize);

            if (headGridY >= 0 && headGridY < WORLD_HEIGHT) {
                if ((headGridX1 >= 0 && headGridX1 < WORLD_WIDTH && worldGrid[headGridX1][headGridY] != 0) ||
                    (headGridX2 >= 0 && headGridX2 < WORLD_WIDTH && worldGrid[headGridX2][headGridY] != 0)) {
                    playerY = headGridY * tileSize - 2 * tileSize;
                    velocityY = 0;
                }
            }
        }
        // the player can't leave the world on the left or right side
        if (playerX < 0) {
            playerX = 0;
        }
        if (playerX > (WORLD_WIDTH - 1) * tileSize) {
            playerX = (WORLD_WIDTH - 1) * tileSize;
        }
    }

    //draw
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, playerX, playerY, width, height,
           0, 0, texture.getWidth(), texture.getHeight(),
           !facingRight, false);
        }
        //shapeRenderer.setColor(Color.BLACK);
        //shapeRenderer.rect(playerX, playerY, tileSize,2 * tileSize);

    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}