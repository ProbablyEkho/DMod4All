package dev.dmod4all.block.entity;

import dev.dmod4all.DMod4All;
import dev.dmod4all.block.BlockRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DMod4All.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BasketBlockEntity>> BASKET = BLOCK_ENTITIES.register("basket",
			() -> BlockEntityType.Builder.of(BasketBlockEntity::new, BlockRegistry.BASKET.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
