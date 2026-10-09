package lsc.kaleidoscopeDollWorkshop.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 把玩家名字解析成带真实 UUID 的完整档案。
 *
 * <p>存在的唯一理由：原版 {@code SkullBlockEntity.loadProperties} 内部对属性为空的档案会调用
 * {@code sessionService.fillProfileProperties(profile, true)} 去补全 textures，
 * 而该方法对 UUID 为 null 的档案会直接原样返回，什么都不填。
 * 模组原本传进去的是 {@code new GameProfile(null, name)}，于是皮肤数据永远拿不到。
 *
 * <p>这里先用服务端档案缓存（自带持久化与过期策略）把真实 UUID 查出来，
 * 再把带 UUID 的档案交回原版流程去取皮肤。
 */
public final class SkinFetcher {

    private SkinFetcher() {
    }

    /**
     * 按名字解析档案。查不到、名字非法或服务端档案缓存不可用时返回 empty，
     * 调用方应保持原有状态而不是写入空属性。
     */
    public static CompletableFuture<Optional<GameProfile>> resolveByName(MinecraftServer server, String name) {
        if (server == null || name == null || name.trim().isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        CompletableFuture<Optional<GameProfile>> future = new CompletableFuture<>();
        try {
            // UserCache 是回调式 API，这里包成 CompletableFuture 方便串联后续的皮肤抓取
            server.getUserCache().findByNameAsync(name, optional ->
                    future.complete(optional.filter(profile -> profile.getId() != null)));
        } catch (Throwable throwable) {
            future.complete(Optional.empty());
        }
        return future;
    }
}
