package com.server;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="inventory")
public class CardInventory extends CardPlayer{
    public CardInventory() {}
    public CardInventory(Player player, byte idItem, byte slotId) {
        super(player, idItem, slotId);
    }
    public CardInventory(CardLoot cardLoot) {
        super(cardLoot.getPlayer(), cardLoot.getIdItem(), cardLoot.getSlotId(), cardLoot.getDurability());
    }
    public CardInventory(Player player, byte idItem, byte slotId, byte durability) {
        super(player, idItem, slotId, durability);
    }
}
