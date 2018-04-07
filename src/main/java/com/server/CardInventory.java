package com.server;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="inventory")
public class CardInventory extends CardPlayer{
    public CardInventory(byte idItem, byte slotId) {
        super(idItem, slotId);
    }

    public CardInventory(byte idItem, byte slotId, byte slotType, byte durability) {
        super(idItem, slotId, slotType, durability);
    }
}
