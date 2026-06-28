package net.hulan.ksd.data;

import mtr.data.IReducedSaveData;
import mtr.data.NameColorDataBase;
import mtr.data.SerializedDataBase;
import net.minecraft.world.level.Level;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.Value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class KSDRailwayDataFileSaveModule extends KSDRailwayDataModuleBase {

    private boolean canAutoSave = false;
    private boolean dataLoaded = false;
    private boolean useReducedHash = true;
    private int filesWritten;
    private int filesDeleted;
    private long autoSaveStartMillis;
    private final List<Long> dirtyStationIds = new ArrayList<>();
    private final List<Long> dirtyPlatformIds = new ArrayList<>();
    private final List<Long> dirtyRouteIds = new ArrayList<>();
    private final Map<Path, Integer> existingFiles = new HashMap<>();
    private final List<Path> checkFilesToDelete = new ArrayList<>();
    private final Path stationsPath;
    private final Path platformsPath;
    private final Path routesPath;

    public KSDRailwayDataFileSaveModule(KSDRailwayData railwayData, Level world, Path savePath) {
        super(railwayData, world);
        stationsPath = savePath.resolve("stations");
        platformsPath = savePath.resolve("platforms");
        routesPath = savePath.resolve("routes");
        try {
            Files.createDirectories(stationsPath);
            Files.createDirectories(platformsPath);
            Files.createDirectories(routesPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        existingFiles.clear();
        readMessagePackFromFile(stationsPath, KSDStation::new, railwayData.stations::add, false);
        readMessagePackFromFile(platformsPath, KSDPlatform::new, railwayData.platforms::add, true);
        readMessagePackFromFile(routesPath, KSDRoute::new, railwayData.routes::add, false);
        System.out.println("KSD data successfully loaded for " + world.dimension().location());
        canAutoSave = true;
        dataLoaded = true;
    }

    public void fullSave() {
        useReducedHash = false;
        dirtyStationIds.clear();
        dirtyPlatformIds.clear();
        dirtyRouteIds.clear();
        checkFilesToDelete.clear();
        autoSave();
        while (true) {
            if (autoSaveTick()) {
                break;
            }
        }
        canAutoSave = false;
    }

    public void autoSave() {
        if (!dataLoaded) {
            dataLoaded = true;
            canAutoSave = true;
        }
        if (canAutoSave && checkFilesToDelete.isEmpty()) {
            autoSaveStartMillis = System.currentTimeMillis();
            filesWritten = 0;
            filesDeleted = 0;
            dirtyStationIds.addAll(railwayData.dataCache.stationIdMap.keySet());
            dirtyPlatformIds.addAll(railwayData.dataCache.platformIdMap.keySet());
            dirtyRouteIds.addAll(railwayData.dataCache.routeIdMap.keySet());
            checkFilesToDelete.addAll(existingFiles.keySet());
        }
    }

    public boolean autoSaveTick() {
        if (canAutoSave) {
            final boolean deleteEmptyOld = checkFilesToDelete.isEmpty();
            boolean hasSpareTime = writeDirtyDataToFile(dirtyStationIds, railwayData.dataCache.stationIdMap::get, id -> id, stationsPath);
            if (hasSpareTime) {
                hasSpareTime = writeDirtyDataToFile(dirtyPlatformIds, railwayData.dataCache.platformIdMap::get, id -> id, platformsPath);
            }
            if (hasSpareTime) {
                hasSpareTime = writeDirtyDataToFile(dirtyRouteIds, railwayData.dataCache.routeIdMap::get, id -> id, routesPath);
            }
            final boolean doneWriting = dirtyStationIds.isEmpty() && dirtyPlatformIds.isEmpty() && dirtyRouteIds.isEmpty();
            if (hasSpareTime && !checkFilesToDelete.isEmpty() && doneWriting) {
                final Path path = checkFilesToDelete.remove(0);
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                existingFiles.remove(path);
                filesDeleted++;
            }
            if (!deleteEmptyOld && checkFilesToDelete.isEmpty()) {
                if (!useReducedHash || filesWritten > 0 || filesDeleted > 0) {
                    System.out.println("KSD save complete for " + world.dimension().location() + " in " + (System.currentTimeMillis() - autoSaveStartMillis) / 1000 + " second(s)");
                    if (filesWritten > 0) {
                        System.out.println("- Changed: " + filesWritten);
                    }
                    if (filesDeleted > 0) {
                        System.out.println("- Deleted: " + filesDeleted);
                    }
                }
            }
            return doneWriting && checkFilesToDelete.isEmpty();
        } else {
            return true;
        }
    }

    private <T extends SerializedDataBase> void readMessagePackFromFile(Path path, Function<Map<String, Value>, T> getData, Consumer<T> callback, boolean skipVerify) {
        try (final Stream<Path> pathStream = Files.list(path)) {
            pathStream.forEach(idFolder -> {
                try (final Stream<Path> folderStream = Files.list(idFolder)) {
                    folderStream.forEach(idFile -> {
                        try (final InputStream inputStream = Files.newInputStream(idFile)) {
                            try (final MessageUnpacker messageUnpacker = MessagePack.newDefaultUnpacker(inputStream)) {
                                final int size = messageUnpacker.unpackMapHeader();
                                final HashMap<String, Value> result = new HashMap<>(size);
                                for (int i = 0; i < size; i++) {
                                    result.put(messageUnpacker.unpackString(), messageUnpacker.unpackValue());
                                }
                                final T data = getData.apply(result);
                                if (skipVerify || !(data instanceof NameColorDataBase) || !((NameColorDataBase) data).name.isEmpty()) {
                                    callback.accept(data);
                                }
                                existingFiles.put(idFile, getHash(data, true));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path writeMessagePackToFile(SerializedDataBase data, long id, Path path) {
        final Path parentPath = path.resolve(String.valueOf(id % 100));
        try {
            Files.createDirectories(parentPath);
            final Path dataPath = parentPath.resolve(String.valueOf(id));
            final int hash = getHash(data, useReducedHash);
            if (!existingFiles.containsKey(dataPath) || hash != existingFiles.get(dataPath)) {
                final MessagePacker messagePacker = MessagePack.newDefaultPacker(Files.newOutputStream(dataPath, StandardOpenOption.CREATE));
                messagePacker.packMapHeader(data.messagePackLength());
                data.toMessagePack(messagePacker);
                messagePacker.close();
                existingFiles.put(dataPath, hash);
                filesWritten++;
            }
            return dataPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T extends SerializedDataBase, U> boolean writeDirtyDataToFile(List<U> dirtyData, Function<U, T> getId, Function<U, Long> idToLong, Path path) {
        final long millis = System.currentTimeMillis();
        while (!dirtyData.isEmpty()) {
            final U id = dirtyData.remove(0);
            final T data = getId.apply(id);
            if (data != null) {
                final Path newPath = writeMessagePackToFile(data, idToLong.apply(id), path);
                if (newPath != null) {
                    checkFilesToDelete.remove(newPath);
                }
            }
            if (System.currentTimeMillis() - millis >= 2) {
                return false;
            }
        }
        return true;
    }

    private static int getHash(SerializedDataBase data, boolean useReducedHash) {
        try {
            final MessageBufferPacker messageBufferPacker = MessagePack.newDefaultBufferPacker();
            if (useReducedHash && data instanceof IReducedSaveData) {
                messageBufferPacker.packMapHeader(((IReducedSaveData) data).reducedMessagePackLength());
                ((IReducedSaveData) data).toReducedMessagePack(messageBufferPacker);
            } else {
                messageBufferPacker.packMapHeader(data.messagePackLength());
                data.toMessagePack(messageBufferPacker);
            }
            final int hash = Arrays.hashCode(messageBufferPacker.toByteArray());
            messageBufferPacker.close();
            return hash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
