package lsc.kaleidoscopeDollWorkshop.compat;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class DollTrinketRenderer implements TrinketRenderer {

    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        // 确保当前渲染的实体模型是具有头部骨骼的二足模型（如玩家）
        if (contextModel instanceof BipedEntityModel<?> bipedModel) {
            matrices.push();

            // 1. 将渲染矩阵对齐到实体模型的头部骨骼，使其跟随玩家头部的旋转与俯仰
            bipedModel.head.rotate(matrices);

            // 2. 模拟原版头部装备栏 (HeadFeatureRenderer) 的矩阵变换
            matrices.translate(0.0F, -0.25F, 0.0F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            matrices.scale(0.625F, -0.625F, -0.625F);

            // 3. 调用原生的物品渲染，传入 HEAD 模式，复用原有的物品 GeckoLib 渲染逻辑和配置
            MinecraftClient.getInstance().getItemRenderer().renderItem(
                    stack,
                    ModelTransformationMode.HEAD,
                    light,
                    net.minecraft.client.render.OverlayTexture.DEFAULT_UV,
                    matrices,
                    vertexConsumers,
                    entity.getWorld(),
                    0
            );

            matrices.pop();
        }
    }
}