package net.hulan.ksd.sreen;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.client.IDrawing;
import mtr.data.EnumHelper;
import mtr.data.IGui;
import mtr.data.RouteType;
import mtr.mappings.ScreenMapper;
import mtr.mappings.Text;
import mtr.mappings.UtilitiesClient;
import net.hulan.ksd.client.KSDClientData;
import net.hulan.ksd.data.KSDRailwayData;
import net.hulan.ksd.data.KSDRoute;
import net.hulan.ksd.data.KSDStation;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KCRTicketMachineScreen extends ScreenMapper implements IGui {

    private final BlockPos machinePos;
    private final Map<RouteType, Set<KSDRoute>> routeTypeToRoute = new HashMap<>();
    private RouteType selectedType;
    private final Component routeTypes = Text.translatable("gui.ksd.route_types");
    private Button classicalButton;
    private Button modernButton;
    private KCRTicketMachineRailMap railMap;

    public KCRTicketMachineScreen(BlockPos machinePos) {
        super(Text.literal(""));
        this.machinePos = machinePos;
        KSDStation station = KSDRailwayData.getStation(KSDClientData.STATIONS,  machinePos);
        if (station == null) return;
        putRoutes(EnumHelper.valueOf(RouteType.NORMAL, "KCR_CLASSICAL"));
        putRoutes(EnumHelper.valueOf(RouteType.NORMAL, "KCR_MODERN"));
        classicalButton = UtilitiesClient.newButton(Text.translatable("gui.ksd.classical"), button -> setSelectedType(EnumHelper.valueOf(RouteType.NORMAL, "KCR_CLASSICAL")));
        modernButton = UtilitiesClient.newButton(Text.translatable("gui.ksd.modern"), button -> setSelectedType(EnumHelper.valueOf(RouteType.NORMAL, "KCR_MODERN")));
        railMap = new KCRTicketMachineRailMap();
    }

    @Override
    protected void init() {
        IDrawing.setPositionAndWidth(classicalButton, 0, 20, 100);
        IDrawing.setPositionAndWidth(modernButton, 0, 40, 100);
        railMap.setPositionAndSize(100, 0, width - 100, height);
        addDrawableChild(classicalButton);
        addDrawableChild(modernButton);
        addWidget(railMap);
        setSelectedType(EnumHelper.valueOf(RouteType.NORMAL, "KCR_CLASSICAL"));
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        railMap.render(matrices, mouseX, mouseY, delta);
        vLine(matrices, 100, 0, height, ARGB_WHITE);
        drawString(matrices, font, routeTypes, TEXT_PADDING, TEXT_PADDING, ARGB_WHITE);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void putRoutes(RouteType routeType) {
        if (!routeTypeToRoute.containsKey(routeType)) routeTypeToRoute.put(routeType, new HashSet<>());
        for (KSDRoute route : KSDClientData.ROUTES) {
            if (route.routeType.equals(routeType)) {
                routeTypeToRoute.get(routeType).add(route);
            }
        }
    }

    private void setSelectedType(RouteType selectedType) {
        this.selectedType = selectedType;
        toggleButtons();
    }

    private void toggleButtons() {
        classicalButton.active = !selectedType.equals(EnumHelper.valueOf(RouteType.NORMAL, "KCR_CLASSICAL"));
        modernButton.active = !selectedType.equals(EnumHelper.valueOf(RouteType.NORMAL, "KCR_MODERN"));
    }
}
