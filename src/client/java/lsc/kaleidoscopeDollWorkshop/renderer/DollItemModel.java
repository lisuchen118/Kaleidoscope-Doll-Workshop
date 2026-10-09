package lsc.kaleidoscopeDollWorkshop.renderer;

import com.mojang.authlib.GameProfile;
import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.item.DollItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DollItemModel extends GeoModel<DollItem> {
    private static final Identifier STEVE_MODEL = new Identifier(KaleidoscopeDollWorkshop.MOD_ID, "geo/steve.json");
    private static final Identifier ALEX_MODEL = new Identifier(KaleidoscopeDollWorkshop.MOD_ID, "geo/alex.json");

    private GameProfile currentProfile;

    public void setProfile(GameProfile profile) {
        this.currentProfile = profile;
    }

    @Override
    public Identifier getModelResource(DollItem object) {
        return SkinTextureCache.isSlim(this.currentProfile) ? ALEX_MODEL : STEVE_MODEL;
    }

    @Override
    public Identifier getTextureResource(DollItem object) {
        return SkinTextureCache.get(this.currentProfile);
    }

    @Override
    public Identifier getAnimationResource(DollItem animatable) {
        return null;
    }
}
