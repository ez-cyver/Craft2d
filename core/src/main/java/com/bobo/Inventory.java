package com.bobo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

public class Inventory {
    public static final int COLS = 9;// slots per row
    public static final int HOTBAR_SIZE = 9;// slots 0 - 8 are the hotbar
    public static final int SIZE = 36;// slots 9 - 35 are the main part (3 rows)
    public static final int MAX_STACK = 64;// max blocks in one slot

    private int[] ids = new int[SIZE];
    private int[] amounts = new int[SIZE];

    private int heldId = Block.AIR;
    private int heldAmount = 0;

    private boolean open = false;

    // layout of the panel in pixels
    private int slotSize = 40;
    private int gap = 4;// space between the slots
    private int pad = 16;// space between the panel border and the slots
    private int hotbarGap = 12;// space between the main part and the hotbar row
    private int titleHeight = 24;
    private int panelW = COLS * slotSize + (COLS - 1) * gap + 2 * pad;
    private int panelH = 4 * slotSize + 2 * gap + hotbarGap + 2 * pad + titleHeight;

    // the inventory is drawn on the screen and not in the world
    private Matrix4 uiMatrix = new Matrix4();


    //blocks
    // a collected block lands here, returns false if the inventory is full
    public boolean add(int id) {
        if (id <= 0 || id >= Block.COUNT || id == Block.BEDROCK) {
            return false;
        }
        // 1. a stack of the same block with free space
        for (int i = 0; i < SIZE; i++) {
            if (ids[i] == id && amounts[i] < MAX_STACK) {
                amounts[i]++;
                return true;
            }
        }
        // 2. the first empty slot (the hotbar comes first)
        for (int i = 0; i < SIZE; i++) {
            if (ids[i] == Block.AIR) {
                ids[i] = id;
                amounts[i] = 1;
                return true;
            }
        }
        return false;
    }
     // uses up one block of the slot (when placing)
    public void removeOne(int slot) {
        if (amounts[slot] > 0) {
            amounts[slot]--;
            if (amounts[slot] == 0) {
                ids[slot] = Block.AIR;
            }
        }
    }
    public int getId(int slot) {
        return ids[slot];
    }

    public int getAmount(int slot) {
        return amounts[slot];
    }

    public boolean isOpen() {
        return open;
    }

