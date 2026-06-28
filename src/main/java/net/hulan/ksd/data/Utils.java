package net.hulan.ksd.data;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Utils {

    public static InteractionResult checkHoldingItem(Level world, Player player, Consumer<Item> callbackItem, Runnable callbackNoItem, Item... items) {
        Item holdingItem = null;
        for(Item item : items) {
            if (player.isHolding(item)) {
                holdingItem = item;
                break;
            }
        }
        if (holdingItem != null) {
            if (!world.isClientSide) {
                callbackItem.accept(holdingItem);
            }
            return InteractionResult.SUCCESS;
        } else if (callbackNoItem == null) {
            return InteractionResult.FAIL;
        } else if (!world.isClientSide) {
            callbackNoItem.run();
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    public static <T> T getFilteredValueFromDataSet(Set<T> dataSet, Predicate<T> filter) {
        return getFilteredValueFromDataSetWithDefaultValue(dataSet, filter, null);
    }

    public static <T> T getFilteredValueFromDataSetWithDefaultValue(Set<T> dataSet, Predicate<T> filter, T defaultValue) {
        return dataSet.stream().filter(filter).findFirst().orElse(defaultValue);
    }

    public static <T> void executeFromDataSet(Set<T> dataSet, Predicate<T> filter, Consumer<T> action) {
        dataSet.stream().filter(filter).findFirst().ifPresent(action);
    }

    public static <T> List<T> getFilteredListFromDataCollection(Collection<T> dataCollection, Predicate<T> filter) {
        return dataCollection.stream().filter(filter).toList();
    }

    public static <T, R> List<R> getSortedAndMappedListFromDataCollection(Collection<T> dataCollection, Function<T, R> mapper) {
        return dataCollection.stream().sorted().map(mapper).toList();
    }
    
    public static <T, R> List<R> getMappedListFromDataCollection(List<T> dataCollection, Function<T, R> mapper) {
        return dataCollection.stream().map(mapper).toList();
    }

    public static <T, R> List<R> getMappedAndNonNullListFromDataCollection(Collection<T> dataCollection, Function<T, R> mapper) {
        return getMappedAndFilteredListFromDataCollection(dataCollection, mapper, Objects::nonNull);
    }

    public static <T, R> List<R> getMappedAndFilteredListFromDataCollection(Collection<T> dataCollection, Function<T, R> mapper, Predicate<R> filter) {
        return dataCollection.stream().map(mapper).filter(filter).toList();
    }
}
