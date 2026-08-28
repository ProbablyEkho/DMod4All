package dev.dmod4all.mixin;

import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Screen.class)
public class ScreenMixin {
    @ModifyArg(method = "renderMenuBackgroundTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIFFIIII)V"), index = 5)
    private static float scrollingDirtBg(float vOffset) {
        return vOffset + (Util.getMillis() / 200F);
    }
}