    //open/close and mouse

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (open) {
                putHeldBack(); // closing: the stack in the hand goes back into the inventory
            }
            open = !open;
        }
        if (!open) {
            return;
        }
        boolean left = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        boolean right = Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT);
        if (!left && !right) {
            return;
        }

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY(); // screen y goes down, our y goes up
        int slot = slotAt(mouseX, mouseY);
        if (slot < 0) {
            return;
        }

        if (left) {
            leftClick(slot);
        } else {
            rightClick(slot);
        }
    }
    // left click: take the whole stack, put it down or swap
    private void leftClick(int slot) {
        if (heldAmount == 0) {
            // take the whole stack
            heldId = ids[slot];
            heldAmount = amounts[slot];
            ids[slot] = Block.AIR;
            amounts[slot] = 0;
        } else if (ids[slot] == Block.AIR) {
            // put the whole stack down
            ids[slot] = heldId;
            amounts[slot] = heldAmount;
            heldId = Block.AIR;
            heldAmount = 0;
        } else if (ids[slot] == heldId) {
            // same block: fill the stack up to 64
            int moved = Math.min(MAX_STACK - amounts[slot], heldAmount);
            amounts[slot] += moved;
            heldAmount -= moved;
            if (heldAmount == 0) {
                heldId = Block.AIR;
            }
        } else {
            // other block: swap
            int tmpId = ids[slot];
            int tmpAmount = amounts[slot];
            ids[slot] = heldId;
            amounts[slot] = heldAmount;
            heldId = tmpId;
            heldAmount = tmpAmount;
        }
    }

    // right click: take half of the stack or put down one block
    private void rightClick(int slot) {
        if (heldAmount == 0) {
            int half = (amounts[slot] + 1) / 2;
            if (half > 0) {
                heldId = ids[slot];
                heldAmount = half;
                amounts[slot] -= half;
                if (amounts[slot] == 0) {
                    ids[slot] = Block.AIR;
                }
            }
        } else if (ids[slot] == Block.AIR) {
            ids[slot] = heldId;
            amounts[slot] = 1;
            heldAmount--;
            if (heldAmount == 0) {
                heldId = Block.AIR;
            }
        } else if (ids[slot] == heldId && amounts[slot] < MAX_STACK) {
            amounts[slot]++;
            heldAmount--;
            if (heldAmount == 0) {
                heldId = Block.AIR;
            }
        }
    }
    // gives the stack from the hand back to the inventory
    private void putHeldBack() {
        while (heldAmount > 0 && add(heldId)) {
            heldAmount--;
        }
        heldId = Block.AIR;
        heldAmount = 0;
    }
    //positions
    private float panelX() {
        return (Gdx.graphics.getWidth() - panelW) / 2f;
    }
    private float panelY() {
        return (Gdx.graphics.getHeight() - panelH) / 2f;
    }
    private float slotX(int index) {
        return panelX() + pad + (index % COLS) * (slotSize + gap);
    }
    private float slotY(int index) {
        if (index < COLS) {
            return panelY() + pad; // hotbar row at the bottom
        }
        int row = (index - COLS) / COLS; // 0 = top row
        return panelY() + pad + slotSize + hotbarGap + (2 - row) * (slotSize + gap);
    }


    // which slot is under the mouse? -1 = none
    private int slotAt(float mouseX, float mouseY){
        for (int i = 0; i < SIZE; i++){
            float x = slotX(i);
            float y = slotY(i);
            if (mouseX >= x && mouseX <= x + slotSize && mouseY >= y && mouseY <= y + slotSize) {
                return i;
            }
        }
        return -1;
    }
    //draw
    // the number in the corner of a slot (only for more than one block), also used by the hotbar
    public static void drawCount(BitmapFont font, SpriteBatch batch, int amount, float slotX, float slotY, int slotSize) {
        String text = "" + amount;
        float x = slotX + slotSize - (amount >= 10 ? 19 : 11);
        float y = slotY + 16;
        font.setColor(Color.BLACK);
        font.draw(batch, text, x + 1, y - 1);// shadow
        font.setColor(Color.WHITE);
        font.draw(batch, text, x, y);
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font) {
        if (!open) {
            return;
        }

        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        uiMatrix.setToOrtho2D(0, 0, screenW, screenH);
        float mouseX = Gdx.input.getX();
        float mouseY = screenH - Gdx.input.getY();
        int hover = slotAt(mouseX, mouseY);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        //drak screen, grey panel slots and the highlighted slots under the mouse
        shapeRenderer.setProjectionMatrix(uiMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.6f);
        shapeRenderer.rect(0, 0, screenW, screenH);
        shapeRenderer.setColor(0.78f, 0.78f, 0.78f, 1f);
        shapeRenderer.rect(panelX(), panelY(), panelW, panelH);
        shapeRenderer.setColor(0.55f, 0.55f, 0.55f, 1f);
        for (int i = 0; i < SIZE; i++){
            shapeRenderer.rect(slotX(i), slotY(i), slotSize, slotSize);
        }
        if (hover >= 0) {
            shapeRenderer.setColor(1f, 1f, 1f, 0.35f);
            shapeRenderer.rect(slotX(hover), slotY(hover), slotSize, slotSize);
        }
        shapeRenderer.end();



        //borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(panelX(), panelY(), panelW, panelH);
        shapeRenderer.setColor(0.35f, 0.35f, 0.35f, 1f);
        for (int i = 0; i < SIZE; i++) {
            shapeRenderer.rect(slotX(i), slotY(i), slotSize, slotSize);
        }
        shapeRenderer.end();


        //title, blockpicture, numbers and stack in the hand
        batch.setProjectionMatrix(uiMatrix);
        batch.begin();
        font.setColor(0.25f, 0.25f, 0.25f, 1f);
        font.draw(batch, "Inventar", panelX() + pad, panelY() + panelH - 8);

        for (int i = 0; i < SIZE; i++) {
            if (ids[i] != Block.AIR) {
                Block.draw(batch, ids[i], slotX(i) + 4, slotY(i) + 4, slotSize - 8);
                if (amounts[i] > 1) {
                    drawCount(font, batch, amounts[i], slotX(i), slotY(i), slotSize);
                }
            }
        }
        if (heldAmount > 0) {
            Block.draw(batch, heldId, mouseX - 16, mouseY - 16, 32);
            if (heldAmount > 1){
                drawCount(font, batch, heldAmount, mouseX - 20, mouseY - 20, 40);
            }
        }
        batch.end();
    }

}