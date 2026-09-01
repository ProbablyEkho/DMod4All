package dev.dmod4all.mixin;

import net.minecraft.world.entity.item.PrimedTnt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PrimedTnt.class)
public class PrimedTntMixin {
    @ModifyConstant(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/entity/LivingEntity;)V", constant = @Constant(intValue = 80))
    private int longerFuse(int fuse) {
        return 100;
    }
}
