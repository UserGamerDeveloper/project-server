package com.server;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="inventory")
public class CardInventory extends CardPlayer{
    public CardInventory() {}
    public CardInventory(Player player, byte idItem, byte slotId, boolean durabilityMax) {
        super(player, idItem, slotId, durabilityMax);
    }
    public CardInventory(Player player, byte idItem, byte slotId, byte durability) {
        super(player, idItem, slotId, durability);
    }
    public CardInventory(CardPlayer card) {
        super(card);
    }
}
