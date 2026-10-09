package lsc.kaleidoscopeDollWorkshop.client.renderer;

import lsc.kaleidoscopeDollWorkshop.block.entity.DollBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DollBlockEntityRenderer extends GeoBlockRenderer<DollBlockEntity> {
    public DollBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new DollBlockModel());
    }
}