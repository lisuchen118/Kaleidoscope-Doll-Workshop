package lsc.kaleidoscopeDollWorkshop.client.renderer;

import com.mojang.authlib.GameProfile;
import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.item.DollItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DollItemModel extends GeoModel<DollItem> {
    private static final ResourceLocation STEVE_MODEL = ResourceLocation.fromNamespaceAndPath(KaleidoscopeDollWorkshop.MOD_ID, "geo/steve.json");
    private static final ResourceLocation ALEX_MODEL = ResourceLocation.fromNamespaceAndPath(KaleidoscopeDollWorkshop.MOD_ID, "geo/alex.json");

    private GameProfile currentProfile;

    // 允许渲染器注入当前物品的 Profile
    public void setProfile(GameProfile profile) {
        this.currentProfile = profile;
    }

    @Override
    public ResourceLocation getModelResource(DollItem animatable) {
        if (currentProfile != null) {
            PlayerSkin skin = Minecraft.getInstance().getSkinManager().getInsecureSkin(currentProfile);
            return skin.model() == PlayerSkin.Model.SLIM ? ALEX_MODEL : STEVE_MODEL;
        }
        return STEVE_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(DollItem animatable) {
        if (currentProfile != null) {
            return Minecraft.getInstance().getSkinManager().getInsecureSkin(currentProfile).texture();
        }
        return DefaultPlayerSkin.getDefaultTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(DollItem animatable) {
        return null;
    }
}