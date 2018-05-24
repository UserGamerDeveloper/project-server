package com.server;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="trade")
public class CardTrade extends CardPlayer{

    public CardTrade(Player player, byte idItem, byte slotId, boolean durabilityMax) {
        super(player, idItem, slotId, durabilityMax);
    }

    public CardTrade(Player player, byte idItem, byte slotId, byte durability) {
        super(player, idItem, slotId, durability);
    }

    public CardTrade() {}
}
