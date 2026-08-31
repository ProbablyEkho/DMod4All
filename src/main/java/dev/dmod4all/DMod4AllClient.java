package dev.dmod4all;

import dev.dmod4all.client.FlowerMixingManager;
import dev.dmod4all.client.KeybindRegistry;
import dev.dmod4all.client.RenameManager;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = DMod4All.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = DMod4All.MODID, value = Dist.CLIENT)
public class DMod4AllClient {
    private static final ResourceLocation CAVE_SHADER = ResourceLocation.fromNamespaceAndPath(DMod4All.MODID, "soretro");
    private static boolean shaderApplied = false;

    public DMod4AllClient(IEventBus modEventBus, ModContainer modContainer) {
        KeybindRegistry.register(modEventBus);
        RenameManager.load();
        FlowerMixingManager.load();
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
    @SubscribeEvent
    static void onItemTooltip(ItemTooltipEvent event) {
        if(Config.VANILLA_TOOLTIPS.get()) return;
        String name = RenameManager.getName(event.getItemStack());
        if (name != null) {
            event.getToolTip().clear();
            event.getToolTip().add(Component.literal(name));
        }
    }
    @SubscribeEvent
    static void onTooltipRender(RenderTooltipEvent.Pre event) {
        if(Config.VANILLA_TOOLTIPS.get()) return;
        if (RenameManager.getName(event.getItemStack()) == null) {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    static void keybindRegistry(ClientTickEvent.Post event) {
        KeybindRegistry.KeyPress();
    }
    @SubscribeEvent
    static void shaderApplication(ClientTickEvent.Post event) {
        if(Minecraft.getInstance().player != null) {
            ShaderProgram shaderProgram = VeilRenderSystem.renderer().getShaderManager().getShader(CAVE_SHADER);
            if(!shaderApplied) {
                VeilRenderSystem.renderer().getPostProcessingManager().add(CAVE_SHADER);
                shaderApplied = true;
            }
            if(shaderProgram != null && shaderProgram.isValid()) {
                float playerY = (float)Minecraft.getInstance().player.getY();
                shaderProgram.getUniform("Resolution").setFloat(256F);
                shaderProgram.getUniform("MosaicSize").setFloat(1F);
                shaderProgram.getUniform("PlayerY").setFloat(Math.clamp(-(float) Minecraft.getInstance().player.getY() / 64F, 0F, 1F));
            }
        }
    }
}
