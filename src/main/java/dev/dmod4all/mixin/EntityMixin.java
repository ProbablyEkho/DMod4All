package dev.dmod4all.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Unique
    Entity entity = (Entity)(Object)this;
    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    private void pushableTnt(CallbackInfoReturnable<Boolean> cir) {
        if(entity instanceof PrimedTnt) cir.setReturnValue(true);
    }
}
