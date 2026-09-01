package dev.dmod4all.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BasePressurePlateBlock.class)
public abstract class BasePressurePlateBlockMixin extends Block {
    public BasePressurePlateBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void reShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(this.getSignalForState(blockState) > 0 ? Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D) : Block.box(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D));
    }

    @Shadow
    protected abstract int getSignalForState(BlockState state);
}
