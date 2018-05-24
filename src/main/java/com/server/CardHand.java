package com.server;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="hand")
public class CardHand extends CardPlayer {
    public CardHand() {}
    public CardHand(Player player, byte idItem, byte slotId) {
        super(player, idItem, slotId, false);
    }
    public CardHand(Player player, byte idItem, byte slotId, byte durability) {
        super(player, idItem, slotId, durability);
    }
}
