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
    public static final int COLS = 9;// Slots per row
    public static final int HOTBAR_SIZE = 9;// Slots 0 - 8 are the hotbar
    public static final int SIZE = 36;// Slots 9 - 35 are the main part (3 rows)
    public static final int MAX_STACK = 64;// Max blocks in one slot

    private int[] ids = new int[SIZE];// Place where a block is in the inventory
    private int[] amounts = new int[SIZE];// Ammounts of the blocks in the inventory
    public  int[] craftingInput = new int[4];
    public  int[] craftingAmounts = new int [4];
    public int craftingOutput = Block.AIR;
    private int craftingOutputAmount = 0;   // how many blocks come out

    private int heldId = Block.AIR;
    private int heldAmount = 0;

    private boolean open = false;

    // Layout of the panel in pixels
    private int slotSize = 40;
    private int gap = 4;// Space between the slots
    private int pad = 16;// Space between the panel border and the slots
    private int hotbarGap = 12;// Space between the main part and the hotbar row
    private int titleHeight = 24;
    private int panelW = COLS * slotSize + (COLS - 1) * gap + 2 * pad;
    private int panelH = 7 * slotSize + 2 * gap + hotbarGap + 2 * pad + titleHeight;

    private Matrix4 uiMatrix = new Matrix4();// Variable for the second layer for the inventory, that is not in the world

    private Crafting2 crafting = new Crafting2();


    // Blocks
    // A collected block lands here, returns false if the inventory is full
    public boolean add(int id) {// Not void, because it returns a boolean value at the end of the method
        if (id <= 0 || id >= Block.COUNT || id == Block.BEDROCK) {// Decides if the block can be collected
            return false;
        }
        for (int i = 0; i < SIZE; i++) {// A stack of the same block with free space
            if (ids[i] == id && amounts[i] < MAX_STACK) {// Looks in which slot the block is (if ids[2] == id, there is the searched block)
                amounts[i]++;
                return true;
            }
        }
        for (int i = 0; i < SIZE; i++) {// The first empty slot (the hotbar comes first)
            if (ids[i] == Block.AIR) { // Looks what the first empzy slot is
                ids[i] = id;// Says, the first empty slot is the brocken block
                amounts[i] = 1; // sets ammount of the block to 1
                return true;
            }
        }
        return false;
    }
     // Uses up one block of the slot (when placing)
    public void removeOne(int slot) {// Slot == the selected block in the hotbar
        if (amounts[slot] > 0) {
            amounts[slot]--;// After the placement of the block, one block is removed of the inventory
            if (amounts[slot] == 0) { // It the slot is empty
                ids[slot] = Block.AIR;// Set the slot to air
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

    // Open/close and mouse

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (open) {
                putHeldBack(); // When holding an item withe the mouse, while closing the inventory, the item gets bach in the inventory
            }
            open = !open;// Does the oposite
        }
        if (!open) {
            return;
        }
       //Output:
        craftingOutput = Block.AIR;
        craftingOutputAmount = 0;
        if (crafting.CraftingTable(craftingInput)) {
            craftingOutput = Block.CRAFTINGTABLE;   // result of the recipe
            craftingOutputAmount = 1;
        }
        if (crafting.OakPlanks(craftingInput)) {
            craftingOutput = Block.OAKPLANKS;   // result of the recipe
            craftingOutputAmount = 4;
        }
        if (crafting.Stick(craftingInput)) {
            craftingOutput = Block.SICK;   // result of the recipe
            craftingOutputAmount = 4;

        }

        boolean left = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        boolean right = Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT);
        if (!left && !right) {
            return;
        }

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Screen y goes down, our y goes up
        int slot = slotAt(mouseX, mouseY);
        if (slot < 0) { // Looks if you klick in a slot
            return;
        }
        if (slot == SIZE + 4) {
            takeOutput();// Click on the crafting output
            return;

        }

        if (left) {// If the left mouse is pressed
            leftClick(slot);// Methode leftClick is started
        } 
        else {
            rightClick(slot);// Method rightClick is started
        }
    }
    // Left click: take the whole stack, put it down or swap
    private void leftClick(int slot) {

        if (slot < 36){
            if (heldAmount == 0) {// Looks if the mouse is not holding anything
                // take the whole stack
                heldId = ids[slot];// Says, the id of the holding block
                heldAmount = amounts[slot];// Says the amount that was in the slot to the mouse
                ids[slot] = Block.AIR;// Removes the the block from it's old place
                amounts[slot] = 0;// Sets the old ammount to = 0
            }
            else if (ids[slot] == Block.AIR) {// When the mouse is holding something
                // put the whole stack down
                ids[slot] = heldId;// Gives the new solt the id of the block in the mouse
                amounts[slot] = heldAmount;// Gives the new slot the amount of the block in the mouse
                heldId = Block.AIR;// Sets the block amount holding the mouse to 0
                heldAmount = 0;
            }
            else if (ids[slot] == heldId) {// Same block: fill the stack up to 64
                int moved = Math.min(MAX_STACK - amounts[slot], heldAmount);
                amounts[slot] += moved;
                heldAmount -= moved;
                if (heldAmount == 0) {
                    heldId = Block.AIR;
                }
            }
            else {
            // other block: swap(swaps the ID and amount of the blocks)
            int tmpId = ids[slot];
            int tmpAmount = amounts[slot];
            ids[slot] = heldId;
            amounts[slot] = heldAmount;
            heldId = tmpId;
            heldAmount = tmpAmount;
            }

        }
        else if (slot >= 36 && slot <= 39) {
            int craftingIndex = slot - 36;
            if (heldAmount == 0) {
            // For crafting slots
            heldId = craftingInput[craftingIndex];
            heldAmount = craftingAmounts[craftingIndex];
            craftingInput[craftingIndex] = Block.AIR;
            craftingAmounts[craftingIndex] = 0;
            }

            else if (craftingInput[craftingIndex] == Block.AIR) {// When the mouse is holding something
                // For crafting slots
                craftingInput[craftingIndex] = heldId;
                craftingAmounts[craftingIndex] = heldAmount;
                heldId = Block.AIR;
                heldAmount = 0;
            }

            else if (craftingInput[craftingIndex] == heldId) {// Same block: fill the stack up to 64
                // For crafting slots
                int movedCraft = Math.min(MAX_STACK - craftingAmounts[craftingIndex], heldAmount);
                craftingAmounts[craftingIndex] += movedCraft;
                heldAmount -= movedCraft;
                if (heldAmount == 0) {
                    heldId = Block.AIR;
                }
            }
            
            else {
                // For crafting slots
                int tmpCraftId = craftingInput[craftingIndex];
                int tmpCraftAmount = craftingAmounts[craftingIndex];
                craftingInput[craftingIndex] = heldId;
                craftingAmounts[craftingIndex] = heldAmount;
                heldId = tmpCraftId;
                heldAmount = tmpCraftAmount;
            }
        }
    }

    // Right click: take half of the stack or put down one block
    // Right click: take half of the stack or put down one block
    private void rightClick(int slot) {
        if (slot < 36) {
            // Normales Inventar (0 bis 35)
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
        else if (slot >= 36 && slot <= 39) {
            // Crafting-Slots (36 bis 39)
            int craftingIndex = slot - 36;
            if (heldAmount == 0) {
                int half = (craftingAmounts[craftingIndex] + 1) / 2;
                if (half > 0) {
                    heldId = craftingInput[craftingIndex];
                    heldAmount = half;
                    craftingAmounts[craftingIndex] -= half;
                    if (craftingAmounts[craftingIndex] == 0) {
                        craftingInput[craftingIndex] = Block.AIR;
                    }
                }
            } else if (craftingInput[craftingIndex] == Block.AIR) {
                craftingInput[craftingIndex] = heldId;
                craftingAmounts[craftingIndex] = 1;
                heldAmount--;
                if (heldAmount == 0) {
                    heldId = Block.AIR;
                }
            } else if (craftingInput[craftingIndex] == heldId && craftingAmounts[craftingIndex] < MAX_STACK) {
                craftingAmounts[craftingIndex]++;
                heldAmount--;
                if (heldAmount == 0) {
                    heldId = Block.AIR;
                }
            }
        }
    }
    // Gives the stack from the hand back to the inventory
    private void putHeldBack() {
        while (heldAmount > 0 && add(heldId)) {
            heldAmount--;
        }
        heldId = Block.AIR;
        heldAmount = 0;
    }
    private void takeOutput() {
        if (craftingOutput == Block.AIR){
            return; // No recept found
        }
        if (heldAmount == 0){
            heldAmount = craftingOutputAmount;
            heldId = craftingOutput;
        }
        else if (heldId == craftingOutput && heldAmount <= MAX_STACK){
            heldAmount += craftingOutputAmount;
        }
        else{
            return; // Stack is full or not the same block ID
        }
        for (int i = 0; i < 4; i++) {
            if (craftingAmounts[i] > 0) {
                craftingAmounts[i]--;
                if (craftingAmounts[i] == 0) {
                    craftingInput[i] = Block.AIR;
                }
            }
        }
    }
    // Positions
    private float panelX() {// Bottom left x-Kordinate of the inventory
           return (Gdx.graphics.getWidth() - panelW) / 2f;
    }
    private float panelY() {// Bottom left y-Kordinate of the inventory
        return (Gdx.graphics.getHeight() - panelH) / 2f;
    }
    private float slotX(int index) {
    if (index == SIZE + 4) {
        return outputX();// Crafting output slot
    }
    if (index >= SIZE) {
        return craftingSlotX(index - SIZE);// Crafting slots are 36 - 39
    }
    return panelX() + pad + (index % COLS) * (slotSize + gap);
    }

    private float slotY(int index) {
    if (index == SIZE + 4) {
        return outputY();
    }
    if (index >= SIZE) {
        return craftingSlotY(index - SIZE);
    }
    if (index < COLS) {
        return panelY() + pad; // Hotbar row at the bottom
    }
    int row = (index - COLS) / COLS; // 0 = top row
    return panelY() + pad + slotSize + hotbarGap + (2 - row) * (slotSize + gap);
    }

    
    private float craftingSlotX(int index) {// Position of the 4 crafting input slots
        int col = index % 2; // 0 = left, 1 = right
        return panelX() + 200 + col * (slotSize + gap);
        }

        private float craftingSlotY(int index) {
            int row = index / 2; // 0 = above, 1 = below
            return panelY() + panelH - 65 - row * (slotSize + gap);
        }

        private float outputX() {// Position of the crafting output slot
            return panelX() + 220 + (2 * slotSize + gap) + 45;
        }

        private float outputY() {
            return panelY() + panelH - 65 - (slotSize / 2f) - (gap / 2f);
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
        for (int i = 0; i < 4; i++){
            float x = craftingSlotX(i);
            float y = craftingSlotY(i);
            if (mouseX >= x && mouseX <= x + slotSize && mouseY >= y && mouseY <= y + slotSize) {
                return 36 + i;
            }
        }
        if (mouseX >= outputX() && mouseX <= outputX() + slotSize && mouseY >= outputY() && mouseY <= outputY() + slotSize) {
            return SIZE + 4;// Crafting output slot
        }

        return -1;  
    }

    // Draw
    // The number in the corner of a slot (only for more than one block), also used by the hotbar
    public static void drawCount(BitmapFont font, SpriteBatch batch, int amount, float slotX, float slotY, int slotSize) {
        String text = "" + amount;// Converts in int string
        float x = slotX + slotSize - (amount >= 10 ? 19 : 11);// Looks the kordinates of the text are placen
        float y = slotY + 16;
        font.setColor(Color.BLACK);
        font.draw(batch, text, x + 1, y - 1);// Position of the shadow text
        font.setColor(Color.WHITE);
        font.draw(batch, text, x, y);// Text
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font) {
        if (!open) {
            return;
        }

        int screenW = Gdx.graphics.getWidth();// Gets the screenWidth
        int screenH = Gdx.graphics.getHeight();// Gets the screenHeight
        uiMatrix.setToOrtho2D(0, 0, screenW, screenH);// The inventory is another layer, not in the World
        float mouseX = Gdx.input.getX();
        float mouseY = screenH - Gdx.input.getY();
        int hover = slotAt(mouseX, mouseY);

        Gdx.gl.glEnable(GL20.GL_BLEND);// Trancperency
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Drak screen, grey panel slots and the highlighted slots under the mouse
        shapeRenderer.setProjectionMatrix(uiMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.6f);// Tracperent black
        shapeRenderer.rect(0, 0, screenW, screenH);// Draws over the whole game a trancperent rect
        shapeRenderer.setColor(0.78f, 0.78f, 0.78f, 1f);// Grey
        shapeRenderer.rect(panelX(), panelY(), panelW, panelH);// Draws the grey rect of the inventory
        shapeRenderer.setColor(0.55f, 0.55f, 0.55f, 1f);// Another grey
        for (int i = 0; i < SIZE; i++){
            shapeRenderer.rect(slotX(i), slotY(i), slotSize, slotSize);// Draws the inventory slots
        }
        if (hover >= 0) {// Slot under the mouse is highlited
            shapeRenderer.setColor(1f, 1f, 1f, 0.35f);
            shapeRenderer.rect(slotX(hover), slotY(hover), slotSize, slotSize);
        }
        shapeRenderer.end();

        // Draw crafting:
        shapeRenderer.setProjectionMatrix(uiMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.55f, 0.55f, 0.55f, 1f);
        for (int i = 0; i < 4; i++){
            // Input:
            int colum = i % 2;// 0 == left; 1 == right (% == Modulo)
            int row = i / 2;// 0 == above; 1 == below

            float xInput = panelX() + 200 + colum * (slotSize + gap);
            float yInput = panelY() + panelH - 65 - row * (slotSize + gap);

            shapeRenderer.rect(xInput, yInput, slotSize, slotSize);
            // Output:
            float craftingX = panelX() + 220;
            float xOutput = craftingX + (2 * slotSize + gap) + 45;
            float yOutput = panelY() + panelH - 65 - (slotSize / 2f) - (gap / 2f);
            shapeRenderer.rect(xOutput, yOutput, slotSize, slotSize);
        }
        shapeRenderer.end();
        
        //borders
        shapeRenderer.setProjectionMatrix(uiMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.6f);// Tracperent black
        shapeRenderer.rect(0, 0, screenW, screenH);// Draws over the whole game a trancperent rect
        shapeRenderer.setColor(0.78f, 0.78f, 0.78f, 1f);// Grey
        shapeRenderer.rect(panelX(), panelY(), panelW, panelH);// Draws the grey rect of the inventory
        shapeRenderer.setColor(0.55f, 0.55f, 0.55f, 1f);// Another grey
        for (int i = 0; i < SIZE + 4; i++){
            shapeRenderer.rect(slotX(i), slotY(i), slotSize, slotSize);// Draws the inventory slots and the 4 crafting slots
        }
        shapeRenderer.rect(outputX(), outputY(), slotSize, slotSize);// Draws the crafting output slot
        if (hover >= 0) {// Slot under the mouse is highlited
            shapeRenderer.setColor(1f, 1f, 1f, 0.35f);
            shapeRenderer.rect(slotX(hover), slotY(hover), slotSize, slotSize);
        }
        shapeRenderer.end();

        // Title, blockpicture, numbers and stack in the hand
        batch.setProjectionMatrix(uiMatrix);
        batch.begin();
        font.setColor(0.25f, 0.25f, 0.25f, 1f);
        font.draw(batch, "Inventory", panelX() + pad, panelY() + panelH - 8);// Draws Text Inventory at the top left

        for (int i = 0; i < SIZE; i++) {// Draws the textures and amont of the blocks in the inventory
            if (ids[i] != Block.AIR) {
                Block.draw(batch, ids[i], slotX(i) + 4, slotY(i) + 4, slotSize - 8);
                if (amounts[i] > 1) {
                    drawCount(font, batch, amounts[i], slotX(i), slotY(i), slotSize);
                }
            }
        }
        // For crafting
        for (int i = 0; i < 4; i++) {// Draws the textures and amont of the blocks in the crafting
            if (craftingInput[i] != Block.AIR) {
                Block.draw(batch, craftingInput[i], craftingSlotX(i) + 4, craftingSlotY(i) + 4, slotSize - 8);
                if (craftingAmounts[i] > 1) {
                    drawCount(font, batch, craftingAmounts[i], craftingSlotX(i), craftingSlotY(i), slotSize);
                }
            }
        }
        //Output:
        if (craftingOutput != Block.AIR) {// Draws the result of the recipe in the output slot
            Block.draw(batch, craftingOutput, outputX() + 4, outputY() + 4, slotSize - 8);
            if (craftingOutputAmount > 1) {
                drawCount(font, batch, craftingOutputAmount, outputX(), outputY(), slotSize);
            }
        }

        if (heldAmount > 0) {// Blocks follow mouse, texture and amount is drawn
            Block.draw(batch, heldId, mouseX - 16, mouseY - 16, 32);
            if (heldAmount > 1){
                drawCount(font, batch, heldAmount, mouseX - 20, mouseY - 20, 40);
            }
        }
        batch.end();
    }

}