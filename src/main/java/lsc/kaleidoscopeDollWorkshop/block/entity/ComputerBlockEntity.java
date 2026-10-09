package lsc.kaleidoscopeDollWorkshop.block.entity;

import lsc.kaleidoscopeDollWorkshop.menu.ComputerMenu;
import lsc.kaleidoscopeDollWorkshop.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ComputerBlockEntity extends BlockEntity implements MenuProvider {

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMPUTER_BE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.kaleidoscope_doll_workshop.computer");
    }

    // 创建服务端菜单容器
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ComputerMenu(
                containerId,
                playerInventory,
                new SimpleContainer(2), // 创建2个槽位的临时容器
                ContainerLevelAccess.create(this.level, this.worldPosition)
        );
    }
}