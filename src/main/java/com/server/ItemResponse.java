package com.server;

public class ItemResponse {
    private byte mID;
    private byte mValueOne;
    private int mType;
    private byte mDurabilityMax;
    private byte mGearScore;
    private int mMobGearScore;
    private int mCost;
    private int mDurability;
    private byte mSlotId;

    ItemResponse(CardPlayer itemState) {
        mID = itemState.getIdItem();
        mDurability = itemState.getDurability();
        mSlotId = itemState.getSlotId();
        Item item = DataBase.getItems().get(mID);
        mValueOne = item.getValueOne();
        mType = item.getType();
        mDurabilityMax = item.getDurabilityMax();
        mGearScore = item.getGearScore();
        mMobGearScore = item.getMobGearScore();
        mCost = item.getBuyCost();
    }

    public byte getSlotId() {
        return mSlotId;
    }

    public void setSlotId(byte slotId) {
        mSlotId = slotId;
    }
    public byte getID() {
        return mID;
    }
    public byte getValueOne() {
        return mValueOne;
    }
    public int getType() {
        return mType;
    }
    public byte getGearScore() {
        return mGearScore;
    }
    public int getMobGearScore() {
        return mMobGearScore;
    }
    public byte getDurabilityMax() {
        return mDurabilityMax;
    }
    boolean isWeaponOrShield(){
        return mType == InventoryType.SHIELD || mType == InventoryType.WEAPON;
    }
    public void setID(byte ID) {
        mID = ID;
    }

    public void setValueOne(byte valueOne) {
        mValueOne = valueOne;
    }

    public void setType(int type) {
        mType = type;
    }

    public void setDurabilityMax(byte durabilityMax) {
        mDurabilityMax = durabilityMax;
    }

    public void setGearScore(byte gearScore) {
        mGearScore = gearScore;
    }

    public void setMobGearScore(int mobGearScore) {
        mMobGearScore = mobGearScore;
    }

    public int getCost() {
        return mCost;
    }

    public void setCost(int cost) {
        mCost = cost;
    }

    public int getDurability() {
        return mDurability;
    }

    public void setDurability(int durability) {
        mDurability = durability;
    }
}
