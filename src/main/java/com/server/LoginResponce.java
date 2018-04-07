package com.server;

import java.util.ArrayList;
import java.util.List;

public class LoginResponce {
    public LoginResponce(
            byte HP,
            byte state,
            int money,
            int moneyBank,
            Byte cardTable0,
            Byte cardTable1,
            Byte cardTable2,
            Byte cardTable3,
            Byte cardTable4,
            Byte cardTable5,
            Byte cardTable6,
            Byte cardTable7,
            Byte cardTable8,
            Byte cardTableTargetHP,
            List<CardInventory> inventory,
            List<CardLoot> loot,
            List<CardHand> hands,
            Stats stats
    ) {
        mHP = HP;
        mState = state;
        mMoney = money;
        mMoneyBank = moneyBank;
        mCardTable0 = cardTable0;
        mCardTable1 = cardTable1;
        mCardTable2 = cardTable2;
        mCardTable3 = cardTable3;
        mCardTable4 = cardTable4;
        mCardTable5 = cardTable5;
        mCardTable6 = cardTable6;
        mCardTable7 = cardTable7;
        mCardTable8 = cardTable8;
        mCardTableTargetHP = cardTableTargetHP;
        mInventory = inventory;
        mLoot = loot;
        mHands = hands;
        mStats = stats;
    }

    public byte getHP() {
        return mHP;
    }
    public byte getState() {
        return mState;
    }
    public int getMoney() {
        return mMoney;
    }
    public int getMoneyBank() {
        return mMoneyBank;
    }
    public Byte getCardTable0() {
        return mCardTable0;
    }
    public Byte getCardTable1() {
        return mCardTable1;
    }
    public Byte getCardTable2() {
        return mCardTable2;
    }
    public Byte getCardTable3() {
        return mCardTable3;
    }
    public Byte getCardTable4() {
        return mCardTable4;
    }
    public Byte getCardTable5() {
        return mCardTable5;
    }
    public Byte getCardTable6() {
        return mCardTable6;
    }
    public Byte getCardTable7() {
        return mCardTable7;
    }
    public Byte getCardTable8() {
        return mCardTable8;
    }
    public Byte getCardTableTargetHP() {
        return mCardTableTargetHP;
    }
    public List<CardInventory> getInventory() {
        return mInventory;
    }
    public List<CardLoot> getLoot() {
        return mLoot;
    }
    public List<CardHand> getHands() {
        return mHands;
    }

    private byte mHP;
    private byte mState;
    private int mMoney;
    private int mMoneyBank;
    private Byte mCardTable0;
    private Byte mCardTable1;
    private Byte mCardTable2;
    private Byte mCardTable3;
    private Byte mCardTable4;
    private Byte mCardTable5;
    private Byte mCardTable6;
    private Byte mCardTable7;
    private Byte mCardTable8;
    private Byte mCardTableTargetHP;
    private List<CardInventory> mInventory;
    private List<CardLoot> mLoot;
    private List<CardHand> mHands;
    private Stats mStats;
}