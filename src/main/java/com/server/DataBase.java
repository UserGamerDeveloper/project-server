package com.server;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public final class DataBase {
    final static ArrayList<Byte> VENDOR_ID = new ArrayList<>(3);
    static Hashtable<Byte, Mob> MOBS;
    static Hashtable<Byte, Item> ITEMS;

    static {
        VENDOR_ID.add((byte) 1);
        VENDOR_ID.add((byte) 2);
        VENDOR_ID.add((byte) 3);

/*
     MOBS.put((byte)0, new Mob((byte) 0,0, (byte) 0,0, CardTableType.VENDOR, 9999, CardTableSubType.TRADER, 0));
        MOBS.put((byte)1, new Mob((byte) 1,0, (byte) 0,0, CardTableType.VENDOR, 9999, CardTableSubType.BLACKSMITH, 0));
        MOBS.put((byte)2, new Mob((byte) 2,0, (byte) 0,0, CardTableType.VENDOR, 9999, CardTableSubType.INNKEEPER, 0));
        MOBS.put((byte)4, new Mob((byte) 4,1, (byte) 1,1, CardTableType.MOB, 0, CardTableSubType.GOBLIN, 1));
        MOBS.put((byte)3, new Mob((byte) 3,4, (byte) 4,4, CardTableType.MOB, 0, CardTableSubType.GOBLIN, 4));
        MOBS.put((byte)8, new Mob((byte) 8,0, (byte) 0,1, CardTableType.CHEST, 9999, 0, 0));
        ITEMS.put((byte)0, new Item((byte) 0,(byte) 1,0, InventoryType.WEAPON, (byte) 0,999,(byte)0));
        ITEMS.put((byte)12, new Item((byte) 12,(byte) 2,2, InventoryType.WEAPON, (byte) 2,0,(byte)10));
        ITEMS.put((byte)7, new Item((byte) 7,(byte) 2,2, InventoryType.SHIELD, (byte) 2,0,(byte)10));
        ITEMS.put((byte)1, new Item((byte) 1,(byte) 1,0, InventoryType.FOOD, (byte) 1,0,(byte)0));
        ITEMS.put((byte)16, new Item((byte) 16,(byte) 1,1, InventoryType.SPELL, (byte) 1,0,(byte)0));
*/
    }

    static Hashtable<Byte, Mob> getMobs(){
        Session session = Util.getSessionFactory().openSession();
        List<Mob> mobs = session.createNativeQuery(
                "SELECT * FROM mobs",
                Mob.class
        ).getResultList();
        MOBS = new Hashtable<>();
        for (Mob mob : mobs) {
            MOBS.put(mob.getID(),mob);
        }
        return MOBS;
    }
    static Hashtable<Byte, Item> getItems(){
        Session session = Util.getSessionFactory().openSession();
        List<Item> items = session.createNativeQuery(
                "SELECT * FROM items",
                Item.class
        ).getResultList();
        ITEMS = new Hashtable<>();
        for (Item item : items) {
            ITEMS.put(item.getID(),item);
        }
        return ITEMS;
    }
}
