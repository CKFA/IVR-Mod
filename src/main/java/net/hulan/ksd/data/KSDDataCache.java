package net.hulan.ksd.data;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import mtr.data.NameColorDataBase;
import mtr.data.RailwayDataRouteFinderModule;
import mtr.data.SavedRailBase;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KSDDataCache {

    private long lastRefreshedTime;
    public final Map<Long, KSDStation> stationIdMap = new HashMap<>();
    public final Map<Long, KSDPlatform> platformIdMap = new HashMap<>();
    public final Map<Long, KSDRoute> routeIdMap = new HashMap<>();
    public final Map<Long, KSDStation> platformIdToStation = new HashMap<>();
    public final Map<KSDStation, Set<KSDStation>> stationIdToConnectingStations = new HashMap<>();
    public final Map<BlockPos, KSDStation> blockPosToStation = new HashMap<>();
    public final Long2LongOpenHashMap blockPosToPlatformId = new Long2LongOpenHashMap();
    public final Long2ObjectOpenHashMap<Long2ObjectOpenHashMap<RailwayDataRouteFinderModule.ConnectionDetails>> platformConnections = new Long2ObjectOpenHashMap<>();
    protected final Set<KSDStation> stations;
    protected final Set<KSDPlatform> platforms;
    protected final Set<KSDRoute> routes;

    public KSDDataCache(Set<KSDStation> stations, Set<KSDPlatform> platforms, Set<KSDRoute> routes) {
        this.stations = stations;
        this.platforms = platforms;
        this.routes = routes;
    }

    public final void sync() {
        try {
            mapIds(stationIdMap, stations);
            mapIds(platformIdMap, platforms);
            mapIds(routeIdMap, routes);
            routes.forEach(route -> route.platformIds.removeIf(platformId -> !platformIdMap.containsKey(platformId.platformId)));
            platformConnections.clear();
            stationIdToConnectingStations.clear();
            stations.forEach(station1 -> {
                stationIdToConnectingStations.put(station1, new HashSet<>());
                stations.forEach(station2 -> {
                    if (station1 != station2 && station1.intersecting(station2)) {
                        stationIdToConnectingStations.get(station1).add(station2);
                    }
                });
            });
            mapSavedRailIdToStation(platformIdToStation, platforms, stations);
            blockPosToPlatformId.clear();
            blockPosToStation.clear();
            FirstClassValidationSystem.sync(routes, platformIdToStation);
            syncAdditional();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        lastRefreshedTime = System.currentTimeMillis();
    }

    public boolean needsRefresh(long cachedRefreshTime) {
        return this.lastRefreshedTime > cachedRefreshTime;
    }

    protected void syncAdditional() {
    }

    protected static <U extends NameColorDataBase> void mapIds(Map<Long, U> map, Set<U> source) {
        map.clear();
        source.forEach((data) -> map.put(data.id, data));
    }

    private static <U extends SavedRailBase, V extends KSDAreaBase> void mapSavedRailIdToStation(Map<Long, V> map, Set<U> savedRails, Set<V> areas) {
        map.clear();
        savedRails.forEach(savedRail -> {
            final BlockPos pos = savedRail.getMidPos();
            for (final V area : areas) {
                if (area.isTransportMode(savedRail.transportMode) && area.inArea(pos.getX(), pos.getY(), pos.getZ())) {
                    map.put(savedRail.id, area);
                    break;
                }
            }
        });
    }
}
