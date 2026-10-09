package lsc.kaleidoscopeDollWorkshop.network;

import com.mojang.authlib.GameProfile;
import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.gui.ComputerScreenHandler;
import lsc.kaleidoscopeDollWorkshop.item.DollItem;
import lsc.kaleidoscopeDollWorkshop.registry.ModItems;
import lsc.kaleidoscopeDollWorkshop.util.SkinFetcher;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

public class ModMessages {
    public static final Identifier CRAFT_DOLL_ID = new Identifier(KaleidoscopeDollWorkshop.MOD_ID, "craft_doll");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(CRAFT_DOLL_ID, (server, player, handler, buf, responseSender) -> {
            // 读取玩家输入的名称和是否批量制作的标志
            String targetName = buf.readString();
            boolean craftAll = buf.readBoolean();

            server.execute(() -> {
                if (player.currentScreenHandler instanceof ComputerScreenHandler screenHandler) {
                    ItemStack inputStack = screenHandler.getInventory().getStack(0);
                    ItemStack outputStack = screenHandler.getInventory().getStack(1);

                    // 1. 检查原材料 (羊毛)
                    if (inputStack.isIn(ItemTags.WOOL)) {
                        int craftAmount = 0;
                        boolean isNewStack = false;

                        // 2. 计算可制作的数量
                        if (outputStack.isEmpty()) {
                            // 输出槽为空：计算最大可堆叠数量与原材料数量的最小值
                            int maxOutputSize = ModItems.PLAYER_DOLL.getMaxCount();
                            int inputCount = inputStack.getCount();

                            // 若批量制作则取最大可能值，否则只制作1个
                            craftAmount = craftAll ? Math.min(inputCount, maxOutputSize) : 1;
                            isNewStack = true;
                        } else {
                            // 输出槽已有物品：检查是否为同名玩家玩偶且未达到堆叠上限
                            if (outputStack.getItem() == ModItems.PLAYER_DOLL) {
                                GameProfile existingProfile = DollItem.getGameProfile(outputStack);
                                if (existingProfile != null && existingProfile.getName() != null && existingProfile.getName().equals(targetName)) {
                                    int spaceLeft = outputStack.getMaxCount() - outputStack.getCount();
                                    int inputCount = inputStack.getCount();

                                    if (spaceLeft > 0) {
                                        // 若批量制作则填满剩余空间或耗尽材料，否则只制作1个
                                        craftAmount = craftAll ? Math.min(inputCount, spaceLeft) : 1;
                                    }
                                }
                            }
                        }

                        // 3. 执行制作逻辑
                        if (craftAmount > 0) {
                            inputStack.decrement(craftAmount);

                            if (isNewStack) {
                                ItemStack doll = new ItemStack(ModItems.PLAYER_DOLL, craftAmount);
                                GameProfile tempProfile = new GameProfile(null, targetName);
                                NbtCompound ownerTag = NbtHelper.writeGameProfile(new NbtCompound(), tempProfile);
                                doll.getOrCreateNbt().put("Owner", ownerTag);
                                screenHandler.getInventory().setStack(1, doll);

                                // 异步获取皮肤数据
                                triggerSkinFetch(server, screenHandler, targetName);
                            } else {
                                outputStack.increment(craftAmount);
                            }

                            if (screenHandler.getInventory() instanceof BlockEntity be) {
                                be.markDirty();
                            }
                        }
                    }
                }
            });
        });
    }

    // 异步皮肤加载
    private static void triggerSkinFetch(MinecraftServer server, ComputerScreenHandler screenHandler, String targetName) {
        // 先按名字查出真实 UUID：UUID 为 null 的档案会被 fillProfileProperties 直接短路，拿不到任何皮肤数据
        SkinFetcher.resolveByName(server, targetName).thenAccept(optionalProfile -> {
            if (optionalProfile.isEmpty()) {
                // 查不到（离线名字、无外网等）：保持只有名字的档案，交给原版默认皮肤
                return;
            }

            SkullBlockEntity.loadProperties(optionalProfile.get(), (fullProfile) -> {
                ItemStack currentOutput = screenHandler.getInventory().getStack(1);
                if (!currentOutput.isEmpty() && currentOutput.getItem() == ModItems.PLAYER_DOLL) {
                    GameProfile currentProfile = DollItem.getGameProfile(currentOutput);
                    if (currentProfile != null && targetName.equalsIgnoreCase(currentProfile.getName())) {
                        // 没拿到皮肤就不要覆盖，避免把只有名字的档案写死
                        if (!fullProfile.getProperties().containsKey("textures")) {
                            return;
                        }

                        NbtCompound fullProfileTag = NbtHelper.writeGameProfile(new NbtCompound(), fullProfile);
                        currentOutput.getOrCreateNbt().put("Owner", fullProfileTag);

                        if (screenHandler.getInventory() instanceof BlockEntity be) {
                            be.markDirty();
                        }
                    }
                }
            });
        });
    }
}