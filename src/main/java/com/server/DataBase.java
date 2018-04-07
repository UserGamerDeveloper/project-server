package com.server;

import java.util.Hashtable;

public final class DataBase {
    final static Hashtable<Byte, Mob> MOBS = new Hashtable<>();
    final static Hashtable<Byte, Item> ITEMS = new Hashtable<>();

    static {
        MOBS.put((byte)0, new Mob(0,0,0, CardTableType.VENDOR,9999, CardTableSubType.TRADER, 0));
        ITEMS.put((byte)0, new Item(1,1, InventoryType.SPELL,1,1,0,(byte)0));
    }

    static class Mob {
        private int mValueOne;
        private int mValueTwo;
        private int mMoney;
        private int mType;
        private int mGearScore;
        private int mSubType;
        private int mExperience;

        public int getValueOne() {
            return mValueOne;
        }
        public int getValueTwo() {
            return mValueTwo;
        }
        public int getMoney() {
            return mMoney;
        }
        public int getType() {
            return mType;
        }
        public int getGearScore() {
            return mGearScore;
        }
        public int getSubType() {
            return mSubType;
        }
        public int getExperience() {
            return mExperience;
        }

        Mob(int valueOne, int valueTwo, int money, int type, int gearscore, int subType, int experience) {
            mValueOne = valueOne;
            mValueTwo = valueTwo;
            mMoney = money;
            mType = type;
            mGearScore = gearscore;
            mSubType = subType;
            mExperience = experience;
        }
    }

    static class Item {
        private int mValueOne;
        private int mCost;
        private int mType;
        private int mGearScore;
        private int mExperience;
        private int mMobGearScore;
        private byte mDurability;

        public int getValueOne() {
            return mValueOne;
        }
        public int getCost() {
            return mCost;
        }
        public int getType() {
            return mType;
        }
        public int getGearScore() {
            return mGearScore;
        }
        public int getExperience() {
            return mExperience;
        }
        public int getMobGearScore() {
            return mMobGearScore;
        }
        public byte getDurability() {
            return mDurability;
        }

        Item(int valueOne, int cost, int type, int gearScore, int experience, int mobGearScore, byte durability) {
            mValueOne = valueOne;
            mCost = cost;
            mType = type;
            mGearScore = gearScore;
            mExperience = experience;
            mMobGearScore = mobGearScore;
            mDurability = durability;
        }
    }
}
