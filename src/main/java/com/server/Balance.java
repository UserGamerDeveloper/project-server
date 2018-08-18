package com.server;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="balance")
class Balance {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int mID;
    @Column(name="GSR")
    private int GEAR_SCORE_RANGE;
    @Column(name="CH")
    private byte CHANCE_HALT;
    @Column(name="CV")
    private byte CHANCE_VENDOR;
    @Column(name="CC")
    private byte CHANCE_CHEST;
    @Column(name="CF")
    private byte CHANCE_FOOD;
    @Column(name="CS")
    private byte CHANCE_SPELL;
    @Column(name="CWOS")
    private byte CHANCE_WEAPON_OE_SHIELD;
    @Column(name="MCVS")
    private int MIN_COST_VENDOR_SKILL;
    @Column(name="HPD")
    private byte HP_DEFAULT;
    @Column(name="CRP")
    private byte CARD_REQUIRED_TO_SPAWN_INNKEEPER;
    @Column(name="CRV")
    private byte CARD_REQUIRED_TO_SPAWN_VENDOR;
    @Column(name="VSC")
    private float VENDOR_SKILL_COST_K;

    @Column(name="GSPS")
    private byte GEAR_SCORE_PER_STAT;
    @Column(name="HPBPS")
    private byte HP_BONUS_PER_STAT;
    @Column(name="RC")
    private short RESET_COST;
    @Column(name="LVL1")
    private Float LVL1;
    @Column(name="LVL2")
    private Float LVL2;
    @Column(name="LVL3")
    private Float LVL3;
    @Column(name="LVL4")
    private Float LVL4;
    @Column(name="LVL5")
    private Float LVL5;
    @Column(name="LVL6")
    private Float LVL6;
    @Column(name="LVL7")
    private Float LVL7;
    @Column(name="LVL8")
    private Float LVL8;
    @Column(name="LVL9")
    private Float LVL9;
    @Column(name="LVL10")
    private Float LVL10;
    @Column(name="LVL11")
    private Float LVL11;
    @Column(name="LVL12")
    private Float LVL12;
    @Column(name="LVL13")
    private Float LVL13;
    @Column(name="LVL14")
    private Float LVL14;
    @Column(name="LVL15")
    private Float LVL15;
    @Column(name="LVL16")
    private Float LVL16;
    @Column(name="LVL17")
    private Float LVL17;
    @Column(name="LVL18")
    private Float LVL18;
    @Column(name="LVL19")
    private Float LVL19;
    @Column(name="LVL20")
    private Float LVL20;

    @OneToOne(mappedBy = "mBalance")
    private Player mPlayer;

    public float getVENDOR_SKILL_COST_K() {
        return VENDOR_SKILL_COST_K;
    }

    public void setVENDOR_SKILL_COST_K(float VENDOR_SKILL_COST_K) {
        this.VENDOR_SKILL_COST_K = VENDOR_SKILL_COST_K;
    }

    public byte getCARD_REQUIRED_TO_SPAWN_INNKEEPER() {
        return CARD_REQUIRED_TO_SPAWN_INNKEEPER;
    }

    public void setCARD_REQUIRED_TO_SPAWN_INNKEEPER(byte CARD_REQUIRED_TO_SPAWN_INNKEEPER) {
        this.CARD_REQUIRED_TO_SPAWN_INNKEEPER = CARD_REQUIRED_TO_SPAWN_INNKEEPER;
    }
    public byte getCARD_REQUIRED_TO_SPAWN_VENDOR() {
        return CARD_REQUIRED_TO_SPAWN_VENDOR;
    }

    public void setCARD_REQUIRED_TO_SPAWN_VENDOR(byte CARD_REQUIRED_TO_SPAWN_VENDOR) {
        this.CARD_REQUIRED_TO_SPAWN_VENDOR = CARD_REQUIRED_TO_SPAWN_VENDOR;
    }

