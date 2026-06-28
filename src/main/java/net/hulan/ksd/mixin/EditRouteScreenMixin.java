package net.hulan.ksd.mixin;

import mtr.data.Route;
import mtr.screen.DashboardScreen;
import mtr.screen.EditNameColorScreenBase;
import mtr.screen.EditRouteScreen;
import net.hulan.ksd.client.KSDClientData;
import net.hulan.ksd.data.Utils;
import net.hulan.ksd.packet.KSDPacket;
import net.hulan.ksd.packet.KSDPacketClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditRouteScreen.class)
public class EditRouteScreenMixin extends EditNameColorScreenBase<Route> {

    public EditRouteScreenMixin(Route data, DashboardScreen dashboardScreen, String nameKey, String colorKey) {
        super(data, dashboardScreen, nameKey, colorKey);
    }

    @Inject(method = "saveData",
            at = @At(value = "TAIL"),
            remap = false)
    private  void saveData(CallbackInfo ci) {
        Utils.executeFromDataSet(KSDClientData.ROUTES, r -> r.id == data.id, ksdRoute -> {
            ksdRoute.name = data.name;
            ksdRoute.color = data.color;
            ksdRoute.routeType = data.routeType;
            ksdRoute.isLightRailRoute = data.isLightRailRoute;
            ksdRoute.lightRailRouteNumber = data.lightRailRouteNumber;
            ksdRoute.isHidden = data.isHidden;
            ksdRoute.disableNextStationAnnouncements = data.disableNextStationAnnouncements;
            ksdRoute.circularState = data.circularState;
            ksdRoute.setExtraData(packet -> KSDPacketClient.sendUpdate(KSDPacket.KSD_PACKET_UPDATE_ROUTE, packet));
        });
    }
}
