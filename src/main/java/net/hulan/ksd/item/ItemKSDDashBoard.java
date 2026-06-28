package net.hulan.ksd.item;

import mtr.data.RailwayData;
import mtr.data.TransportMode;
import mtr.item.ItemWithCreativeTabBase;
import net.hulan.ksd.KSDCreativeModTabs;
import net.hulan.ksd.packet.KSDPacketServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemKSDDashBoard extends ItemWithCreativeTabBase {
    private final TransportMode transportMode;

    public ItemKSDDashBoard(TransportMode transportMode) {
        super(KSDCreativeModTabs.KCR_PLATFORM_BLOCKS, (properties) -> properties.stacksTo(1));
        this.transportMode = transportMode;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand interactionHand) {
        if (!world.isClientSide()) {
            RailwayData railwayData = RailwayData.getInstance(world);
            if (railwayData != null) {
                KSDPacketServer.openKSDDashboardScreenS2C((ServerPlayer)player, this.transportMode, railwayData.getUseTimeAndWindSync());
            }
        }
        return super.use(world, player, interactionHand);
    }
}
