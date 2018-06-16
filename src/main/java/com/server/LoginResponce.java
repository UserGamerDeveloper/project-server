package com.server;

import java.util.ArrayList;
import java.util.List;

public class LoginResponce {

    private byte mHP;
    private byte mState;
    private int mMoney;
    private int mMoneyBank;
    private Mob mCardTable0;
    private Mob mCardTable1;
    private Mob mCardTable2;
    private Mob mCardTable3;
    private Mob mCardTable4;
    private Mob mCardTable5;
    private Mob mCardTable6;
    private Mob mCardTable7;
    private Byte mCardTableTargetIDInArray;
    private Byte mCardTableTargetHP;
    private List<ItemResponse> mInventory;
    private List<ItemResponse> mLoot;
    private List<ItemResponse> mTrade;
    private StatsResponse mStats;
    private Integer mCostVendorSkill;
    private byte mHpDefault;

    LoginResponce(Player player) {
        mHP = player.getHP();
        mState = player.getState();
        mMoney = player.getMoney();
        mMoneyBank = player.getMoneyBank();
        mCardTable0 = player.getCardTable0()!=null ? DataBase.getMobs().get(player.getCardTable0()) : null;
        mCardTable1 = player.getCardTable1()!=null ? DataBase.getMobs().get(player.getCardTable1()) : null;
        mCardTable2 = player.getCardTable2()!=null ? DataBase.getMobs().get(player.getCardTable2()) : null;
        mCardTable3 = player.getCardTable3()!=null ? DataBase.getMobs().get(player.getCardTable3()) : null;
        mCardTable4 = player.getCardTable4()!=null ? DataBase.getMobs().get(player.getCardTable4()) : null;
        mCardTable5 = player.getCardTable5()!=null ? DataBase.getMobs().get(player.getCardTable5()) : null;
        mCardTable6 = player.getCardTable6()!=null ? DataBase.getMobs().get(player.getCardTable6()) : null;
        mCardTable7 = player.getCardTable7()!=null ? DataBase.getMobs().get(player.getCardTable7()) : null;
        mCardTableTargetIDInArray = player.getCardTableTargetIDInArray();
        mCardTableTargetHP = player.getCardTableTargetHP();
        mInventory = Util.convertItems(player.getInventory());
        mLoot = Util.convertItems(player.getLoot());
        mTrade = Util.convertItems(player.getTrade());
        mStats = new StatsResponse(player.getStats());
        mCostVendorSkill = player.getCostVendorSkill();
        mHpDefault = player.getBalance().getHP_DEFAULT();
    }

    public byte getHP() {
        return mHP;
    }

    public void setHP(byte HP) {
        mHP = HP;
    }

    public byte getState() {
        return mState;
    }

    public void setState(byte state) {
        mState = state;
    }

    public int getMoney() {
        return mMoney;
    }

    public void setMoney(int money) {
        mMoney = money;
    }

    public int getMoneyBank() {
        return mMoneyBank;
    }

    public void setMoneyBank(int moneyBank) {
        mMoneyBank = moneyBank;
    }

    public Mob getCardTable0() {
        return mCardTable0;
    }

    public void setCardTable0(Mob cardTable0) {
        mCardTable0 = cardTable0;
    }

    public Mob getCardTable1() {
        return mCardTable1;
    }

    public void setCardTable1(Mob cardTable1) {
        mCardTable1 = cardTable1;
    }

    public Mob getCardTable2() {
        return mCardTable2;
    }

    public void setCardTable2(Mob cardTable2) {
        mCardTable2 = cardTable2;
    }

    public Mob getCardTable3() {
        return mCardTable3;
    }

    public void setCardTable3(Mob cardTable3) {
        mCardTable3 = cardTable3;
    }

    public Mob getCardTable4() {
        return mCardTable4;
    }

    public void setCardTable4(Mob cardTable4) {
        mCardTable4 = cardTable4;
    }

    public Mob getCardTable5() {
        return mCardTable5;
    }

    public void setCardTable5(Mob cardTable5) {
        mCardTable5 = cardTable5;
    }

    public Mob getCardTable6() {
        return mCardTable6;
    }

    public void setCardTable6(Mob cardTable6) {
        mCardTable6 = cardTable6;
    }

    public Mob getCardTable7() {
        return mCardTable7;
    }

    public void setCardTable7(Mob cardTable7) {
        mCardTable7 = cardTable7;
    }

    public Byte getCardTableTargetIDInArray() {
        return mCardTableTargetIDInArray;
    }

    public void setCardTableTargetIDInArray(Byte cardTableTargetIDInArray) {
        mCardTableTargetIDInArray = cardTableTargetIDInArray;
    }

    public Byte getCardTableTargetHP() {
        return mCardTableTargetHP;
    }

    public void setCardTableTargetHP(Byte cardTableTargetHP) {
        mCardTableTargetHP = cardTableTargetHP;
    }

    public List<ItemResponse> getInventory() {
        return mInventory;
    }

    public void setInventory(List<ItemResponse> inventory) {
        mInventory = inventory;
    }

    public List<ItemResponse> getLoot() {
        return mLoot;
    }

    public void setLoot(List<ItemResponse> loot) {
        mLoot = loot;
    }

    public List<ItemResponse> getTrade() {
        return mTrade;
    }

    public void setTrade(List<ItemResponse> trade) {
        mTrade = trade;
    }

    public StatsResponse getStats() {
        return mStats;
    }

    public void setStats(StatsResponse stats) {
        mStats = stats;
    }

    public Integer getCostVendorSkill() {
        return mCostVendorSkill;
    }

    public void setCostVendorSkill(Integer costVendorSkill) {
        mCostVendorSkill = costVendorSkill;
    }

    public byte getHpDefault() {
        return mHpDefault;
    }

    public void setHpDefault(byte hpDefault) {
        mHpDefault = hpDefault;
    }
}