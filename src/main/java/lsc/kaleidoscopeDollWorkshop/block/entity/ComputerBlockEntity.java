package lsc.kaleidoscopeDollWorkshop.block.entity;

import lsc.kaleidoscopeDollWorkshop.gui.ComputerScreenHandler;
import lsc.kaleidoscopeDollWorkshop.registry.ModBlockEntities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ComputerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, Inventory {
    // 0: 输入槽, 1: 输出槽
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMPUTER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.kaleidoscope_doll_workshop.computer");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ComputerScreenHandler(syncId, playerInventory, this);
    }

    // --- Inventory 接口实现 ---
    public DefaultedList<ItemStack> getItems() { return inventory; }

    @Override
    public int size() { return inventory.size(); }

    @Override
    public boolean isEmpty() { return inventory.isEmpty(); }

    @Override
    public ItemStack getStack(int slot) { return inventory.get(slot); }

    @Override
    public ItemStack removeStack(int slot, int amount) { return Inventories.splitStack(this.inventory, slot, amount); }

    @Override
    public ItemStack removeStack(int slot) { return Inventories.removeStack(this.inventory, slot); }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 0) {
            // 输入槽：只允许放入属于 WOOL 标签（羊毛）的物品
            return stack.isIn(ItemTags.WOOL);
        }
        // 输出槽（或其他槽）：不允许外部直接放入物品
        return false;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) { return true; }

    @Override
    public void clear() { inventory.clear(); }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
    }
}