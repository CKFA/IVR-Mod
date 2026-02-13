package net.hulan.ivr.block;

import mtr.block.BlockPoleCheckBase;
import mtr.block.IBlock;
import net.hulan.ivr.IVRBlocks;
import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class BlockClassicalSignPole extends BlockPoleCheckBase {
    
    public static final IntProperty TYPE = IntProperty.of("type", 0, 4);

    public BlockClassicalSignPole() {
        super(AbstractBlock.Settings.of(Material.METAL).requiresTool().strength(1).nonOpaque());
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView blockGetter, BlockPos pos, ShapeContext collisionContext) {
        Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final VoxelShape poleL = IBlock.getVoxelShapeByDirection(6.25, 0, 7.5, 7.25, 16, 8.5, facing), poleR = IBlock.getVoxelShapeByDirection(0, 0, 7.5, 9.755, 16, 8.5, facing);
        return switch (IBlock.getStatePropertySafe(state, TYPE)) {
            case 0 -> IBlock.getVoxelShapeByDirection(18.5, 0, 7.5, 19.5, 16, 8.5, facing);
            case 1 -> IBlock.getVoxelShapeByDirection(14, 0, 7.5, 15, 16, 8.5, facing);
            case 2 -> IBlock.getVoxelShapeByDirection(9.25, 0, 7.5, 10.25, 16, 8.5, facing);
            case 3 -> IBlock.getVoxelShapeByDirection(6.25, 0, 7.5, 7.25, 16, 8.5, facing);
            case 4 -> VoxelShapes.union(poleL, poleR);
            default -> VoxelShapes.fullCube();
        };
    }

    @Override
    protected BlockState placeWithState(BlockState stateBelow) {
        Block block = stateBelow.getBlock();
        int type;
        if (block instanceof BlockClassicalSign) {
            if (block.equals(IVRBlocks.CLASSICAL_SIGN_1_ODD.get())) {
                type = 4;
            } else {
                type = (((BlockClassicalSign)block).length + (((BlockClassicalSign)block).isOdd ? 2 : 0)) % 4;
            }
        } else {
            type = IBlock.getStatePropertySafe(stateBelow, TYPE);
        }

        return super.placeWithState(stateBelow).with(TYPE, type);
    }

    @Override
    public String getTranslationKey() {
        return "block.ivr.classical_sign_pole";
    }

    @Override
    protected boolean isBlock(Block block) {
        return block instanceof BlockClassicalSign && ((BlockClassicalSign)block).length > 0 || block instanceof BlockClassicalSignPole;
    }

    @Override
    protected Text getTooltipBlockText() {
        return mtr.mappings.Text.translatable("block.mtr.railway_sign");
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }
}
