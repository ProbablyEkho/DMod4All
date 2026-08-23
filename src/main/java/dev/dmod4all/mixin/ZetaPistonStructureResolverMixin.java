package dev.dmod4all.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.violetmoon.zeta.api.ICollateralMover;
import org.violetmoon.zeta.piston.ZetaPistonStructureResolver;

import java.util.List;

@Mixin(ZetaPistonStructureResolver.class)
public class ZetaPistonStructureResolverMixin {
    @Shadow @Final private Level world;
    @Shadow @Final private BlockPos pistonPos;
    @Shadow @Final private Direction moveDirection;
    @Shadow @Final private List<BlockPos> myToPush;
    @Shadow @Final private List<BlockPos> myToDestroy;
    @Shadow
    private void reorderListAtCollision(int collisionEnd, int collisionStart) {}
    @Shadow private ICollateralMover.MoveResult addBranchingBlocks(Level world, BlockPos fromPos, boolean branching) { throw new AssertionError(); }
    @Shadow private boolean isBlockBranching(Level world, BlockPos pos) { throw new AssertionError(); }
    @Shadow private ICollateralMover.MoveResult getBranchResult(Level world, BlockPos pos) { throw new AssertionError(); }
    @Shadow private ICollateralMover.MoveResult getStickCompatibility(Level world, BlockState a, BlockState b, BlockPos pa, BlockPos pb) { throw new AssertionError(); }

    /**
     * @author ProbablyEkho
     * @reason Makes piston destroy blocks pushed onto non-pushable ones
     */
    @Overwrite
    private boolean addBlockLine(BlockPos origin, Direction face) {
        int max = ZetaPistonStructureResolver.GlobalSettings.getPushLimit();
        BlockPos target = origin;
        BlockState state = this.world.getBlockState(origin);
        if (!state.isAir() && PistonBaseBlock.isPushable(state, this.world, origin, this.moveDirection, false, face) && !origin.equals(this.pistonPos) && !this.myToPush.contains(origin)) {
            int lineLen = 1;
            if (lineLen + this.myToPush.size() > max) {
                return false;
            } else {
                BlockPos oldPos = origin;
                BlockState oldState = this.world.getBlockState(origin);
                boolean skippingNext = false;

                while(this.isBlockBranching(this.world, target)) {
                    ICollateralMover.MoveResult res = this.getBranchResult(this.world, target);
                    if (res == ICollateralMover.MoveResult.PREVENT) {
                        return false;
                    }

                    if (res != ICollateralMover.MoveResult.MOVE) {
                        skippingNext = true;
                        break;
                    }

                    target = origin.relative(this.moveDirection.getOpposite(), lineLen);
                    state = this.world.getBlockState(target);
                    if (state.isAir() || !oldState.canStickTo(state) || !state.canStickTo(oldState) || !PistonBaseBlock.isPushable(state, this.world, target, this.moveDirection, false, this.moveDirection.getOpposite()) || target.equals(this.pistonPos) || this.getStickCompatibility(this.world, state, oldState, target, oldPos) != ICollateralMover.MoveResult.MOVE) {
                        break;
                    }

                    oldState = state;
                    oldPos = target;
                    ++lineLen;
                    if (lineLen + this.myToPush.size() > max) {
                        return false;
                    }
                }

                int collisionEnd = 0;

                for(int j = lineLen - 1; j >= 0; --j) {
                    BlockPos movePos = origin.relative(this.moveDirection.getOpposite(), j);
                    if (this.myToDestroy.contains(movePos)) {
                        break;
                    }

                    this.myToPush.add(movePos);
                    ++collisionEnd;
                }

                if (!skippingNext) {
                    int offset = 1;

                    boolean doneFinding;
                    do {
                        BlockPos currentPos = origin.relative(this.moveDirection, offset);
                        int collisionStart = this.myToPush.indexOf(currentPos);
                        if (collisionStart > -1) {
                            this.reorderListAtCollision(collisionEnd, collisionStart);

                            for (int i = 0; i <= collisionStart + collisionEnd; ++i) {
                                BlockPos collidingPos = this.myToPush.get(i);
                                if (this.addBranchingBlocks(this.world, collidingPos, this.isBlockBranching(this.world, collidingPos)) == ICollateralMover.MoveResult.PREVENT) {
                                    return false;
                                }
                            }

                            return true;
                        }

                        state = this.world.getBlockState(currentPos);
                        if (state.isAir()) {
                            return true;
                        }

                        if (!PistonBaseBlock.isPushable(state, this.world, currentPos, this.moveDirection, true, this.moveDirection) || currentPos.equals(this.pistonPos)) {
                            BlockPos destroyPos = this.myToPush.getLast();
                            this.myToDestroy.add(destroyPos);
                            this.myToPush.remove(destroyPos);
                            return true;
                        }

                        if (state.getPistonPushReaction() == PushReaction.DESTROY) {
                            this.myToDestroy.add(currentPos);
                            return true;
                        }

                        doneFinding = false;
                        if (this.isBlockBranching(this.world, currentPos)) {
                            ICollateralMover.MoveResult res = this.getBranchResult(this.world, currentPos);
                            if (res == ICollateralMover.MoveResult.PREVENT) {
                                return false;
                            }

                            if (res != ICollateralMover.MoveResult.MOVE) {
                                doneFinding = true;
                            }
                        }

                        if (this.myToPush.size() >= max) {
                            return false;
                        }

                        this.myToPush.add(currentPos);
                        ++collisionEnd;
                        ++offset;
                    } while (!doneFinding);

                }
                return true;
            }
        } else {
            return true;
        }
    }
}
