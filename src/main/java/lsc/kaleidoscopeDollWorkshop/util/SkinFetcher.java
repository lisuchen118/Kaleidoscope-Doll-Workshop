package lsc.kaleidoscopeDollWorkshop.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 把玩家名字解析成带真实 UUID 的档案。
 *
 * <p>存在的第一个理由：原版 {@code SkullBlockEntity.loadProperties} 内部对属性为空的档案会调用
 * {@code sessionService.fillProfileProperties(profile, true)} 去补全 textures，
 * 而该方法对 UUID 为 null 的档案会直接原样返回，什么都不填。
 * 模组原本传进去的是 {@code new GameProfile(null, name)}，于是皮肤数据永远拿不到。
 *
 * <p>存在的第二个理由（更隐蔽）：{@code loadProperties} 开头有一段短路——
 * 只要传入的档案「已 complete 且已带 textures」，它就直接原样回调，不再向 Mojang 查询。
 * 而服务端档案缓存里那份档案是玩家登录时塞进去的，带着当时的旧纹理，玩家换皮肤后不会更新，
 * 于是做出的玩偶会固化在旧皮肤上。所以这里刻意只保留 UUID 与名字、剥掉全部 properties，
 * 让原版流程必须去查一次当前皮肤。
 */
public final class SkinFetcher {

    private SkinFetcher() {
    }

    /**
     * 按名字解析档案，返回值只带真实 UUID 和名字，不含任何属性。
     *
     * <p>查不到或名字非法时返回 empty，调用方应保持原有状态而不是写入空属性。
     */
    public static CompletableFuture<Optional<GameProfile>> resolveByName(MinecraftServer server, String name) {
        if (server == null || name == null || name.trim().isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        CompletableFuture<Optional<GameProfile>> future = new CompletableFuture<>();
        try {
            // UserCache 是回调式 API，这里包成 CompletableFuture 方便串联后续的皮肤抓取
            server.getUserCache().findByNameAsync(name, optional -> future.complete(
                    optional.filter(profile -> profile.getId() != null)
                            // 只取身份信息，丢掉可能已经过期的 textures
                            .map(profile -> new GameProfile(profile.getId(), profile.getName()))));
        } catch (Throwable throwable) {
            future.complete(Optional.empty());
        }
        return future;
    }
}
