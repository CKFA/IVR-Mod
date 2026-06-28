package net.hulan.ksd.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import mtr.data.Train;
import mtr.path.PathData;
import net.hulan.ksd.client.KSDClientData;
import net.hulan.ksd.data.KSDPlatform;
import net.hulan.ksd.data.KSDRailwayData;
import net.hulan.ksd.data.Utils;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(Train.class)
public abstract class TrainMixin {

    @Shadow(remap = false)
    protected float elapsedDwellTicks;

    @Shadow(remap = false)
    public abstract int getTotalDwellTicks();

    @Shadow(remap = false)
    @Final
    public static int DOOR_MOVE_TIME;

    @Shadow(remap = false)
    @Final
    protected static int DOOR_DELAY;

    @Shadow(remap = false)
    @Final
    public List<PathData> path;

    @Shadow(remap = false)
    protected int nextPlatformIndex;

    @Redirect(method = "calculateCar", at = @At(value = "INVOKE", target = "Lmtr/data/Train;scanDoors(Lnet/minecraft/world/level/Level;DDDFFDI)Z", ordinal = 0))
    private boolean scanLeftDoor(Train instance,
                                 Level world,
                                 double trainX,
                                 double trainY,
                                 double trainZ,
                                 float checkYaw,
                                 float pitch,
                                 double halfSpacing,
                                 int dwellTicks) {
        TrainInvoker invoker = (TrainInvoker) instance;
        boolean original = invoker.invokeScanDoors(world, trainX, trainY, trainZ, checkYaw, pitch, halfSpacing, dwellTicks);
        float phase = (float) getTotalDwellTicks() / 2;
        boolean spanishCheck;
        long platformId = path.get(nextPlatformIndex).savedRailBaseId;
        KSDPlatform platform = getPlatform(world, platformId);
        if (platform != null && platform.isSpanishPlatform && !platform.doorOpeningSide.equals(KSDPlatform.DoorOpeningSide.DEFAULT)) {
            spanishCheck = platform.doorOpeningSide.equals(KSDPlatform.DoorOpeningSide.LEFT);
            if (phase + DOOR_DELAY <= elapsedDwellTicks && elapsedDwellTicks < phase * 3) {
                spanishCheck = !spanishCheck;
            }
            return original && spanishCheck;
        } else {
            return original;
        }
    }

    @Redirect(method = "calculateCar", at = @At(value = "INVOKE", target = "Lmtr/data/Train;scanDoors(Lnet/minecraft/world/level/Level;DDDFFDI)Z", ordinal = 1))
    private boolean scanRightDoor(Train instance,
                                 Level world,
                                 double trainX,
                                 double trainY,
                                 double trainZ,
                                 float checkYaw,
                                 float pitch,
                                 double halfSpacing,
                                 int dwellTicks) {
        TrainInvoker invoker = (TrainInvoker) instance;
        boolean original = invoker.invokeScanDoors(world, trainX, trainY, trainZ, checkYaw, pitch, halfSpacing, dwellTicks);
        float phase = (float) getTotalDwellTicks() / 2;
        boolean spanishCheck;
        long platformId = path.get(nextPlatformIndex).savedRailBaseId;
        KSDPlatform platform = getPlatform(world, platformId);
        if (platform != null && platform.isSpanishPlatform && !platform.doorOpeningSide.equals(KSDPlatform.DoorOpeningSide.DEFAULT)) {
            spanishCheck = platform.doorOpeningSide.equals(KSDPlatform.DoorOpeningSide.RIGHT);
            if (phase + DOOR_DELAY <= elapsedDwellTicks && elapsedDwellTicks < phase * 2) {
                spanishCheck = !spanishCheck;
            }
            return original && spanishCheck;
        } else {
            return original;
        }
    }

    @ModifyVariable(method = "simulateTrain", at = @At(value = "STORE", ordinal = 0), name = "tempDoorOpen")
    private boolean tempDoorOpen(boolean tempDoorOpen, @Local(name = "world") Level world) {
        return openDoors(world, tempDoorOpen);
    }

    @Unique
    private boolean openDoors(Level world, boolean original) {
        long platformId = path.get(nextPlatformIndex).savedRailBaseId;
        KSDPlatform platform = getPlatform(world, platformId);
        if (platform == null || !platform.isSpanishPlatform || platform.doorOpeningSide.equals(KSDPlatform.DoorOpeningSide.DEFAULT)) {
            return original;
        }
        float phase = (float) getTotalDwellTicks() / 2;
        if (0 <= elapsedDwellTicks && elapsedDwellTicks < DOOR_DELAY) {
            return false;
        } else if (DOOR_DELAY <= elapsedDwellTicks && elapsedDwellTicks < phase - DOOR_MOVE_TIME) {
            return true;
        } else if (phase - DOOR_MOVE_TIME <= elapsedDwellTicks && elapsedDwellTicks < phase + DOOR_DELAY) {
            return false;
        } else if (phase + DOOR_DELAY <= elapsedDwellTicks && elapsedDwellTicks < phase * 2 - DOOR_MOVE_TIME) {
            return true;
        } else if (phase * 2 - DOOR_MOVE_TIME <= elapsedDwellTicks && elapsedDwellTicks < phase * 2) {
            return false;
        } else {
            return original;
        }
    }

    @Unique
    private KSDPlatform getPlatform(Level world, long platformId) {
        if (world.isClientSide) {
            return Utils.getFilteredValueFromDataSet(KSDClientData.PLATFORMS, p -> p.id == platformId);
        } else {
            KSDRailwayData railwayData = KSDRailwayData.getInstance(world);
            if (railwayData == null) {
                return null;
            }
            return Utils.getFilteredValueFromDataSet(railwayData.platforms, p -> p.id == platformId);
        }
    }
}
