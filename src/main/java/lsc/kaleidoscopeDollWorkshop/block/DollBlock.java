package lsc.kaleidoscopeDollWorkshop.block;

import com.mojang.serialization.MapCodec;
import lsc.kaleidoscopeDollWorkshop.block.entity.DollBlockEntity;
import lsc.kaleidoscopeDollWorkshop.registry.ModDataComponents;
import lsc.kaleidoscopeDollWorkshop.registry.ModItems;
import lsc.kaleidoscopeDollWorkshop.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DollBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<DollBlock> CODEC = simpleCodec(DollBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    // 精确的玩偶碰撞箱尺寸
    private static final VoxelShape DOLL_SHAPE = Block.box(2.0d, 0.0d, 2.0d, 14.0d, 12.0d, 14.0d);

    // --- 粒子与音效配置常量 ---
    private static final double PARTICLE_OFFSET_RANGE = 0.25;
    private static final double PARTICLE_HEIGHT_OFFSET = 1.0;
    private static final double PARTICLE_HEIGHT_VARIANCE = 0.2;
    private static final float NOTE_COLOR_DIVISOR = 24.0F;
    private static final int MAX_NOTE_COLORS = 4;
    private static final float BASE_VOLUME = 1.0f;
    private static final float PITCH_VARIANCE = 0.5f;
    private static final float BASE_PITCH = 0.75f;

    public DollBlock(Properties properties) {
        super(properties.instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.WOOL)
                .strength(0f, 10f)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return DOLL_SHAPE;
    }

    // 指定渲染类型为实体方块动画 (GeckoLib)
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DollBlockEntity(pos, state);
    }

    // 玩家右键点击时播放随机音调的音效并生成粒子
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level instanceof ServerLevel serverLevel) {
            spawnNoteParticles(serverLevel, pos);
            playDollSound(serverLevel, pos);
        }
        return InteractionResult.SUCCESS;
    }

    // 生成随机颜色的音符粒子
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

    private void playDollSound(ServerLevel serverLevel, BlockPos blockPos) {
        float pitch = BASE_PITCH + serverLevel.getRandom().nextFloat() * PITCH_VARIANCE;
        serverLevel.playSound(null, blockPos, ModSounds.DUCK_TOY.get(),
                SoundSource.BLOCKS, BASE_VOLUME, pitch);
    }

    // 中键提取逻辑
    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(ModItems.PLAYER_DOLL_ITEM.get());
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DollBlockEntity dollBe && dollBe.getOwnerProfile() != null) {
            stack.set(ModDataComponents.DOLL_OWNER.get(), new ResolvableProfile(dollBe.getOwnerProfile()));
        }
        return stack;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}