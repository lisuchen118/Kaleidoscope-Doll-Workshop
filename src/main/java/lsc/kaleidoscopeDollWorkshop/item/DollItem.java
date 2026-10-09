package lsc.kaleidoscopeDollWorkshop.item;

import com.mojang.authlib.GameProfile;
import lsc.kaleidoscopeDollWorkshop.block.entity.DollBlockEntity;
import lsc.kaleidoscopeDollWorkshop.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DollItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static Consumer<Consumer<Object>> RENDERER_REGISTER = null;
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public DollItem(Settings settings) {
        super(settings);
    }

    // 获取物品名称：动态显示 "xxx的玩偶"
    @Override
    public Text getName(ItemStack stack) {
        GameProfile profile = getGameProfile(stack);
        if (profile != null && profile.getName() != null && !profile.getName().isEmpty()) {
            return Text.translatable("item.kaleidoscope_doll_workshop.player_doll.owner", profile.getName());
        }
        return super.getName(stack);
    }

    // 放置方块逻辑：传递 NBT 数据
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemPlacementContext placementContext = new ItemPlacementContext(context);
        World world = context.getWorld();
        BlockPos pos = placementContext.getBlockPos();
        PlayerEntity player = context.getPlayer();

        if (placementContext.canPlace() && world.getBlockState(pos).canReplace(placementContext)) {
            BlockState state = ModBlocks.DOLL_BLOCK.getPlacementState(placementContext);

            if (state != null && world.setBlockState(pos, state, Block.NOTIFY_ALL)) {
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof DollBlockEntity dollBe) {
                    GameProfile profile = getGameProfile(context.getStack());
                    if (profile != null) {
                        dollBe.setOwnerProfile(profile);
                    }
                }

                BlockSoundGroup soundGroup = state.getSoundGroup();
                world.playSound(player, pos, soundGroup.getPlaceSound(), SoundCategory.BLOCKS, (soundGroup.getVolume() + 1.0F) / 2.0F, soundGroup.getPitch() * 0.8F);

                if (player == null || !player.getAbilities().creativeMode) {
                    context.getStack().decrement(1);
                }
                return ActionResult.SUCCESS;
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static GameProfile getGameProfile(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains("Owner")) {
            NbtCompound nbt = stack.getNbt();
            if (nbt.contains("Owner", NbtElement.COMPOUND_TYPE)) {
                return NbtHelper.toGameProfile(nbt.getCompound("Owner"));
            } else if (nbt.contains("Owner", NbtElement.STRING_TYPE)) {
                return new GameProfile(null, nbt.getString("Owner"));
            }
        }
        return null;
    }

    // --- GeckoLib 接口 ---
    @Override public void createRenderer(Consumer<Object> consumer) { if (RENDERER_REGISTER != null) RENDERER_REGISTER.accept(consumer); }
    @Override public Supplier<Object> getRenderProvider() { return renderProvider; }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}