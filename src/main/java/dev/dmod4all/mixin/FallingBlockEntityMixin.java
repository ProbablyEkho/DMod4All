package dev.dmod4all.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.SoundType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void fallSound(CallbackInfo ci) {
        FallingBlockEntity fallingBlockEntity = (FallingBlockEntity)(Object)this;
        BlockPos blockPos = fallingBlockEntity.blockPosition();
        SoundType soundType = fallingBlockEntity.getBlockState().getSoundType(fallingBlockEntity.level(), blockPos, fallingBlockEntity);
        fallingBlockEntity.level().playSound(null, blockPos, soundType.getPlaceSound(), SoundSource.BLOCKS, soundType.getVolume(), soundType.getPitch());
    }
}
