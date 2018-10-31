package com.server;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "items")
class Item {
    @Id
    @Column(name="id")
    private byte mID;
    @Column(name="VO")
    private byte mValueOne;
    @Column(name="T")
    private int mType;
    @Column(name="D")
    private byte mDurabilityMax;
    @Column(name="G")
    private byte mGearScore;
    @Column(name="MG")
    private int mMobGearScore;
    @Column(name="C")
    private int mCost;

    public byte getID() {
        return mID;
    }
    public byte getValueOne() {
        return mValueOne;
    }
    public int getBuyCost() {
        return mCost;
    }
    public int getSellCost() {
        return mCost/2;
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

    Item(){}
    Item(byte ID,byte valueOne, int cost, int type, byte gearScore, int mobGearScore, byte durability) {
        mID = ID;
        mValueOne = valueOne;
        mCost = cost;
        mType = type;
        mGearScore = gearScore;
        mMobGearScore = mobGearScore;
        mDurabilityMax = durability;
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
    public void setBuyCost(int cost) {
        mCost = cost;
    }
    public void setSellCost(int cost) {
    }
    public void setCost(int cost) {
        mCost = cost;
    }
}