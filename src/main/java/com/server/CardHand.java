package com.server;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="hand")
public class CardHand extends CardPlayer {
    public CardHand(byte idItem, byte slotId, byte durability) {
        super(idItem, slotId, durability);
    }

    public CardHand() {
    }
    public CardHand(Player player, byte idItem, byte slotId) {
        super(player, idItem, slotId);
    }
}
