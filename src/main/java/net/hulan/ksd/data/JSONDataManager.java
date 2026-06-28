package net.hulan.ksd.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class JSONDataManager {

    public final Set<FirstClassPlayer> fps = new HashSet<>();
    public final Set<Octopus> octopusCards = new HashSet<>();
    private final Path fcpPath;
    private final Path ocPath;

    public JSONDataManager(ServerLevel world) {
        Path savedPath = world.getServer().getWorldPath(LevelResource.ROOT).resolve("JsonData").resolve(world.dimension().location().getPath());
        fcpPath = savedPath.resolve("FirstClassPlayers");
        ocPath = savedPath.resolve("Octopus");
        try {
            Files.createDirectories(fcpPath);
            Files.createDirectories(ocPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onLoad() {
        fps.clear();
        loadData(fcpPath, FirstClassPlayer::new, fps::add);
        loadData(ocPath, Octopus::new, octopusCards::add);
    }

    public void onSave() {
        saveData(fcpPath, fps);
        saveData(ocPath, octopusCards);
    }

    private <T extends JSONData> void loadData(Path path, Function<String, T> instance, Consumer<T> storeData) {
        try (Stream<Path> dataPathStream = Files.list(path)) {
            dataPathStream.forEach(dataPath -> {
                try (JsonReader reader = new JsonReader(Files.newBufferedReader(dataPath))) {
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    if (!jsonElement.isJsonObject()) return;
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    String id = jsonObject.keySet().iterator().next();
                    T data = instance.apply(id);
                    data.readFromJson(jsonObject.getAsJsonObject(id));
                    storeData.accept(data);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends JSONData> void saveData(Path path, Set<T> dataSet) {
        dataSet.forEach(data -> {
            String id = data.getId();
            Path filePath = path.resolve(id + ".json");
            try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
                writer.setIndent("  ");
                writer.beginObject();
                writer.name(id).beginObject();
                data.writeToJson(writer);
                writer.endObject();
                writer.endObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
