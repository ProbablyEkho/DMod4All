package dev.dmod4all;

import dev.dmod4all.client.KeybindRegistry;
import dev.dmod4all.client.RenameManager;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = DMod4All.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = DMod4All.MODID, value = Dist.CLIENT)
public class DMod4AllClient {
    public DMod4AllClient(IEventBus modEventBus) {
        KeybindRegistry.register(modEventBus);
        RenameManager.load();
    }

    @SubscribeEvent
    static void onItemTooltip(ItemTooltipEvent event) {
        String name = RenameManager.getName(event.getItemStack());

        if (name != null) {
            event.getToolTip().clear();
            event.getToolTip().add(Component.literal(name));
        }
    }
    @SubscribeEvent
    static void onTooltipRender(RenderTooltipEvent.Pre event) {
        if (RenameManager.getName(event.getItemStack()) == null) {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        KeybindRegistry.KeyPress();
    }
}
