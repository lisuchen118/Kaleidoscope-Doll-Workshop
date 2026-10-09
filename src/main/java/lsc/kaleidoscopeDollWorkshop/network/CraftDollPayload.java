package lsc.kaleidoscopeDollWorkshop.network;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CraftDollPayload(String targetName, boolean craftAll) implements CustomPacketPayload {
    public static final Type<CraftDollPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(KaleidoscopeDollWorkshop.MOD_ID, "craft_doll"));

    // 定义数据包的序列化/反序列化编解码器
    public static final StreamCodec<FriendlyByteBuf, CraftDollPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CraftDollPayload::targetName,
            ByteBufCodecs.BOOL, CraftDollPayload::craftAll,
            CraftDollPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}