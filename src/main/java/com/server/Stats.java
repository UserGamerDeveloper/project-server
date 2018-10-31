package com.server;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="stats")
class Stats {

    static class Request {
        private byte mDamagePoints;
        private byte mDefencePoints;
        private byte mHPPoints;

        public byte getDamagePoints() {
            return mDamagePoints;
        }
        public byte getDefencePoints() {
            return mDefencePoints;
        }
        public byte getHPPoints() {
            return mHPPoints;
        }

        public void setDamagePoints(byte damagePoints) {
            mDamagePoints = damagePoints;
        }
        public void setDefencePoints(byte defencePoints) {
            mDefencePoints = defencePoints;
        }
        public void setHPPoints(byte HPPoints) {
            mHPPoints = HPPoints;
        }
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int mID;
    @Column(name="level")
    private byte mLevel;
    @Column(name="experienceValue")
    private float mExperienceValue;
    @Column(name="damagePoints")
    private byte mDamagePoints;
    @Column(name="defencePoints")
    private byte mDefencePoints;
    @Column(name="HPPoints")
    private byte mHPPoints;
    @Column(name="points")
    private byte mPoints;

    @OneToOne(mappedBy = "mStats")
    private Player mPlayer;

    Stats() {}
    Stats(Player player) {
        mLevel = 0;
        mExperienceValue = 0;
        mDamagePoints = 0;
        mDefencePoints = 0;
        mHPPoints = 0;
        mPoints = 0;
        mPlayer = player;
    }

    public byte getLevel() {
        return mLevel;
    }
    public float getExperienceValue() {
        return mExperienceValue;
    }
    public byte getDamagePoints() {
        return mDamagePoints;
    }
    public byte getDefencePoints() {
        return mDefencePoints;
    }
    public byte getHPPoints() {
        return mHPPoints;
    }
    public byte getPoints() {
        return mPoints;
    }

    void addExperience(int experience) {
        mExperienceValue += experience;
        if (mExperienceValue >= mPlayer.getBalance().getREQUIREMENT_EXPERIENCE()[mLevel]) {
            mExperienceValue -= mPlayer.getBalance().getREQUIREMENT_EXPERIENCE()[mLevel];
            mLevel++;
            mPoints++;
        }
    }

    void addDamagePoint() {
        mDamagePoints++;
        mPoints--;
    }

    void addDefencePoint() {
        mDefencePoints++;
        mPoints--;
    }

    void addHPPoint() {
        mHPPoints++;
        mPoints--;
    }

    boolean confirm(Request request) {
        if (mPlayer.getState()==State.NONE){
            if (mDamagePoints > request.getDamagePoints()){
                return false;
            }
            byte points = 0;
            points += request.getDamagePoints()-mDamagePoints;
            if (mDefencePoints > request.getDefencePoints()){
                return false;
            }
            points += request.getDefencePoints()-mDefencePoints;
            if (mHPPoints > request.getHPPoints()){
                return false;
            }
            points += request.getHPPoints()-mHPPoints;
            if (mPoints>=points)
            {
                mDamagePoints = request.getDamagePoints();
                mDefencePoints = request.getDefencePoints();
                mHPPoints = request.getHPPoints();
                mPoints -= points;
                return true;
            }
        }
        return false;
    }

    boolean reset() {
        if (mPlayer.getState()!=State.NONE){
            if (mPlayer.getMoneyBank()>=mPlayer.getBalance().getRESET_COST()){
                mPlayer.changeMoneyBank(-mPlayer.getBalance().getRESET_COST());
                mPoints = mLevel;
                mDamagePoints = 0;
                mDefencePoints = 0;
                mHPPoints = 0;
                return true;
            }
        }
        return false;
    }

    int getGearScoreBonus() {
        return (mHPPoints + mDamagePoints + mDefencePoints) * mPlayer.getBalance().getGEAR_SCORE_PER_STAT();
    }

    int getDamageBonus() {
        return mDamagePoints;
    }

    int getDefenceBonus() {
        return mDefencePoints;
    }

    int getHPBonus() {
        return mHPPoints * mPlayer.getBalance().getHP_BONUS_PER_STAT();
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public void setPlayer(Player player) {
        mPlayer = player;
    }
}