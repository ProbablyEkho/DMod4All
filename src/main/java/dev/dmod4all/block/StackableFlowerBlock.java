package dev.dmod4all.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.TriState;

public class StackableFlowerBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<StackableFlowerBlock> CODEC = simpleCodec(StackableFlowerBlock::new);
    public static final int MIN_FLOWERS = 1;
    public static final int MAX_FLOWERS = 4;
    public static final IntegerProperty AMOUNT = BlockStateProperties.FLOWER_AMOUNT;
    protected MapCodec<? extends BushBlock> codec() { return CODEC; }

    protected StackableFlowerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AMOUNT, MIN_FLOWERS));
    }

    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        return !blockPlaceContext.isSecondaryUseActive() && blockPlaceContext.getItemInHand().is(this.asItem()) && blockState.getValue(AMOUNT) < 4 || super.canBeReplaced(blockState, blockPlaceContext);
    }

    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Block.box(6.0F - blockState.getValue(AMOUNT), 0.0F, 6.0F - blockState.getValue(AMOUNT), 10.0F + blockState.getValue(AMOUNT), 10.0F, 10.0F + blockState.getValue(AMOUNT));
    }

    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockState blockstate = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos());
        return blockstate.is(this) ? blockstate.setValue(AMOUNT, Math.min(4, blockstate.getValue(AMOUNT) + 1)) : this.defaultBlockState();
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AMOUNT);
    }

    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return blockState.getValue(AMOUNT) < MAX_FLOWERS;
    }

    protected boolean isRandomlyTicking(BlockState blockState){
        return blockState.getValue(AMOUNT) < MAX_FLOWERS;
    }

    protected void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (serverLevel.isAreaLoaded(blockPos, 1)) {
            if (serverLevel.getRawBrightness(blockPos, 0) >= 9) {
                if (blockState.getValue(AMOUNT) < MAX_FLOWERS) {
                    float f = getGrowthSpeed(blockState, serverLevel, blockPos);
                    if (CommonHooks.canCropGrow(serverLevel, blockPos, blockState, randomSource.nextInt((int)(25.0F / f) + 1) == 0)) {
                        serverLevel.setBlock(blockPos, blockState.setValue(AMOUNT, blockState.getValue(AMOUNT) + 1), 2);
                        CommonHooks.fireCropGrowPost(serverLevel, blockPos, blockState);
                    }
                }
            }

        }
    }

    protected static float getGrowthSpeed(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        Block block = blockState.getBlock();
        float f = 1.0F;
        BlockPos blockpos = blockPos.below();

        for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
                float f1;
                label77: {
                    f1 = 0.0F;
                    BlockState blockstate = blockGetter.getBlockState(blockpos.offset(i, 0, j));
                    TriState soilDecision = blockstate.canSustainPlant(blockGetter, blockpos.offset(i, 0, j), Direction.UP, blockState);
                    if (soilDecision.isDefault()) {
                        if (!(blockstate.getBlock() instanceof FarmBlock)) {
                            break label77;
                        }
                    } else if (!soilDecision.isTrue()) {
                        break label77;
                    }

                    f1 = 1.0F;
                    if (blockstate.isFertile(blockGetter, blockPos.offset(i, 0, j))) {
                        f1 = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        BlockPos blockpos1 = blockPos.north();
        BlockPos blockpos2 = blockPos.south();
        BlockPos blockpos3 = blockPos.west();
        BlockPos blockpos4 = blockPos.east();
        boolean flag = blockGetter.getBlockState(blockpos3).is(block) || blockGetter.getBlockState(blockpos4).is(block);
        boolean flag1 = blockGetter.getBlockState(blockpos1).is(block) || blockGetter.getBlockState(blockpos2).is(block);
        if (flag && flag1) {
            f /= 2.0F;
        } else {
            boolean flag2 = blockGetter.getBlockState(blockpos3.north()).is(block) || blockGetter.getBlockState(blockpos4.north()).is(block) || blockGetter.getBlockState(blockpos4.south()).is(block) || blockGetter.getBlockState(blockpos3.south()).is(block);
            if (flag2) {
                f /= 2.0F;
            }
        }

        return f;
    }

    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        int i = blockState.getValue(AMOUNT) + Mth.nextInt(serverLevel.random, 1, 3);
        if (i > MAX_FLOWERS) {
            i = MAX_FLOWERS;
        }

        serverLevel.setBlock(blockPos, blockState.setValue(AMOUNT, i), 2);
    }
}
