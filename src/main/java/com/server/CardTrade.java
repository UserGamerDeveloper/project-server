package com.server;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="trade")
public class CardTrade extends CardPlayer{

    public CardTrade(Player player, byte idItem, byte slotId) {
        super(player, idItem, slotId);
    }

    public CardTrade(Player player, byte idItem, byte slotId, byte durability) {
        super(player, idItem, slotId, durability);
    }

    public CardTrade() {}
}
