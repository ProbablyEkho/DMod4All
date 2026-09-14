package dev.dmod4all.mixin;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Spider.class)
public class SpiderMixin {
    @Redirect(method = "finalizeSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Spider$SpiderEffectsGroupData;setRandomEffect(Lnet/minecraft/util/RandomSource;)V"))
    private void noRandomEffects(Spider.SpiderEffectsGroupData instance, RandomSource random) {
        // spider effects are lost media :sob:
    }
}
