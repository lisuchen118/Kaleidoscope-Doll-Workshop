package lsc.kaleidoscopeDollWorkshop.menu;

import lsc.kaleidoscopeDollWorkshop.registry.ModBlocks;
import lsc.kaleidoscopeDollWorkshop.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ComputerMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerLevelAccess levelAccess;

    // 客户端构造函数
    public ComputerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, new SimpleContainer(2), ContainerLevelAccess.NULL);
    }

    // 服务端构造函数
    public ComputerMenu(int containerId, Inventory playerInventory, Container container, ContainerLevelAccess levelAccess) {
        super(ModMenuTypes.COMPUTER_MENU.get(), containerId);
        checkContainerSize(container, 2);
        this.container = container;
        this.levelAccess = levelAccess;

        // 槽位 0：输入槽
        // 限制只能放入羊毛 (ItemTags.WOOL)
        this.addSlot(new Slot(container, 0, 108, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ItemTags.WOOL);
            }
        });

        // 槽位 1：输出槽
        this.addSlot(new Slot(container, 1, 151, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });

        addPlayerInventory(playerInventory);
    }

    // 关闭 GUI 时将临时容器内的物品返还给玩家或掉落
    @Override
    public void removed(Player player) {
        super.removed(player);
        this.levelAccess.execute((level, pos) -> {
            this.clearContainer(player, this.container);
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.levelAccess, player, ModBlocks.COMPUTER_BLOCK.get());
    }

    // 处理 Shift+点击 物品转移逻辑
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 2) {
                // 从 GUI 移动到背包
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从背包移动到 GUI
                // 仅当物品是羊毛时才允许移动到输入槽 (索引0)
                if (itemstack1.is(ItemTags.WOOL)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 46 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 104));
        }
    }
}