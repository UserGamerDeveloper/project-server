package com.server;

import java.util.ArrayList;
import java.util.Hashtable;

public final class DataBase {
    final static ArrayList<Byte> VENDOR_ID = new ArrayList<>(3);
    final static Hashtable<Byte, Mob> MOBS = new Hashtable<>();
    final static Hashtable<Byte, Item> ITEMS = new Hashtable<>();

    static {
        VENDOR_ID.add((byte) 0);
        VENDOR_ID.add((byte) 1);
        VENDOR_ID.add((byte) 2);
        MOBS.put((byte)4, new Mob((byte) 4,1, (byte) 1,1, CardTableType.MOB, 0, CardTableSubType.GOBLIN, 1));
        MOBS.put((byte)3, new Mob((byte) 3,4, (byte) 4,4, CardTableType.MOB, 0, CardTableSubType.GOBLIN, 4));
        MOBS.put((byte)0, new Mob((byte) 0,0, (byte) 0,0, CardTableType.VENDOR, 9999, CardTableSubType.TRADER, 0));
        MOBS.put((byte)1, new Mob((byte) 1,0, (byte) 0,0, CardTableType.VENDOR, 9999, CardTableSubType.BLACKSMITH, 0));
        MOBS.put((byte)2, new Mob((byte) 2,0, (byte) 0,0, CardTableType.VENDOR, 9999, CardTableSubType.INNKEEPER, 0));
        MOBS.put((byte)8, new Mob((byte) 8,0, (byte) 0,1, CardTableType.CHEST, 9999, 0, 0));
        ITEMS.put((byte)0, new Item((byte) 0,(byte) 1,0, InventoryType.WEAPON, (byte) 0,999,(byte)0));
        ITEMS.put((byte)12, new Item((byte) 12,(byte) 2,2, InventoryType.WEAPON, (byte) 2,0,(byte)10));
        ITEMS.put((byte)7, new Item((byte) 7,(byte) 2,2, InventoryType.SHIELD, (byte) 2,0,(byte)10));
        ITEMS.put((byte)1, new Item((byte) 1,(byte) 1,0, InventoryType.FOOD, (byte) 1,0,(byte)0));
        ITEMS.put((byte)16, new Item((byte) 16,(byte) 1,1, InventoryType.SPELL, (byte) 1,0,(byte)0));
    }

    static class Mob {
        private byte mID;
        private int mValueOne;
        private byte mValueTwo;
        private int mMoney;
        private int mType;
        private int mGearScore;
        private int mSubType;
        private int mExperience;

        Mob(byte ID, int valueOne, byte valueTwo, int money, int type, int gearScore, int subType, int experience) {
            mID = ID;
            mValueOne = valueOne;
            mValueTwo = valueTwo;
            mMoney = money;
            mType = type;
            mGearScore = gearScore;
            mSubType = subType;
            mExperience = experience;
        }

        public byte getID() {
            return mID;
        }
        public int getValueOne() {
            return mValueOne;
        }
        public byte getValueTwo() {
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
    }

    static class Item {
        private byte mID;
        private byte mValueOne;
        private int mCost;
        private int mType;
        private byte mGearScore;
        private int mMobGearScore;
        private byte mDurabilityMax;

        public byte getID() {
            return mID;
        }
        public byte getValueOne() {
            return mValueOne;
        }
        public int getCost() {
            return mCost;
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

        Item(byte ID,byte valueOne, int cost, int type, byte gearScore, int mobGearScore, byte durability) {
            mID = ID;
            mValueOne = valueOne;
            mCost = cost;
            mType = type;
            mGearScore = gearScore;
            mMobGearScore = mobGearScore;
            mDurabilityMax = durability;
        }
    }
}
