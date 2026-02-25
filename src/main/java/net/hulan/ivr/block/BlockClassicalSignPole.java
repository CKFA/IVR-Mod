package net.hulan.ivr.block;

import mtr.block.BlockPoleCheckBase;
import mtr.block.IBlock;
import mtr.mappings.Text;
import net.hulan.ivr.IVRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlockClassicalSignPole extends BlockPoleCheckBase {
    
    public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 4);

    public BlockClassicalSignPole() {
        super(BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(1).noOcclusion());
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final VoxelShape poleL = IBlock.getVoxelShapeByDirection(6.25, 0, 7.5, 7.25, 16, 8.5, facing), poleR = IBlock.getVoxelShapeByDirection(0, 0, 7.5, 9.755, 16, 8.5, facing);
        return switch (IBlock.getStatePropertySafe(state, TYPE)) {
            case 0 -> IBlock.getVoxelShapeByDirection(18.5, 0, 7.5, 19.5, 16, 8.5, facing);
            case 1 -> IBlock.getVoxelShapeByDirection(14, 0, 7.5, 15, 16, 8.5, facing);
            case 2 -> IBlock.getVoxelShapeByDirection(9.25, 0, 7.5, 10.25, 16, 8.5, facing);
            case 3 -> IBlock.getVoxelShapeByDirection(6.25, 0, 7.5, 7.25, 16, 8.5, facing);
            case 4 -> Shapes.or(poleL, poleR);
            default -> Shapes.block();
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
        return super.placeWithState(stateBelow).setValue(TYPE, type);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "block.ivr.classical_sign_pole";
    }

    @Override
    protected boolean isBlock(Block block) {
        return block instanceof BlockClassicalSign && ((BlockClassicalSign)block).length > 0 || block instanceof BlockClassicalSignPole;
    }

    @Override
    protected Component getTooltipBlockText() {
        return Text.translatable("block.mtr.railway_sign");
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }
}
