package net.hulan.ksd.data;

import io.netty.buffer.Unpooled;
import mtr.data.MessagePackHelper;
import mtr.data.NameColorDataBase;
import mtr.data.RailwayData;
import mtr.data.TransportMode;
import net.hulan.Tuples;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

public abstract class KSDAreaBase extends NameColorDataBase {

    public Tuples<Integer, Integer, Integer> corner1;
    public Tuples<Integer, Integer, Integer> corner2;
    private static final String KEY_X_MIN = "x_min";
    private static final String KEY_Y_MIN = "y_min";
    private static final String KEY_Z_MIN = "z_min";
    private static final String KEY_X_MAX = "x_max";
    private static final String KEY_Y_MAX = "y_max";
    private static final String KEY_Z_MAX = "z_max";
    private static final String KEY_CORNERS = "corners";
    private static final String KEY_KSD_CORNERS = "ksd_corners";

    public KSDAreaBase() {
    }

    public KSDAreaBase(long id) {
        super(id);
    }

    public KSDAreaBase(TransportMode transportMode) {
        super(transportMode);
    }

    public KSDAreaBase(long id, TransportMode transportMode) {
        super(id, transportMode);
    }

    public KSDAreaBase(Map<String, Value> map) {
        super(map);
        MessagePackHelper messagePackHelper = new MessagePackHelper(map);
        setCorners(messagePackHelper.getInt(KEY_X_MIN),
                messagePackHelper.getInt(KEY_Y_MIN),
                messagePackHelper.getInt(KEY_Z_MIN),
                messagePackHelper.getInt(KEY_X_MAX),
                messagePackHelper.getInt(KEY_Y_MAX),
                messagePackHelper.getInt(KEY_Z_MAX));
    }

    /** @deprecated */
    @Deprecated
    public KSDAreaBase(CompoundTag compoundTag) {
        super(compoundTag);
        setCorners(compoundTag.getInt(KEY_X_MIN),
                compoundTag.getInt(KEY_Y_MIN),
                compoundTag.getInt(KEY_Z_MIN),
                compoundTag.getInt(KEY_X_MAX),
                compoundTag.getInt(KEY_Y_MAX),
                compoundTag.getInt(KEY_Z_MAX));
    }

    public KSDAreaBase(FriendlyByteBuf packet) {
        super(packet);
        setCorners(packet.readInt(), packet.readInt(), packet.readInt(), packet.readInt(), packet.readInt(), packet.readInt());
    }

    public void toMessagePack(MessagePacker messagePacker) throws IOException {
        super.toMessagePack(messagePacker);
        messagePacker.packString(KEY_X_MIN).packInt(corner1 == null ? 0 : corner1.getX());
        messagePacker.packString(KEY_Y_MIN).packInt(corner1 == null ? 0 : corner1.getY());
        messagePacker.packString(KEY_Z_MIN).packInt(corner1 == null ? 0 : corner1.getZ());
        messagePacker.packString(KEY_X_MAX).packInt(corner2 == null ? 0 : corner2.getX());
        messagePacker.packString(KEY_Y_MAX).packInt(corner2 == null ? 0 : corner2.getY());
        messagePacker.packString(KEY_Z_MAX).packInt(corner2 == null ? 0 : corner2.getZ());
    }

    public int messagePackLength() {
        return super.messagePackLength() + 6;
    }

    public void writePacket(FriendlyByteBuf packet) {
        super.writePacket(packet);
        packet.writeInt(corner1 == null ? 0 : corner1.getX());
        packet.writeInt(corner1 == null ? 0 : corner1.getY());
        packet.writeInt(corner1 == null ? 0 : corner1.getZ());
        packet.writeInt(corner2 == null ? 0 : corner2.getX());
        packet.writeInt(corner2 == null ? 0 : corner2.getY());
        packet.writeInt(corner2 == null ? 0 : corner2.getZ());
    }

