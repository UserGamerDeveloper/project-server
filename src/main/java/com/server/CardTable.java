package com.server;

class CardTable {

    private byte mIdInArray;
    private int mValueTwo;
    static float sGearScoreRangeRate;

    void Copy(CardTable card){
        this.mValueTwo = card.getValueTwo();
    }

    int getValueTwo(){
        return mValueTwo;
    }
    void setValueTwo(int damage){
        this.mValueTwo = damage;
    }

    public byte getIdInArray() {
        return mIdInArray;
    }
    public void setIdInArray(byte idInArray) {
        mIdInArray = idInArray;
    }

    public static float getGearScoreRangeRate() {
        return sGearScoreRangeRate;
    }
    public static void setGearScoreRangeRate(float gearScoreRangeRate) {
        sGearScoreRangeRate = gearScoreRangeRate;
    }
}