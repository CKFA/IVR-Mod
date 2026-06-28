package net.hulan.ksd.client;

import mtr.client.ClientCache;
import mtr.data.IGui;
import mtr.data.Route;
import mtr.data.SavedRailBase;
import mtr.data.TransportMode;
import net.hulan.ksd.data.*;
import net.minecraft.core.BlockPos;

import java.util.*;

public class KSDClientCache extends KSDDataCache {

    public final Map<Long, Map<Integer, ClientCache.ColorNameTuple>> stationIdToRoutes = new HashMap<>();
    private final Map<TransportMode, Map<BlockPos, List<KSDPlatform>>> posToPlatforms = new HashMap<>();
    private final Map<Long, Map<Long, KSDPlatform>> stationIdToPlatforms = new HashMap<>();
    private final Map<Long, List<ClientCache.PlatformRouteDetails>> platformIdToRoutes = new HashMap<>();
    private final List<Long> clearStationIdToPlatforms = new ArrayList<>();
    private final List<Long> clearPlatformIdToRoutes = new ArrayList<>();

    public KSDClientCache(Set<KSDStation> stations, Set<KSDPlatform> platforms, Set<KSDRoute> routes) {
        super(stations, platforms, routes);
        for (final TransportMode transportMode : TransportMode.values()) {
            posToPlatforms.put(transportMode, new HashMap<>());
        }
    }

    @Override
    protected void syncAdditional() {
        for (final TransportMode transportMode : TransportMode.values()) {
            mapPosToSavedRails(posToPlatforms.get(transportMode), platforms, transportMode);
        }
        stationIdToRoutes.clear();
        routes.forEach(route -> {
            if (!route.isHidden) {
                route.platformIds.forEach(platformId -> {
                    final KSDStation station = platformIdToStation.get(platformId.platformId);
                    if (station != null) {
                        if (!stationIdToRoutes.containsKey(station.id)) {
                            stationIdToRoutes.put(station.id, new HashMap<>());
                        }
                        stationIdToRoutes.get(station.id).put(route.color, new ClientCache.ColorNameTuple(route.color, route.name.split("\\|\\|")[0]));
                    }
                });
            }
        });
        stationIdToPlatforms.keySet().forEach(id -> {
            if (!clearStationIdToPlatforms.contains(id)) {
                clearStationIdToPlatforms.add(id);
            }
        });
        platformIdToRoutes.keySet().forEach(id -> {
            if (!clearPlatformIdToRoutes.contains(id)) {
                clearPlatformIdToRoutes.add(id);
            }
        });
    }

    public Map<Long, KSDPlatform> requestStationIdToPlatforms(long stationId) {
        if (!stationIdToPlatforms.containsKey(stationId)) {
            final KSDStation station = stationIdMap.get(stationId);
            if (station != null) {
                stationIdToPlatforms.put(stationId, areaIdToSavedRails(station, platforms));
            } else {
                stationIdToPlatforms.put(stationId, new HashMap<>());
            }
        }
        return stationIdToPlatforms.get(stationId);
    }

    public List<ClientCache.PlatformRouteDetails> requestPlatformIdToRoutes(long platformId) {
        if (!platformIdToRoutes.containsKey(platformId)) {
            platformIdToRoutes.put(platformId, Utils.getMappedAndNonNullListFromDataCollection(routes, route -> {
                final int index = route.getPlatformIdIndex(platformId);
                if (index < 0) {
                    return null;
                } else {
                    final List<ClientCache.PlatformRouteDetails.StationDetails> stationDetails =
                            Utils.getMappedListFromDataCollection(route.platformIds, pi -> {
                                final KSDStation station = platformIdToStation.get(pi.platformId);
                                if (station == null || !stationIdToRoutes.containsKey(station.id)) {
                                    return new ClientCache.PlatformRouteDetails.StationDetails("", new ArrayList<>());
                                } else {
                                    return new ClientCache.PlatformRouteDetails.StationDetails(station.name,
                                            Utils.getFilteredListFromDataCollection(stationIdToRoutes.get(station.id).values(),
                                                    colorNameTuple -> colorNameTuple.color != route.color));
                                }
                            });
                    return new ClientCache.PlatformRouteDetails(route.name.split("\\|\\|")[0], route.color, route.circularState, index, stationDetails);
                }
            }));
        }
        return platformIdToRoutes.get(platformId);
    }

