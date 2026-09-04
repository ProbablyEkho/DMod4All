package dev.dmod4all.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StairBlock.class)
public class StairBlockMixin {
    @Inject(method = "getStairsShape", at = @At("RETURN"), cancellable = true)
    private static void noCornerStairs(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<StairsShape> cir) {
        cir.setReturnValue(StairsShape.STRAIGHT);
    }
}
