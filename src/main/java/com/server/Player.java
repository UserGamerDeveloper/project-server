package com.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="player")
class Player {
    private static final float GEAR_SCORE_RANGE_RATE = 0.5f;
    private static final byte CHANCE_HALT = 6;
    private static final byte CHANCE_VENDOR = 20;
    private static final byte CHANCE_CHEST = 40;
    private static final byte LOOT_MAX_COUNT = 3;
    private static final byte INVENTORY_MAX_COUNT = 4;
    private static final byte HP_DEFAULT = 30;
    private static final byte CARD_TABLE_MAX_COUNT = 8;
    private static final long LOGIN_COOLDOWN = 60000L;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int mID;
    @Column(name="email")
    private String mEmail;
    @Column(name="hp")
    private byte mHP;
    @Column(name="state")
    private byte mState;
    @Column(name="money")
    private int mMoney;
    @Column(name="moneyBank")
    private int mMoneyBank;
    @Column(name="cardTable0")
    private Byte mCardTable0;
    @Column(name="cardTable1")
    private Byte mCardTable1;
    @Column(name="cardTable2")
    private Byte mCardTable2;
    @Column(name="cardTable3")
    private Byte mCardTable3;
    @Column(name="cardTable4")
    private Byte mCardTable4;
    @Column(name="cardTable5")
    private Byte mCardTable5;
    @Column(name="cardTable6")
    private Byte mCardTable6;
    @Column(name="cardTable7")
    private Byte mCardTable7;
    @Column(name="cardTableTargetID")
    private Byte mCardTableTargetID;
    @Column(name="cardTableTargetHP")
    private Byte mCardTableTargetHP;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy="mPlayer", cascade= CascadeType.ALL)
    private List<CardInventory> mInventory =new ArrayList<>();
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy="mPlayer", cascade= CascadeType.ALL)
    private List<CardLoot> mLoot =new ArrayList<>();
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy="mPlayer", cascade= CascadeType.ALL)
    private List<CardHand> mHands =new ArrayList<>();
    @Column(name="isLogin")
    private boolean mIsLogin;
    @Column(name="loginTime")
    private Timestamp mLoginTime;
    @OneToOne(fetch = FetchType.EAGER, cascade= CascadeType.ALL)
    @JoinColumn(name="idStats", nullable = false)
    private Stats mStats;
    @Column(name="gearScore")
    private Integer mGearScore;
    @Transient
    private ArrayList<Byte> mGearScoreWeaponOrShieldInInventory = new ArrayList<>();
    @Transient
    private short mTopGearScoreWeaponOrShieldInInventory;
    @Transient
    private List<DataBase.Mob> mMobList;

    Player(String email){
        mEmail = email;
        mState = State.NONE;
        mMoneyBank = 0;
        mMoney = 0;
        mHP = HP_DEFAULT;
        mIsLogin = true;
        mStats = new Stats();
    }
    Player(ResultSet playerResultSet) {

/*
        try {
            mID = playerResultSet.getInt(1);
            mMoneyBank = playerResultSet.getInt(3);
            mState = playerResultSet.getByte(5);
            if (mState != State.NONE) {
                mMoney = playerResultSet.getInt(4);
                mHP = playerResultSet.getByte(6);
                mInventory = new CardPlayer[INVENTORY_MAX_COUNT];
                mCardTable = new byte[CARD_TABLE_MAX_COUNT];
                Connection con = DataBaseUtil.getConnection();
                PreparedStatement preparedStatement = con.prepareStatement(
                        "SELECT * FROM player WHERE playerInventory=?"
                );
                preparedStatement.setString(1, String.valueOf(mID));
                ResultSet inventoryResultSet = preparedStatement.executeQuery();
                if(mState != State.SELECT_LOOT){
                    mLoot = new CardPlayer[LOOT_MAX_COUNT];
                    mLootCount = 0;
                }
                if(inventoryResultSet.first()){
                    do {
                        switch (inventoryResultSet.getByte(3)){
                            case SlotType.HAND:{
                                if (mHandOne!=null){
                                    mHandOne = new CardPlayer(
                                            inventoryResultSet.getByte(4),
                                            (byte) 0,
                                            SlotType.HAND,
                                            inventoryResultSet.getByte(5)
                                    );
                                }
                                else{
                                    mHandTwo = new CardPlayer(
                                            inventoryResultSet.getByte(4),
                                            (byte) 0,
                                            SlotType.HAND,
                                            inventoryResultSet.getByte(5)
                                    );
                                }
                                break;
                            }
                            case SlotType.INVENTORY:{
                                mInventory[inventoryResultSet.getByte(2)] = new CardPlayer(
                                        inventoryResultSet.getByte(4),
                                        mInventoryItemCount,
                                        SlotType.INVENTORY,
                                        inventoryResultSet.getByte(5)
                                );
                                mInventoryItemCount++;
                                break;
                            }
                            case SlotType.LOOT:{
                                mLoot[mLootCount] = new CardPlayer(
                                        inventoryResultSet.getByte(4),
                                        mLootCount,
                                        SlotType.LOOT,
                                        inventoryResultSet.getByte(5)
                                );
                                mLootCount++;
                                break;
                            }
                        }
                    } while(inventoryResultSet.next());
                }
                for (byte i = 7;i<16;i++){
                    mCardTable[i-7] = inventoryResultSet.getByte(i);
                }
                if (mState==State.COMBAT){
                    mCardTableTargetHP = inventoryResultSet.getByte(16);
                }
            }
            System.out.printf("mMoneyBank: %d\n", mMoneyBank);
            System.out.printf("mMoney: %d\n", mMoney);
            System.out.printf("mState: %d\n", mState);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
*/
    }
    public Player() {
    }

    @JsonIgnore
    public int getID() {
        return mID;
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
    public Byte getCardTableTargetID() {
        return mCardTableTargetID;
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
    public Stats getStats() {
        return mStats;
    }
    @JsonIgnore
    public boolean isLogin() {
        return mIsLogin;
    }
    public void setLogin(boolean login) {
        mIsLogin = login;
    }

    String getLoginResponce() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String resposse = mapper.writeValueAsString(this);
        System.out.println("getLoginResponce: "+resposse);
        return resposse;
    }
    String getStartResponce() throws JsonProcessingException {
        mState = State.SELECT_TARGET;
        mCardTable1 = getCardTable();
        mCardTable3 = getCardTable();
        mCardTable4 = getCardTable();
        mCardTable6 = getCardTable();
        ObjectMapper mapper = new ObjectMapper();
        String response = mapper.writeValueAsString(getStartCardTable());
        System.out.println("getStartResponce: "+response);
        return response;
    }

    private short[] getStartCardTable() {
        short[] startCardTableID = new short[4];
        startCardTableID[0] = mCardTable1;
        startCardTableID[1] = mCardTable3;
        startCardTableID[2] = mCardTable4;
        startCardTableID[3] = mCardTable6;
        return startCardTableID;
    }

    private byte getCardTable() {
        Random random = new Random();
        if (random.nextInt(CHANCE_CHEST)==0){
            return 8;
        }
        else{
            if (random.nextInt(CHANCE_VENDOR)==0){
                return DataBase.VENDOR_ID.get(random.nextInt(DataBase.VENDOR_ID.size()));
            }
            else{
                if (random.nextInt(CHANCE_HALT)==0){
                    return 7;
                }
                else{
                    if (mMobList == null){
                        mMobList = getMobsList();
                    }
                    return mMobList.get(random.nextInt(mMobList.size())).getID();
                }
            }
        }
    }

    boolean setStartInventory(Byte[] startItemId){
        try{
            if (checkStartInventory(startItemId)){
                mGearScore = mStats.getGearScoreBonus();
                mHands.add(new CardHand(this, startItemId[4], (byte)0));
                mGearScoreWeaponOrShieldInInventory.add(DataBase.ITEMS.get(startItemId[4]).getGearScore());
                mGearScore += DataBase.ITEMS.get(startItemId[4]).getGearScore();
                mHands.add(new CardHand(this, startItemId[5], (byte)1));
                mGearScoreWeaponOrShieldInInventory.add(DataBase.ITEMS.get(startItemId[5]).getGearScore());
                mGearScore += DataBase.ITEMS.get(startItemId[5]).getGearScore();
                for (byte i = 0; i<INVENTORY_MAX_COUNT; i++) {
                    if (startItemId[i]!=null){
                        mInventory.add(new CardInventory(this, startItemId[i], i));
                        if (DataBase.ITEMS.get(startItemId[i]).getType()==InventoryType.SHIELD||
                                DataBase.ITEMS.get(startItemId[i]).getType() == InventoryType.WEAPON)
                        {
                            mGearScore += getChangeGearScoreAfterReplace(
                                    null,
                                    DataBase.ITEMS.get(startItemId[i]).getGearScore()
                            );
                        }
                    }
                    else{
                        break;
                    }
                }
                return true;
            }
            else{
                return false;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private short getChangeGearScoreAfterReplace(Byte remove, Byte add) {
        if (remove!=null){
            mGearScoreWeaponOrShieldInInventory.remove(remove);
        }
        if (add!=null){
            mGearScoreWeaponOrShieldInInventory.add(add);
        }
        int listSize = mGearScoreWeaponOrShieldInInventory.size();
        Byte[] a = new Byte[listSize];
        mGearScoreWeaponOrShieldInInventory.toArray(a);
        Arrays.sort(a);
        for (Byte b:a) {
            System.out.println("mGearScorelist: "+String.valueOf(b));
        }
        short b = (short) (a[listSize-1]+a[listSize-2]-mTopGearScoreWeaponOrShieldInInventory);
        mTopGearScoreWeaponOrShieldInInventory = (short) (a[listSize-1]+a[listSize-2]);
        return b;
    }

    private boolean checkStartInventory(Byte[] startItemId){
        short startItemCost = 0;
        for (byte i = 0; i<INVENTORY_MAX_COUNT; i++) {
            if (startItemId[i]!=null){
                startItemCost += DataBase.ITEMS.get(startItemId[i]).getCost();
            }
            else{
                break;
            }
        }
        startItemCost += DataBase.ITEMS.get(startItemId[4]).getCost();
        startItemCost += DataBase.ITEMS.get(startItemId[5]).getCost();
        if (startItemCost <= mMoneyBank){
            mMoneyBank -= startItemCost;
            return true;
        }
        else{
            return false;
        }
    }

    public void setLoginTime(Timestamp loginTime) {
        mLoginTime = loginTime;
    }

    boolean isLoginCooldown(){
        return mLoginTime.before(new Timestamp(mLoginTime.getTime()+LOGIN_COOLDOWN));
    }

    @JsonIgnore
    private List<DataBase.Mob> getMobsList() {
        return DataBase.MOBS.values().stream().filter(
                mob -> (mob.getGearScore() <= mGearScore) &&
                        (mob.getGearScore() >= mGearScore*GEAR_SCORE_RANGE_RATE)
        ).collect(Collectors.toList());
    }
}