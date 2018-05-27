package com.server;

import java.util.ArrayList;
import java.util.List;

public class ResponceTrade {
    private List<CardTrade> mTrade;
    private Integer mSkillCost;

    public List<CardTrade> getTrade() {
        return mTrade;
    }

    public void setTrade(List<CardTrade> trade) {
        mTrade = trade;
    }

    public Integer getSkillCost() {
        return mSkillCost;
    }

    public void setSkillCost(Integer skillCost) {
        mSkillCost = skillCost;
    }
}
