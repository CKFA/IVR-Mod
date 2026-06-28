package net.hulan.ksd.mixin;

import mtr.data.TrainServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrainServer.class)
public interface TrainServerAccessor {

    @Accessor(remap = false)
    long getRouteId();
}
