package dev.dmod4all.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceBlock.class)
public class FenceBlockMixin {
    @Inject(method = "connectsTo", at = @At("RETURN"), cancellable = true)
    public void lessConnectiveFences(BlockState state, boolean isSideSolid, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(state.getBlock() instanceof FenceBlock);
    }
}
