package com.server;

import java.util.ArrayList;

public class UseSpellRequest {
    ArrayList<CardInventory> mInventory;
    byte mSlotSpell;

    public ArrayList<CardInventory> getInventory() {
        return mInventory;
    }

    public void setInventory(ArrayList<CardInventory> inventory) {
        mInventory = inventory;
    }

    public byte getSlotSpell() {
        return mSlotSpell;
    }

    public void setSlotSpell(byte slotSpell) {
        mSlotSpell = slotSpell;
    }

}