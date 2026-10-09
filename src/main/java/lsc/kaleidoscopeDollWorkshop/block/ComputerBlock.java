package lsc.kaleidoscopeDollWorkshop.block;

import com.mojang.serialization.MapCodec;
import lsc.kaleidoscopeDollWorkshop.block.entity.ComputerBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ComputerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<ComputerBlock> CODEC = simpleCodec(ComputerBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING; // 水平朝向
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED; // 含水状态

    // --- 碰撞箱定义（针对不同朝向的旋转） ---
    private static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(0, 0, 0, 16, 4, 15), Block.box(1, 4, 5, 15, 15, 13));
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(0, 0, 1, 16, 4, 16), Block.box(1, 4, 3, 15, 15, 11));
    private static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(1, 0, 0, 16, 4, 16), Block.box(3, 4, 1, 11, 15, 15));
    private static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(0, 0, 0, 15, 4, 16), Block.box(5, 4, 1, 13, 15, 15));

    public ComputerBlock() {
        super(Properties.of()
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.METAL)
                .strength(1.25F, 4.2F)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(WATERLOGGED, false));
    }

    public ComputerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    // 根据方块状态中的朝向返回对应的碰撞箱
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_WEST;
            case WEST -> SHAPE_EAST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        // 自动计算朝向（背对玩家）并检查是否放置在水中
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ComputerBlockEntity(pos, state);
    }

    // 方块右键交互逻辑
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ComputerBlockEntity computerBE) {
                serverPlayer.openMenu(computerBE, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    // 添加物品悬浮提示 (Tooltip)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.kaleidoscope_doll_workshop.computer").withStyle(ChatFormatting.DARK_GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}