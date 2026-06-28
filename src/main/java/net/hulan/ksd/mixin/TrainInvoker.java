package net.hulan.ksd.mixin;

import mtr.data.Train;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Train.class)
public interface TrainInvoker {

    @Invoker(remap = false)
    Vec3 invokeGetRoutePosition(int car, int spacing);

    @Invoker(remap = false)
    boolean invokeScanDoors(Level world, double trainX, double trainY, double trainZ,
                            float checkYaw, float pitch, double halfSpacing, int dwellTicks);
}
