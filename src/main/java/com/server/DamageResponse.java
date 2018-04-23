package com.server;

public class DamageResponse {
    public String getLoot() {
        return mLoot;
    }
    public void setLoot(String loot) {
        mLoot = loot;
    }
    public short[] getCardTableID() {
        return cardTableID;
    }
    public void setCardTableID(short[] cardTableID) {
        this.cardTableID = cardTableID;
    }

    private String mLoot;
    private short[] cardTableID;
}