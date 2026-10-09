package lsc.kaleidoscopeDollWorkshop.client.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Accessories 帽子槽位 (hat) 的玩偶渲染器。
 * 参考 Accessories 官方文档: https://docs.wispforest.io/legacy/accessories/developer/rendering_api/
 */
public class DollAccessoryRenderer implements AccessoryRenderer {

    @Override
    public <M extends LivingEntity> void render(ItemStack stack,
                                                SlotReference reference,
                                                PoseStack poseStack,
                                                EntityModel<M> model,
                                                MultiBufferSource multiBufferSource,
                                                int light,
                                                float limbSwing,
                                                float limbSwingAmount,
                                                float partialTicks,
                                                float ageInTicks,
                                                float netHeadYaw,
                                                float headPitch) {
        // 确保当前渲染的实体模型是具有头部骨骼的二足模型（如玩家）
        if (model instanceof HumanoidModel<?> humanoidModel) {
            poseStack.pushPose();

            // 1. 将渲染矩阵对齐到实体模型的头部骨骼，使其跟随玩家头部的旋转与俯仰
            humanoidModel.head.translateAndRotate(poseStack);

            // 2. 模拟原版头部装备栏 (HeadLayer) 的矩阵变换
            poseStack.translate(0.0F, -0.25F, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.scale(0.625F, -0.625F, -0.625F);

            // 3. 调用原生的物品渲染，传入 HEAD 模式，复用原有的物品 GeckoLib 渲染逻辑和配置
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack,
                    ItemDisplayContext.HEAD,
                    light,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    multiBufferSource,
                    reference.entity().level(),
                    0
            );

            poseStack.popPose();
        }
    }
}
