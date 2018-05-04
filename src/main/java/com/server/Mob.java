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
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private byte mID;
    @Column(name="VO")
    private int mValueOne;
    @Column(name="VT")
    private byte mValueTwo;
    @Column(name="MN")
    private int mMoney;
    @Column(name="T")
    private int mType;
    @Column(name="GS")
    private int mGearScore;
    @Column(name="ST")
    private int mSubType;
    @Column(name="EXP")
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