package com.server;

import java.util.ArrayList;
import java.util.List;

public class ResponceTrade {
    private List<ItemResponse> mTrade;
    private Integer mSkillCost;

    public List<ItemResponse> getTrade() {
        return mTrade;
    }

    public void setTrade(List<ItemResponse> trade) {
        mTrade = trade;
    }

    public Integer getSkillCost() {
        return mSkillCost;
    }

    public void setSkillCost(Integer skillCost) {
        mSkillCost = skillCost;
    }
}
