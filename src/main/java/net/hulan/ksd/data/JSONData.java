package net.hulan.ksd.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class JSONData {

    public abstract void readFromJson(JsonObject json) throws JsonSyntaxException;

    public abstract void writeToJson(JsonWriter writer) throws IOException;

    public abstract String getId();

    protected static <T> T parseId(String id, Function<String, T> supplier, Supplier<T> supplierWhenException) {
        try {
            return supplier.apply(id);
        } catch (RuntimeException e) {
            return supplierWhenException.get();
        }
    }
}
