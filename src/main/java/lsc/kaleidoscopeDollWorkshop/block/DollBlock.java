package lsc.kaleidoscopeDollWorkshop.block;

import com.mojang.authlib.GameProfile;
import lsc.kaleidoscopeDollWorkshop.block.entity.DollBlockEntity;
import lsc.kaleidoscopeDollWorkshop.registry.ModItems;
import lsc.kaleidoscopeDollWorkshop.registry.ModSounds;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class DollBlock extends Block implements BlockEntityProvider, Waterloggable {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

    // 粒子与音效参数
    private static final double PARTICLE_OFFSET = 0.25;
    private static final float NOTE_DIVISOR = 24.0F;

    public DollBlock(Settings settings) {
        super(settings.sounds(BlockSoundGroup.WOOL));
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(WATERLOGGED, false));
    }

    // 中键拾取：保留 NBT 数据
    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(ModItems.PLAYER_DOLL);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DollBlockEntity dollEntity) {
            GameProfile profile = dollEntity.getOwnerProfile();
            if (profile != null) {
                NbtCompound nbt = stack.getOrCreateNbt();
                nbt.put("Owner", NbtHelper.writeGameProfile(new NbtCompound(), profile));
            }
        }
        return stack;
    }

    // 右键交互：播放声音和粒子
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            spawnParticles(serverWorld, pos);
            playSound(serverWorld, pos);
        }
        return ActionResult.SUCCESS;
    }

    private void spawnParticles(ServerWorld world, BlockPos pos) {
        Vec3d center = Vec3d.ofBottomCenter(pos);
        float noteColor = world.random.nextInt(4) / NOTE_DIVISOR;
        world.spawnParticles(ParticleTypes.NOTE,
                center.x + (world.random.nextFloat() - 0.5) * PARTICLE_OFFSET * 2,
                center.y + 1.0 + world.random.nextFloat() * 0.2,
                center.z + (world.random.nextFloat() - 0.5) * PARTICLE_OFFSET * 2,
                0, noteColor, 0, 0, 1);
    }

    private void playSound(ServerWorld world, BlockPos pos) {
        float pitch = 0.75f + world.random.nextFloat() * 0.5f;
        world.playSound(null, pos, ModSounds.DUCK_TOY, SoundCategory.BLOCKS, 1.0f, pitch);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DollBlockEntity(pos, state);
    }
}