package com.server;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="stats")
class Stats {
    private final static float[] mRequirementExperience = {};
    private final static byte mGearScorePerStat = 1;
    private final static byte mHPBonus = 1;
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

    Stats() {
        mLevel = 0;
        mExperienceValue = 0;
        mDamagePoints = 0;
        mDefencePoints = 0;
        mHPPoints = 0;
        mPoints = 0;
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
    void setPoints(byte points) {
        mPoints = points;
    }

    void addExperience(int experience) {
        mExperienceValue += experience;
        if (mExperienceValue >= mRequirementExperience[mLevel]) {
            mExperienceValue -= mRequirementExperience[mLevel];
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

    void reset() {
        setPoints(mLevel);
        mDamagePoints = 0;
        mDefencePoints = 0;
        mHPPoints = 0;
    }

    int getGearScoreBonus() {
        return (mHPPoints + mDamagePoints + mDefencePoints) * mGearScorePerStat;
    }

    int getDamageBonus() {
        return mDamagePoints;
    }

    int getDefenceBonus() {
        return mDefencePoints;
    }

    int getHPBonus() {
        return mHPPoints * mHPBonus;
    }
}