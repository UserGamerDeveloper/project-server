package com.server;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="loot")
public class CardLoot extends CardPlayer{
    public CardLoot(byte idItem, byte slotId) {
        super(idItem, slotId);
    }

    public CardLoot(byte idItem, byte slotId, byte slotType, byte durability) {
        super(idItem, slotId, slotType, durability);
    }
}
