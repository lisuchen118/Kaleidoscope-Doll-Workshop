package lsc.kaleidoscopeDollWorkshop.renderer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 直接从物品/方块 NBT 里的 {@code textures} 属性解析并托管玩偶皮肤纹理。
 *
 * <p>刻意不经过 {@code PlayerSkinProvider}：皮肤补丁类模组会接管那条路径，并且对
 * 同一个 {@link GameProfile} 可能返回空纹理（实测：NBT 里 textures 属性存在，
 * 但 {@code getTextures} 不返回 SKIN），导致玩偶退化成默认皮肤。
 * 玩偶的皮肤数据本来就完整地存在 NBT 里，直接用它即可。
 *
 * <p>URL 到纹理标识符的映射带缓存，同一个皮肤只会下载并注册一次。
 */
public final class SkinTextureCache {

    private static final Identifier DEFAULT_TEXTURE =
            new Identifier("minecraft", "textures/entity/player/wide/steve.png");

    private static final String TEXTURES_KEY = "textures";

    /** 皮肤 URL → 已注册的纹理标识符。 */
    private static final Map<String, Identifier> TEXTURES = new ConcurrentHashMap<>();

    /** 档案 → 皮肤数据，避免每帧重复解码 base64 与解析 JSON。 */
    private static final Map<GameProfile, SkinData> PARSED = new ConcurrentHashMap<>();

    /** 正在下载中的 URL，避免每帧重复发起请求。 */
    private static final Set<String> PENDING = ConcurrentHashMap.newKeySet();

    private SkinTextureCache() {
    }

    /**
     * 取该档案对应的皮肤纹理。
     *
     * <p>数据缺失或尚未下载完成时返回默认皮肤；下载完成后会自动切换到真实皮肤。
     */
    public static Identifier get(GameProfile profile) {
        SkinData data = skinData(profile);
        if (data == null) {
            return DEFAULT_TEXTURE;
        }

        Identifier cached = TEXTURES.get(data.url());
        if (cached != null) {
            return cached;
        }

        requestDownload(data.url());
        return DEFAULT_TEXTURE;
    }

    /** 判断该档案是否使用瘦模型（slim）。 */
    public static boolean isSlim(GameProfile profile) {
        SkinData data = skinData(profile);
        return data != null && "slim".equals(data.model());
    }

    private static SkinData skinData(GameProfile profile) {
        if (profile == null) {
            return null;
        }
        SkinData cached = PARSED.get(profile);
        if (cached != null) {
            return cached == SkinData.EMPTY ? null : cached;
        }

        SkinData parsed = readSkinData(profile);
        PARSED.put(profile, parsed == null ? SkinData.EMPTY : parsed);
        return parsed;
    }

    private static void requestDownload(String url) {
        if (!PENDING.add(url)) {
            return; // 已经在下载中
        }

        Thread worker = new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(10_000);
                connection.setReadTimeout(10_000);
                connection.setRequestProperty("User-Agent", "kaleidoscope_doll_workshop");

                NativeImage image;
                try (InputStream in = connection.getInputStream()) {
                    image = NativeImage.read(in);
                }

                Identifier id = skinIdentifier(url);
                // 纹理上传必须在渲染线程执行
                MinecraftClient.getInstance().execute(() -> {
                    MinecraftClient.getInstance().getTextureManager()
                            .registerTexture(id, new NativeImageBackedTexture(image));
                    TEXTURES.put(url, id);
                });
            } catch (Exception e) {
                // 下载失败就静默放弃，等待下次渲染时重试
                PENDING.remove(url);
            }
        }, "kaleidoscope-doll-skin");
        worker.setDaemon(true);
        worker.start();
    }

    private static Identifier skinIdentifier(String url) {
        return new Identifier(KaleidoscopeDollWorkshop.MOD_ID, "doll_skin/" + Integer.toHexString(url.hashCode()));
    }

    /** 从 base64 的 textures 属性里解出皮肤 URL 与模型类型。 */
    private static SkinData readSkinData(GameProfile profile) {
        if (profile == null) {
            return null;
        }

        Property property = profile.getProperties().get(TEXTURES_KEY).stream().findFirst().orElse(null);
        if (property == null || property.getValue() == null || property.getValue().isEmpty()) {
            return null;
        }

        try {
            String json = new String(Base64.getDecoder().decode(property.getValue()), StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject textures = root.getAsJsonObject("textures");
            if (textures == null) {
                return null;
            }
            JsonObject skin = textures.getAsJsonObject("SKIN");
            if (skin == null || skin.get("url") == null) {
                return null;
            }

            JsonElement metadata = skin.get("metadata");
            String model = metadata != null && metadata.isJsonObject() && metadata.getAsJsonObject().has("model")
                    ? metadata.getAsJsonObject().get("model").getAsString()
                    : null;

            return new SkinData(skin.get("url").getAsString(), model);
        } catch (Exception e) {
            return null; // 数据损坏时安静地退化成默认皮肤
        }
    }

    private record SkinData(String url, String model) {
        /** 占位符，用于记住「这个档案没有可用的皮肤数据」。 */
        private static final SkinData EMPTY = new SkinData("", null);
    }
}
