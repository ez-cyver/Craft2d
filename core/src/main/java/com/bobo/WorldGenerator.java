package com.bobo;

import java.util.Random;

public class WorldGenerator {

    // Stores the ground surface height for each column (used for spawning and trees)
    private static int[] surfaceHeights;

    // creates the whole world, same seed = same world
    public static void generate(int[][] worldGrid, long seed) {
        Random random = new Random (seed);// Initialize a random number generator using the given seed
        generateTerrain(worldGrid, random);// Generate the terrain (hills, bumps, bedrock, dirt, grass, and stone layers)
        generateTrees(worldGrid, random);// Generate and place trees on top of the surface
    }

    //height of the ground at the column x, needed for the spwwn of the player
    public static int getSurfaceHeight(int x) {
        return surfaceHeights[x];// Look up and return the height at index x
    }

    //terrain:hills and layers
    private static void generateTerrain(int[][] worldGrid, Random random) {
        int width = worldGrid.length;
        int height = worldGrid[0].length;
        surfaceHeights = new int[width];

        //two layers of random points big hills and small bumps
        float[] hills = createPoints(width, 50, random);
        float[] bumps = createPoints(width, 16, random);

        for (int x = 0; x <width; x++) {
            float h = GameData.GROUND_HEIGHT_BLOCKS 
                + (sample(hills, x, 50) - 0.5f) * 2 * 8    // hills: up to 8 blocks up or down
                + (sample(bumps, x, 16) - 0.5f) * 2 * 2;   // bumps: up to 2 blocks

            int surface = Math.round(h);
            surface = Math.max(10, Math.min(height - 25, surface));
            surfaceHeights[x] = surface;

            for (int y = 0; y <surface; y++){
                if (y == 0){
                    worldGrid[x][y] = Block.BEDROCK;// bottom layer
                }
                else if (y == surface -1){
                    worldGrid[x][y] = Block.GRASS;// top layer
                }
                else if (y >= surface -5){
                    worldGrid[x][y] = Block.DIRT;// 4 layers of dirt below
                }
                else{
                    worldGrid[x][y] = Block.STONE;// everything below 
                }

            }
        }
    }
 // random values, one point every "wavelength" columns
    private static float[] createPoints(int width, int wavelength, Random random) {
        float[] points = new float[width / wavelength + 2];
        for (int i = 0; i < points.length; i++) {
            points[i] = random.nextFloat();
        }
        return points;
    }
    private static float sample(float[] points, int x, int wavelength) {
        float t = x / (float) wavelength;
        int i = (int) t;
        float f = t - i;
        f = f * f * (3 - 2 * f);
        return points[i] * (1 - f) + points[i + 1] * f;
    }

    //trees
    private static void generateTrees(int[][] worldGrid, Random random) {
        int width = worldGrid.length;
        int spawnX = width / 2;
        int lastTreeX = -100;
        for (int x = 4; x < width - 4; x++) {
            if (Math.abs(x - spawnX) < 4) {
                continue; // no trees near the spawn point
            }
            if (x - lastTreeX < 5) {
                continue; // trees need some space
            }
            if (random.nextFloat() < 0.08f){ //8% chance for a tree
                placeTree(worldGrid, x, surfaceHeights[x], random);
                lastTreeX = x;
                }
            }
        }

        
        private static void placeTree(int[][] worldGrid, int x, int groundY, Random random ){
            int trunkHeight = 4 + random.nextInt(3); // 4-6 blocks high
            int top = groundY + trunkHeight;
            
            //trunk
            for (int y = groundY; y < top; y++){
                setIfAir(worldGrid, x, y, Block.WOOD);
            }
        //leaves: 2 wide layers (5 blocks) and 2 narrow layers (3 blocks) on top
        for (int dy = -2; dy <= 1; dy++){
            int radius = (dy <= -1) ? 2 : 1;
            for (int dx = -radius; dx <= radius; dx++){
                setIfAir(worldGrid, x + dx, top + dy, Block.LEAVES);
            }
        }
    }
    //sets the block only if the field is inside of the world and empty
    private static void setIfAir(int[][] worldGrid, int x, int y, int id){
        if (x >= 0 && x < worldGrid.length && y >= 0 && y < worldGrid[0].length && worldGrid[x][y] == Block.AIR){
            worldGrid[x][y] = id;
        }
    }
}