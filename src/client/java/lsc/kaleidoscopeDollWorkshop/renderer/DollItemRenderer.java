package lsc.kaleidoscopeDollWorkshop.renderer;

import com.mojang.authlib.GameProfile;
import lsc.kaleidoscopeDollWorkshop.item.DollItem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DollItemRenderer extends GeoItemRenderer<DollItem> {

    public DollItemRenderer() {
        super(new DollItemModel());
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode transformType, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {

        DollItemModel model = (DollItemModel) this.getGeoModel();
        GameProfile profile = DollItem.getGameProfile(stack);
        model.setProfile(profile);

        super.render(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void renderRecursively(MatrixStack poseStack, DollItem animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
