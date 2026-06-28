package net.hulan.ksd.data;

import io.netty.buffer.Unpooled;
import mtr.data.*;
import net.hulan.ksd.mixin.SavedRailBaseAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class KSDPlatform extends SavedRailBase {

    public boolean isSpanishPlatform;
    public DoorOpeningSide doorOpeningSide;
    private static final String KEY_DWELL_TIME = "dwell_time";
    private static final String KEY_IS_SPANISH_PLATFORM = "is_spanish_platform";
    private static final String KEY_DOOR_OPENING_SIDE = "door_opening_side";

    public static KSDPlatform fromMTRPlatform(Platform platform) {
        List<BlockPos> positions = new ArrayList<>(((SavedRailBaseAccessor) (SavedRailBase) platform).getPositions());
        KSDPlatform ksdPlatform = new KSDPlatform(platform.id, platform.transportMode, positions.get(0), positions.get(1));
        ksdPlatform.name = platform.name;
        ksdPlatform.color = platform.color;
        ksdPlatform.dwellTime = platform.getDwellTime();
        return ksdPlatform;
    }

    public static Set<KSDPlatform>  fromMTRPlatforms(Set<Platform> platforms) {
        Set<KSDPlatform> ksdPlatforms = new HashSet<>();
        platforms.forEach(platform -> ksdPlatforms.add(fromMTRPlatform(platform)));
        return ksdPlatforms;
    }

    public KSDPlatform(long id, TransportMode transportMode, BlockPos pos1, BlockPos pos2) {
        super(id, transportMode, pos1, pos2);
        isSpanishPlatform = false;
        doorOpeningSide = DoorOpeningSide.DEFAULT;
    }

    public KSDPlatform(TransportMode transportMode, BlockPos pos1, BlockPos pos2) {
        super(transportMode, pos1, pos2);
        isSpanishPlatform = false;
        doorOpeningSide = DoorOpeningSide.DEFAULT;
    }

    public KSDPlatform(Map<String, Value> map) {
        super(map);
        MessagePackHelper messagePackHelper = new MessagePackHelper(map);
        isSpanishPlatform = messagePackHelper.getBoolean(KEY_IS_SPANISH_PLATFORM);
        doorOpeningSide = EnumHelper.valueOf(DoorOpeningSide.DEFAULT, messagePackHelper.getString(KEY_DOOR_OPENING_SIDE));
    }

    public KSDPlatform(CompoundTag compoundTag) {
        super(compoundTag);
        isSpanishPlatform = compoundTag.getBoolean(KEY_IS_SPANISH_PLATFORM);
        doorOpeningSide = EnumHelper.valueOf(DoorOpeningSide.DEFAULT, compoundTag.getString(KEY_DOOR_OPENING_SIDE));
    }

    public KSDPlatform(FriendlyByteBuf packet) {
        super(packet);
        isSpanishPlatform = packet.readBoolean();
        doorOpeningSide = EnumHelper.valueOf(DoorOpeningSide.DEFAULT, packet.readUtf());
    }

    @Override
    public void toMessagePack(MessagePacker messagePacker) throws IOException {
        super.toMessagePack(messagePacker);
        messagePacker.packString(KEY_IS_SPANISH_PLATFORM).packBoolean(isSpanishPlatform);
        messagePacker.packString(KEY_DOOR_OPENING_SIDE).packString(doorOpeningSide.getSerializedName());
    }

    @Override
    public int messagePackLength() {
        return super.messagePackLength() + 2;
    }

    @Override
    public void writePacket(FriendlyByteBuf packet) {
        super.writePacket(packet);
        packet.writeBoolean(isSpanishPlatform);
        packet.writeUtf(doorOpeningSide.getSerializedName());
    }

    @Override
    public void update(String key, FriendlyByteBuf packet) {
        if (KEY_DWELL_TIME.equals(key)) {
            name = packet.readUtf(PACKET_STRING_READ_LENGTH);
            color = packet.readInt();
            dwellTime = packet.readInt();
            dwellTime = transportMode.continuousMovement ? 1 : dwellTime;
        } else if (KEY_IS_SPANISH_PLATFORM.equals(key)) {
            isSpanishPlatform = packet.readBoolean();
            doorOpeningSide = EnumHelper.valueOf(DoorOpeningSide.DEFAULT, packet.readUtf());
        } else {
            super.update(key, packet);
        }
    }

    public void setDwellTime(int newDwellTime, Consumer<FriendlyByteBuf> sendPacket) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeLong(id);
        packet.writeUtf(transportMode.toString());
        packet.writeUtf(KEY_DWELL_TIME);
        packet.writeUtf(name);
        packet.writeInt(color);
        writeDwellTimePacket(packet, newDwellTime);
        sendPacket.accept(packet);
    }

    public void setSpanishPlatform(boolean isSpanishPlatform, DoorOpeningSide doorOpeningSide, Consumer<FriendlyByteBuf> sendPacket) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeLong(id);
        packet.writeUtf(transportMode.toString());
        packet.writeUtf(KEY_IS_SPANISH_PLATFORM);
        writeSpanishPlatformPacket(packet, isSpanishPlatform, doorOpeningSide);
        sendPacket.accept(packet);
    }

    protected void writeSpanishPlatformPacket(FriendlyByteBuf packet, boolean isSpanishPlatform, DoorOpeningSide doorOpeningSide) {
        this.isSpanishPlatform = isSpanishPlatform;
        this.doorOpeningSide = doorOpeningSide;
        packet.writeBoolean(isSpanishPlatform);
        packet.writeUtf(doorOpeningSide.getSerializedName());
    }

    public enum DoorOpeningSide implements StringRepresentable {
        LEFT,
        RIGHT,
        DEFAULT;

        @Override
        public @NotNull String getSerializedName() {
            return name();
        }

        public DoorOpeningSide next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }
}
