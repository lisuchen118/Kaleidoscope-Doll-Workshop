package lsc.kaleidoscopeDollWorkshop.client.renderer;

import com.mojang.authlib.GameProfile;
import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.block.entity.DollBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DollBlockModel extends GeoModel<DollBlockEntity> {
    private static final ResourceLocation STEVE_MODEL = ResourceLocation.fromNamespaceAndPath(KaleidoscopeDollWorkshop.MOD_ID, "geo/steve.json");
    private static final ResourceLocation ALEX_MODEL = ResourceLocation.fromNamespaceAndPath(KaleidoscopeDollWorkshop.MOD_ID, "geo/alex.json");

    @Override
    public ResourceLocation getModelResource(DollBlockEntity animatable) {
        GameProfile profile = animatable.getOwnerProfile();
        if (profile != null) {
            // 获取皮肤信息
            PlayerSkin skin = Minecraft.getInstance().getSkinManager().getInsecureSkin(profile);
            // 如果是纤细模型 (Slim)，则返回 Alex 模型
            if (skin.model() == PlayerSkin.Model.SLIM) {
                return ALEX_MODEL;
            }
        }
        return STEVE_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(DollBlockEntity animatable) {
        GameProfile profile = animatable.getOwnerProfile();
        if (profile != null) {
            // 返回玩家皮肤的 ResourceLocation
            return Minecraft.getInstance().getSkinManager().getInsecureSkin(profile).texture();
        }
        return DefaultPlayerSkin.getDefaultTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(DollBlockEntity animatable) {
        return null;
    }
}