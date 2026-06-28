package net.hulan.ksd.mixin;

import mtr.client.ClientData;
import mtr.data.*;
import net.hulan.ksd.KSDMain;
import net.hulan.ksd.client.KSDClientData;
import net.hulan.ksd.data.KSDRailwayData;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Mixin(DataCache.class)
public class DataCacheMixin {

    @Inject(method = "mapSavedRailIdToStation", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static <U extends SavedRailBase, V extends AreaBase> void areaIdToSavedRails(Map<Long, V> map, Set<U> savedRails, Set<V> areas, CallbackInfo ci) {
        savedRails.forEach((savedRail) -> {
            BlockPos pos = savedRail.getMidPos();
            if (savedRail instanceof Platform) {
                if (map.isEmpty()) {
                    return;
                }
                map.clear();
                RailwayData mtrOverworld = RailwayData.getInstance(KSDMain.overworld);
                KSDRailwayData ksdOverworld = KSDRailwayData.getInstance(KSDMain.overworld);
                RailwayData mtrTheNether = RailwayData.getInstance(KSDMain.the_nether);
                KSDRailwayData ksdTheNether = KSDRailwayData.getInstance(KSDMain.the_nether);
                RailwayData mtrTheEnd = RailwayData.getInstance(KSDMain.the_end);
                KSDRailwayData ksdTheEnd = KSDRailwayData.getInstance(KSDMain.the_end);
                if (areas == ClientData.STATIONS) {
                    KSDClientData.STATIONS.forEach(station -> {
                        for(V area : areas) {
                            if (Objects.equals(station.id, area.id) && station.isTransportMode(savedRail.transportMode) && station.inArea(pos.getX(), pos.getY(), pos.getZ())) {
                                map.put(savedRail.id, area);
                                break;
                            }
                        }
                    });
                    return;
                }
                if (ksdOverworld != null && mtrOverworld != null && areas == mtrOverworld.stations) {
                    ksdOverworld.stations.forEach(station -> {
                        for(V area : areas) {
                            if (Objects.equals(station.id, area.id) && station.isTransportMode(savedRail.transportMode) && station.inArea(pos.getX(), pos.getY(), pos.getZ())) {
                                map.put(savedRail.id, area);
                                break;
                            }
                        }
                    });
                    return;
                }
                if (ksdTheNether != null && mtrTheNether != null && areas == mtrTheNether.stations) {
                    ksdTheNether.stations.forEach(station -> {
                        for(V area : areas) {
                            if (Objects.equals(station.id, area.id) && station.isTransportMode(savedRail.transportMode) && station.inArea(pos.getX(), pos.getY(), pos.getZ())) {
                                map.put(savedRail.id, area);
                                break;
                            }
                        }
                    });
                    return;
                }
                if (ksdTheEnd != null && mtrTheEnd != null && areas == mtrTheEnd.stations) {
                    ksdTheEnd.stations.forEach(station -> {
                        for(V area : areas) {
                            if (Objects.equals(station.id, area.id) && station.isTransportMode(savedRail.transportMode) && station.inArea(pos.getX(), pos.getY(), pos.getZ())) {
                                map.put(savedRail.id, area);
                                break;
                            }
                        }
                    });
                }
                ci.cancel();
            }
        });
    }
}
