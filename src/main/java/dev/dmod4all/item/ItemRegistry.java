package dev.dmod4all.item;

import dev.dmod4all.DMod4All;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DMod4All.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}