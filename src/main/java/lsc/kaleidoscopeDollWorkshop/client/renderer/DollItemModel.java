package lsc.kaleidoscopeDollWorkshop.client.renderer;

import com.mojang.authlib.GameProfile;
import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.item.DollItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DollItemModel extends GeoModel<DollItem> {
    @SuppressWarnings("removal")
    private static final ResourceLocation STEVE_MODEL = new ResourceLocation(KaleidoscopeDollWorkshop.MOD_ID, "geo/steve.json");
    @SuppressWarnings("removal")
    private static final ResourceLocation ALEX_MODEL = new ResourceLocation(KaleidoscopeDollWorkshop.MOD_ID, "geo/alex.json");

    private GameProfile currentProfile;

    public void setProfile(GameProfile profile) {
        this.currentProfile = profile;
    }

    @Override
    public ResourceLocation getModelResource(DollItem object) {
        return SkinTextureCache.isSlim(this.currentProfile) ? ALEX_MODEL : STEVE_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(DollItem object) {
        return SkinTextureCache.get(this.currentProfile);
    }

    @Override
    public ResourceLocation getAnimationResource(DollItem animatable) {
        return null;
    }
}
