package com.server;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Random;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
abstract class CardPlayer {
    static final byte ID_FIST = 1;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int mId;
    @Column(name="idSlot")
    private byte mSlotId;
    @Column(name="idItem")
    private byte mIdItem;
    @Column(name="durability")
    private byte mDurability;
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "idPlayer", nullable = false)
    private Player mPlayer;

    public CardPlayer(Player player, byte idItem, byte slotId, boolean durabilityMax) {
        mPlayer = player;
        mIdItem = idItem;
        mSlotId = slotId;
        Item item = DataBase.getItems().get(idItem);
        if (item.getType()==InventoryType.SHIELD ||
                item.getType()==InventoryType.WEAPON){
            if (durabilityMax){
                mDurability = item.getDurabilityMax();
            }
            else{
                Random random = new Random();
                mDurability = (byte) ((byte) random.nextInt(DataBase.getItems().get(idItem).getDurabilityMax())+1);
            }
        }
    }
    public CardPlayer(Player player, byte idItem, byte slotId, byte durability) {
        mPlayer = player;
        mIdItem = idItem;
        mSlotId = slotId;
        mDurability = durability;
    }
    public CardPlayer() {}
    public CardPlayer(CardPlayer card) {
        this.mPlayer = card.getPlayer();
        this.mIdItem = card.getIdItem();
        this.mSlotId = card.getSlotId();
        this.mDurability = card.getDurability();
    }

    void decrementDurability(){
        mDurability--;
        if (mDurability<1){
            mPlayer.getInventory().remove(this);
            Item item = DataBase.getItems().get(mIdItem);
            mPlayer.tryChangeGearScore(item);
        }
    }

    public byte getSlotId() {
        return mSlotId;
    }
    public byte getIdItem() {
        return mIdItem;
    }
    public byte getDurability(){
        return mDurability;
    }
    @JsonIgnore
    public Player getPlayer() {
        return mPlayer;
    }
    public void setPlayer(Player player) {
        mPlayer = player;
    }
    public void setId(int id) {
        mId = id;
    }
    public void setSlotId(byte slotId) {
        mSlotId = slotId;
    }
    public void setIdItem(byte idItem) {
        mIdItem = idItem;
    }
    void setDurability(byte durability){
        this.mDurability = durability;
    }

    boolean isFist(){
        return mIdItem == ID_FIST;
    }

    boolean isWeaponOrShield(){
        Item item = DataBase.getItems().get(mIdItem);
        return item.getType()==InventoryType.WEAPON || item.getType()==InventoryType.SHIELD;
    }

    @Override
    public boolean equals(Object obj) {
        CardPlayer cardPlayer = (CardPlayer)obj;
        return mIdItem == cardPlayer.getIdItem() && mDurability == cardPlayer.getDurability();
    }

    @Override
    public String toString() {
        return "slot "+mSlotId+" id "+mIdItem+" durability "+mDurability;
    }
}