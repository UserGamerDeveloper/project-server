package com.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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

import static com.server.Util.getObjectMapper;

@Entity
@Table(name="player")
class Player {
    private static final byte INVENTORY_MAX_COUNT = 6;
    private static final byte LOOT_AND_TRADECARD_MAX_COUNT = 3;
    private static final long LOGIN_COOLDOWN = 60000L;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int mID;
    @Column(name="email")
    private String mEmail;
    @OneToOne(fetch = FetchType.EAGER, cascade= CascadeType.ALL)
    @JoinColumn(name="idBalance", nullable = false)
    private Balance mBalance;
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
    @Column(name="cardTableTargetIDInArray")
    private Byte mCardTableTargetIDInArray;
    @Column(name="cardTableTargetID")
    private Byte mCardTableTargetID;
    @Column(name="cardTableTargetHP")
    private Byte mCardTableTargetHP;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy="mPlayer", orphanRemoval = true, cascade= CascadeType.ALL)
    private List<CardInventory> mInventory =new ArrayList<>();
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy="mPlayer", orphanRemoval = true, cascade= CascadeType.ALL)
    private List<CardLoot> mLoot =new ArrayList<>();
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy="mPlayer", orphanRemoval = true, cascade= CascadeType.ALL)
    private List<CardTrade> mTrade = new ArrayList<>();
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
    private List<Mob> mMobList;

    Player(String email){
        mEmail = email;
        mState = State.NONE;
        mMoneyBank = 0;
        mMoney = 0;
        mIsLogin = true;
        mStats = new Stats();
    }
    public Player() {}

    String getLoginResponse() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String resposse = mapper.writeValueAsString(this);
        System.out.println("getLoginResponse: "+resposse);
        return resposse;
    }

    String getStartResponse() throws JsonProcessingException {
        mState = State.SELECT_TARGET;
        mCardTable1 = getCardTable();
        mCardTable3 = getCardTable();
        mCardTable4 = getCardTable();
        mCardTable6 = getCardTable();
        mHP = (byte) (mBalance.getHP_DEFAULT() + mStats.getHPBonus());
        ObjectMapper mapper = new ObjectMapper();
        String response = mapper.writeValueAsString(getStartCardTable());
        System.out.println("getStartResponse: "+response);
        return response;
    }

    String getSetTargetResponse() throws JsonProcessingException {
        String responseStr = null;
        Mob mob = DataBase.getMobs().get(mCardTableTargetID);
        switch (mob.getType()){
            case CardTableType.MOB:{
                mState = State.COMBAT;
                mCardTableTargetHP = mob.getValueTwo();
                break;
            }
            case CardTableType.VENDOR:{
                mState = State.TRADE;
                Random random = com.server.Util.getRandom();
                switch (mob.getSubType()){
                    case CardTableSubType.TRADER:{
                        for (byte i = 0; i < LOOT_AND_TRADECARD_MAX_COUNT; i++) {
                            mTrade.add(new CardTrade(this, getCardLoot((byte)random.nextInt(4)), i));
                            System.out.println("trader: "+mTrade.get(i).toString());
                        }
                        break;
                    }
                    case CardTableSubType.BLACKSMITH:{
                        for (byte i = 0; i < LOOT_AND_TRADECARD_MAX_COUNT; i++) {
                            mTrade.add(new CardTrade(this, getCardLoot((byte)random.nextInt(2)), i));
                            System.out.println("BLACKSMITH: "+mTrade.get(i).toString());
                        }
                        break;
                    }
                    case CardTableSubType.INNKEEPER:{
                        for (byte i = 0; i < LOOT_AND_TRADECARD_MAX_COUNT; i++) {
                            mTrade.add(new CardTrade(this, getCardLoot(InventoryType.FOOD), i));
                            System.out.println("INNKEEPER: "+mTrade.get(i).toString());
                        }
                        break;
                    }
                }

                responseStr = getObjectMapper().writeValueAsString(mTrade);
                break;
            }
            case CardTableType.CHEST:{
                mState = State.SELECT_LOOT;
                mMoney += mob.getMoney();
                Random random = com.server.Util.getRandom();
                for (byte i = 0; i < LOOT_AND_TRADECARD_MAX_COUNT; i++) {
                    mLoot.add(new CardLoot(this, getCardLoot((byte)random.nextInt(4)), i));
                    System.out.println("CHEST: "+mLoot.get(i).toString());
                }
                DamageResponse response = new DamageResponse();
                response.setLoot(getObjectMapper().writeValueAsString(mLoot));
                tryPickingLoot();
                if (mLoot.isEmpty()) {
                    setStateSelectTarget();
                    response.setCardTableID(getStartCardTable());
                }
                responseStr = getObjectMapper().writeValueAsString(response);
                break;
            }
        }
        System.out.println("getSetTargetResponse: " + responseStr);
        return responseStr;
    }

    String getDamageResponse() throws JsonProcessingException {
        String resonceStr = null;
        if (mState==State.SELECT_LOOT){
            ObjectMapper mapper = getObjectMapper();
            DamageResponse response = new DamageResponse();
            response.setLoot(mapper.writeValueAsString(mLoot));
            tryPickingLoot();
            if (mLoot.isEmpty()) {
                setStateSelectTarget();
                response.setCardTableID(getStartCardTable());
            }
            resonceStr = mapper.writeValueAsString(response);
        }
        System.out.println("getDamageResponse: "+resonceStr);
        return resonceStr;
    }

    String getUseSkillTraderResponse() throws JsonProcessingException {
        String response = getObjectMapper().writeValueAsString(mTrade);
        System.out.println("getUseSkillTraderResponse: " + response);
        return response;
    }

    String getContinueResponse() throws JsonProcessingException {
        setStateSelectTarget();
        ObjectMapper mapper = new ObjectMapper();
        String response = mapper.writeValueAsString(getStartCardTable());
        System.out.println("getContinueResponse: "+response);
        return response;
    }

    String getUseFoodResponse() throws JsonProcessingException {
        String response = null;
        if (mState == State.SELECT_LOOT){
            tryPickingLoot();
            if (mLoot.isEmpty()) {
                setStateSelectTarget();
                ObjectMapper mapper = new ObjectMapper();
                response = mapper.writeValueAsString(getStartCardTable());
            }
        }
        System.out.println("getUseFoodResponse: "+response);
        return response;
    }

    boolean setStartInventory(Byte[] startItemId){
        try{
            if (checkStartInventory(startItemId)){
                mGearScore = mStats.getGearScoreBonus();
                mInventory.add(new CardInventory(this, startItemId[4], (byte)4, (byte) 0));
                mGearScoreWeaponOrShieldInInventory.add(DataBase.getItems().get(startItemId[4]).getGearScore());
                mGearScore += DataBase.getItems().get(startItemId[4]).getGearScore();
                mInventory.add(new CardInventory(this, startItemId[5], (byte)5, (byte) 0));
                mGearScoreWeaponOrShieldInInventory.add(DataBase.getItems().get(startItemId[5]).getGearScore());
                mGearScore += DataBase.getItems().get(startItemId[5]).getGearScore();
                for (byte i = 0; i<INVENTORY_MAX_COUNT; i++) {
                    if (startItemId[i]!=null){
                        mInventory.add(new CardInventory(this, startItemId[i], i));
                        if (DataBase.getItems().get(startItemId[i]).getType()==InventoryType.SHIELD ||
                                DataBase.getItems().get(startItemId[i]).getType() == InventoryType.WEAPON)
                        {
                            mGearScore += getChangeGearScoreAfterReplace(
                                    null,
                                    DataBase.getItems().get(startItemId[i]).getGearScore()
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

    boolean setTarget(byte id){
        if (mState == State.SELECT_TARGET){
            switch (id){
                case 1:{
                    mCardTableTargetIDInArray = 1;
                    mCardTableTargetID = mCardTable1;
                    break;
                }
                case 3:{
                    mCardTableTargetIDInArray = 3;
                    mCardTableTargetID = mCardTable3;
                    break;
                }
                case 4:{
                    mCardTableTargetIDInArray = 4;
                    mCardTableTargetID = mCardTable4;
                    break;
                }
                case 6:{
                    mCardTableTargetIDInArray = 6;
                    mCardTableTargetID = mCardTable6;
                    break;
                }
                default:{
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    boolean damage(CardInventory[] hands){
        if (mState == State.COMBAT){
            Item handOne = DataBase.getItems().get(hands[0].getIdItem());
            Item handTwo = DataBase.getItems().get(hands[1].getIdItem());
            if ((handOne.getType()==InventoryType.WEAPON || handOne.getType()==InventoryType.SHIELD) &&
                    (handTwo.getType()==InventoryType.WEAPON || handTwo.getType()==InventoryType.SHIELD))
            {
                List<CardInventory> handOneState = mInventory.stream().filter(
                        item -> hands[0].equals(item)
                ).collect(Collectors.toList());
                System.out.println("handOneState.get(0).toString() " + handOneState.get(0).toString());
                if (!handOneState.isEmpty()){
                    List<CardInventory> handTwoState = mInventory.stream().filter(
                            item -> hands[1].equals(item) && handOneState.get(0)!=item
                    ).collect(Collectors.toList());
                    System.out.println("handTwoState.get(0).toString() " + handTwoState.get(0).toString());
                    if (!handTwoState.isEmpty()){
                        Mob mobTarget = DataBase.getMobs().get(mCardTableTargetID);
                        int mobDamage = mobTarget.getValueOne();
                        if(mobDamage > 0 && handOne.getType() == InventoryType.SHIELD){
                            if (handOne.getValueOne() < mobDamage){
                                mobDamage -= handOne.getValueOne() + mStats.getDefenceBonus();
                            }
                            else{
                                mobDamage = 0;
                            }
                        }
                        if(mobDamage > 0 && handTwo.getType() == InventoryType.SHIELD){
                            if (handTwo.getValueOne() < mobDamage){
                                mobDamage -= handTwo.getValueOne() + mStats.getDefenceBonus();
                            }
                            else{
                                mobDamage = 0;
                            }
                        }
                        if (mobDamage>0){
                            mHP -= mobDamage;
                        }
                        if (mHP < 1) {
                            mState = State.NONE;
                            mHP = 0;
                            mMoneyBank += mMoney;
                            mMoney = 0;
                            mInventory.clear();
                            return true;
                        }

                        byte damage = (byte) ((handOne.getType() == InventoryType.WEAPON ?
                                handOne.getValueOne()+mStats.getDamageBonus() : 0) +
                                (handTwo.getType() == InventoryType.WEAPON ?
                                handTwo.getValueOne()+mStats.getDamageBonus() : 0));
                        mCardTableTargetHP = (byte)(mCardTableTargetHP-damage);

                        if (handOneState.get(0).getIdItem()!=0){
                            handOneState.get(0).decrementDurability();
                        }
                        if (handTwoState.get(0).getIdItem()!=0){
                            handTwoState.get(0).decrementDurability();
                        }

                        if (mCardTableTargetHP<1){
                            deadMob(mobTarget);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    boolean selectLoot(CardInventory[] inventory){
        if (mState == State.SELECT_LOOT){
            List<CardInventory> inventoryList = Arrays.asList(inventory);
            ArrayList<CardPlayer> invetoryAndLoot = new ArrayList<>();
            invetoryAndLoot.addAll(mInventory);
            invetoryAndLoot.addAll(mLoot);
            for (CardPlayer cardPlayer : invetoryAndLoot) {
                System.out.println("invetoryAndLoot: "+cardPlayer.toString());
            }
            for (CardPlayer cardPlayer : inventoryList) {
                System.out.println("inventoryList: "+cardPlayer.toString());
            }
            if (invetoryAndLoot.containsAll(inventoryList)){
                mInventory.clear();
                mLoot.clear();
                for (CardInventory cardPlayer : inventoryList) {
                    cardPlayer.setPlayer(this);
                    mInventory.add(cardPlayer);
                }
                return true;
            }
        }
        return false;
    }

    boolean buy(CardTrade cardTrade){
        if (mState == State.TRADE){
            if (mInventory.size() < INVENTORY_MAX_COUNT){
                Item item = DataBase.getItems().get(cardTrade.getIdItem());
                if (mMoney >= item.getCost()){
                    List<CardTrade> cardTradeState = mTrade.stream().filter(
                            cardTrade::equals
                    ).collect(Collectors.toList());
                    if (!cardTradeState.isEmpty()){
                        cardTrade = cardTradeState.get(0);
                        mInventory.add(new CardInventory(cardTrade));
                        mTrade.remove(cardTrade);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    boolean sell(CardInventory cardInventory){
        if (mState == State.TRADE){
            if (cardInventory.getIdItem()!=0){
                List<CardInventory> cardInventoryState = mInventory.stream().filter(
                        cardInventory::equals
                ).collect(Collectors.toList());
                if (!cardInventoryState.isEmpty()){
                    mInventory.remove(cardInventoryState.get(0));
                    Item item = DataBase.getItems().get(cardInventory.getIdItem());
                    mMoney += item.getCost();
                    return true;
                }
            }
        }
        return false;
    }

    boolean useSkillTrader(){
        if (mState == State.TRADE){
            if (mMoney >= mBalance.getCOST_VENDOR_SKILL()){
                Mob mob = DataBase.getMobs().get(mCardTableTargetID);
                if (mob.getSubType()==CardTableSubType.TRADER){
                    mMoney -= mBalance.getCOST_VENDOR_SKILL();
                    mTrade.clear();
                    Random random = Util.getRandom();
                    for (byte i = 0; i < LOOT_AND_TRADECARD_MAX_COUNT; i++) {
                        mTrade.add(new CardTrade(this, getCardLoot((byte)random.nextInt(4)), i));
                        System.out.println("trader: "+mTrade.get(i).toString());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    boolean useSkillBlacksmith(CardInventory itemState) {
        if (mState == State.TRADE){
            if (mMoney >= mBalance.getCOST_VENDOR_SKILL()){
                if (itemState.getIdItem()!=0){
                    Mob mob = DataBase.getMobs().get(mCardTableTargetID);
                    if (mob.getSubType()==CardTableSubType.BLACKSMITH){
                        List<CardInventory> cardInventoryState = mInventory.stream().filter(
                                itemState::equals
                        ).collect(Collectors.toList());
                        if (!cardInventoryState.isEmpty()){
                            mMoney -= mBalance.getCOST_VENDOR_SKILL();
                            Item item = DataBase.getItems().get(itemState.getIdItem());
                            cardInventoryState.get(0).setDurability(item.getDurabilityMax());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    boolean useSkillInnkeeper(){
        if (mState == State.TRADE){
            if (mMoney >= mBalance.getCOST_VENDOR_SKILL()){
                Mob mob = DataBase.getMobs().get(mCardTableTargetID);
                if (mob.getSubType()==CardTableSubType.INNKEEPER){
                    mMoney -= mBalance.getCOST_VENDOR_SKILL();
                    mHP = (byte) (mBalance.getHP_DEFAULT() + mStats.getHPBonus());
                    return true;
                }
            }
        }
        return false;
    }

    boolean exitTrade(){
        if (mState == State.TRADE){
            mTrade.clear();
            mCardTableTargetID = null;
            return true;
        }
        return false;
    }

    boolean useFood(byte itemID) {
        Item item = DataBase.getItems().get(itemID);
        if (item.getType()==InventoryType.FOOD){
            List<CardPlayer> itemList;
            if (mState==State.SELECT_LOOT){
                itemList = mLoot.stream().filter(
                        itemInLoot -> itemInLoot.getIdItem() == itemID
                ).collect(Collectors.toList());
                if (!itemList.isEmpty()){
                    mHP += item.getValueOne();
                    mLoot.remove(itemList.get(0));
                    mGearScore -= item.getGearScore();
                    return true;
                }
            }
            itemList = mInventory.stream().filter(
                    itemInInventory -> itemInInventory.getIdItem() == itemID
            ).collect(Collectors.toList());
            if (!itemList.isEmpty()){
                mHP += item.getValueOne();
                mInventory.remove(itemList.get(0));
                mGearScore -= item.getGearScore();
                return true;
            }
        }
        return false;
    }

    boolean useSpell(byte itemID) {
        List<CardInventory> itemList = mInventory.stream().filter(
                item -> item.getIdItem() == itemID
        ).collect(Collectors.toList());
        if (!itemList.isEmpty()){
            Item item = DataBase.getItems().get(itemID);
            if (item.getType()==InventoryType.SPELL){
                if (mState == State.COMBAT){
                    mCardTableTargetHP = (byte)(mCardTableTargetHP-item.getValueOne());
                    mGearScore -= item.getGearScore();
                    mInventory.remove(itemList.get(0));
                    if (mCardTableTargetHP<1){
                        deadMob(DataBase.getMobs().get(mCardTableTargetID));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    void changeMoneyBank(int delta){
        mMoneyBank += delta;
    }

    boolean isLoginCooldown(){
        return mLoginTime.before(new Timestamp(mLoginTime.getTime()+LOGIN_COOLDOWN));
    }

    private void setStateSelectTarget() {
        mState = State.SELECT_TARGET;
        if (mCardTableTargetIDInArray==1){
            mCardTable5 = mCardTable3;
            mCardTable7 = mCardTable4;
            mCardTable3 = mCardTable0!=null ? mCardTable0 : getCardTable();
            mCardTable4 = mCardTable2!=null ? mCardTable2 : getCardTable();
            mCardTable0 = null;
            mCardTable2 = null;
            mCardTable1 = getCardTable();
            mCardTable6 = getCardTable();
        }
        if (mCardTableTargetIDInArray==3){
            mCardTable7 = mCardTable6;
            mCardTable2 = mCardTable1;
            mCardTable6 = mCardTable5!=null ? mCardTable5 : getCardTable();
            mCardTable1 = mCardTable0!=null ? mCardTable0 : getCardTable();
            mCardTable5 = null;
            mCardTable0 = null;
            mCardTable3 = getCardTable();
            mCardTable4 = getCardTable();
        }
        if (mCardTableTargetIDInArray==4){
            mCardTable5 = mCardTable6;
            mCardTable0 = mCardTable1;
            mCardTable6 = mCardTable7!=null ? mCardTable7 : getCardTable();
            mCardTable1 = mCardTable2!=null ? mCardTable2 : getCardTable();
            mCardTable7 = null;
            mCardTable2 = null;
            mCardTable3 = getCardTable();
            mCardTable4 = getCardTable();
        }
        if (mCardTableTargetIDInArray==6){
            mCardTable0 = mCardTable3;
            mCardTable2 = mCardTable4;
            mCardTable3 = mCardTable5!=null ? mCardTable5 : getCardTable();
            mCardTable4 = mCardTable7!=null ? mCardTable7 : getCardTable();
            mCardTable5 = null;
            mCardTable7 = null;
            mCardTable1 = getCardTable();
            mCardTable6 = getCardTable();
        }
        mCardTableTargetIDInArray = null;
    }

    private void tryPickingLoot() {
        while (!mLoot.isEmpty() && mInventory.size() < INVENTORY_MAX_COUNT) {
            Item item = DataBase.getItems().get(mLoot.get(0).getIdItem());
            if (item.getType()== InventoryType.WEAPON ||
                    item.getType()== InventoryType.SHIELD)
            {
                mGearScore += getChangeGearScoreAfterReplace(
                        null,
                        item.getGearScore()
                );
            }
            else{
                mGearScore += item.getGearScore();
            }
            mInventory.add(new CardInventory(mLoot.get(0)));
            mLoot.remove(0);
        }
    }

    private void deadMob(Mob mobTarget) {
        mState = State.SELECT_LOOT;
        mMoney += mobTarget.getMoney();
        mStats.addExperience(mobTarget.getExperience());
        generateLoot();
    }

    private void generateLoot() {
        int[] typeLoot = new int[3];
        Mob mob = DataBase.getMobs().get(mCardTableTargetID);
        mCardTableTargetID = null;
        mCardTableTargetHP = null;
        Random random = new Random();
        int lootCount = 0;
        if (mob.getType()==CardTableType.MOB){
            if (mob.getSubType()!=CardTableSubType.FOREST){
                if (random.nextInt(mBalance.getCHANCE_WEAPON_OE_SHIELD())==0){
                    if (random.nextBoolean()){
                        typeLoot[lootCount]=InventoryType.WEAPON;
                    }
                    else{
                        typeLoot[lootCount]=InventoryType.SHIELD;
                    }
                    lootCount++;
                }
                if (random.nextInt(mBalance.getCHANCE_FOOD())==0){
                    typeLoot[lootCount]= InventoryType.FOOD;
                    lootCount++;
                }
                if (random.nextInt(mBalance.getCHANCE_SPELL())==0){
                    typeLoot[lootCount]= InventoryType.SPELL;
                    lootCount++;
                }
            }
        }
        else{
            lootCount = random.nextInt(3)+1;
            for (byte i = 0; i < lootCount; i++){
                typeLoot[i]=random.nextInt(4);
            }
        }
        for (byte i = 0; i < lootCount; i++) {
            mLoot.add(new CardLoot(this, getCardLoot((byte) typeLoot[i]), i));
            System.out.println("loot: "+mLoot.get(i).toString());
        }
    }

    private byte getCardLoot(byte type) {
        Random random = new Random();
        List<Item> itemList = DataBase.getItems().values().stream().filter(
                item -> (item.getMobGearScore() <= mGearScore) && (item.getType() == type)
        ).collect(Collectors.toList());;
        return itemList.get(random.nextInt(itemList.size())).getID();
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
        boolean chest = random.nextInt(mBalance.getCHANCE_CHEST())==0;
        boolean halt = false;
        boolean vendor = random.nextInt(mBalance.getCHANCE_VENDOR())==0;
/*
        boolean vendor = random.nextInt(CHANCE_VENDOR)==0;
        boolean chest = random.nextInt(CHANCE_CHEST)==0;
        boolean halt = random.nextInt(CHANCE_HALT)==0;
*/
        if(chest && vendor && halt) {
            switch (random.nextInt(3)){
                case 0:{
                    return 8;
                }
                case 1:{
                    return DataBase.VENDOR_ID.get(random.nextInt(DataBase.VENDOR_ID.size()));
                }
                case 3:{
                    return 7;
                }
            }
        }
        if(chest&&!vendor&&!halt){
            return 8;
        }
        if(!chest&&vendor&&!halt){
            return DataBase.VENDOR_ID.get(random.nextInt(DataBase.VENDOR_ID.size()));
        }
        if(!chest && !vendor && halt){
            return 7;
        }
        if(chest && vendor){
            if (random.nextBoolean()){
                return 8;
            }
            else {
                return DataBase.VENDOR_ID.get(random.nextInt(DataBase.VENDOR_ID.size()));
            }
        }
        if(halt && vendor){
            if (random.nextBoolean()){
                return 7;
            }
            else {
                return DataBase.VENDOR_ID.get(random.nextInt(DataBase.VENDOR_ID.size()));
            }
        }
        if(halt && chest){
            if (random.nextBoolean()){
                return 7;
            }
            else {
                return 8;
            }
        }
        if (mMobList == null){
            mMobList = getMobsList();
        }
        return mMobList.get(random.nextInt(mMobList.size())).getID();
    }

    private short getChangeGearScoreAfterReplace(Byte remove, Byte add) {
        for (CardInventory itemState :
                mInventory) {
            Item item = DataBase.getItems().get(itemState.getIdItem());
            if (item.getType()==InventoryType.WEAPON ||
                    item.getType()==InventoryType.SHIELD){
                mGearScoreWeaponOrShieldInInventory.add(item.getGearScore());
            }
        }
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
        startItemCost += DataBase.getItems().get(startItemId[4]).getCost();
        startItemCost += DataBase.getItems().get(startItemId[5]).getCost();
        for (byte i = 0; i<INVENTORY_MAX_COUNT; i++) {
            if (startItemId[i]!=null){
                startItemCost += DataBase.getItems().get(startItemId[i]).getCost();
            }
            else{
                break;
            }
        }
        if (startItemCost <= mMoneyBank){
            mMoneyBank -= startItemCost;
            return true;
        }
        else{
            return false;
        }
    }

    //region setters/getters
    public void setLoginTime(Timestamp loginTime) {
        mLoginTime = loginTime;
    }
    @JsonIgnore
    public Balance getBalance() {
        return mBalance;
    }
    @JsonIgnore
    public void setBalance(Balance balance) {
        mBalance = balance;
    }
    @JsonIgnore
    private List<Mob> getMobsList() {
        return DataBase.getMobs().values().stream().filter(
                mob -> (mob.getGearScore() <= mGearScore) /*&&
                        (mob.getGearScore() >= mGearScore*GEAR_SCORE_RANGE_RATE)*/
        ).collect(Collectors.toList());
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
    public Byte getCardTableTargetIDInArray() {
        return mCardTableTargetIDInArray;
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
    public List<CardTrade> getTrade() {
        return mTrade;
    }
    public void setTrade(List<CardTrade> trade) {
        mTrade = trade;
    }

    //endregion
}