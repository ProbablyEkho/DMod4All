package dev.dmod4all.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.entity.Mob.checkMobSpawnRules;
import static net.minecraft.world.entity.monster.Monster.isDarkEnoughToSpawn;

@Mixin(Slime.class)
public class SlimeMixin {
    @Unique
    protected final RandomSource random = RandomSource.create();
    @Unique
    private static boolean checkMonsterlikeSpawnRules(EntityType<Slime> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL
            && ((MobSpawnType.ignoresLightRequirements(spawnType) || isDarkEnoughToSpawn((ServerLevelAccessor) level, pos, random)) || (((ServerLevelAccessor) level).getLevel().isRainingAt(pos)))
            && checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    @Inject(method = "checkSlimeSpawnRules", at = @At("HEAD"), cancellable = true)
    private static void normalSlimeSpawnRules(EntityType<Slime> slime, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(checkMonsterlikeSpawnRules(slime, level, spawnType, pos, random));
    }

    @Inject(method = "getJumpDelay", at = @At("HEAD"), cancellable = true)
    private void lowerJumpDelay(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.random.nextInt(10) + 5);
    }


    @Inject(method = "isDealsDamage", at = @At("HEAD"), cancellable = true)
    private void smallSlimeDamage(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @ModifyArg(method = "setSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;setBaseValue(D)V", ordinal = 1))
    private double setValue(double baseValue) {
        return 0.45F + 0.1F * ((baseValue - 0.2) / 0.1);
    }
}