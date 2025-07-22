package derekahedron.invexp.loot;

import derekahedron.invexp.item.InvExpItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;

import java.util.concurrent.atomic.AtomicInteger;

public class InvExpLootTableModifiers {

    public static void initialize() {
        LootTableEvents.MODIFY.register((lootTable, tableBuilder, source, lookup) -> {
            if (source.isBuiltin()) {
                if (lootTable == LootTables.SIMPLE_DUNGEON_CHEST) {
                    AtomicInteger i = new AtomicInteger(0);
                    tableBuilder.modifyPools(builder -> {
                        if (i.getAndIncrement() == 0) {
                            builder.with(ItemEntry.builder(InvExpItems.SACK).weight(20));
                        }
                    });
                } else if (lootTable == LootTables.DESERT_PYRAMID_CHEST) {
                    AtomicInteger i = new AtomicInteger(0);
                    tableBuilder.modifyPools(builder -> {
                        if (i.getAndIncrement() == 0) {
                            builder.with(ItemEntry.builder(InvExpItems.SACK).weight(15));
                        }
                    });
                } else if (lootTable == LootTables.JUNGLE_TEMPLE_CHEST) {
                    AtomicInteger i = new AtomicInteger(0);
                    tableBuilder.modifyPools(builder -> {
                        if (i.getAndIncrement() == 0) {
                            builder.with(ItemEntry.builder(InvExpItems.SACK).weight(3));
                        }
                    });
                } else if (lootTable == LootTables.ANCIENT_CITY_CHEST) {
                    AtomicInteger i = new AtomicInteger(0);
                    tableBuilder.modifyPools(builder -> {
                        if (i.getAndIncrement() == 0) {
                            builder.with(ItemEntry.builder(InvExpItems.SACK).weight(2));
                        }
                    });
                } else if (lootTable == LootTables.TRIAL_CHAMBERS_REWARD_RARE_CHEST) {
                    AtomicInteger i = new AtomicInteger(0);
                    tableBuilder.modifyPools(builder -> {
                        if (i.getAndIncrement() == 0) {
                            builder.with(ItemEntry.builder(InvExpItems.SACK).weight(3));
                        }
                    });
                } else if (lootTable == LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE_CHEST) {
                    AtomicInteger i = new AtomicInteger(0);
                    tableBuilder.modifyPools(builder -> {
                        if (i.getAndIncrement() == 0) {
                            builder.with(ItemEntry.builder(InvExpItems.QUIVER).weight(3));
                        }
                    });
                }
            }
        });
    }
}
