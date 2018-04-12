package com.server;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="inventory")
public class CardInventory extends CardPlayer{
    public CardInventory(byte idItem, byte slotId, byte durability) {
        super(idItem, slotId, durability);
    }
    public CardInventory() {
    }
    public CardInventory(Player player, byte idItem, byte slotId) {
        super(player, idItem, slotId);
    }
}
