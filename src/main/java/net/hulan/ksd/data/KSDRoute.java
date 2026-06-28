package net.hulan.ksd.data;

import io.netty.buffer.Unpooled;
import mtr.data.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class KSDRoute extends NameColorDataBase implements IGui {

    public RouteType routeType;
    public boolean isLightRailRoute;
    public boolean isHidden;
    public boolean disableNextStationAnnouncements;
    public Route.CircularState circularState;
    public String lightRailRouteNumber;
    public final List<Route.RoutePlatform> platformIds;
    public boolean hasFirstClassService;
    public int firstClassCar;
    private static final String KEY_PLATFORM_IDS = "platform_ids";
    private static final String KEY_CUSTOM_DESTINATIONS = "custom_destinations";
    private static final String KEY_ROUTE_TYPE = "route_type";
    private static final String KEY_IS_LIGHT_RAIL_ROUTE = "is_light_rail_route";
    private static final String KEY_LIGHT_RAIL_ROUTE_NUMBER = "light_rail_route_number";
    private static final String KEY_IS_ROUTE_HIDDEN = "is_route_hidden";
    private static final String KEY_DISABLE_NEXT_STATION_ANNOUNCEMENTS = "disable_next_station_announcements";
    private static final String KEY_CIRCULAR_STATE = "circular_state";
    private static final String KEY_HAS_FC_SERVICE = "has_fc_service";
    private static final String KEY_FC_CAR = "fc_car";

    public static KSDRoute fromMTRRoute(Route route) {
        KSDRoute ksdRoute = new KSDRoute(route.id, route.transportMode);
        ksdRoute.name = IGui.textOrUntitled(route.name);
        ksdRoute.color = route.color;
        ksdRoute.routeType = route.routeType;
        ksdRoute.isLightRailRoute = route.isLightRailRoute;
        ksdRoute.isHidden = route.isHidden;
        ksdRoute.disableNextStationAnnouncements = route.disableNextStationAnnouncements;
        ksdRoute.circularState = route.circularState;
        ksdRoute.lightRailRouteNumber = route.lightRailRouteNumber;
        ksdRoute.platformIds.clear();
        ksdRoute.platformIds.addAll(route.platformIds);
        return ksdRoute;
    }

    public static Set<KSDRoute> fromMTRRoutes(Set<Route> routes) {
        Set<KSDRoute> ksdRoutes = new HashSet<>();
        routes.forEach(route -> ksdRoutes.add(fromMTRRoute(route)));
        return ksdRoutes;
    }
    
    public KSDRoute(TransportMode transportMode) {
        this(0L, transportMode);
    }

    public KSDRoute(long id, TransportMode transportMode) {
        super(id, transportMode);
        platformIds = new ArrayList<>();
        routeType = RouteType.NORMAL;
        isLightRailRoute = false;
        circularState = Route.CircularState.NONE;
        lightRailRouteNumber = "";
        isHidden = false;
        disableNextStationAnnouncements = false;
        hasFirstClassService = false;
        firstClassCar = -1;
    }

    public KSDRoute(Map<String, Value> map) {
        super(map);
        MessagePackHelper messagePackHelper = new MessagePackHelper(map);
        platformIds = new ArrayList<>();
        messagePackHelper.iterateArrayValue(KEY_PLATFORM_IDS, (platformId) -> platformIds.add(new Route.RoutePlatform(platformId.asIntegerValue().asLong())));
        List<String> customDestinations = new ArrayList<>();
        messagePackHelper.iterateArrayValue(KEY_CUSTOM_DESTINATIONS, (customDestination) -> customDestinations.add(customDestination.asStringValue().asString()));
        for(int i = 0; i < Math.min(platformIds.size(), customDestinations.size()); ++i) {
            platformIds.get(i).customDestination = customDestinations.get(i);
        }
        routeType = EnumHelper.valueOf(RouteType.NORMAL, messagePackHelper.getString(KEY_ROUTE_TYPE));
        isLightRailRoute = messagePackHelper.getBoolean(KEY_IS_LIGHT_RAIL_ROUTE);
        isHidden = messagePackHelper.getBoolean(KEY_IS_ROUTE_HIDDEN);
        disableNextStationAnnouncements = messagePackHelper.getBoolean(KEY_DISABLE_NEXT_STATION_ANNOUNCEMENTS);
        lightRailRouteNumber = messagePackHelper.getString(KEY_LIGHT_RAIL_ROUTE_NUMBER);
        circularState = EnumHelper.valueOf(Route.CircularState.NONE, messagePackHelper.getString(KEY_CIRCULAR_STATE));
        hasFirstClassService = messagePackHelper.getBoolean(KEY_HAS_FC_SERVICE);
        firstClassCar = messagePackHelper.getInt(KEY_FC_CAR);
    }
    
    public KSDRoute(CompoundTag compoundTag) {
        super(compoundTag);
        platformIds = new ArrayList<>();
        long[] platformIdsArray = compoundTag.getLongArray(KEY_PLATFORM_IDS);
        for(long platformId : platformIdsArray) {
            platformIds.add(new Route.RoutePlatform(platformId));
        }
        routeType = EnumHelper.valueOf(RouteType.NORMAL, compoundTag.getString(KEY_ROUTE_TYPE));
        isLightRailRoute = compoundTag.getBoolean(KEY_IS_LIGHT_RAIL_ROUTE);
        isHidden = compoundTag.getBoolean(KEY_IS_ROUTE_HIDDEN);
        disableNextStationAnnouncements = compoundTag.getBoolean(KEY_DISABLE_NEXT_STATION_ANNOUNCEMENTS);
        lightRailRouteNumber = compoundTag.getString(KEY_LIGHT_RAIL_ROUTE_NUMBER);
        circularState = EnumHelper.valueOf(Route.CircularState.NONE, compoundTag.getString(KEY_CIRCULAR_STATE));
        hasFirstClassService = compoundTag.getBoolean(KEY_HAS_FC_SERVICE);
        firstClassCar = compoundTag.getInt(KEY_FC_CAR);
    }

    public KSDRoute(FriendlyByteBuf packet) {
        super(packet);
        platformIds = new ArrayList<>();
        int platformCount = packet.readInt();
        for(int i = 0; i < platformCount; ++i) {
            Route.RoutePlatform routePlatform = new Route.RoutePlatform(packet.readLong());
            routePlatform.customDestination = packet.readUtf(32767);
            platformIds.add(routePlatform);
        }
        routeType = EnumHelper.valueOf(RouteType.NORMAL, packet.readUtf(32767));
        isLightRailRoute = packet.readBoolean();
        isHidden = packet.readBoolean();
        disableNextStationAnnouncements = packet.readBoolean();
        lightRailRouteNumber = packet.readUtf(32767);
        circularState = EnumHelper.valueOf(Route.CircularState.NONE, packet.readUtf(32767));
        hasFirstClassService = packet.readBoolean();
        firstClassCar = packet.readInt();
    }

    public void toMessagePack(MessagePacker messagePacker) throws IOException {
        super.toMessagePack(messagePacker);
        messagePacker.packString(KEY_PLATFORM_IDS).packArrayHeader(platformIds.size());
        for(Route.RoutePlatform routePlatform : platformIds) {
            messagePacker.packLong(routePlatform.platformId);
        }
        messagePacker.packString(KEY_CUSTOM_DESTINATIONS).packArrayHeader(platformIds.size());
        for(Route.RoutePlatform routePlatform : platformIds) {
            messagePacker.packString(routePlatform.customDestination);
        }
        messagePacker.packString(KEY_ROUTE_TYPE).packString(routeType.toString());
        messagePacker.packString(KEY_IS_LIGHT_RAIL_ROUTE).packBoolean(isLightRailRoute);
        messagePacker.packString(KEY_IS_ROUTE_HIDDEN).packBoolean(isHidden);
        messagePacker.packString(KEY_DISABLE_NEXT_STATION_ANNOUNCEMENTS).packBoolean(disableNextStationAnnouncements);
        messagePacker.packString(KEY_LIGHT_RAIL_ROUTE_NUMBER).packString(lightRailRouteNumber);
        messagePacker.packString(KEY_CIRCULAR_STATE).packString(circularState.toString());
        messagePacker.packString(KEY_HAS_FC_SERVICE).packBoolean(hasFirstClassService);
        messagePacker.packString(KEY_FC_CAR).packInt(firstClassCar);
    }

    public int messagePackLength() {
        return super.messagePackLength() + 10;
    }

    public void writePacket(FriendlyByteBuf packet) {
        super.writePacket(packet);
        packet.writeInt(platformIds.size());
        platformIds.forEach((routePlatform) -> {
            packet.writeLong(routePlatform.platformId);
            packet.writeUtf(routePlatform.customDestination);
        });
        packet.writeUtf(routeType.toString());
        packet.writeBoolean(isLightRailRoute);
        packet.writeBoolean(isHidden);
        packet.writeBoolean(disableNextStationAnnouncements);
        packet.writeUtf(lightRailRouteNumber);
        packet.writeUtf(circularState.toString());
        packet.writeBoolean(hasFirstClassService);
        packet.writeInt(firstClassCar);
    }

    public void update(String key, FriendlyByteBuf packet) {
        switch (key) {
            case KEY_PLATFORM_IDS -> {
                platformIds.clear();
                int platformCount = packet.readInt();
                for (int i = 0; i < platformCount; ++i) {
                    Route.RoutePlatform routePlatform = new Route.RoutePlatform(packet.readLong());
                    routePlatform.customDestination = packet.readUtf(32767);
                    platformIds.add(routePlatform);
                }
            }
            case KEY_IS_LIGHT_RAIL_ROUTE -> {
                name = packet.readUtf(32767);
                color = packet.readInt();
                routeType = EnumHelper.valueOf(RouteType.NORMAL, packet.readUtf(32767));
                isLightRailRoute = packet.readBoolean();
                lightRailRouteNumber = packet.readUtf(32767);
                isHidden = packet.readBoolean();
                disableNextStationAnnouncements = packet.readBoolean();
                circularState = EnumHelper.valueOf(Route.CircularState.NONE, packet.readUtf(32767));
            }
            case KEY_HAS_FC_SERVICE -> {
                hasFirstClassService = packet.readBoolean();
                firstClassCar = packet.readInt();
            }
            default -> super.update(key, packet);
        }
    }

    protected boolean hasTransportMode() {
        return true;
    }

    public Route toMTRRoute() {
        Route mtrRoute = new Route(id, transportMode);
        mtrRoute.name = IGui.textOrUntitled(name);
        mtrRoute.color = color;
        mtrRoute.routeType = routeType;
        mtrRoute.isLightRailRoute = isLightRailRoute;
        mtrRoute.isHidden = isHidden;
        mtrRoute.disableNextStationAnnouncements = disableNextStationAnnouncements;
        mtrRoute.circularState = circularState;
        mtrRoute.lightRailRouteNumber = lightRailRouteNumber;
        mtrRoute.platformIds.clear();
        mtrRoute.platformIds.addAll(platformIds);
        return mtrRoute;
    }

    public void setPlatformIds(Consumer<FriendlyByteBuf> sendPacket) {
        FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeLong(id);
        packet.writeUtf(transportMode.toString());
        packet.writeUtf(KEY_PLATFORM_IDS);
        packet.writeInt(platformIds.size());
        platformIds.forEach((routePlatform) -> {
            packet.writeLong(routePlatform.platformId);
            packet.writeUtf(routePlatform.customDestination);
        });
        sendPacket.accept(packet);
    }

    public void setExtraData(Consumer<FriendlyByteBuf> sendPacket) {
        FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeLong(id);
        packet.writeUtf(transportMode.toString());
        packet.writeUtf(KEY_IS_LIGHT_RAIL_ROUTE);
        packet.writeUtf(name);
        packet.writeInt(color);
        packet.writeUtf(routeType.toString());
        packet.writeBoolean(isLightRailRoute);
        packet.writeUtf(lightRailRouteNumber);
        packet.writeBoolean(isHidden);
        packet.writeBoolean(disableNextStationAnnouncements);
        packet.writeUtf(circularState.toString());
        sendPacket.accept(packet);
    }

    public void setFirstClassData(Consumer<FriendlyByteBuf> sendPacket) {
        FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeLong(id);
        packet.writeUtf(transportMode.toString());
        packet.writeUtf(KEY_HAS_FC_SERVICE);
        packet.writeBoolean(hasFirstClassService);
        packet.writeInt(firstClassCar);
        sendPacket.accept(packet);
    }

    public int getPlatformIdIndex(long platformId) {
        for(int i = 0; i < platformIds.size(); ++i) {
            if (platformIds.get(i).platformId == platformId) {
                return i;
            }
        }
        return -1;
    }

    public boolean containsPlatformId(long platformId) {
        return getPlatformIdIndex(platformId) >= 0;
    }

    public long getFirstPlatformId() {
        return platformIds.isEmpty() ? 0L : platformIds.get(0).platformId;
    }

    public long getLastPlatformId() {
        return platformIds.isEmpty() ? 0L : platformIds.get(platformIds.size() - 1).platformId;
    }

    public String getDestination(int index) {
        for(int i = Math.min(platformIds.size() - 1, index); i >= 0; --i) {
            String customDestination = platformIds.get(i).customDestination;
            if (destinationIsReset(customDestination)) {
                return null;
            }
            if (!customDestination.isEmpty()) {
                return customDestination;
            }
        }
        return null;
    }

    public static boolean destinationIsReset(String destination) {
        return destination.equals("\\r") || destination.equals("\\reset");
    }
}
