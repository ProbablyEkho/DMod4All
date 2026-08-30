package dev.dmod4all.block;

import com.mojang.serialization.MapCodec;
import dev.dmod4all.sound.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;
import java.util.function.Function;

public class FaceBlock extends Block {
    public static final MapCodec<FaceBlock> CODEC = simpleCodec(properties1 -> new FaceBlock("block.dmod4all.face.revenite_talk", properties1));
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty TALK = BooleanProperty.create("talk");
    public final String dialogue;
    protected MapCodec<? extends Block> codec() { return CODEC; }

    public FaceBlock(String dialogue, BlockBehaviour.Properties properties) {
        super(properties);
        this.dialogue = dialogue;
        this.registerDefaultState(this.stateDefinition.any().setValue(TALK, false).setValue(FACING, Direction.NORTH));
    }

    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TALK, FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if(blockState.getValue(TALK)) {
            return InteractionResult.PASS;
        }
        if(!level.isClientSide) {
            level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable(this.dialogue).withStyle(style -> style.withColor(16686585)), false);
            level.setBlock(blockPos, blockState.setValue(TALK, true), 3);
            level.playSound(null, blockPos, SoundRegistry.FACE_TALK.get(), SoundSource.BLOCKS);
            level.scheduleTick(blockPos, this, 100);
        }
        return InteractionResult.SUCCESS;
    }

    protected void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        serverLevel.removeBlock(blockPos, false);
        serverLevel.explode(null, null, new SimpleExplosionDamageCalculator(false, true, Optional.of(5F), BuiltInRegistries.BLOCK.getTag(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 5F, false, Level.ExplosionInteraction.BLOCK);
    }
}
