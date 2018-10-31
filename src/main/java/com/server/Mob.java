package com.server;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="mobs")
class Mob {
    @Id
    @Column(name="id")
    private byte mID;

    public void setID(byte ID) {
        mID = ID;
    }

    public void setValueOne(int valueOne) {
        mValueOne = valueOne;
    }

    public void setValueTwo(byte valueTwo) {
        mValueTwo = valueTwo;
    }

    public void setMoney(int money) {
        mMoney = money;
    }

    public void setType(int type) {
        mType = type;
    }

    public void setGearScore(int gearScore) {
        mGearScore = gearScore;
    }

    public void setSubType(int subType) {
        mSubType = subType;
    }

    public void setExperience(int experience) {
        mExperience = experience;
    }

    @Column(name="VO")
    private int mValueOne;
    @Column(name="VT")
    private byte mValueTwo;
    @Column(name="M")
    private int mMoney;
    @Column(name="T")
    private int mType;
    @Column(name="G")
    private int mGearScore;
    @Column(name="S")
    private int mSubType;
    @Column(name="E")
    private int mExperience;

    Mob(){}
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