    public Set<KSDStation> getConnectingStationsIncludingThisOne(KSDStation station) {
        final Set<KSDStation> stationsToCheck = new HashSet<>();
        stationsToCheck.add(station);
        if (stationIdToConnectingStations.containsKey(station)) {
            stationsToCheck.addAll(stationIdToConnectingStations.get(station));
        }
        return stationsToCheck;
    }

    public Map<Integer, ClientCache.ColorNameTuple> getAllRoutesIncludingConnectingStations(KSDStation station) {
        final Map<Integer, ClientCache.ColorNameTuple> routeMap = new HashMap<>();
        getConnectingStationsIncludingThisOne(station).forEach(checkStation -> {
            if (stationIdToRoutes.containsKey(checkStation.id)) {
                routeMap.putAll(stationIdToRoutes.get(checkStation.id));
            }
        });
        return routeMap;
    }

    public String getFormattedRouteDestination(KSDRoute route, int currentStationIndex, String circularMarker) {
        try {
            final String customDestination = route.getDestination(currentStationIndex);
            if (customDestination != null) {
                return customDestination;
            }

            if (route.circularState == Route.CircularState.NONE) {
                return platformIdToStation.get(route.getLastPlatformId()).name;
            } else {
                boolean isVia = false;
                String text = "";

                for (int i = currentStationIndex + 1; i < route.platformIds.size() - 1; i++) {
                    if (stationIdToRoutes.get(platformIdToStation.get(route.platformIds.get(i).platformId).id).size() > 1) {
                        text = platformIdToStation.get(route.platformIds.get(i).platformId).name;
                        isVia = true;
                        break;
                    }
                }

                if (!isVia) {
                    text = platformIdToStation.get(route.getLastPlatformId()).name;
                }

                final String translationString = String.format("%s_%s", route.circularState == Route.CircularState.CLOCKWISE ? "clockwise" : "anticlockwise", isVia ? "via" : "to");
                return circularMarker + IGui.insertTranslation("gui.mtr." + translationString + "_cjk", "gui.mtr." + translationString, 1, text);
            }
        } catch (Exception ignored) {
            return "";
        }
    }

    public void clearDataIfNeeded() {
        if (!clearStationIdToPlatforms.isEmpty()) {
            stationIdToPlatforms.remove(clearStationIdToPlatforms.remove(0));
        }
        if (!clearPlatformIdToRoutes.isEmpty()) {
            platformIdToRoutes.remove(clearPlatformIdToRoutes.remove(0));
        }
    }

    public Map<BlockPos, List<KSDPlatform>> getPosToPlatforms(TransportMode transportMode) {
        return posToPlatforms.get(transportMode);
    }

    private static <U extends KSDAreaBase, V extends SavedRailBase> Map<Long, V> areaIdToSavedRails(U area, Set<V> savedRails) {
        final Map<Long, V> savedRailMap = new HashMap<>();
        savedRails.forEach(savedRail -> {
            final BlockPos pos = savedRail.getMidPos();
            if (area.isTransportMode(savedRail.transportMode) && area.inArea(pos.getX(), pos.getY(), pos.getZ())) {
                savedRailMap.put(savedRail.id, savedRail);
            }
        });
        return savedRailMap;
    }

    private static <U extends SavedRailBase> void mapPosToSavedRails(Map<BlockPos, List<U>> posToSavedRails, Set<U> savedRails, TransportMode transportMode) {
        posToSavedRails.clear();
        savedRails.forEach(savedRail -> {
            if (savedRail.isTransportMode(transportMode)) {
                final BlockPos pos = savedRail.getMidPos(false);
                if (!posToSavedRails.containsKey(pos)) {
                    posToSavedRails.put(pos, new ArrayList<>());
                }
                posToSavedRails.get(pos).add(savedRail);
            }
        });
    }
}
