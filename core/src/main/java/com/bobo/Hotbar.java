package com.bobo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

public class Hotbar {

    private int selected = 0; // index of the selected slot (0 - 8)

    private int slotSize = 44; // size of one slot in pixels
    private int gap = 5;       // space between the slots
    private int padding = 6;   // space between the slot border and the block picture

    // the hotbar is drawn on the screen and not in the world, so it needs its own matrix
    private Matrix4 uiMatrix = new Matrix4();

    // choose the slot with the keys 1 - 9
    public void update() {
        for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                selected = i;
            }
        }
    }

    // the slot, whose block will be placed with the right click
    public int getSelectedSlot() {
        return selected;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, Inventory inventory, BitmapFont font) {
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        uiMatrix.setToOrtho2D(0, 0, screenW, screenH);

        int count = Inventory.HOTBAR_SIZE;
        // the hotbar is centered at the bottom of the screen
        float totalWidth = count * slotSize + (count - 1) * gap;
        float startX = (screenW - totalWidth) / 2f;
        float y = 20;

        // 1. dark background of the slots
        shapeRenderer.setProjectionMatrix(uiMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
        for (int i = 0; i < count; i++) {
            shapeRenderer.rect(startX + i * (slotSize + gap), y, slotSize, slotSize);
        }
        shapeRenderer.end();

        // 2. the block pictures and the number of blocks
        batch.setProjectionMatrix(uiMatrix);
        batch.begin();
        for (int i = 0; i < count; i++) {
            if (inventory.getId(i) != Block.AIR) {
                float x = startX + i * (slotSize + gap);
                Block.draw(batch, inventory.getId(i), x + padding, y + padding, slotSize - 2 * padding);
                if (inventory.getAmount(i) > 1) {
                    Inventory.drawCount(font, batch, inventory.getAmount(i), x, y, slotSize);
                }
            }
        }
        batch.end();

        // 3. border of the slots, the selected one is white and thicker
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < count; i++) {
            float x = startX + i * (slotSize + gap);
            if (i == selected) {
                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.rect(x, y, slotSize, slotSize);
                shapeRenderer.rect(x + 1, y + 1, slotSize - 2, slotSize - 2);
            } else {
                shapeRenderer.setColor(Color.GRAY);
                shapeRenderer.rect(x, y, slotSize, slotSize);
            }
        }
        shapeRenderer.end();
    }
}