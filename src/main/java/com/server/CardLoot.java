package com.server;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="loot")
public class CardLoot extends CardPlayer{
    public CardLoot(byte idItem, byte slotId, byte durability) {
        super(idItem, slotId, durability);
    }

    public CardLoot() {
    }
    public CardLoot(Player player, byte idItem, byte slotId) {
        super(player, idItem, slotId);
    }
}
