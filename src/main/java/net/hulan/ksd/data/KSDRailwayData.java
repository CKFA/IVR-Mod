package net.hulan.ksd.data;

import mtr.data.RailwayData;
import mtr.mappings.PersistentStateMapper;
import net.hulan.ksd.packet.KSDPacketServer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.NotNull;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KSDRailwayData extends PersistentStateMapper {

    public final Set<KSDStation> stations = new HashSet<>();
    public final Set<KSDPlatform> platforms = new HashSet<>();
    public final Set<KSDRoute> routes = new HashSet<>();
    public final JSONDataManager jsonDataManager;
    public final KSDDataCache dataCache;
    private final Level world;
    private final KSDRailwayDataFileSaveModule railwayDataFileSaveModule;
    public final KSDRailwayDataLoggingModule railwayDataLoggingModule;
    private boolean useTimeAndWindSync;
    private boolean syncedFromMTR;
    private static final String KEY_STATIONS = "stations";
    private static final String KEY_PLATFORMS = "platforms";
    private static final String KEY_ROUTES = "routes";

    public KSDRailwayData(Level world) {
        super("ksd_train_data");
        this.world = world;
        dataCache = new KSDDataCache(stations, platforms, routes);
        ResourceLocation dimensionLocation = world.dimension().location();
        Path savePath = ((ServerLevel) world).getServer().getWorldPath(LevelResource.ROOT).resolve("ksd").resolve(dimensionLocation.getNamespace()).resolve(dimensionLocation.getPath());
        railwayDataFileSaveModule = new KSDRailwayDataFileSaveModule(this, world, savePath);
        railwayDataLoggingModule = new KSDRailwayDataLoggingModule(savePath);
        jsonDataManager = new JSONDataManager((ServerLevel) world);
    }

    public static KSDRailwayData getInstance(Level world) {
        return getInstance(world, () -> new KSDRailwayData(world), "ksd_train_data");
    }

    @Override
    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains("raw_message_pack")) {
            try {
                MessageUnpacker messageUnpacker = MessagePack.newDefaultUnpacker(compoundTag.getByteArray("raw_message_pack"));
                int mapSize = messageUnpacker.unpackMapHeader();

                for(int i = 0; i < mapSize; ++i) {
                    String key = messageUnpacker.unpackString();
                    if (key.equals("mtr_data_version")) {
                        if (messageUnpacker.unpackInt() > 1) {
                            throw new IllegalArgumentException("Unsupported data version");
                        }
                    } else {
                        int arraySize = messageUnpacker.unpackArrayHeader();
                        switch (key) {
                            case KEY_STATIONS -> {
                                for (int j = 0; j < arraySize; ++j) {
                                    stations.add(new KSDStation(readMessagePackSKMap(messageUnpacker)));
                                }
                            }
                            case KEY_PLATFORMS -> {
                                for (int j = 0; j < arraySize; ++j) {
                                    platforms.add(new KSDPlatform(readMessagePackSKMap(messageUnpacker)));
                                }
                            }
                            case KEY_ROUTES -> {
                                for (int j = 0; j < arraySize; ++j) {
                                    routes.add(new KSDRoute(readMessagePackSKMap(messageUnpacker)));
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                final CompoundTag tagStations = compoundTag.getCompound(KEY_STATIONS);
                for (final String key : tagStations.getAllKeys()) {
                    stations.add(new KSDStation(tagStations.getCompound(key)));
                }
                final CompoundTag tagNewPlatforms = compoundTag.getCompound(KEY_PLATFORMS);
                for (final String key : tagNewPlatforms.getAllKeys()) {
                    platforms.add(new KSDPlatform(tagNewPlatforms.getCompound(key)));
                }
                final CompoundTag tagNewRoutes = compoundTag.getCompound(KEY_ROUTES);
                for (final String key : tagNewRoutes.getAllKeys()) {
                    routes.add(new KSDRoute(tagNewRoutes.getCompound(key)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        railwayDataFileSaveModule.load();
        jsonDataManager.onLoad();
        dataCache.sync();
        useTimeAndWindSync = compoundTag.getBoolean("use_time_and_wind_sync");
        syncedFromMTR = compoundTag.getBoolean("synced_from_mtr");
        if (!syncedFromMTR) {
            syncFromMTR();
        }
    }

    public void onPlayerJoin(ServerPlayer serverPlayer) {
        KSDPacketServer.sendAllInChunks(serverPlayer, stations, platforms, routes);
    }

    public static boolean hasNoPermission(ServerPlayer serverPlayer) {
        return !hasPermission(serverPlayer.gameMode.getGameModeForPlayer());
    }

    public static boolean hasPermission(GameType gameType) {
        return gameType == GameType.CREATIVE || gameType == GameType.SURVIVAL;
    }

    @Override
    public void save(File file) {
        MinecraftServer minecraftServer = ((ServerLevel) world).getServer();
        if (!minecraftServer.isStopped() && minecraftServer.isRunning()) {
            railwayDataFileSaveModule.autoSave();
        } else {
            railwayDataFileSaveModule.fullSave();
        }
        railwayDataLoggingModule.save();
        jsonDataManager.onSave();
        setDirty();
        super.save(file);
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putBoolean("use_time_and_wind_sync", useTimeAndWindSync);
        compoundTag.putBoolean("synced_from_mtr", syncedFromMTR);
        return compoundTag;
    }

    private void syncFromMTR() {
        try {
            RailwayData railwayData = RailwayData.getInstance(world);
            if (railwayData != null) {
                stations.clear();
                stations.addAll(KSDStation.fromMTRStations(railwayData.stations));
                platforms.clear();
                platforms.addAll(KSDPlatform.fromMTRPlatforms(railwayData.platforms));
                routes.clear();
                routes.addAll(KSDRoute.fromMTRRoutes(railwayData.routes));
            }
        } catch (Exception e) {
            e.printStackTrace();
            syncedFromMTR = false;
            return;
        }
        syncedFromMTR = true;
    }

    public static KSDStation getStation(Set<KSDStation> stations, BlockPos pos) {
        try {
            return Utils.getFilteredValueFromDataSet(stations, station -> station.inArea(pos.getX(), pos.getY(), pos.getZ()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String, Value> readMessagePackSKMap(MessageUnpacker messageUnpacker) throws IOException {
        int size = messageUnpacker.unpackMapHeader();
        HashMap<String, Value> result = new HashMap<>(size);
        for(int i = 0; i < size; ++i) {
            result.put(messageUnpacker.unpackString(), messageUnpacker.unpackValue());
        }
        return result;
    }
}
