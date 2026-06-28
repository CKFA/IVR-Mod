package net.hulan.ksd.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import mtr.data.NameColorDataBase;
import mtr.data.Station;
import mtr.screen.DashboardScreen;
import mtr.screen.EditNameColorScreenBase;
import mtr.screen.EditStationScreen;
import net.hulan.ksd.client.KSDClientData;
import net.hulan.ksd.data.KSDStation;
import net.hulan.ksd.data.Utils;
import net.hulan.ksd.packet.KSDPacket;
import net.hulan.ksd.packet.KSDPacketClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

@Mixin(EditStationScreen.class)
public class EditStationScreenMixin extends EditNameColorScreenBase<Station> {

    @Shadow(remap = false)
    String editingExit;

    @Shadow(remap = false)
    int editingDestinationIndex;

    public EditStationScreenMixin(Station data, DashboardScreen dashboardScreen, String nameKey, String colorKey) {
        super(data, dashboardScreen, nameKey, colorKey);
    }

    @Inject(method = "saveData",
            at = @At(value = "TAIL"),
            remap = false)
    private void onSaveData(CallbackInfo ci) {
        executeKSDStationUpdate(ksdStation -> {
            ksdStation.name = data.name;
            ksdStation.color = data.color;
            ksdStation.zone = data.zone;
            ksdStation.setZone((packet) -> KSDPacketClient.sendUpdate(KSDPacket.KSD_PACKET_UPDATE_STATION, packet));
        });
    }

    @Inject(method = "onDoneExitParent",
            at = @At(value = "INVOKE",
                    target = "Lmtr/data/Station;setExitParent(Ljava/lang/String;Ljava/lang/String;Ljava/util/function/Consumer;)V",
                    shift = At.Shift.AFTER),
            remap = false)
    private void onDoneExitParent(CallbackInfo ci, @Local(name = "exitParent") String exitParent) {
        executeKSDStationUpdate(ksdStation -> ksdStation.setExitParent(editingExit, exitParent, (packet) -> KSDPacketClient.sendUpdate(KSDPacket.KSD_PACKET_UPDATE_STATION, packet)));
    }

    @Inject(method = "onDoneExitDestination",
            at = @At(value = "INVOKE",
                    target = "Lmtr/data/Station;setExitDestinations(Ljava/lang/String;Ljava/util/function/Consumer;)V",
                    shift = At.Shift.AFTER),
            remap = false)
    private void onDoneExitDestination(CallbackInfo ci, @Local(name = "destination") String destination) {
        executeKSDStationUpdate(ksdStation -> {
            List<String> destinations = ksdStation.exits.get(editingExit);
            if (editingDestinationIndex < destinations.size()) {
                destinations.set(editingDestinationIndex, destination);
            } else {
                destinations.add(destination);
            }
            ksdStation.setExitDestinations(editingExit, (packet) -> KSDPacketClient.sendUpdate(KSDPacket.KSD_PACKET_UPDATE_STATION, packet));
        });
    }

    @Inject(method = "onDeleteExitParent",
            at = @At(value = "INVOKE",
                    target = "Lmtr/data/Station;deleteExitParent(Ljava/lang/String;Ljava/util/function/Consumer;)V",
                    shift = At.Shift.AFTER),
            remap = false)
    private void onDeleteExitParent(NameColorDataBase listData, int index, CallbackInfo ci) {
        executeKSDStationUpdate(ksdStation -> ksdStation.deleteExitParent(formatExitName(listData.name), (packet) -> KSDPacketClient.sendUpdate(KSDPacket.KSD_PACKET_UPDATE_STATION, packet)));
    }

    @Inject(method = "onSortExitDestination",
            at = @At(value = "INVOKE",
                    target = "Lmtr/data/Station;setExitDestinations(Ljava/lang/String;Ljava/util/function/Consumer;)V",
                    shift = At.Shift.AFTER),
            remap = false)
    private void onSortExitDestination(CallbackInfo ci) {
        executeKSDStationUpdate(ksdStation -> ksdStation.setExitDestinations(editingExit, (packet) -> KSDPacketClient.sendUpdate(KSDPacket.KSD_PACKET_UPDATE_STATION, packet)));
    }

    @Inject(method = "onDeleteExitDestination",
            at = @At(value = "INVOKE",
                    target = "Lmtr/data/Station;setExitDestinations(Ljava/lang/String;Ljava/util/function/Consumer;)V",
                    shift = At.Shift.AFTER),
            remap = false)
    private void onDeleteExitDestination(NameColorDataBase listData, int index, CallbackInfo ci) {
        executeKSDStationUpdate(ksdStation -> {
            ksdStation.exits.get(editingExit).remove(listData.name);
            ksdStation.deleteExitParent(editingExit, (packet) -> KSDPacketClient.sendUpdate(KSDPacket.KSD_PACKET_UPDATE_STATION, packet));
        });
    }

    @Unique
    private static String formatExitName(String text) {
        return text.split("\\|")[0];
    }

    @Unique
    private void executeKSDStationUpdate(Consumer<KSDStation> action) {
        Utils.executeFromDataSet(KSDClientData.STATIONS, s -> s.id == data.id, action);
    }
}
