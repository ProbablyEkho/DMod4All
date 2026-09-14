package dev.dmod4all.mixin;

import mod.adrenix.nostalgic.helper.sound.CaveSoundManager;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CaveSoundManager.class)
public class CaveSoundManagerMixin {
    @ModifyArg(method = "getSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/util/RandomSource;ZILnet/minecraft/client/resources/sounds/SoundInstance$Attenuation;DDDZ)V"), index = 1)
    private static SoundSource getRecordsSound(SoundSource source) {
        return SoundSource.RECORDS;
    }
}
