package lsc.kaleidoscopeDollWorkshop.network.packet;

import com.mojang.authlib.GameProfile;
import lsc.kaleidoscopeDollWorkshop.item.DollItem;
import lsc.kaleidoscopeDollWorkshop.menu.ComputerMenu;
import lsc.kaleidoscopeDollWorkshop.registry.ModItems;
import lsc.kaleidoscopeDollWorkshop.util.SkinFetcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketCraftDoll {
    private final String targetName;
    private final boolean craftAll; // 是否执行批量合成

    public PacketCraftDoll(String targetName, boolean craftAll) {
        this.targetName = targetName;
        this.craftAll = craftAll;
    }

    public PacketCraftDoll(FriendlyByteBuf buf) {
        this.targetName = buf.readUtf();
        this.craftAll = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(targetName);
        buf.writeBoolean(craftAll);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof ComputerMenu menu) {
                handleCrafting(menu, player);
            }
        });
        return true;
    }

    private void handleCrafting(ComputerMenu menu, ServerPlayer player) {
        ItemStack inputStack = menu.blockEntity.getItemHandler().getStackInSlot(0);
        ItemStack outputStack = menu.blockEntity.getItemHandler().getStackInSlot(1);

        // 验证输入槽是否有羊毛
        if (!inputStack.is(ItemTags.WOOL)) return;

        int craftAmount = 0;
        boolean isNewStack = false;

        // 计算合成数量
        if (outputStack.isEmpty()) {
            isNewStack = true;
            // 如果批量制作，则数量为输入数量与堆叠上限的较小值；否则为1
            craftAmount = craftAll ? Math.min(inputStack.getCount(), ModItems.PLAYER_DOLL.get().getMaxStackSize()) : 1;
        } else if (outputStack.getItem() == ModItems.PLAYER_DOLL.get()) {
            GameProfile profile = DollItem.getGameProfile(outputStack);
            // 验证输出槽物品的NBT数据与目标玩家名是否一致
            if (profile != null && targetName.equals(profile.getName())) {
                int space = outputStack.getMaxStackSize() - outputStack.getCount();
                if (space > 0) {
                    // 如果批量制作，取输入数量与剩余空间的较小值；否则为1
                    craftAmount = craftAll ? Math.min(inputStack.getCount(), space) : 1;
                }
            }
        }

        // 执行物品消耗与生成
        if (craftAmount > 0) {
            inputStack.shrink(craftAmount);

            if (isNewStack) {
                ItemStack doll = new ItemStack(ModItems.PLAYER_DOLL.get(), craftAmount);
                GameProfile temp = new GameProfile(null, targetName);

                CompoundTag nbt = new CompoundTag();
                nbt.put("Owner", NbtUtils.writeGameProfile(new CompoundTag(), temp));
                doll.setTag(nbt);

                menu.blockEntity.getItemHandler().setStackInSlot(1, doll);
                // 异步更新皮肤数据
                updateSkinDataAsync(menu, player, targetName);
            } else {
                outputStack.grow(craftAmount);
            }
        }
    }

    private void updateSkinDataAsync(ComputerMenu menu, ServerPlayer player, String targetName) {
        // 先按名字查出真实 UUID：UUID 为 null 的档案会被 fillProfileProperties 直接短路，拿不到任何皮肤数据
        SkinFetcher.resolveByName(player.server, targetName).thenAccept(optionalProfile -> {
            if (optionalProfile.isEmpty()) {
                // 查不到（离线名字、无外网等）：保持只有名字的档案，交给原版默认皮肤
                return;
            }

            SkullBlockEntity.updateGameprofile(optionalProfile.get(), (fullProfile) -> {
                // 回到服务端主线程再改物品数据
                player.server.execute(() -> {
                    ItemStack current = menu.blockEntity.getItemHandler().getStackInSlot(1);
                    // 确保更新时物品仍然是对应的玩偶
                    if (current.isEmpty() || current.getItem() != ModItems.PLAYER_DOLL.get()) {
                        return;
                    }

                    GameProfile currentProfile = DollItem.getGameProfile(current);
                    if (currentProfile == null || currentProfile.getName() == null
                            || !targetName.equalsIgnoreCase(currentProfile.getName())) {
                        return;
                    }

                    // 没拿到皮肤就不要覆盖，避免把只有名字的档案写死
                    if (!fullProfile.getProperties().containsKey("textures")) {
                        return;
                    }

                    CompoundTag fullTag = NbtUtils.writeGameProfile(new CompoundTag(), fullProfile);
                    current.getOrCreateTag().put("Owner", fullTag);
                    menu.blockEntity.setChanged();
                });
            });
        });
    }
}