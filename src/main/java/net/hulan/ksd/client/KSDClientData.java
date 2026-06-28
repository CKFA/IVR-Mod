package net.hulan.ksd.client;

import mtr.data.SerializedDataBase;
import net.hulan.ksd.data.KSDPlatform;
import net.hulan.ksd.data.KSDRoute;
import net.hulan.ksd.data.KSDStation;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class KSDClientData {

    public static final Set<KSDStation> STATIONS = new HashSet<>();
    public static final Set<KSDPlatform> PLATFORMS = new HashSet<>();
    public static final Set<KSDRoute> ROUTES = new HashSet<>();
    public static final KSDClientCache DATA_CACHE = new KSDClientCache(STATIONS, PLATFORMS, ROUTES);

    public static void receivePacket(FriendlyByteBuf packet) {
        final FriendlyByteBuf packetCopy = new FriendlyByteBuf(packet.copy());
        clearAndAddAll(STATIONS, deserializeData(packetCopy, KSDStation::new));
        clearAndAddAll(PLATFORMS, deserializeData(packetCopy, KSDPlatform::new));
        clearAndAddAll(ROUTES, deserializeData(packetCopy, KSDRoute::new));
        DATA_CACHE.sync();
    }

    private static <U> void clearAndAddAll(Collection<U> target, Collection<U> source) {
        target.clear();
        target.addAll(source);
    }

    private static <T extends SerializedDataBase> Set<T> deserializeData(FriendlyByteBuf packet, Function<FriendlyByteBuf, T> supplier) {
        Set<T> objects = new HashSet<>();
        int dataCount = packet.readInt();
        for(int i = 0; i < dataCount; ++i) {
            objects.add(supplier.apply(packet));
        }
        return objects;
    }
}
