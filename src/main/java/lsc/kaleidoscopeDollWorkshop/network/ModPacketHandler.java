package lsc.kaleidoscopeDollWorkshop.network;

import com.mojang.authlib.GameProfile;
import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.menu.ComputerMenu;
import lsc.kaleidoscopeDollWorkshop.registry.ModDataComponents;
import lsc.kaleidoscopeDollWorkshop.registry.ModItems;
import net.minecraft.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPacketHandler {

    // 注册网络载荷处理器
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(KaleidoscopeDollWorkshop.MOD_ID);

        registrar.playToServer(
                CraftDollPayload.TYPE,
                CraftDollPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player && player.containerMenu instanceof ComputerMenu menu) {
                        handleCrafting(menu, payload);
                    }
                })
        );
    }

    // 核心制作逻辑
    private static void handleCrafting(ComputerMenu menu, CraftDollPayload payload) {
        ItemStack inputStack = menu.getSlot(0).getItem();

        // 安全检查：输入必须是羊毛
        if (inputStack.isEmpty() || !inputStack.is(ItemTags.WOOL)) return;

        // 计算制作数量
        int craftAmount = payload.craftAll() ? inputStack.getCount() : 1;
        ItemStack resultStack = new ItemStack(ModItems.PLAYER_DOLL_ITEM.get(), craftAmount);

        // 创建初始 Profile（仅包含名字）
        GameProfile initialProfile = new GameProfile(Util.NIL_UUID, payload.targetName());
        resultStack.set(ModDataComponents.DOLL_OWNER.get(), new ResolvableProfile(initialProfile));

        ItemStack existingOutput = menu.getSlot(1).getItem();

        if (existingOutput.isEmpty()) {
            // 输出槽为空，直接放置并扣除材料
            menu.getSlot(1).set(resultStack);
            inputStack.shrink(craftAmount);
            // 异步加载皮肤数据
            fetchAndApplySkin(menu, payload.targetName());
        } else {
            // 输出槽不为空，执行堆叠检查
            boolean isSameItem = existingOutput.is(ModItems.PLAYER_DOLL_ITEM.get());
            boolean isSameName = false;

            ResolvableProfile existingProfile = existingOutput.get(ModDataComponents.DOLL_OWNER.get());
            if (existingProfile != null && existingProfile.name().isPresent()) {
                if (existingProfile.name().get().equals(payload.targetName())) {
                    isSameName = true;
                }
            }

            // 仅当物品相同且名字相同时允许堆叠（忽略 UUID 差异）
            if (isSameItem && isSameName) {
                int totalCount = existingOutput.getCount() + craftAmount;
                if (totalCount <= existingOutput.getMaxStackSize()) {
                    existingOutput.setCount(totalCount);
                    inputStack.shrink(craftAmount);
                }
            }
        }
    }

    // 异步获取玩家皮肤完整数据 (UUID + Texture)
    private static void fetchAndApplySkin(ComputerMenu menu, String targetName) {
        SkullBlockEntity.fetchGameProfile(targetName).thenAccept(optionalProfile -> {
            optionalProfile.ifPresent(fullProfile -> {
                ItemStack currentOutput = menu.getSlot(1).getItem();
                // 再次检查输出槽是否匹配，防止玩家中途取出物品
                if (!currentOutput.isEmpty() && currentOutput.is(ModItems.PLAYER_DOLL_ITEM.get())) {
                    ResolvableProfile existingRes = currentOutput.get(ModDataComponents.DOLL_OWNER.get());
                    if (existingRes != null && existingRes.name().isPresent() &&
                            existingRes.name().get().equals(targetName)) {
                        currentOutput.set(ModDataComponents.DOLL_OWNER.get(), new ResolvableProfile(fullProfile));
                    }
                }
            });
        });
    }
}