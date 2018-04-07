package com.server;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="hand")
public class CardHand extends CardPlayer {

    public CardHand(byte idItem, byte slotId) {
        super(idItem, slotId);
    }

    public CardHand(byte idItem, byte slotId, byte slotType, byte durability) {
        super(idItem, slotId, slotType, durability);
    }
}
