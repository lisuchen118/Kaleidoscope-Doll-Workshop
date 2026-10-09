package lsc.kaleidoscopeDollWorkshop.client.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * Curios 帽子槽位 (head) 的玩偶渲染器。
 * 参考 Curios 官方文档: https://docs.illusivesoulworks.com/1.20.x/curios/items/rendering-registry
 */
public class DollCurioRenderer implements ICurioRenderer {

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack,
                                                                          SlotContext slotContext,
                                                                          PoseStack poseStack,
                                                                          RenderLayerParent<T, M> renderLayerParent,
                                                                          MultiBufferSource renderTypeBuffer,
                                                                          int light,
                                                                          float limbSwing,
                                                                          float limbSwingAmount,
                                                                          float partialTicks,
                                                                          float ageInTicks,
                                                                          float netHeadYaw,
                                                                          float headPitch) {
        // 确保当前渲染的实体模型是具有头部骨骼的二足模型（如玩家）
        if (renderLayerParent.getModel() instanceof HumanoidModel<?> humanoidModel) {
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
                    renderTypeBuffer,
                    slotContext.entity().level(),
                    0
            );

            poseStack.popPose();
        }
    }
}
