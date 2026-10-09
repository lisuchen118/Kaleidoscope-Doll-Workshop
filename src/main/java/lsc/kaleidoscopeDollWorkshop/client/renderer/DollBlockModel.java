package lsc.kaleidoscopeDollWorkshop.client.renderer;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.block.entity.DollBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DollBlockModel extends GeoModel<DollBlockEntity> {
    @SuppressWarnings("removal")
    private static final ResourceLocation STEVE_MODEL = new ResourceLocation(KaleidoscopeDollWorkshop.MOD_ID, "geo/steve.json");
    @SuppressWarnings("removal")
    private static final ResourceLocation ALEX_MODEL = new ResourceLocation(KaleidoscopeDollWorkshop.MOD_ID, "geo/alex.json");

    @Override
    public ResourceLocation getModelResource(DollBlockEntity object) {
        return SkinTextureCache.isSlim(object.getOwnerProfile()) ? ALEX_MODEL : STEVE_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(DollBlockEntity object) {
        return SkinTextureCache.get(object.getOwnerProfile());
    }

    @Override
    public ResourceLocation getAnimationResource(DollBlockEntity animatable) {
        return null;
    }
}
