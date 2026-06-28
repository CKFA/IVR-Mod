package net.hulan.ksd.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import mtr.data.EnumHelper;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Octopus extends JSONData {

    public final long cardId;
    public int balance;
    public List<History> histories;
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_HISTORY = "histories";

    public Octopus(String id) {
        cardId = parseId(id, Long::parseLong, () -> new Random().nextLong());
    }

    public Octopus() {
        cardId = new Random().nextLong();
        histories = new ArrayList<>(20);
    }

    @Override
    public void readFromJson(JsonObject json) throws JsonSyntaxException {
        balance = json.get(KEY_BALANCE).getAsInt();
        JsonObject historyObjects = json.get(KEY_HISTORY).getAsJsonObject();
        histories = new ArrayList<>(historyObjects.size());
        for (String key : historyObjects.keySet()) {
            History history = new History(key);
            JsonObject historyObject = historyObjects.get(key).getAsJsonObject();
            history.readFromJson(historyObject);
            histories.add(history);
        }
    }

    @Override
    public void writeToJson(JsonWriter writer) throws IOException {
        writer.name(KEY_BALANCE).value(balance);
        writer.name(KEY_HISTORY).beginObject();
        for (History history : histories) {
            writer.name(history.getId()).beginObject();
            history.writeToJson(writer);
            writer.endObject();
        }
        writer.endObject();
    }

    @Override
    public String getId() {
        return String.valueOf(cardId);
    }

    public void addBalance(int balance) {
        this.balance += balance;
    }

    public void decBalance(int balance) {
        this.balance -= balance;
    }

    public static class History extends JSONData {

        public final long cardId;
        public long time;
        public long amount;
        public long balance;
        public TransactionType transactionType;
        private static final String KEY_TIME = "time";
        private static final String KEY_AMOUNT = "amount";
        private static final String KEY_BALANCE = "balance";
        private static final String KEY_TRANSACTION_TYPE = "transaction_type";

        public History(String cardId) {
            this.cardId = parseId(cardId, Long::parseLong, () -> new Random().nextLong());
        }

        public History(long carId, long amount, long balance, TransactionType transactionType) {
            this.cardId = carId;
            time = System.currentTimeMillis();
            this.amount = amount;
            this.balance = balance;
            this.transactionType = transactionType;
        }

        @Override
        public void readFromJson(JsonObject json) throws JsonSyntaxException {
            time = json.get(KEY_TIME).getAsLong();
            amount = json.get(KEY_AMOUNT).getAsLong();
            balance = json.get(KEY_BALANCE).getAsLong();
            transactionType = EnumHelper.valueOf(TransactionType.NONE, json.get(KEY_TRANSACTION_TYPE).getAsString());
        }

        @Override
        public void writeToJson(JsonWriter writer) throws IOException {
            writer.name(KEY_TIME).value(time);
            writer.name(KEY_AMOUNT).value(amount);
            writer.name(KEY_BALANCE).value(balance);
            writer.name(KEY_TRANSACTION_TYPE).value(transactionType.name());
        }

        @Override
        public String getId() {
            return String.valueOf(cardId);
        }

        public enum TransactionType implements StringRepresentable {

            NONE,
            MTR,
            KCR,
            LIGHT_RAIL,
            BUS,
            TAXI,
            SHOP,
            RESTAURANT;

            @Override
            public @NotNull String getSerializedName() {
                return name();
            }
        }
    }
}
