package com.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.Session;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
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
    private static final byte ID_CHEST = 3;
    private static final byte ID_INNKEEPER = 2;
    private static final byte ID_HUND = 1;
    private static final long LOGIN_COOLDOWN = 60000L;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int mID;
    @Column(name="email")
    private String mEmail;
    @OneToOne(fetch = FetchType.EAGER, cascade= CascadeType.ALL)
    @JoinColumn(name="idBalance")
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
    @Column(name="topOneGearScoreWeaponOrShieldInInventory")
    private byte mTopOneGearScoreWeaponOrShieldInInventory;
    @Column(name="topTwoGearScoreWeaponOrShieldInInventory")
    private byte mTopTwoGearScoreWeaponOrShieldInInventory;
    @Column(name="costVendorSkill")
    private Integer mCostVendorSkill;
    @Column(name="countMobsToPortal")
    private Integer mCountMobsToExit;
    @Column(name="countMobsToVendor")
    private Integer mCountMobsToVendor;
    @Transient
    private List<Mob> mMobList;
    @Transient
    private Hashtable<Byte, Mob> MOBS;

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
        String resposse = mapper.writeValueAsString(new LoginResponce(this));
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
        String response = mapper.writeValueAsString(getNextMobs());
        System.out.println("getStartResponse: "+response);
        return response;
    }

    String getSetTargetResponse() throws JsonProcessingException {
        String responseStr = null;
        Mob mob = getMobs().get(mCardTableTargetID);
        switch (mob.getType()){
            case CardTableType.MOB:{
                mState = State.COMBAT;
                mCardTableTargetHP = mob.getValueTwo();
                break;
            }
            case CardTableType.VENDOR:{
                mState = State.TRADE;
                mCountMobsToVendor = 0;
                Random random = com.server.Util.getRandom();
                ResponceTrade responceTrade = new ResponceTrade();
                switch (mob.getSubType()){
                    case CardTableSubType.TRADER:{
                        for (byte i = 0; i < LOOT_AND_TRADECARD_MAX_COUNT; i++) {
                            int typeItem;
                            if (mGearScore>0){
                                typeItem = random.nextInt(4);
                            }
                            else{
                                typeItem = random.nextInt(3)+1;
                            }
                            mTrade.add(
                                    new CardTrade(
                                            this,
                                            getCardLoot((byte)typeItem),
                                            i,
                                            false
                                    )
                            );
                            System.out.println("trader: "+mTrade.get(i).toString());
                        }
                        setCostVendorSkill();
                        responceTrade.setSkillCost(mCostVendorSkill);
                        break;
                    }
                    case CardTableSubType.BLACKSMITH:{
                        for (byte i = 0; i < LOOT_AND_TRADECARD_MAX_COUNT; i++) {
                            int typeItem;
                            if (mGearScore>0){
                                typeItem = random.nextInt(2);
                            }
                            else{
                                typeItem = InventoryType.WEAPON;
                            }
                            mTrade.add(new CardTrade(
                                    this,
                                    getCardLoot((byte)typeItem),
                                    i,
                                    true
                                    )
                            );
                            System.out.println("BLACKSMITH: "+mTrade.get(i).toString());
                        }
                        break;
                    }
                }
                responceTrade.setTrade(Util.convertItems(mTrade));
                responseStr = getObjectMapper().writeValueAsString(responceTrade);
                break;
            }
            case CardTableType.CHEST:{
                mState = State.SELECT_LOOT;
                mMoney += mob.getMoney();
                Random random = com.server.Util.getRandom();
                int typeItem;
                for (byte i = 0; i < LOOT_AND_TRADECARD_MAX_COUNT; i++) {
                    if (mGearScore>0){
                        typeItem = random.nextInt(4);
                    }
                    else{
                        typeItem = random.nextInt(3)+1;
                    }
                    mLoot.add(new CardLoot(this, getCardLoot((byte)typeItem), i));
                    System.out.println("CHEST: "+mLoot.get(i).toString());
                }
                DamageResponse response = new DamageResponse();
                response.setLoot(getObjectMapper().writeValueAsString(Util.convertItems(mLoot)));
                tryPickingLoot();
                if (mLoot.isEmpty()) {
                    setStateSelectTarget();
                    response.setNextMobs(getNextMobs());
                }
                responseStr = getObjectMapper().writeValueAsString(response);
                break;
            }
            case CardTableType.INNKEEPER:{
                mState = State.TRADE;
                mCountMobsToVendor = 0;
                ResponceTrade responceTrade = new ResponceTrade();

                for (byte i = 0; i < LOOT_AND_TRADECARD_MAX_COUNT; i++) {
                    mTrade.add(
                            new CardTrade(
                                    this,
                                    getCardLoot(InventoryType.FOOD),
                                    i,
                                    false
                            )
                    );
                    System.out.println("INNKEEPER: "+mTrade.get(i).toString());
                }
                setCostVendorSkill();
                responceTrade.setSkillCost(mCostVendorSkill);
                responceTrade.setTrade(Util.convertItems(mTrade));
                responseStr = getObjectMapper().writeValueAsString(responceTrade);
                break;
            }
        }
        System.out.println("getSetTargetResponse: " + responseStr);
        return responseStr;
    }

    private void setCostVendorSkill() {
        if (mGearScore>0){
            mCostVendorSkill = (int)(mGearScore*mBalance.getVENDOR_SKILL_COST_K());
        }
        else{
            mCostVendorSkill = mBalance.getMIN_COST_VENDOR_SKILL();
        }
    }

    String getDamageResponse() throws JsonProcessingException {
        String resonceStr = null;
        if (mState==State.SELECT_LOOT){
            ObjectMapper mapper = getObjectMapper();
            DamageResponse response = new DamageResponse();
            response.setLoot(mapper.writeValueAsString(Util.convertItems(mLoot)));
            tryPickingLoot();
            if (mLoot.isEmpty()) {
                setStateSelectTarget();
                response.setNextMobs(getNextMobs());
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
        String response = mapper.writeValueAsString(getNextMobs());
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
                response = mapper.writeValueAsString(getNextMobs());
            }
        }
        System.out.println("getUseFoodResponse: "+response);
        return response;
    }

    boolean setStartInventory(Byte[] startItemId){
        try{
            if (checkStartInventory(startItemId)){
                mGearScore = mStats.getGearScoreBonus();
                ArrayList<Byte> gearScoreWeaponOrShieldInInventory = new ArrayList<>();
                Item handOne = DataBase.getItems().get(startItemId[4]);
                if (startItemId[4]!=1){
                    mInventory.add(new CardInventory(this, startItemId[4], (byte)4, handOne.getDurabilityMax()));
                }
                gearScoreWeaponOrShieldInInventory.add(handOne.getGearScore());
                Item handTwo = DataBase.getItems().get(startItemId[5]);
                if (startItemId[5]!=1){
                    mInventory.add(new CardInventory(this, startItemId[5], (byte)5, handTwo.getDurabilityMax()));
                }
                gearScoreWeaponOrShieldInInventory.add(handTwo.getGearScore());
                for (byte idSlot = 0; idSlot<INVENTORY_MAX_COUNT; idSlot++) {
                    if (startItemId[idSlot]!=null){
                        Item item = DataBase.getItems().get(startItemId[idSlot]);
                        mInventory.add(new CardInventory(this, startItemId[idSlot], idSlot, item.getDurabilityMax()));
                        if (item.isWeaponOrShield())
                        {
                            gearScoreWeaponOrShieldInInventory.add(item.getGearScore());
                        }
                    }
                    else{
                        break;
                    }
                }
                sortGearScoreWeaponOrShieldInInventoryAndChangeGearScore(gearScoreWeaponOrShieldInInventory);
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

    boolean damageCheck(CardInventory[] inventory){
        if (mState == State.COMBAT){
            for (CardPlayer cardPlayer : inventory) {
                System.out.println("inventory: "+cardPlayer.toString());
            }
            for (CardPlayer cardPlayer : mInventory) {
                System.out.println("mInventory: "+cardPlayer.toString());
            }
            List<CardInventory> inventoryClientList = Arrays.asList(inventory);
            ArrayList<CardPlayer> inventoryServerList = new ArrayList<>();
            inventoryServerList.addAll(mInventory);
            addHund(inventoryServerList);
            if (inventoryServerList.containsAll(inventoryClientList)){
                mInventory.clear();
                for (CardInventory cardPlayer : inventoryClientList) {
                    cardPlayer.setPlayer(this);
                    mInventory.add(cardPlayer);
                    Item item = DataBase.getItems().get(cardPlayer.getIdItem());
                    tryChangeGearScore(item);
                }
                CardInventory[] sortBySlotInventory = new CardInventory[6];
                for (CardInventory item : mInventory) {
                    sortBySlotInventory[item.getSlotId()] = item;
                }
                CardInventory clientHandOneState = sortBySlotInventory[4];
                if (!clientHandOneState.isFist()){
                    Item handOne = DataBase.getItems().get(clientHandOneState.getIdItem());
                    if (handOne.isWeaponOrShield()) {
                        List<CardInventory> handOneState = mInventory.stream().filter(
                                clientHandOneState::equals
                        ).collect(Collectors.toList());
                        if (!handOneState.isEmpty()){
                            CardInventory clientHandTwoState = sortBySlotInventory[5];
                            if (!clientHandTwoState.isFist()){
                                Item handTwo = DataBase.getItems().get(clientHandTwoState.getIdItem());
                                if (handTwo.isWeaponOrShield()){
                                    List<CardInventory> handTwoState = mInventory.stream().filter(
                                            item -> clientHandTwoState.equals(item) && handOneState.get(0)!=item
                                    ).collect(Collectors.toList());
                                    if (!handTwoState.isEmpty()){
                                        damage(handOne, handTwo, handOneState.get(0), handTwoState.get(0));
                                        return true;
                                    }
                                }
                            }
                            else{
                                mInventory.remove(clientHandTwoState);
                                damage(handOne, null, handOneState.get(0), null);
                                return true;
                            }
                        }
                    }
                }
                else{
                    mInventory.remove(clientHandOneState);
                    CardInventory clientHandTwoState = sortBySlotInventory[5];
                    if (!clientHandTwoState.isFist()){
                        Item handTwo = DataBase.getItems().get(clientHandTwoState.getIdItem());
                        if (handTwo.isWeaponOrShield()){
                            List<CardInventory> handTwoState = mInventory.stream().filter(
                                    clientHandTwoState::equals
                            ).collect(Collectors.toList());
                            if (!handTwoState.isEmpty()){
                                damage(null, handTwo, null, handTwoState.get(0));
                                return true;
                            }
                        }
                    }
                    else{
                        mInventory.remove(clientHandTwoState);
                        damage(null, null, null, null);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void damage(Item handOne, Item handTwo, CardInventory handOneState, CardInventory handTwoState){
        Mob mobTarget = getMobs().get(mCardTableTargetID);
        int mobDamage = mobTarget.getValueOne();
        if (handOne!=null){
            if(mobDamage > 0 && handOne.getType() == InventoryType.SHIELD){
                if (handOne.getValueOne() < mobDamage){
                    mobDamage -= handOne.getValueOne() + mStats.getDefenceBonus();
                }
                else{
                    mobDamage = 0;
                }
            }
        }
        if (handTwo!=null){
            if(mobDamage > 0 && handTwo.getType() == InventoryType.SHIELD){
                if (handTwo.getValueOne() < mobDamage){
                    mobDamage -= handTwo.getValueOne() + mStats.getDefenceBonus();
                }
                else{
                    mobDamage = 0;
                }
            }
        }
        if (mobDamage>0){
            mHP -= mobDamage;
        }

        if (mHP < 1) {
            dead();
            return;
        }

        byte damage = 0;
        if (handOne!=null){
            if (handOne.getType() == InventoryType.WEAPON){
                damage += handOne.getValueOne()+mStats.getDamageBonus();
            }
            handOneState.decrementDurability();
        }
        else{
            damage += 1+mStats.getDamageBonus();
        }
        if (handTwo!=null){
            if (handTwo.getType() == InventoryType.WEAPON){
                damage += handTwo.getValueOne()+mStats.getDamageBonus();
            }
            handTwoState.decrementDurability();
        }
        else{
            damage += 1+mStats.getDamageBonus();
        }
        mCardTableTargetHP = (byte)(mCardTableTargetHP-damage);

        if (mCardTableTargetHP<1){
            deadMob(mobTarget);
        }
    }

    void resetAccount() {
        dead();
        mMoneyBank = 0;
        mStats = new Stats();
    }

    void dead() {
        mMoneyBank += mMoney/2;
        resetGame();
    }

    void resetGame() {
        mState = State.NONE;
        mCountMobsToExit = 0;
        mCountMobsToVendor = 0;
        mHP = 0;
        mMoney = 0;
        mInventory.clear();
        mLoot.clear();
        mTrade.clear();
        mGearScore = 0;
        mTopTwoGearScoreWeaponOrShieldInInventory = 0;
        mTopOneGearScoreWeaponOrShieldInInventory = 0;
        mCardTable0 = null;
        mCardTable2 = null;
        mCardTable5 = null;
        mCardTable7 = null;
    }

    boolean exit(){
        if (mState == State.TRADE){
            mMoneyBank += mMoney;
            resetGame();
            return true;
        }
        return false;
    }

    boolean selectLoot(CardInventory[] inventory){
        if (mState == State.SELECT_LOOT){
            List<CardInventory> inventoryList = Arrays.asList(inventory);
            ArrayList<CardPlayer> invetoryAndLoot = new ArrayList<>();
            addHund(invetoryAndLoot);
            invetoryAndLoot.addAll(mInventory);
            invetoryAndLoot.addAll(mLoot);
            for (CardPlayer cardPlayer : invetoryAndLoot) {
                System.out.println("inventoryAndLoot: "+cardPlayer.toString());
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
                    Item item = DataBase.getItems().get(cardPlayer.getIdItem());
                    tryChangeGearScore(item);
                }
                return true;
            }
        }
        return false;
    }

    private void addHund(List<CardPlayer> list) {
        list.add(new CardInventory(this,ID_HUND,(byte)0,(byte)0));
        list.add(new CardInventory(this,ID_HUND,(byte)0,(byte)0));
    }

    boolean buy(CardTrade cardTrade){
        if (mState == State.TRADE){
            if (mInventory.size() < INVENTORY_MAX_COUNT){
                Item item = DataBase.getItems().get(cardTrade.getIdItem());
                if (mMoney >= item.getBuyCost()){
                    List<CardTrade> cardTradeStateList = mTrade.stream().filter(
                            cardTrade::equals
                    ).collect(Collectors.toList());
                    if (!cardTradeStateList.isEmpty()){
                        CardTrade cardTradeState = cardTradeStateList.get(0);
                        cardTradeState.setSlotId(cardTrade.getSlotId());
                        mInventory.add(new CardInventory(cardTradeState));
                        mTrade.remove(cardTrade);
                        tryChangeGearScore(item);
                        mMoney -= item.getBuyCost();
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
                    mMoney += item.getSellCost();
                    tryChangeGearScore(item);
                    return true;
                }
            }
        }
        return false;
    }

    boolean useSkillTrader(){
        if (mState == State.TRADE){
            if (mMoney >= mCostVendorSkill){
                Mob mob = getMobs().get(mCardTableTargetID);
                if (mob.getSubType()==CardTableSubType.TRADER){
                    mMoney -= mCostVendorSkill;
                    mTrade.clear();
                    Random random = Util.getRandom();
                    for (byte i = 0; i < LOOT_AND_TRADECARD_MAX_COUNT; i++) {
                        mTrade.add(new CardTrade(this, getCardLoot((byte)random.nextInt(4)), i, false));
                        System.out.println("trader: "+mTrade.get(i).toString());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    boolean useSkillBlacksmith(CardInventory itemStateClient) {
        if (mState == State.TRADE){
            Item item = DataBase.getItems().get(itemStateClient.getIdItem());
            if (itemStateClient.getDurability() < item.getDurabilityMax()){
                List<CardInventory> itemStateListServer = mInventory.stream().filter(
                        itemStateClient::equals
                ).collect(Collectors.toList());
                if (!itemStateListServer.isEmpty()){
                    CardInventory itemStateServer = itemStateListServer.get(0);
                    mCostVendorSkill = (int)((float)(item.getDurabilityMax() -
                            itemStateServer.getDurability())/(float)item.getDurabilityMax()*
                            (float)item.getBuyCost()/2f);
                    if (mMoney >= mCostVendorSkill){
                        if (!itemStateServer.isFist()){
                            Mob mob = getMobs().get(mCardTableTargetID);
                            if (mob.getSubType()==CardTableSubType.BLACKSMITH){
                                mMoney -= mCostVendorSkill;
                                itemStateServer.setDurability(item.getDurabilityMax());
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    boolean useSkillInnkeeper(){
        if (mState == State.TRADE){
            if (mMoney >= mCostVendorSkill){
                Mob mob = getMobs().get(mCardTableTargetID);
                if (mob.getSubType()==CardTableType.INNKEEPER){
                    mMoney -= mCostVendorSkill;
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
                    return true;
                }
            }
            itemList = mInventory.stream().filter(
                    itemInInventory -> itemInInventory.getIdItem() == itemID
            ).collect(Collectors.toList());
            if (!itemList.isEmpty()){
                mHP += item.getValueOne();
                mInventory.remove(itemList.get(0));
                return true;
            }
        }
        return false;
    }

    boolean useSpell(UseSpellRequest useSpellRequest) {
        List<CardInventory> inventoryClientList = useSpellRequest.getInventory();
        ArrayList<CardPlayer> inventoryServerList = new ArrayList<>();
        inventoryServerList.addAll(mInventory);
        addHund(inventoryServerList);
        if (inventoryServerList.containsAll(inventoryClientList)){
            mInventory.clear();
            for (CardInventory cardPlayer : inventoryClientList) {
                cardPlayer.setPlayer(this);
                mInventory.add(cardPlayer);
                Item item = DataBase.getItems().get(cardPlayer.getIdItem());
                tryChangeGearScore(item);
            }
            List<CardInventory> itemList = mInventory.stream().filter(
                    item -> item.getSlotId() == useSpellRequest.getSlotSpell()
            ).collect(Collectors.toList());
            CardInventory spell = itemList.get(0);
            Item item = DataBase.getItems().get(spell.getIdItem());
            if (item.getType()==InventoryType.SPELL){
                if (mState == State.COMBAT){
                    mCardTableTargetHP = (byte)(mCardTableTargetHP-item.getValueOne());
                    if (mCardTableTargetHP<1){
                        deadMob(getMobs().get(mCardTableTargetID));
                    }
                    mInventory.remove(spell);
                    return true;
                }
            }
        }
        return false;
    }

    void changeMoneyBank(int delta){
        mMoneyBank += delta;
    }

    boolean isLoginCoolDown(){
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

        mCardTableTargetID = null;
        mCardTableTargetHP = null;
        CardInventory[] inventory = new CardInventory[6];
        for (CardInventory item : mInventory) {
            inventory[item.getSlotId()] = item;
        }
        for (byte i = 0; mInventory.size() < INVENTORY_MAX_COUNT; i++){
            if (!mLoot.isEmpty() && i < INVENTORY_MAX_COUNT){
                if (inventory[i]==null){
                    Item item = DataBase.getItems().get(mLoot.get(0).getIdItem());
                    if (i!=4 && i!=5){
                        CardLoot loot = mLoot.get(0);
                        loot.setSlotId(i);
                        mInventory.add(new CardInventory(loot));
                        mLoot.remove(0);
                        tryChangeGearScore(item);
                    }
                    else{
                        if (item.isWeaponOrShield()){
                            CardLoot loot = mLoot.get(0);
                            loot.setSlotId(i);
                            mInventory.add(new CardInventory(loot));
                            mLoot.remove(0);
                            tryChangeGearScore(item);
                        }
                    }
                }
            }
            else{
                break;
            }
        }
    }

    private void deadMob(Mob mobTarget) {
        mState = State.SELECT_LOOT;
        mCountMobsToExit++;
        mCountMobsToVendor++;
        mMoney += mobTarget.getMoney();
        mStats.addExperience(mobTarget.getExperience());
        generateLoot();
    }

    private void generateLoot() {
        int[] typeLoot = new int[3];
        Mob mob = getMobs().get(mCardTableTargetID);
        Random random = new Random();
        int lootCount = 0;
        if (mob.getType()==CardTableType.MOB){
            if (mob.getSubType()!=CardTableSubType.FOREST){
                if (random.nextInt(mBalance.getCHANCE_WEAPON_OE_SHIELD())==0){
                    if (random.nextBoolean()/* && mob.getGearScore()>0*/){
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
                if (mGearScore>0){
                    typeLoot[i]=random.nextInt(4);
                }
                else{
                    typeLoot[i]=random.nextInt(3)+1;
                }
            }
        }
        for (byte i = 0; i < lootCount; i++) {
            mLoot.add(new CardLoot(this, getCardLoot((byte) typeLoot[i]), i));
            System.out.println("loot: "+mLoot.get(i).toString());
        }
    }

    private byte getCardLoot(byte type) {
        System.out.println("type: "+type);
        Random random = new Random();
        Mob mob = getMobs().get(mCardTableTargetID);
        int gearscore;
        if (mob.getType()==CardTableType.MOB){
            gearscore = mob.getGearScore();
        }
        else{
            gearscore = mGearScore;
        }
        List<Item> itemList = DataBase.getItems().values().stream().filter(
                item -> (item.getMobGearScore() == gearscore) && (item.getType() == type)
        ).collect(Collectors.toList());
        int positionInList = random.nextInt(itemList.size());
        return itemList.get(positionInList).getID();
    }

    private Mob[] getNextMobs() {
        Mob[] nextMobs = new Mob[4];
        nextMobs[0] = getMobs().get(mCardTable1);
        nextMobs[1] = getMobs().get(mCardTable3);
        nextMobs[2] = getMobs().get(mCardTable4);
        nextMobs[3] = getMobs().get(mCardTable6);
        return nextMobs;
    }

    private byte getCardTable() {
        Random random = new Random();
        if (mCountMobsToExit >= mBalance.getCARD_REQUIRED_TO_SPAWN_INNKEEPER()){
            if (random.nextBoolean()){
                mCountMobsToExit = 0;
                return ID_INNKEEPER;
            }
        }
        if (mCountMobsToVendor >= mBalance.getCARD_REQUIRED_TO_SPAWN_VENDOR()){
            if (random.nextBoolean()){
                mCountMobsToVendor = 0;
                return DataBase.VENDOR_ID.get(random.nextInt(DataBase.VENDOR_ID.size()));
            }
        }
/*
        boolean chest = random.nextInt(mBalance.getCHANCE_CHEST())==0;
*/
        boolean chest = false;
        if(chest){
            return ID_CHEST;
        }
        if (mMobList == null){
            mMobList = getMobsList();
        }
        return mMobList.get(random.nextInt(mMobList.size())).getID();
    }

    void tryChangeGearScore(Item item){
        if (item.isWeaponOrShield()){
            if (item.getGearScore() >= mTopTwoGearScoreWeaponOrShieldInInventory){
                changeGearScore();
            }
        }
    }

    private void changeGearScore() {
        ArrayList<Byte> gearScoreWeaponOrShieldInInventory = new ArrayList<>();
        gearScoreWeaponOrShieldInInventory.add((byte) 0);
        gearScoreWeaponOrShieldInInventory.add((byte) 0);
        for (CardInventory cardPlayer : mInventory) {
            Item item = DataBase.getItems().get(cardPlayer.getIdItem());
            if (item.isWeaponOrShield()){
                gearScoreWeaponOrShieldInInventory.add(item.getGearScore());
            }
        }
        sortGearScoreWeaponOrShieldInInventoryAndChangeGearScore(gearScoreWeaponOrShieldInInventory);
    }

    private void sortGearScoreWeaponOrShieldInInventoryAndChangeGearScore(ArrayList<Byte> gearScoreWeaponOrShieldInInventory) {
        int listSize = gearScoreWeaponOrShieldInInventory.size();
        Byte[] arrayGearScoreWeaponOrShieldInInventory = new Byte[listSize];
        gearScoreWeaponOrShieldInInventory.toArray(arrayGearScoreWeaponOrShieldInInventory);
        Arrays.sort(arrayGearScoreWeaponOrShieldInInventory);
        for (Byte gearScoreValue : arrayGearScoreWeaponOrShieldInInventory) {
            System.out.println("sort arrayGearScoreWeaponOrShieldInInventory: " + String.valueOf(gearScoreValue));
        }
        int newTopGearScoreWeaponOrShieldInInventory = arrayGearScoreWeaponOrShieldInInventory[listSize-1] +
                arrayGearScoreWeaponOrShieldInInventory[listSize-2];
        mGearScore += newTopGearScoreWeaponOrShieldInInventory -
                mTopOneGearScoreWeaponOrShieldInInventory - mTopTwoGearScoreWeaponOrShieldInInventory;
        mTopOneGearScoreWeaponOrShieldInInventory = arrayGearScoreWeaponOrShieldInInventory[listSize-1];
        mTopTwoGearScoreWeaponOrShieldInInventory = arrayGearScoreWeaponOrShieldInInventory[listSize-2];
    }

    private boolean checkStartInventory(Byte[] startItemId){
        short startItemCost = 0;
        Item handOne = DataBase.getItems().get(startItemId[4]);
        if (handOne.isWeaponOrShield()){
            Item handTwo = DataBase.getItems().get(startItemId[5]);
            if (handTwo.isWeaponOrShield()){
                startItemCost += handOne.getBuyCost();
                startItemCost += handTwo.getBuyCost();
                for (byte i = 0; i<INVENTORY_MAX_COUNT; i++) {
                    if (startItemId[i]!=null){
                        startItemCost += DataBase.getItems().get(startItemId[i]).getBuyCost();
                    }
                    else{
                        break;
                    }
                }
                if (startItemCost <= mMoneyBank){
                    mMoneyBank -= startItemCost;
                    return true;
                }
            }
        }
        return false;
    }

    @JsonIgnore
    private List<Mob> getMobsList() {
        return getMobs().values().stream().filter(
                mob -> (mob.getGearScore() <= mGearScore) &&
                        (mob.getGearScore() >= mGearScore-mBalance.getGEAR_SCORE_RANGE())
        ).collect(Collectors.toList());
    }
    @JsonIgnore
    Hashtable<Byte, Mob> getMobs(){
        if (MOBS == null){
            Session session = Util.getSessionFactory().openSession();
            List<Mob> mobs = session.createNativeQuery(
                    "SELECT * FROM mobs",
                    Mob.class
            ).getResultList();
            MOBS = new Hashtable<>();
            for (Mob mob : mobs) {
                MOBS.put(mob.getID(),mob);
            }
        }
        return MOBS;
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
    public Integer getCostVendorSkill() {
        return mCostVendorSkill;
    }
    public Stats getStats() {
        return mStats;
    }
    @JsonIgnore
    public Integer getGearScore() {
        return mGearScore;
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