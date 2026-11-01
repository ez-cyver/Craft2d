package com.bobo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

public class Hotbar {
    // the blocks in the slots (key 1, 2, 3)
    private int[] slots = { Block.GRASS, Block.DIRT, Block.STONE,};
    private int selectedBlock = 1; // index of the selected slot, 1 = dirt at the beginning

    private int slotSize = 48; //Size of the slot in pixels
    private int gap = 6; //Gap between the slots in pixels
    private int padding = 6; //space between slot border and block picture

    //draw the hotbar, but on the screen, not in the world
    private Matrix4 uiMatrix = new  Matrix4();


    //select the block in the hotbar
    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            selectedBlock = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)){
            selectedBlock = 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)){
            selectedBlock = 2;
        }
    }
    //block, that will be placed with the right click
    public int getSelectedBlock() {
        return slots[selectedBlock];
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        uiMatrix.setToOrtho2D(0, 0, screenWidth, screenHeight);

        //place the hotbar at the center at the bottom of the screen
        float totalWidth = slots.length * slotSize + (slots.length -1) * gap;
        float startX = (screenWidth - totalWidth) / 2f;
        float y = 20; //20 pixels from the bottom of the screen

        //dark background for the hotbar
        shapeRenderer.setProjectionMatrix(uiMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < slots.length; i++) {
            shapeRenderer.rect(startX + i * (slotSize + gap), y, slotSize, slotSize);
        }
        shapeRenderer.end();

        // the block pictures in te hotbar
        batch.setProjectionMatrix(uiMatrix);
        batch.begin();
        for (int i = 0; i < slots.length; i++) {
            float x = startX + i * (slotSize + gap);
            Block.draw(batch, slots[i], x + padding, y + padding, slotSize - 2 * padding);
        }
        batch.end();

        // border of the slots, the selected slot is highlighted
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < slots.length; i++) {
            float x = startX + i * (slotSize + gap);
            if (i == selectedBlock) {
                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.rect(x, y, slotSize, slotSize);
                shapeRenderer.rect(x + 1, y + 1, slotSize - 2, slotSize - 2);
            } 
            else {
                shapeRenderer.setColor(Color.GRAY);
                shapeRenderer.rect(x, y, slotSize, slotSize);
            }
        }
        shapeRenderer.end();
    }
}