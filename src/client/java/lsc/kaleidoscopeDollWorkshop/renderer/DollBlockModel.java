package lsc.kaleidoscopeDollWorkshop.renderer;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.block.entity.DollBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DollBlockModel extends GeoModel<DollBlockEntity> {
    private static final Identifier STEVE_MODEL = new Identifier(KaleidoscopeDollWorkshop.MOD_ID, "geo/steve.json");
    private static final Identifier ALEX_MODEL = new Identifier(KaleidoscopeDollWorkshop.MOD_ID, "geo/alex.json");

    @Override
    public Identifier getModelResource(DollBlockEntity object) {
        return SkinTextureCache.isSlim(object.getOwnerProfile()) ? ALEX_MODEL : STEVE_MODEL;
    }

    @Override
    public Identifier getTextureResource(DollBlockEntity object) {
        return SkinTextureCache.get(object.getOwnerProfile());
    }

    @Override
    public Identifier getAnimationResource(DollBlockEntity animatable) {
        return null;
    }
}
