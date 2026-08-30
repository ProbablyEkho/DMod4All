package dev.dmod4all.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class KeybindRegistry {
    public static final KeyMapping RENAME = new KeyMapping(
            "key.dmod4all.rename",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_R,
            KeyMapping.CATEGORY_MISC
    );
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(KeybindRegistry::registerKeyMappings);
    }
    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(RENAME);
    }
    public static void KeyPress() {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.screen == null) {
            if(RENAME.consumeClick()) {
                ItemStack itemStack = minecraft.player.getMainHandItem();

                if(!itemStack.isEmpty()) {
                    minecraft.setScreen(new RenameScreen(minecraft.player, itemStack));
                }
            }
        }
    }
}
