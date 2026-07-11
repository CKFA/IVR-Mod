package net.hulan.ksd.sreen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.data.RailwayData;
import mtr.data.SavedRailBase;
import mtr.mappings.SelectableMapper;
import mtr.mappings.Text;
import mtr.mappings.UtilitiesClient;
import mtr.mappings.WidgetMapper;
import net.hulan.ksd.client.KSDClientData;
import net.hulan.ksd.data.KSDAreaBase;
import net.hulan.ksd.data.KSDStation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;
import java.util.function.BiConsumer;

public class KCRTicketMachineRailMap implements WidgetMapper, SelectableMapper, GuiEventListener, IGui {
    
    private int x;
    private int y;
    private int width;
    private int height;
    private double scale;
    private double centerX;
    private double centerY;
    private final ClientLevel world;
    private final LocalPlayer player;
    private final Font textRenderer;
    private static final int ARGB_BLUE = -12417548;
    private static final double SCALE_UPPER_LIMIT = 64F;
    private static final double SCALE_LOWER_LIMIT = 0.0078125F;

    public KCRTicketMachineRailMap() {
        Minecraft minecraftClient = Minecraft.getInstance();
        world = minecraftClient.level;
        player = minecraftClient.player;
        textRenderer = minecraftClient.font;
        if (player == null) {
            centerX = 0.0F;
            centerY = 0.0F;
        } else {
            centerX = player.getX();
            centerY = player.getZ();
        }
        scale = 1.0F;
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        UtilitiesClient.beginDrawingRectangle(buffer);
        RenderSystem.enableBlend();
        Tuple<Integer, Integer> topLeft = cordsToWorldPos(0, 0);
        Tuple<Integer, Integer> bottomRight = cordsToWorldPos(width, height);
        int increment = scale >= (double) 1.0F ? 1 : (int) Math.ceil((double) 1.0F / scale);
        for (int i = topLeft.getA(); i <= bottomRight.getA(); i += increment) {
            for (int j = topLeft.getB(); j <= bottomRight.getB(); j += increment) {
                int color;
                if (world != null) {
                    color = divideColorRGB(world.getBlockState(RailwayData.newBlockPos(i, world.getHeight(Heightmap.Types.MOTION_BLOCKING, i, j) - 1, j)).getBlock().defaultMaterialColor().col);
                    drawRectangleFromWorldCords(buffer, i, j, i + increment, j + increment, -16777216 | color);
                }
            }
        }
        try {
            for (KSDStation station : KSDClientData.STATIONS) {
                if (KSDAreaBase.nonNullCorners(station)) {
                    drawRectangleFromWorldCords(buffer, station.corner1.toTupleWithXZ(), station.corner2.toTupleWithXZ(), 2130706432 + station.color);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (player != null) {
            drawFromWorldCords(player.getX(), player.getZ(), (x1, y1) -> {
                drawRectangle(buffer, x1 - (double) 2.0F, y1 - (double) 3.0F, x1 + (double) 2.0F, y1 + (double) 3.0F, -1);
                drawRectangle(buffer, x1 - (double) 3.0F, y1 - (double) 2.0F, x1 + (double) 3.0F, y1 + (double) 2.0F, -1);
                drawRectangle(buffer, x1 - (double) 2.0F, y1 - (double) 2.0F, x1 + (double) 2.0F, y1 + (double) 2.0F, ARGB_BLUE);
            });
        }
        tesselator.end();
        RenderSystem.disableBlend();
        UtilitiesClient.finishDrawingRectangle();
        MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        for (KSDStation station : KSDClientData.STATIONS) {
            if (canDrawAreaText(station)) {
                BlockPos pos = station.getCenter();
                String stationString = String.format("%s|(%s)", station.name, Text.translatable("gui.mtr.zone_number", station.zone).getString());
                drawFromWorldCords(pos.getX(), pos.getZ(), (x1, y1) -> IDrawing.drawStringWithFont(matrices, this.textRenderer, immediate, stationString, (float) this.x + x1.floatValue(), (float) this.y + y1.floatValue(), 15728880));
            }
        }
        immediate.endBatch();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        centerX -= deltaX / scale;
        centerY -= deltaY / scale;
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isMouseOver(mouseX, mouseY)) {
            if (ClientData.hasPermission()) {
                final Tuple<Double, Double> mouseWorldPos = cordsToWorldPos(mouseX - x, mouseY - y);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        double oldScale = this.scale;
        if (oldScale > SCALE_LOWER_LIMIT && amount < (double) 0.0F) {
            this.centerX -= (mouseX - (double) this.x - (double) this.width / (double) 2.0F) / this.scale;
            this.centerY -= (mouseY - (double) this.y - (double) this.height / (double) 2.0F) / this.scale;
        }
        this.scale(amount);
        if (oldScale < SCALE_UPPER_LIMIT && amount > (double) 0.0F) {
            this.centerX += (mouseX - (double) this.x - (double) this.width / (double) 2.0F) / this.scale;
            this.centerY += (mouseY - (double) this.y - (double) this.height / (double) 2.0F) / this.scale;
        }
        return true;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= (double)this.x && mouseY >= (double)this.y && mouseX < (double)(this.x + this.width) && mouseY < (double)(this.y + this.height) && (!(mouseX >= (double)(this.x + this.width - 200)) || !(mouseY >= (double)(this.y + this.height - 20)));
    }

    //@Override
    public void setFocused(boolean focused) {
    }

    //@Override
    public boolean isFocused() {
        return false;
    }

    public void setPositionAndSize(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void scale(double amount) {
        this.scale *= Math.pow(2.0F, amount);
        this.scale = Mth.clamp(this.scale, SCALE_LOWER_LIMIT, SCALE_UPPER_LIMIT);
    }

    public void find(double x1, double z1, double x2, double z2) {
        this.centerX = (x1 + x2) / (double)2.0F;
        this.centerY = (z1 + z2) / (double)2.0F;
        this.scale = Math.max(2.0F, this.scale);
    }

    public void find(BlockPos pos) {
        this.centerX = pos.getX();
        this.centerY = pos.getZ();
        this.scale = Math.max(8.0F, this.scale);
    }

    private Tuple<Integer, Integer> cordsToWorldPos(int mouseX, int mouseY) {
        Tuple<Double, Double> worldPos = cordsToWorldPos(mouseX, (double)mouseY);
        return new Tuple<>((int) Math.floor(worldPos.getA()), (int) Math.floor(worldPos.getB()));
    }

    private Tuple<Double, Double> cordsToWorldPos(double mouseX, double mouseY) {
        double left = (mouseX - (double) width / (double) 2.0F) / scale + centerX;
        double right = (mouseY - (double) height / (double) 2.0F) / scale + centerY;
        return new Tuple<>(left, right);
    }

    private void drawFromWorldCords(double worldX, double worldZ, BiConsumer<Double, Double> callback) {
        double cordsX = (worldX - centerX) * scale + (double) width / (double) 2.0F;
        double cordsY = (worldZ - centerY) * scale + (double) height / (double) 2.0F;
        if (RailwayData.isBetween(cordsX, 0.0F, width) && RailwayData.isBetween(cordsY, 0.0F, height)) {
            callback.accept(cordsX, cordsY);
        }
    }

    private void drawRectangleFromWorldCords(BufferBuilder buffer, Tuple<Integer, Integer> corner1, Tuple<Integer, Integer> corner2, int color) {
        drawRectangleFromWorldCords(
                buffer,
                corner1.getA(),
                corner1.getB(),
                corner2.getA(),
                corner2.getB(),
                color);
    }

    private void drawRectangleFromWorldCords(BufferBuilder buffer, double posX1, double posZ1, double posX2, double posZ2, int color) {
        double x1 = (posX1 - centerX) * scale + (double) width / (double) 2.0F;
        double z1 = (posZ1 - centerY) * scale + (double) height / (double) 2.0F;
        double x2 = (posX2 - centerX) * scale + (double) width / (double) 2.0F;
        double z2 = (posZ2 - centerY) * scale + (double) height / (double) 2.0F;
        drawRectangle(buffer, x1, z1, x2, z2, color);
    }

    private void drawRectangle(BufferBuilder buffer, double xA, double yA, double xB, double yB, int color) {
        double x1 = Math.min(xA, xB);
        double y1 = Math.min(yA, yB);
        double x2 = Math.max(xA, xB);
        double y2 = Math.max(yA, yB);
        if (x1 < (double) width && y1 < (double) height && x2 >= (double) 0.0F && y2 >= (double) 0.0F) {
            IDrawing.drawRectangle(buffer, (double) x + Math.max(0.0F, x1), (double) y + y1, (double) x + x2, (double) y + y2, color);
        }

    }

    private boolean canDrawAreaText(KSDAreaBase areaBase) {
        return areaBase.getCenter() != null && scale >= (double) (80.0F / (float)Math.max(Math.abs(areaBase.corner1.getX() - areaBase.corner2.getX()), Math.abs(areaBase.corner1.getZ() - areaBase.corner2.getZ())));
    }

    private void drawSavedRail(PoseStack matrices, BlockPos savedRailPos, List<? extends SavedRailBase> savedRails) {
        int savedRailCount = savedRails.size();
        for(int i = 0; i < savedRailCount; ++i) {
            int finalI = i;
            drawFromWorldCords((double) savedRailPos.getX() + (double) 0.5F,
                    (double) savedRailPos.getZ() + ((double) i + (double) 0.5F) / (double) savedRailCount,
                    (x1, y1) -> Gui.drawCenteredString(matrices,
                            textRenderer,
                            savedRails.get(finalI).name,
                            x + x1.intValue(),
                            y + y1.intValue() - 4,
                            -1));
        }
    }

    private static int divideColorRGB(int color) {
        int r = (color >> 16 & 255) / 2;
        int g = (color >> 8 & 255) / 2;
        int b = (color & 255) / 2;
        return (r << 16) + (g << 8) + b;
    }
}
