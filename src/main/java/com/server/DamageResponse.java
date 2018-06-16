package com.server;

public class DamageResponse {
    public String getLoot() {
        return mLoot;
    }
    public void setLoot(String loot) {
        mLoot = loot;
    }
    public Mob[] getNextMobs() {
        return mNextMobs;
    }
    public void setNextMobs(Mob[] nextMobs) {
        this.mNextMobs = nextMobs;
    }

    private String mLoot;
    private Mob[] mNextMobs;
}