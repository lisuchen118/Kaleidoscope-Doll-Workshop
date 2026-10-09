package lsc.kaleidoscopeDollWorkshop.menu;

import lsc.kaleidoscopeDollWorkshop.block.entity.ComputerBlockEntity;
import lsc.kaleidoscopeDollWorkshop.registry.ModBlocks;
import lsc.kaleidoscopeDollWorkshop.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ComputerMenu extends AbstractContainerMenu {
    public final ComputerBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    // 客户端构造函数
    public ComputerMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    // 服务端构造函数
    public ComputerMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.COMPUTER_MENU.get(), pContainerId);
        this.blockEntity = (ComputerBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());

        // 槽位 0: 输入槽
        this.addSlot(new SlotItemHandler(this.blockEntity.getItemHandler(), 0, 108, 18));
        // 槽位 1: 输出槽 (不可手动放入)
        this.addSlot(new SlotItemHandler(this.blockEntity.getItemHandler(), 1, 151, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    // 当 GUI 关闭时将物品返还给玩家
    @Override
    public void removed(Player player) {
        super.removed(player);
        // 只在服务端执行，防止刷物品
        if (blockEntity != null && !player.level().isClientSide) {
            IItemHandler handler = blockEntity.getItemHandler();
            for (int i = 0; i < handler.getSlots(); i++) {
                // 强制取出槽位内所有物品
                ItemStack stack = handler.extractItem(i, 64, false);
                if (!stack.isEmpty()) {
                    // 尝试放入玩家背包，放不下则丢在脚下
                    player.getInventory().placeItemBackInInventory(stack);
                }
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // 0-1 是方块槽位，2-37 是玩家背包
            if (pIndex < 2) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(pPlayer, itemstack1);
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(this.levelAccess, pPlayer, ModBlocks.COMPUTER_BLOCK.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 46 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 104));
        }
    }
}