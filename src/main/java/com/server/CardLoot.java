package com.server;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="loot")
public class CardLoot extends CardPlayer{
    public CardLoot(){}
    public CardLoot(Player player, byte idItem, byte slotId) {
        super(player, idItem, slotId, false);
    }
    public CardLoot(Player player, byte idItem, byte slotId, byte durability) {
        super(player, idItem, slotId, durability);
    }
}
