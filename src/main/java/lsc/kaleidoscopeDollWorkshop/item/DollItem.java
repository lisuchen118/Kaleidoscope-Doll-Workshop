package lsc.kaleidoscopeDollWorkshop.item;

import com.mojang.authlib.GameProfile;
import lsc.kaleidoscopeDollWorkshop.block.entity.DollBlockEntity;
import lsc.kaleidoscopeDollWorkshop.client.renderer.DollItemRenderer;
import lsc.kaleidoscopeDollWorkshop.registry.ModBlocks;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class DollItem extends Item implements GeoItem, Equipable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DollItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public Component getName(ItemStack pStack) {
        GameProfile profile = getGameProfile(pStack);
        if (profile != null && profile.getName() != null && !profile.getName().isEmpty()) {
            return Component.translatable("item.kaleidoscope_doll_workshop.player_doll.owner", profile.getName());
        }
        return super.getName(pStack);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPlaceContext placeContext = new BlockPlaceContext(pContext);
        BlockPos pos = placeContext.getClickedPos();
        Player player = pContext.getPlayer();

        if (placeContext.canPlace() && level.getBlockState(pos).canBeReplaced(placeContext)) {
            BlockState state = ModBlocks.DOLL_BLOCK.get().getStateForPlacement(placeContext);
            if (state != null && level.setBlock(pos, state, Block.UPDATE_ALL)) {
                // 放置成功，传递 Profile 数据给方块实体
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof DollBlockEntity dollBe) {
                    GameProfile profile = getGameProfile(pContext.getItemInHand());
                    if (profile != null) {
                        dollBe.setOwnerProfile(profile);
                    }
                }

                SoundType soundType = state.getSoundType(level, pos, player);
                level.playSound(player, pos, soundType.getPlaceSound(), net.minecraft.sounds.SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);

                if (player == null || !player.isCreative()) {
                    pContext.getItemInHand().shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public static GameProfile getGameProfile(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("Owner")) {
            CompoundTag nbt = stack.getTag();
            if (nbt.contains("Owner", Tag.TAG_COMPOUND)) {
                return NbtUtils.readGameProfile(nbt.getCompound("Owner"));
            } else if (nbt.contains("Owner", Tag.TAG_STRING)) {
                return new GameProfile(null, nbt.getString("Owner"));
            }
        }
        return null;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private DollItemRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new DollItemRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}