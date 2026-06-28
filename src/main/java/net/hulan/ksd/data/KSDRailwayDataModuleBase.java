package net.hulan.ksd.data;

import net.minecraft.world.level.Level;

public class KSDRailwayDataModuleBase {

    protected final KSDRailwayData railwayData;
    protected final Level world;

    public KSDRailwayDataModuleBase(KSDRailwayData railwayData, Level world) {
        this.railwayData = railwayData;
        this.world = world;
    }
}
