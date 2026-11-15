package com.bobo;

public class Crafting2 {

    // Craftingtable:
    public boolean CraftingTable(int[] craftingInput) {
        if(craftingInput[0] == 7 && craftingInput[1] == 7 && craftingInput[2] == 7 && craftingInput[3] == 7 ){
            return true;
        }
        else{
            return false;

        }

    }
    public boolean OakPlanks(int[] craftingInput) {
        int wood = 0;
        if (craftingInput[0] == Block.WOOD) {wood++;}
        if (craftingInput[1] == Block.WOOD) {wood++;}
        if (craftingInput[2] == Block.WOOD) {wood++;}
        if (craftingInput[3] == Block.WOOD) {wood++;}
        if (wood == 1) {
            return true;
        }
        else {
            return false;
        }
    }
    public boolean Stick(int[] craftingInput) {
        int plank = 0;
        if (craftingInput[0] == Block.OAKPLANKS) {plank++;}
        if (craftingInput[1] == Block.OAKPLANKS) {plank++;}
        if (craftingInput[2] == Block.OAKPLANKS) {plank++;}
        if (craftingInput[3] == Block.OAKPLANKS) {plank++;}

        if (craftingInput[0] == Block.OAKPLANKS && craftingInput[2] == Block.OAKPLANKS && plank == 2){
            return true;
        }
        else if (craftingInput[1] == Block.OAKPLANKS && craftingInput[3] == Block.OAKPLANKS && plank == 2){
            return true;
        }
        else{
            return false;
        }


    }
}
