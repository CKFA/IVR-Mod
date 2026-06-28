package net.hulan.ksd.mixin;

import mtr.data.RailwayData;
import mtr.data.Station;
import mtr.data.TicketSystem;
import net.hulan.ksd.data.FirstClassValidationSystem;
import net.hulan.ksd.data.KSDRailwayData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Score;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TicketSystem.class)
public class TicketSystemMixin {

    @Inject(method = "onEnter", at = @At("HEAD"))
    private static void onEnter(Station station, Player player, Score balanceScore, Score entryZoneScore, boolean remindIfNoRecord, CallbackInfoReturnable<Boolean> cir) {
        if (balanceScore.getScore() < 0 || (entryZoneScore.getScore() != 0 && remindIfNoRecord)) return;
        Level world = player.level;
        KSDRailwayData ksdRailwayData = KSDRailwayData.getInstance(world);
        RailwayData railwayData = RailwayData.getInstance(world);
        if (ksdRailwayData != null && railwayData != null) {
            ksdRailwayData.stations.forEach(ksdStation -> {
                if (ksdStation.id == station.id) {
                    FirstClassValidationSystem.onEnterStation(ksdRailwayData, player, ksdStation);
                }
            });
        }
    }

    @Inject(method = "onExit", at = @At("HEAD"), cancellable = true)
    private static void onExit(Station station, Player player, Score balanceScore, Score entryZoneScore, boolean remindIfNoRecord, CallbackInfoReturnable<Boolean> cir) {
        if (entryZoneScore.getScore() == 0 && remindIfNoRecord) return;
        Level world = player.level;
        KSDRailwayData ksdRailwayData = KSDRailwayData.getInstance(world);
        RailwayData railwayData = RailwayData.getInstance(world);
        if (ksdRailwayData != null && railwayData != null) {
            ksdRailwayData.stations.forEach(ksdStation -> {
                if (station.id == ksdStation.id) {
                    FirstClassValidationSystem.FirstClassState state = FirstClassValidationSystem.onExitStation(world, ksdRailwayData, ksdStation, player, balanceScore, entryZoneScore);
                    if (state != FirstClassValidationSystem.FirstClassState.MTR) {
                        cir.setReturnValue(true);
                    }
                }
            });
        }
    }
}
