package net.hulan.ksd.mixin;

import mtr.data.Siding;
import mtr.data.TrainServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(Siding.class)
public interface SidingAccessor {

    @Accessor(remap = false)
    Set<TrainServer> getTrains();
}
