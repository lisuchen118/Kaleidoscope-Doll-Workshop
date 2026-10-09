package lsc.kaleidoscopeDollWorkshop.renderer;

import lsc.kaleidoscopeDollWorkshop.block.entity.DollBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DollBlockEntityRenderer extends GeoBlockRenderer<DollBlockEntity> {
    public DollBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new DollBlockModel());
    }
}