package lsc.kaleidoscopeDollWorkshop.block;

import com.mojang.authlib.GameProfile;
import lsc.kaleidoscopeDollWorkshop.block.entity.DollBlockEntity;
import lsc.kaleidoscopeDollWorkshop.registry.ModItems;
import lsc.kaleidoscopeDollWorkshop.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DollBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

    // 粒子与音效参数常量
    private static final double PARTICLE_OFFSET_RANGE = 0.25;
    private static final double PARTICLE_HEIGHT_OFFSET = 1.0;
    private static final double PARTICLE_HEIGHT_VARIANCE = 0.2;
    private static final int MAX_NOTE_COLORS = 4;
    private static final float NOTE_COLOR_DIVISOR = 24.0F;

    public DollBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(ModItems.PLAYER_DOLL.get());
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DollBlockEntity dollEntity) {
            GameProfile profile = dollEntity.getOwnerProfile();
            if (profile != null) {
                CompoundTag nbt = stack.getOrCreateTag();
                nbt.put("Owner", NbtUtils.writeGameProfile(new CompoundTag(), profile));
            }
        }
        return stack;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pLevel instanceof ServerLevel serverLevel) {
            spawnNoteParticles(serverLevel, pPos);
            playSound(pLevel, pPos);
        }
        return InteractionResult.SUCCESS;
    }

    private void spawnNoteParticles(ServerLevel serverLevel, BlockPos blockPos) {
        Vec3 particlePosition = calculateParticlePosition(serverLevel, blockPos);
        float noteColor = calculateNoteColor(serverLevel);

        serverLevel.sendParticles(ParticleTypes.NOTE,
                particlePosition.x(), particlePosition.y(), particlePosition.z(),
                0, noteColor, 0, 0, 1);
    }

    private Vec3 calculateParticlePosition(ServerLevel serverLevel, BlockPos blockPos) {
        return Vec3.atBottomCenterOf(blockPos).add(
                (serverLevel.getRandom().nextFloat() - 0.5) * PARTICLE_OFFSET_RANGE * 2,
                PARTICLE_HEIGHT_OFFSET + serverLevel.getRandom().nextFloat() * PARTICLE_HEIGHT_VARIANCE,
                (serverLevel.getRandom().nextFloat() - 0.5) * PARTICLE_OFFSET_RANGE * 2
        );
    }

    private float calculateNoteColor(ServerLevel serverLevel) {
        return serverLevel.getRandom().nextInt(MAX_NOTE_COLORS) / NOTE_COLOR_DIVISOR;
    }

    private void playSound(Level level, BlockPos pos) {
        float pitch = 0.75f + level.random.nextFloat() * 0.5f;
        level.playSound(null, pos, ModSounds.DUCK_TOY.get(), SoundSource.BLOCKS, 1.0f, pitch);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new DollBlockEntity(pPos, pState);
    }
}