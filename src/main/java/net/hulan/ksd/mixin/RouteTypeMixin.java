package net.hulan.ksd.mixin;

import mtr.data.RouteType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(RouteType.class)
public class RouteTypeMixin {

    @Shadow(remap = false)
    @Final
    @Mutable
    private static RouteType[] $VALUES;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void registerEnum(CallbackInfo ci) {
        List<RouteType> values = new ArrayList<>(Arrays.asList(RouteType.values()));
        RouteType kcrClassical = createInstance("KCR_CLASSICAL", values.size());
        values.add(kcrClassical);
        RouteType kcrModern = createInstance("KCR_MODERN", values.size());
        values.add(kcrModern);
        RouteType kcrLightRail = createInstance("KCR_LIGHT_RAIL", values.size());
        values.add(kcrLightRail);
        $VALUES = values.toArray(new RouteType[0]);
    }

    @Invoker("<init>")
    public static RouteType createInstance(String name, int ordinal) {
        throw new AssertionError();
    }
}
