package com.bobo;

public class GameData {
    //all constants are in this class saved
    public static final int originalTileSize = 8; // Size of the player square in pixels
    public static final int scale = 3; // Scale factor for the player square (1pixel -> 3 pixel)
    public static final int tileSize = originalTileSize * scale; // Size of the player square
    public static final int GROUND_HEIGHT_BLOCKS = 60; // Height of the ground in Blocks                            <-Wolrd height is 60, so the ground will be at 10 blocks from the bottom


    public static final int WORLD_WIDTH = 6000; // Width of the game world in Blocks
    public static final int WORLD_HEIGHT = 100; // Height of the game world in Blocks

    public static final int VIEWPORT_WIDTH_BLOCKS = 60; // Width of the viewport in Blocks
    public  static final int VIEWPORT_HEIGHT_BLOCKS = 40; // Height of the viewport in Blocks

    public static float velocityY = 0; //vertical speed of the player
    public static float gravity =  -900f; //gravity strength (negative value to pull down)
    public static float groundLevel = GROUND_HEIGHT_BLOCKS * tileSize; // Calculate the ground level based on the floor position and height
    public static final float REACH_BLOCKS = 5; // how far the player can reach in blocks


    public static float playerSpeed = 200f; // Speed in pixels per second

}