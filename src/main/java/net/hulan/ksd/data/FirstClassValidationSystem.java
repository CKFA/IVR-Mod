package net.hulan.ksd.data;

import mtr.SoundEvents;
import mtr.data.*;
import mtr.mappings.Text;
import net.hulan.ksd.client.KSDClientCache;
import net.hulan.ksd.mixin.SidingAccessor;
import net.hulan.ksd.mixin.TrainInvoker;
import net.hulan.ksd.mixin.TrainServerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class FirstClassValidationSystem {

    private static final Map<Long, Set<KSDRoute>> stationIdToRoutes = new HashMap<>();
    private static final int BASE_FARE = 2;
    private static final int ZONE_FARE = 1;
    private static final int EVASION_FINE = 1000;
    private static final String PLAYER_CAR_OBJECTIVE = "player_car";

    public static void sync(Set<KSDRoute> routes, Map<Long, KSDStation> platformIdToStation) {
        stationIdToRoutes.clear();
        routes.forEach((route) -> {
            if (!route.isHidden) {
                route.platformIds.forEach((platformId) -> {
                    KSDStation station = platformIdToStation.get(platformId.platformId);
                    if (station != null) {
                        if (!stationIdToRoutes.containsKey(station.id)) {
                            stationIdToRoutes.put(station.id, new HashSet<>());
                        }
                        stationIdToRoutes.get(station.id).add(route);
                    }
                });
            }
        });
    }

    public static void tick(KSDRailwayData ksd, RailwayData mtr, Level world, List<ServerPlayer> players) {
        addObjectivesIfMissing(world);
        Set<Train> trains = getAllTrains(mtr);
        TrainServer playerTrain = null;
        int newCar = -1;
        for (ServerPlayer player : players) {
            if (player.isSpectator()) continue;
            Score carScore = getPlayerScore(world, player, PLAYER_CAR_OBJECTIVE);
            for (Train t : trains) {
                if (t.isPlayerRiding(player)) {
                    playerTrain = (TrainServer) t;
                    newCar = getCarForPlayer(player, playerTrain);
                    break;
                }
            }
            if (playerTrain == null) {
                carScore.setScore(0);
            } else {
                int oldCar = carScore.getScore() - 1;
                if (oldCar == newCar) return;
                carScore.setScore(newCar + 1);
                FirstClassPlayer firstClassPlayer = Utils.getFilteredValueFromDataSet(ksd.jsonDataManager.fps, f -> f.uuid.equals(player.getUUID()));
                if (firstClassPlayer == null || oldCar == -1) continue;
                long routeId = ((TrainServerAccessor) playerTrain).getRouteId();
                KSDStation enteredStation = Utils.getFilteredValueFromDataSet(ksd.stations, s -> s.id == firstClassPlayer.enteredStationId);
                if (enteredStation == null) continue;
                KSDRoute route = Utils.getFilteredValueFromDataSet(ksd.routes, r -> r.id == routeId);
                System.out.println(newCar + 1);
                if (route != null
                        && route.routeType.name().equals("KCR_CLASSICAL")
                        && route.hasFirstClassService
                        && newCar == route.firstClassCar) {
                    KSDStation firstStation;
                    if (isInRoute(ksd.dataCache.platformIdToStation, firstClassPlayer.enteredStationId, route)) {
                        firstStation = enteredStation;
                    } else if ((firstStation = getNearestInterchangeStation(ksd, route,  firstClassPlayer.enteredStationId)) == null){
                        firstStation = ksd.dataCache.platformIdToStation.get(route.getFirstPlatformId());
                    }
                    validate(ksd, world, player, player.blockPosition(), firstStation, route);
                }
            }
        }
    }

    public static void onEnterStation(KSDRailwayData railwayData, Player player, KSDStation enteredStation) {
        railwayData.jsonDataManager.fps.add(new FirstClassPlayer(player.getUUID(), enteredStation));
        railwayData.dataCache.sync();
    }

    public static void illegallyEntered(Level world, KSDDataCache dataCache, FirstClassPlayer firstClassPlayer, int percentageOffset) {
        if (dataCache instanceof KSDClientCache) {
            throw new IllegalStateException();
        }
        addObjectivesIfMissing(world);
        Player levelPlayer = world.getPlayerByUUID(firstClassPlayer.uuid);
        if (levelPlayer != null) {
            firstClassPlayer.state = FirstClassState.ILLEGALLY;
            Score carScore = getPlayerScore(world, levelPlayer, PLAYER_CAR_OBJECTIVE);
            carScore.setScore(percentageOffset + 1);
            dataCache.sync();
            playSoundAndSendMessage(world, levelPlayer.blockPosition(), levelPlayer, "gui.ksd.first_class_illegal");
        }
    }

    public static FirstClassState validateOnMachine(BlockPos clickedPos, Level world, Player player) {
        KSDRailwayData ksd = KSDRailwayData.getInstance(world);
        if (ksd == null) {
            playSoundAndSendMessage(world, clickedPos, player, "gui.ksd.first_class_denied");
            return FirstClassState.DENIED;
        }
        KSDStation station = KSDRailwayData.getStation(ksd.stations, clickedPos);
        if (station == null) {
            playSoundAndSendMessage(world, clickedPos, player, "gui.ksd.first_class_denied");
            return FirstClassState.DENIED;
        }
        for (KSDRoute route : stationIdToRoutes.get(station.id)) {
            if (route.routeType.name().equals("KCR_CLASSICAL") && route.hasFirstClassService) {
                return validate(ksd, world, player, clickedPos, station, route);
            }
        }
        playSoundAndSendMessage(world, clickedPos, player, "gui.ksd.first_class_denied_no_matched_route");
        return FirstClassState.DENIED;
    }

    private static FirstClassState validate(KSDRailwayData ksd, Level world, Player player, BlockPos pos, KSDStation validateStation, KSDRoute route) {
        Score balance = getPlayerScore(world, player, TicketSystem.BALANCE_OBJECTIVE);
        Score entryZone = getPlayerScore(world, player, "mtr_entry_zone");
        FirstClassPlayer firstClassPlayer = Utils.getFilteredValueFromDataSet(ksd.jsonDataManager.fps, f -> f.uuid.equals(player.getUUID()));
        if (entryZone.getScore() == 0 || firstClassPlayer == null) {
            playSoundAndSendMessage(world, pos, player, "gui.ksd.first_class_denied_no_entry");
            return FirstClassState.DENIED;
        }
        if (!isValidated(firstClassPlayer)) {
            firstClassPlayer.validatedStationId = validateStation.id;
            firstClassPlayer.routeId = route.id;
            if (balance.getScore() < 0) {
                firstClassPlayer.state = FirstClassState.DENIED;
                playSoundAndSendMessage(world, pos, player, "gui.ksd.first_class_denied");
            } else {
                if (isConcessionary(player)) {
                    firstClassPlayer.state = FirstClassState.ENABLED_ACCESS_CONCESSIONARY;
                    playSoundAndSendMessage(world, pos, player, "gui.ksd.first_class_enabled_access_concessionary");
                } else {
                    firstClassPlayer.state = FirstClassState.ENABLED_ACCESS;
                    playSoundAndSendMessage(world, pos, player, "gui.ksd.first_class_enabled_access");
                }
            }
            ksd.dataCache.sync();
            return firstClassPlayer.state;
        } else {
            playSoundAndSendMessage(world, pos, player, "gui.ksd.first_class_already_valid");
            return FirstClassState.DENIED;
        }
    }

    public static FirstClassState onExitStation(Level world, KSDRailwayData railwayData, KSDStation exitStation, Player player, Score balanceScore, Score entryZoneScore) {
        FirstClassPlayer firstClassPlayer = Utils.getFilteredValueFromDataSet(railwayData.jsonDataManager.fps, f -> f.uuid.equals(player.getUUID()));
        if (firstClassPlayer != null) {
            Score playerCarScore = getPlayerScore(world, player, PLAYER_CAR_OBJECTIVE);
            KSDStation enteredStation = Utils.getFilteredValueFromDataSet(railwayData.stations, s -> s.id == firstClassPlayer.enteredStationId);
            KSDStation validatedStation = Utils.getFilteredValueFromDataSet(railwayData.stations, s -> s.id == firstClassPlayer.validatedStationId);
            KSDRoute route = Utils.getFilteredValueFromDataSet(railwayData.routes, r -> r.id == firstClassPlayer.routeId);
            if (!firstClassPlayer.state.equals(FirstClassState.MTR) && enteredStation != null) {
                final int fare;
                if (firstClassPlayer.state.equals(FirstClassState.ILLEGALLY) && validatedStation == null) {
                    fare = EVASION_FINE;
                } else {
                    if (validatedStation.id == enteredStation.id) {
                        fare = getFare(railwayData, validatedStation, exitStation, route, player);
                    } else {
                        fare = getMTRFare(enteredStation.zone, validatedStation.zone, player) + getFare(railwayData, validatedStation, exitStation, route, player);
                    }
                }
                entryZoneScore.setScore(0);
                balanceScore.add(-fare);
                player.displayClientMessage(Text.translatable("gui.mtr.exit_barrier", String.format("%s (%s)", exitStation.name.replace('|', ' '), enteredStation.zone), fare, balanceScore.getScore()), true);
                playerCarScore.setScore(0);
                railwayData.jsonDataManager.fps.removeIf(fcPlayer -> fcPlayer.uuid.equals(player.getUUID()));
                railwayData.dataCache.sync();
                return firstClassPlayer.state;
            }
        }
        return FirstClassState.MTR;
    }

    public static boolean isValidated(FirstClassPlayer firstClassPlayer) {
        return firstClassPlayer.state.equals(FirstClassState.ENABLED_ACCESS)
                || firstClassPlayer.state.equals(FirstClassState.ENABLED_ACCESS_CONCESSIONARY);
    }

    private static Score getPlayerScore(Level world, Player player, String objectiveName) {
        return world.getScoreboard().getOrCreatePlayerScore(player.getGameProfile().getName(), world.getScoreboard().getObjective(objectiveName));
    }

    private static void addObjectivesIfMissing(Level world) {
        try {
            world.getScoreboard().addObjective(PLAYER_CAR_OBJECTIVE, ObjectiveCriteria.DUMMY, Text.literal("Player Car"), ObjectiveCriteria.RenderType.INTEGER);
        } catch (Exception ignored) {
        }
    }

    private static boolean isConcessionary(Player player) {
        return player.isCreative();
    }

    private static int getFare(KSDRailwayData railwayData, KSDStation enteredStation, KSDStation exitStation, KSDRoute route, Player player) {
        KSDStation interchangeStation;
        final int entryZone = enteredStation.zone;
        final int exitZone = exitStation.zone;
        Map<Long, KSDStation> platformIdToStation = railwayData.dataCache.platformIdToStation;
        if (isInRoute(platformIdToStation, exitStation.id, route)) {
            return getMTRFare(entryZone, exitZone, player) * 2;
        } else if ((interchangeStation = getNearestInterchangeStation(railwayData, route, exitStation.id)) != null) {
            final int fcFare = getMTRFare(entryZone, interchangeStation.zone, player) * 2;
            final int mtrFare = getMTRFare(exitZone, interchangeStation.zone, player);
            return fcFare + mtrFare;
        }
        return getMTRFare(entryZone, exitZone, player);
    }

    private static int getMTRFare(int entryZone, int exitZone, Player player) {
        final int fare = BASE_FARE + ZONE_FARE * Math.abs(entryZone - exitZone);
        return (isConcessionary(player) ? (int) Math.ceil(fare / 2F) : fare);
    }

    private static boolean isInRoute(Map<Long, KSDStation> platformIdToStation, long stationId, KSDRoute route) {
        return getStationIdsInRoute(platformIdToStation, route).contains(stationId);
    }

    private static KSDStation getNearestInterchangeStation(KSDRailwayData railwayData, KSDRoute route, long currentStationId) {
        Map<Long, KSDStation> platformIdToStation = railwayData.dataCache.platformIdToStation;
        if (!isInRoute(platformIdToStation, currentStationId, route)) return null;
        Set<Long> interchangeStationIdsInRoute =
                Utils.getFilteredSetFromDataCollection(getStationIdsInRoute(platformIdToStation, route),
                        id -> stationIdToRoutes.get(id).size() >= 2);
        interchangeStationIdsInRoute.removeIf(id -> id == currentStationId);
        int currentIndex = getStationIndex(platformIdToStation, route, currentStationId);
        long nearestInterchangeStationId = interchangeStationIdsInRoute.stream().min(Comparator.comparingInt(id -> getStationIndex(platformIdToStation, route, id) - currentIndex)).orElse(0L);
        return Utils.getFilteredValueFromDataSet(railwayData.stations, s -> s.id == nearestInterchangeStationId);
    }

    private static Set<Long> getStationIdsInRoute(Map<Long, KSDStation> platformIdToStation, KSDRoute route) {
        Set<Long> stations = new HashSet<>();
        for (Route.RoutePlatform platformId : route.platformIds) {
            stations.add(platformIdToStation.get(platformId.platformId).id);
        }
        return stations;
    }

    private static int getStationIndex(Map<Long, KSDStation> platformIdToStation, KSDRoute route, long stationId) {
        for (int i = 0; i < route.platformIds.size(); i++) {
            if (platformIdToStation.get(route.platformIds.get(i).platformId).id == stationId) return i;
        }
        return -1;
    }

    private static void playSoundAndSendMessage(Level world, BlockPos pos, Player player, String message) {
        world.playSound(null, pos, SoundEvents.TICKET_BARRIER, SoundSource.PLAYERS, 1, 1);
        player.displayClientMessage(Text.translatable(message), false);
    }

    private static Set<Train> getAllTrains(@NotNull RailwayData railwayData) {
        Set<Train> allTrains = new HashSet<>();
        Set<Siding> sidings = railwayData.sidings;
        for (Siding siding : sidings) {
            Set<TrainServer> trains = ((SidingAccessor) siding).getTrains();
            for (TrainServer train : trains) {
                if (train.isOnRoute()) {
                    allTrains.add(train);
                }
            }
        }
        return allTrains;
    }

    private static int getCarForPlayer(Player player, Train train) {
        for (int i = 0; i < train.trainCars; i++) {
            if (isPlayerInCar(player, train, i)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isPlayerInCar(Player player, Train train, int carriageIndex) {
        Vec3[] points = getCarConnectionPoints(train, carriageIndex);
        Vec3 p1 = points[0];
        Vec3 p2 = points[1];
        if (p1 == null || p2 == null) return false;
        Vec3 center = p1.add(p2).scale(0.5);
        double dx = p2.x - p1.x;
        double dz = p2.z - p1.z;
        double dy = p2.y - p1.y;
        float yaw = (float) Mth.atan2(dx, dz);
        float pitch = (float) Math.asin(dy / p1.distanceTo(p2));
        Vec3 delta = player.position().subtract(center);
        Vec3 local = delta.yRot(-yaw).xRot(-pitch);
        double halfLength = p1.distanceTo(p2) / 2;
        double halfWidth = train.width / 2.0;
        return Math.abs(local.x) < halfWidth + 0.8F &&
                Math.abs(local.z) < halfLength + 0.8F &&
                local.y >= -0.5 && local.y <= 3.0;
    }

    private static Vec3[] getCarConnectionPoints(Train train, int carriageIndex) {
        TrainInvoker accessor = (TrainInvoker) train;
        int spacing = train.spacing;
        int trainCars = train.trainCars;
        boolean reversed = train.isReversed();
        int physicalCarFront = reversed ? trainCars - 1 - carriageIndex : carriageIndex;
        int physicalCarBack = physicalCarFront + 1;
        Vec3 front = accessor.invokeGetRoutePosition(physicalCarFront, spacing);
        Vec3 back = accessor.invokeGetRoutePosition(physicalCarBack, spacing);
        return new Vec3[] {front, back};
    }

    public enum FirstClassState implements StringRepresentable {

        MTR("mtr"),
        ILLEGALLY("illegally"),
        ENABLED_ACCESS("enabled_access"),
        ENABLED_ACCESS_CONCESSIONARY("enabled_access_concessionary"),
        DENIED("denied");

        private final String name;

        FirstClassState(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}
