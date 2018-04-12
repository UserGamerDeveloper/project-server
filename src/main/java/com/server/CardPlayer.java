package com.server;

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

    public CardPlayer(Player player, byte idItem, byte slotId) {
        mPlayer = player;
        mIdItem = idItem;
        mSlotId = slotId;
        mDurability = DataBase.ITEMS.get(idItem).getDurability();
    }
    public CardPlayer(byte idItem, byte slotId, byte durability) {
        mIdItem = idItem;
        mSlotId = slotId;
        mDurability = durability;
    }
    public CardPlayer() {
    }

    void copy(CardPlayer card){
        this.mDurability = card.getDurability();
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
    void setDurability(byte durability){
        this.mDurability = durability;
    }
}