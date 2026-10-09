package lsc.kaleidoscopeDollWorkshop.item;

import lsc.kaleidoscopeDollWorkshop.block.entity.DollBlockEntity;
import lsc.kaleidoscopeDollWorkshop.registry.ModBlocks;
import lsc.kaleidoscopeDollWorkshop.registry.ModDataComponents;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class DollItem extends BlockItem implements GeoItem, Equipable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DollItem(Properties properties) {
        super(ModBlocks.DOLL_BLOCK.get(), properties);
    }

    // 指定装备槽位为头部
    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public String getDescriptionId() {
        return "item.kaleidoscope_doll_workshop.player_doll";
    }

    // 动态显示物品名称
    @Override
    public Component getName(ItemStack stack) {
        ResolvableProfile profile = stack.get(ModDataComponents.DOLL_OWNER.get());
        if (profile != null && profile.name().isPresent()) {
            return Component.translatable("item.kaleidoscope_doll_workshop.player_doll.owner", profile.name().get());
        }
        return super.getName(stack);
    }

    // 放置方块逻辑
    @Override
    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        if (result.consumesAction()) {
            BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());
            if (be instanceof DollBlockEntity dollBe) {
                ResolvableProfile profile = context.getItemInHand().get(ModDataComponents.DOLL_OWNER.get());
                if (profile != null) {
                    dollBe.setOwnerProfile(profile.gameProfile());
                }
            }
        }
        return result;
    }

    // 注册客户端自定义渲染器
    @Override
    @SuppressWarnings("removal")
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private lsc.kaleidoscopeDollWorkshop.client.renderer.DollItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new lsc.kaleidoscopeDollWorkshop.client.renderer.DollItemRenderer();
                }
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}