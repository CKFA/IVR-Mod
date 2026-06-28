package net.hulan.ksd.mixin;

import mtr.block.BlockTicketBarrier;
import mtr.mappings.BlockDirectionalMapper;
import net.hulan.ksd.KSDItems;
import net.hulan.ksd.data.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockTicketBarrier.class)
public class BlockTicketBarrierMixin extends BlockDirectionalMapper {

    public BlockTicketBarrierMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, CallbackInfo ci) {
        //ci.cancel();
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState blockState, Level world, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return Utils.checkHoldingItem(world, player, (item) -> System.out.println(1), null, KSDItems.OCTOPUS.get());
    }
}
