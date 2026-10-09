package lsc.kaleidoscopeDollWorkshop.block.entity;

import com.mojang.authlib.GameProfile;
import lsc.kaleidoscopeDollWorkshop.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DollBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private GameProfile ownerProfile = null;

    public DollBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DOLL_BLOCK_ENTITY, pos, state);
    }

    // --- 数据持久化 ---

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.ownerProfile != null) {
            nbt.put("Owner", NbtHelper.writeGameProfile(new NbtCompound(), this.ownerProfile));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Owner", 10)) {
            this.ownerProfile = NbtHelper.toGameProfile(nbt.getCompound("Owner"));
        }
    }

    // --- 数据同步 ---

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    // --- 访问器 ---

    public GameProfile getOwnerProfile() {
        return ownerProfile;
    }

    public void setOwnerProfile(GameProfile profile) {
        this.ownerProfile = profile;
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    // --- GeckoLib ---

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}