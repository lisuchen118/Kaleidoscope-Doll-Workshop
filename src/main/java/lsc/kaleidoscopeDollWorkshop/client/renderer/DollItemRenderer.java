package lsc.kaleidoscopeDollWorkshop.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import lsc.kaleidoscopeDollWorkshop.item.DollItem;
import lsc.kaleidoscopeDollWorkshop.registry.ModDataComponents;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DollItemRenderer extends GeoItemRenderer<DollItem> {
    public DollItemRenderer() {
        super(new DollItemModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // 从物品组件中获取皮肤数据
        ResolvableProfile resolvable = stack.get(ModDataComponents.DOLL_OWNER.get());

        DollItemModel model = (DollItemModel) this.getGeoModel();

        if (resolvable != null) {
            model.setProfile(resolvable.gameProfile());
        } else {
            model.setProfile(null);
        }

        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
}