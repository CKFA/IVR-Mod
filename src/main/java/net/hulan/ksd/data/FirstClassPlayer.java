package net.hulan.ksd.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import mtr.data.EnumHelper;

import java.io.IOException;
import java.util.UUID;

public final class FirstClassPlayer extends JSONData {

    public final UUID uuid;
    public FirstClassValidationSystem.FirstClassState state;
    public long enteredStationId;
    public long validatedStationId;
    public long routeId;
    private static final String KEY_STATE = "state";
    private static final String KEY_ENTERED_STATION_ID = "entered_station_id";
    private static final String KEY_VALIDATED_STATION_ID = "validated_station_id";
    private static final String KEY_ROUTE_ID = "route_id";

    public FirstClassPlayer(String id) {
        uuid = parseId(id, UUID::fromString, UUID::randomUUID);
    }

    public FirstClassPlayer(UUID uuid, KSDStation enteredStation) {
        this.uuid = uuid;
        state = FirstClassValidationSystem.FirstClassState.MTR;
        enteredStationId = enteredStation.id;
    }

    @Override
    public void readFromJson(JsonObject json) throws JsonSyntaxException {
        state = EnumHelper.valueOf(FirstClassValidationSystem.FirstClassState.MTR, json.get(KEY_STATE).getAsString());
        enteredStationId = json.get(KEY_ENTERED_STATION_ID).getAsLong();
        validatedStationId = json.get(KEY_VALIDATED_STATION_ID).getAsLong();
        routeId = json.get(KEY_ROUTE_ID).getAsLong();
    }

    @Override
    public void writeToJson(JsonWriter writer) throws IOException {
        writer.name(KEY_STATE).value(state.name());
        writer.name(KEY_ENTERED_STATION_ID).value(enteredStationId);
        writer.name(KEY_VALIDATED_STATION_ID).value(validatedStationId);
        writer.name(KEY_ROUTE_ID).value(routeId);
    }

    @Override
    public String getId() {
        return uuid.toString();
    }
}
