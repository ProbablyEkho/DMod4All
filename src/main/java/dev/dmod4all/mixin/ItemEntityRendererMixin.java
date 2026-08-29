package dev.dmod4all.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.math.Axis;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.GameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin {
    @ModifyExpressionValue(
        method = {"render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
        at = @At(value = "INVOKE", target = "Lcom/mojang/math/Axis;rotation(F)Lorg/joml/Quaternionf;")
    )

    private Quaternionf setFullItemRotation(Quaternionf quaternion, ItemEntity entity) {
        if (!(Boolean)CandyTweak.OLD_2D_ITEMS.get()) {
            var camera = Minecraft.getInstance().gameRenderer.getMainCamera();

            boolean isModelFlat = GameUtil.isModelFlat(entity.getItem());
            return isModelFlat ? Axis.YP.rotationDegrees(180.0F - camera.getYRot()).mul(Axis.XN.rotationDegrees(camera.getXRot())) : quaternion;
        } else {
            return quaternion;
        }
    }
}
