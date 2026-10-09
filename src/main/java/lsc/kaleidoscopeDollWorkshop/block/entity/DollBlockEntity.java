package lsc.kaleidoscopeDollWorkshop.block.entity;

import com.mojang.authlib.GameProfile;
import lsc.kaleidoscopeDollWorkshop.registry.ModBlockEntities;
import lsc.kaleidoscopeDollWorkshop.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DollBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private GameProfile ownerProfile = null;

    public DollBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DOLL_BE.get(), pos, state);
    }

    public GameProfile getOwnerProfile() {
        return ownerProfile;
    }

    public void setOwnerProfile(GameProfile profile) {
        this.ownerProfile = profile;
        this.setChanged(); // 标记方块实体已修改
        if (level != null) {
            // 通知客户端更新方块数据
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // 将方块实体内部的皮肤数据收集到数据组件中
    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        if (this.ownerProfile != null) {
            builder.set(ModDataComponents.DOLL_OWNER.get(), new ResolvableProfile(this.ownerProfile));
        }
    }

    // 当带有数据组件的物品被放置为方块时，从此方法恢复数据
    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);
        ResolvableProfile profile = input.get(ModDataComponents.DOLL_OWNER.get());
        if (profile != null) {
            this.ownerProfile = profile.gameProfile();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.ownerProfile != null) {
            // 将 Profile 序列化到 NBT
            ResolvableProfile.CODEC.encodeStart(NbtOps.INSTANCE, new ResolvableProfile(this.ownerProfile))
                    .result().ifPresent(t -> tag.put("Owner", t));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Owner", 10)) {
            // 从 NBT 反序列化 Profile
            ResolvableProfile.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("Owner"))
                    .result().ifPresent(resolvable -> this.ownerProfile = resolvable.gameProfile());
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}