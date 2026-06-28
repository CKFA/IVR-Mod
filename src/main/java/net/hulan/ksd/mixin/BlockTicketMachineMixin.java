package net.hulan.ksd.mixin;

import mtr.block.BlockTicketMachine;
import net.hulan.ksd.packet.KSDPacketServer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockTicketMachine.class)
public class BlockTicketMachineMixin {

    @Inject(method = "use", at = @At("HEAD"))
    private void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!world.isClientSide) {
            KSDPacketServer.openTicketMachineScreenS2C(world, (ServerPlayer)player);
        }
    }
}