    float[] getREQUIREMENT_EXPERIENCE(){
        return new float[]{
                mPlayer.getBalance().getLVL1(),
                mPlayer.getBalance().getLVL2(),
                mPlayer.getBalance().getLVL3(),
                mPlayer.getBalance().getLVL4(),
                mPlayer.getBalance().getLVL5(),
                mPlayer.getBalance().getLVL6(),
                mPlayer.getBalance().getLVL7(),
                mPlayer.getBalance().getLVL8(),
                mPlayer.getBalance().getLVL9(),
                mPlayer.getBalance().getLVL10(),
                mPlayer.getBalance().getLVL11(),
                mPlayer.getBalance().getLVL12(),
                mPlayer.getBalance().getLVL13(),
                mPlayer.getBalance().getLVL14(),
                mPlayer.getBalance().getLVL15(),
                mPlayer.getBalance().getLVL16(),
                mPlayer.getBalance().getLVL17(),
                mPlayer.getBalance().getLVL18(),
                mPlayer.getBalance().getLVL19(),
                mPlayer.getBalance().getLVL20()
        };
    }

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
    }

    public int getGEAR_SCORE_RANGE() {
        return GEAR_SCORE_RANGE;
    }

    public void setGEAR_SCORE_RANGE(int GEAR_SCORE_RANGE) {
        this.GEAR_SCORE_RANGE = GEAR_SCORE_RANGE;
    }

    public byte getCHANCE_HALT() {
        return CHANCE_HALT;
    }

    public void setCHANCE_HALT(byte CHANCE_HALT) {
        this.CHANCE_HALT = CHANCE_HALT;
    }

    public byte getCHANCE_VENDOR() {
        return CHANCE_VENDOR;
    }

    public void setCHANCE_VENDOR(byte CHANCE_VENDOR) {
        this.CHANCE_VENDOR = CHANCE_VENDOR;
    }

    public byte getCHANCE_CHEST() {
        return CHANCE_CHEST;
    }

    public void setCHANCE_CHEST(byte CHANCE_CHEST) {
        this.CHANCE_CHEST = CHANCE_CHEST;
    }

    public byte getCHANCE_FOOD() {
        return CHANCE_FOOD;
    }

    public void setCHANCE_FOOD(byte CHANCE_FOOD) {
        this.CHANCE_FOOD = CHANCE_FOOD;
    }

    public byte getCHANCE_SPELL() {
        return CHANCE_SPELL;
    }

    public void setCHANCE_SPELL(byte CHANCE_SPELL) {
        this.CHANCE_SPELL = CHANCE_SPELL;
    }

    public byte getCHANCE_WEAPON_OE_SHIELD() {
        return CHANCE_WEAPON_OE_SHIELD;
    }

    public void setCHANCE_WEAPON_OE_SHIELD(byte CHANCE_WEAPON_OE_SHIELD) {
        this.CHANCE_WEAPON_OE_SHIELD = CHANCE_WEAPON_OE_SHIELD;
    }

    public int getMIN_COST_VENDOR_SKILL() {
        return MIN_COST_VENDOR_SKILL;
    }

    public void setMIN_COST_VENDOR_SKILL(int MIN_COST_VENDOR_SKILL) {
        this.MIN_COST_VENDOR_SKILL = MIN_COST_VENDOR_SKILL;
    }

    public byte getHP_DEFAULT() {
        return HP_DEFAULT;
    }

    public void setHP_DEFAULT(byte HP_DEFAULT) {
        this.HP_DEFAULT = HP_DEFAULT;
    }

    public byte getGEAR_SCORE_PER_STAT() {
        return GEAR_SCORE_PER_STAT;
    }

    public void setGEAR_SCORE_PER_STAT(byte GEAR_SCORE_PER_STAT) {
        this.GEAR_SCORE_PER_STAT = GEAR_SCORE_PER_STAT;
    }

    public byte getHP_BONUS_PER_STAT() {
        return HP_BONUS_PER_STAT;
    }

    public void setHP_BONUS_PER_STAT(byte HP_BONUS_PER_STAT) {
        this.HP_BONUS_PER_STAT = HP_BONUS_PER_STAT;
    }

    public short getRESET_COST() {
        return RESET_COST;
    }

    public void setRESET_COST(short RESET_COST) {
        this.RESET_COST = RESET_COST;
    }

    public Float getLVL1() {
        return LVL1;
    }

    public void setLVL1(Float LVL1) {
        this.LVL1 = LVL1;
    }

    public Float getLVL2() {
        return LVL2;
    }

    public void setLVL2(Float LVL2) {
        this.LVL2 = LVL2;
    }

    public Float getLVL3() {
        return LVL3;
    }

    public void setLVL3(Float LVL3) {
        this.LVL3 = LVL3;
    }

    public Float getLVL4() {
        return LVL4;
    }

    public void setLVL4(Float LVL4) {
        this.LVL4 = LVL4;
    }

    public Float getLVL5() {
        return LVL5;
    }

    public void setLVL5(Float LVL5) {
        this.LVL5 = LVL5;
    }

    public Float getLVL6() {
        return LVL6;
    }

    public void setLVL6(Float LVL6) {
        this.LVL6 = LVL6;
    }

    public Float getLVL7() {
        return LVL7;
    }

    public void setLVL7(Float LVL7) {
        this.LVL7 = LVL7;
    }

    public Float getLVL8() {
        return LVL8;
    }

    public void setLVL8(Float LVL8) {
        this.LVL8 = LVL8;
    }

    public Float getLVL9() {
        return LVL9;
    }

    public void setLVL9(Float LVL9) {
        this.LVL9 = LVL9;
    }

    public Float getLVL10() {
        return LVL10;
    }

    public void setLVL10(Float LVL10) {
        this.LVL10 = LVL10;
    }

    public Float getLVL11() {
        return LVL11;
    }

    public void setLVL11(Float LVL11) {
        this.LVL11 = LVL11;
    }

    public Float getLVL12() {
        return LVL12;
    }

    public void setLVL12(Float LVL12) {
        this.LVL12 = LVL12;
    }

    public Float getLVL13() {
        return LVL13;
    }

    public void setLVL13(Float LVL13) {
        this.LVL13 = LVL13;
    }

    public Float getLVL14() {
        return LVL14;
    }

    public void setLVL14(Float LVL14) {
        this.LVL14 = LVL14;
    }

    public Float getLVL15() {
        return LVL15;
    }

    public void setLVL15(Float LVL15) {
        this.LVL15 = LVL15;
    }

    public Float getLVL16() {
        return LVL16;
    }

    public void setLVL16(Float LVL16) {
        this.LVL16 = LVL16;
    }

    public Float getLVL17() {
        return LVL17;
    }

    public void setLVL17(Float LVL17) {
        this.LVL17 = LVL17;
    }

    public Float getLVL18() {
        return LVL18;
    }

    public void setLVL18(Float LVL18) {
        this.LVL18 = LVL18;
    }

    public Float getLVL19() {
        return LVL19;
    }

    public void setLVL19(Float LVL19) {
        this.LVL19 = LVL19;
    }

    public Float getLVL20() {
        return LVL20;
    }

    public void setLVL20(Float LVL20) {
        this.LVL20 = LVL20;
    }

    @JsonIgnore
    public Player getPlayer() {
        return mPlayer;
    }

    public void setPlayer(Player player) {
        mPlayer = player;
    }
}