    public void update(String key, FriendlyByteBuf packet) {
        if (key.equals(KEY_CORNERS)) {
            setCorners(packet.readInt(), corner1 == null ? -64 : corner1.getY(), packet.readInt(), packet.readInt(), corner2 == null ? 319 : corner2.getY(), packet.readInt());
        } else if (key.equals(KEY_KSD_CORNERS)) {
            setCorners(packet.readInt(), packet.readInt(), packet.readInt(), packet.readInt(), packet.readInt(), packet.readInt());
        } else {
            super.update(key, packet);
        }
    }

    @Override
    public String toString() {
        return "KSDAreaBase";
    }

    public void setCorners(Consumer<FriendlyByteBuf> sendPacket) {
        FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeLong(id);
        packet.writeUtf(transportMode.toString());
        packet.writeUtf(KEY_KSD_CORNERS);
        packet.writeInt(corner1 == null ? 0 : corner1.getX());
        packet.writeInt(corner1 == null ? 0 : corner1.getY());
        packet.writeInt(corner1 == null ? 0 : corner1.getZ());
        packet.writeInt(corner2 == null ? 0 : corner2.getX());
        packet.writeInt(corner2 == null ? 0 : corner2.getY());
        packet.writeInt(corner2 == null ? 0 : corner2.getZ());
        sendPacket.accept(packet);
    }

    public void setCornersToMTRStation(Consumer<FriendlyByteBuf> sendPacket) {
        FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeLong(id);
        packet.writeUtf(transportMode.toString());
        packet.writeUtf(KEY_CORNERS);
        packet.writeInt(corner1 == null ? 0 : corner1.getX());
        packet.writeInt(corner1 == null ? 0 : corner1.getZ());
        packet.writeInt(corner2 == null ? 0 : corner2.getX());
        packet.writeInt(corner2 == null ? 0 : corner2.getZ());
        sendPacket.accept(packet);
    }

    public boolean inArea(int x, int y, int z) {
        return nonNullCorners(this)
                && RailwayData.isBetween(x, corner1.getX(), corner2.getX())
                && RailwayData.isBetween(y, corner1.getY(), corner2.getY())
                && RailwayData.isBetween(z, corner1.getZ(), corner2.getZ());
    }

    public boolean intersecting(KSDAreaBase areaBase) {
        return nonNullCorners(this) && nonNullCorners(areaBase) && (inThis(areaBase) || areaBase.inThis(this));
    }

    public BlockPos getCenter() {
        return nonNullCorners(this) ?
                RailwayData.newBlockPos((corner1.getX() + corner2.getX()) / 2,
                        (corner1.getY() + corner2.getY()) / 2,
                        (corner1.getZ() + corner2.getZ()) / 2) : null;
    }

    private void setCorners(int x1, int y1, int z1, int x2, int y2, int z2) {
        corner1 = new Tuples<>(x1, y1, z1);
        corner2 = new Tuples<>(x2, y2, z2);
    }

    private boolean inThis(KSDAreaBase areaBase) {
        return inArea(areaBase.corner1.getX(), areaBase.corner1.getY(), areaBase.corner1.getZ())
                || inArea(areaBase.corner1.getX(), areaBase.corner2.getY(), areaBase.corner1.getZ())
                || inArea(areaBase.corner1.getX(), areaBase.corner1.getY(), areaBase.corner2.getZ())
                || inArea(areaBase.corner1.getX(), areaBase.corner2.getY(), areaBase.corner2.getZ())
                || inArea(areaBase.corner2.getX(), areaBase.corner1.getY(), areaBase.corner1.getZ())
                || inArea(areaBase.corner2.getX(), areaBase.corner2.getY(), areaBase.corner1.getZ())
                || inArea(areaBase.corner2.getX(), areaBase.corner1.getY(), areaBase.corner2.getZ())
                || inArea(areaBase.corner2.getX(), areaBase.corner2.getY(), areaBase.corner2.getZ());
    }

    public static boolean nonNullCorners(KSDAreaBase station) {
        return station != null && station.corner1 != null && station.corner2 != null;
    }
}
