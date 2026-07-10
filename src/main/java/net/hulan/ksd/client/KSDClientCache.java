package net.hulan.ksd.client;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import mtr.block.BlockLiftTrackFloor;
import mtr.client.ClientCache;
import mtr.client.Config;
import mtr.data.IGui;
import mtr.data.Route;
import mtr.data.SavedRailBase;
import mtr.data.TransportMode;
import mtr.mappings.Utilities;
import net.hulan.ivr.client.IVRRouteMapGenerator;
import net.hulan.ksd.data.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.AttributedString;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class KSDClientCache extends KSDDataCache {

    private Font font;
    private Font fontCjk;
    private final Object2ObjectLinkedOpenHashMap<String, DynamicResource> dynamicResources = new Object2ObjectLinkedOpenHashMap<>();
    private final ObjectLinkedOpenHashSet<String> resourcesToRefresh = new ObjectLinkedOpenHashSet<>();
    public final Map<Long, Map<Integer, ClientCache.ColorNameTuple>> stationIdToRoutes = new HashMap<>();
    private final Map<TransportMode, Map<BlockPos, List<KSDPlatform>>> posToPlatforms = new HashMap<>();
    private final Map<Long, Map<Long, KSDPlatform>> stationIdToPlatforms = new HashMap<>();
    private final Map<Long, List<ClientCache.PlatformRouteDetails>> platformIdToRoutes = new HashMap<>();
    private final List<Long> clearStationIdToPlatforms = new ArrayList<>();
    private final List<Long> clearPlatformIdToRoutes = new ArrayList<>();
    private final java.util.List<Runnable> resourceRegistryQueue = new ArrayList<>();
    private static final ResourceLocation DEFAULT_BLACK_RESOURCE = new ResourceLocation("mtr", "textures/block/black.png");
    private static final ResourceLocation DEFAULT_WHITE_RESOURCE = new ResourceLocation("mtr", "textures/block/white.png");
    private static final ResourceLocation DEFAULT_TRANSPARENT_RESOURCE = new ResourceLocation("mtr", "textures/block/transparent.png");

    public KSDClientCache(Set<KSDStation> stations, Set<KSDPlatform> platforms, Set<KSDRoute> routes) {
        super(stations, platforms, routes);
        for (final TransportMode transportMode : TransportMode.values()) {
            posToPlatforms.put(transportMode, new HashMap<>());
        }
    }

    @Override
    protected void syncAdditional() {
        for (final TransportMode transportMode : TransportMode.values()) {
            mapPosToSavedRails(posToPlatforms.get(transportMode), platforms, transportMode);
        }
        stationIdToRoutes.clear();
        routes.forEach(route -> {
            if (!route.isHidden) {
                route.platformIds.forEach(platformId -> {
                    final KSDStation station = platformIdToStation.get(platformId.platformId);
                    if (station != null) {
                        if (!stationIdToRoutes.containsKey(station.id)) {
                            stationIdToRoutes.put(station.id, new HashMap<>());
                        }
                        stationIdToRoutes.get(station.id).put(route.color, new ClientCache.ColorNameTuple(route.color, route.name.split("\\|\\|")[0]));
                    }
                });
            }
        });
        stationIdToPlatforms.keySet().forEach(id -> {
            if (!clearStationIdToPlatforms.contains(id)) {
                clearStationIdToPlatforms.add(id);
            }
        });
        platformIdToRoutes.keySet().forEach(id -> {
            if (!clearPlatformIdToRoutes.contains(id)) {
                clearPlatformIdToRoutes.add(id);
            }
        });
    }

    public void resetFonts() {
        font = null;
        fontCjk = null;
        refreshDynamicResources();
    }

    public void refreshDynamicResources() {
        System.out.println("Refreshing dynamic resources");
        resourcesToRefresh.addAll(dynamicResources.keySet());
    }

    public Map<Long, KSDPlatform> requestStationIdToPlatforms(long stationId) {
        if (!stationIdToPlatforms.containsKey(stationId)) {
            final KSDStation station = stationIdMap.get(stationId);
            if (station != null) {
                stationIdToPlatforms.put(stationId, areaIdToSavedRails(station, platforms));
            } else {
                stationIdToPlatforms.put(stationId, new HashMap<>());
            }
        }
        return stationIdToPlatforms.get(stationId);
    }

    public List<ClientCache.PlatformRouteDetails> requestPlatformIdToRoutes(long platformId) {
        if (!platformIdToRoutes.containsKey(platformId)) {
            platformIdToRoutes.put(platformId, Utils.getMappedAndNonNullListFromDataCollection(routes, route -> {
                final int index = route.getPlatformIdIndex(platformId);
                if (index < 0) {
                    return null;
                } else {
                    final List<ClientCache.PlatformRouteDetails.StationDetails> stationDetails =
                            Utils.getMappedListFromDataCollection(route.platformIds, pi -> {
                                final KSDStation station = platformIdToStation.get(pi.platformId);
                                if (station == null || !stationIdToRoutes.containsKey(station.id)) {
                                    return new ClientCache.PlatformRouteDetails.StationDetails("", new ArrayList<>());
                                } else {
                                    return new ClientCache.PlatformRouteDetails.StationDetails(station.name,
                                            Utils.getFilteredListFromDataCollection(stationIdToRoutes.get(station.id).values(),
                                                    colorNameTuple -> colorNameTuple.color != route.color));
                                }
                            });
                    return new ClientCache.PlatformRouteDetails(route.name.split("\\|\\|")[0], route.color, route.circularState, index, stationDetails);
                }
            }));
        }
        return platformIdToRoutes.get(platformId);
    }

    public String[] requestLiftFloorText(BlockPos pos) {
        Level world = Minecraft.getInstance().level;
        String[] text = new String[]{"", ""};
        if (world != null && pos != null) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BlockLiftTrackFloor.TileEntityLiftTrackFloor) {
                text[0] = ((BlockLiftTrackFloor.TileEntityLiftTrackFloor)blockEntity).getFloorNumber();
                text[1] = ((BlockLiftTrackFloor.TileEntityLiftTrackFloor)blockEntity).getFloorDescription();
            }
        }

        return text;
    }

    public Set<KSDStation> getConnectingStationsIncludingThisOne(KSDStation station) {
        final Set<KSDStation> stationsToCheck = new HashSet<>();
        stationsToCheck.add(station);
        if (stationIdToConnectingStations.containsKey(station)) {
            stationsToCheck.addAll(stationIdToConnectingStations.get(station));
        }
        return stationsToCheck;
    }

    public Map<Integer, ClientCache.ColorNameTuple> getAllRoutesIncludingConnectingStations(KSDStation station) {
        final Map<Integer, ClientCache.ColorNameTuple> routeMap = new HashMap<>();
        getConnectingStationsIncludingThisOne(station).forEach(checkStation -> {
            if (stationIdToRoutes.containsKey(checkStation.id)) {
                routeMap.putAll(stationIdToRoutes.get(checkStation.id));
            }
        });
        return routeMap;
    }

    public String getFormattedRouteDestination(KSDRoute route, int currentStationIndex, String circularMarker) {
        try {
            final String customDestination = route.getDestination(currentStationIndex);
            if (customDestination != null) {
                return customDestination;
            }

            if (route.circularState == Route.CircularState.NONE) {
                return platformIdToStation.get(route.getLastPlatformId()).name;
            } else {
                boolean isVia = false;
                String text = "";

                for (int i = currentStationIndex + 1; i < route.platformIds.size() - 1; i++) {
                    if (stationIdToRoutes.get(platformIdToStation.get(route.platformIds.get(i).platformId).id).size() > 1) {
                        text = platformIdToStation.get(route.platformIds.get(i).platformId).name;
                        isVia = true;
                        break;
                    }
                }

                if (!isVia) {
                    text = platformIdToStation.get(route.getLastPlatformId()).name;
                }

                final String translationString = String.format("%s_%s", route.circularState == Route.CircularState.CLOCKWISE ? "clockwise" : "anticlockwise", isVia ? "via" : "to");
                return circularMarker + IGui.insertTranslation("gui.mtr." + translationString + "_cjk", "gui.mtr." + translationString, 1, text);
            }
        } catch (Exception ignored) {
            return "";
        }
    }

    public DynamicResource getPixelatedText(String text, int textColor, int maxWidth, float cjkSizeRatio, boolean fullPixel) {
        return getResource(String.format("pixelated_text_%s_%s_%s_%s_%s", text, textColor, maxWidth, cjkSizeRatio, fullPixel), () -> IVRRouteMapGenerator.generatePixelatedText(text, textColor, maxWidth, cjkSizeRatio, fullPixel), DefaultRenderingColor.TRANSPARENT);
    }

    public DynamicResource getColorStrip(long platformId) {
        return getResource(String.format("color_%s", platformId), () -> IVRRouteMapGenerator.generateColorStrip(platformId), DefaultRenderingColor.TRANSPARENT);
    }

    public DynamicResource getStationName(String stationName, float aspectRatio) {
        return getResource(String.format("station_name_%s_%s", stationName, aspectRatio), () -> IVRRouteMapGenerator.generateStationName(stationName, aspectRatio), DefaultRenderingColor.TRANSPARENT);
    }

    public DynamicResource getStationNameEntrance(int textColor, String stationName, float aspectRatio) {
        return getResource(String.format("station_name_entrance_%s_%s_%s", textColor, stationName, aspectRatio), () -> IVRRouteMapGenerator.generateStationNameEntrance(textColor, stationName, aspectRatio), DefaultRenderingColor.TRANSPARENT);
    }

    public DynamicResource getSingleRowStationName(long platformId, float aspectRatio) {
        return getResource(String.format("single_row_station_name_%s_%s", platformId, aspectRatio), () -> IVRRouteMapGenerator.generateSingleRowStationName(platformId, aspectRatio), DefaultRenderingColor.WHITE);
    }

    public DynamicResource getSignText(String text, IGui.HorizontalAlignment horizontalAlignment, float paddingScale, int backgroundColor, int textColor) {
        return getResource(String.format("sign_text_%s_%s_%s_%s_%s", text, horizontalAlignment, paddingScale, backgroundColor, textColor), () -> IVRRouteMapGenerator.generateSignText(text, horizontalAlignment, paddingScale, backgroundColor, textColor), DefaultRenderingColor.TRANSPARENT);
    }

    public DynamicResource getLiftPanelDisplay(String originalText, int textColor) {
        return getResource(String.format("lift_panel_display_%s", originalText), () -> IVRRouteMapGenerator.generateLiftPanel(originalText, textColor), DefaultRenderingColor.BLACK);
    }

    public DynamicResource getExitSignLetter(String exitLetter, String exitNumber, int backgroundColor) {
        return getResource(String.format("exit_sign_letter_%s_%s", exitLetter, exitNumber),
                () -> IVRRouteMapGenerator.generateExitSignLetter(exitLetter, exitNumber, backgroundColor),
                DefaultRenderingColor.TRANSPARENT);
    }

    public DynamicResource getRouteSquare(int color, String routeName, IGui.HorizontalAlignment horizontalAlignment) {
        return getResource(String.format("route_square_%s_%s_%s", color, routeName, horizontalAlignment),
                () -> IVRRouteMapGenerator.generateRouteSquare(color, routeName, horizontalAlignment),
                DefaultRenderingColor.TRANSPARENT);
    }

    public DynamicResource getDirectionArrow(long platformId,
                                                            boolean hasLeft,
                                                            boolean hasRight,
                                                            IGui.HorizontalAlignment horizontalAlignment,
                                                            boolean showToString,
                                                            float paddingScale,
                                                            float aspectRatio,
                                                            int backgroundColor,
                                                            int textColor,
                                                            int transparentColor) {
        return getResource(String.format("direction_arrow_%s_%s_%s_%s_%s_%s_%s_%s_%s_%s",
                        platformId,
                        hasLeft,
                        hasRight,
                        horizontalAlignment,
                        showToString,
                        paddingScale,
                        aspectRatio,
                        backgroundColor,
                        textColor,
                        transparentColor),
                () -> IVRRouteMapGenerator.generateDirectionArrow(platformId,
                        hasLeft,
                        hasRight,
                        horizontalAlignment,
                        showToString,
                        paddingScale,
                        aspectRatio,
                        backgroundColor,
                        textColor,
                        transparentColor),
                transparentColor == 0 && backgroundColor == -1 ? DefaultRenderingColor.WHITE : DefaultRenderingColor.TRANSPARENT);
    }

    public DynamicResource getDirectionArrowForRS(long platformId,
                                                                 boolean hasLeft,
                                                                 boolean hasRight,
                                                                 IGui.HorizontalAlignment horizontalAlignment,
                                                                 float paddingScale,
                                                                 float aspectRatio,
                                                                 int backgroundColor,
                                                                 int textColor,
                                                                 int transparentColor) {
        return getResource(String.format("direction_arrow_for_rs_%s_%s_%s_%s_%s_%s_%s_%s",
                        platformId,
                        hasLeft,
                        hasRight,
                        horizontalAlignment,
                        paddingScale,
                        aspectRatio,
                        textColor,
                        transparentColor),
                () -> IVRRouteMapGenerator.generateDirectionArrowForRS(platformId,
                        hasLeft,
                        hasRight,
                        horizontalAlignment,
                        paddingScale,
                        aspectRatio,
                        textColor,
                        transparentColor),
                transparentColor == 0 && backgroundColor == -1 ? DefaultRenderingColor.WHITE : DefaultRenderingColor.TRANSPARENT);
    }

    public DynamicResource getRouteMap(long platformId,
                                                      boolean vertical,
                                                      boolean flip,
                                                      float aspectRatio,
                                                      boolean transparentWhite) {
        return getResource(String.format("route_map_%s_%s_%s_%s_%s",
                        platformId,
                        vertical,
                        flip,
                        aspectRatio,
                        transparentWhite),
                () -> IVRRouteMapGenerator.generateRouteMap(platformId,
                        vertical,
                        flip,
                        aspectRatio,
                        transparentWhite),
                transparentWhite ? DefaultRenderingColor.TRANSPARENT : DefaultRenderingColor.WHITE);
    }

    public byte[] getTextPixels(String text, int[] dimensions, int fontSizeCjk, int fontSize) {
        return getTextPixels(text, dimensions, 2147483647, (int)((float)Math.max(fontSizeCjk, fontSize) * 1.25F), fontSizeCjk, fontSize, 0, null);
    }

    public byte[] getTextPixels(String text,
                                int[] dimensions,
                                int maxWidth,
                                int maxHeight,
                                int fontSizeCjk,
                                int fontSize,
                                int padding,
                                IGui.HorizontalAlignment horizontalAlignment) {
        if (maxWidth <= 0) {
            dimensions[0] = 0;
            dimensions[1] = 0;
            return new byte[0];
        } else {
            boolean oneRow = horizontalAlignment == null;
            String[] defaultTextSplit = IGui.textOrUntitled(text).split("\\|");
            String[] textSplit;
            if (Config.languageOptions() == 0) {
                textSplit = defaultTextSplit;
            } else {
                String[] tempTextSplit = Arrays.stream(IGui.textOrUntitled(text).split("\\|")).filter((textPart) -> IGui.isCjk(textPart) == (Config.languageOptions() == 1)).toArray(String[]::new);
                textSplit = tempTextSplit.length == 0 ? defaultTextSplit : tempTextSplit;
            }
            AttributedString[] attributedStrings = new AttributedString[textSplit.length];
            int[] textWidths = new int[textSplit.length];
            int[] fontSizes = new int[textSplit.length];
            FontRenderContext context = new FontRenderContext(new AffineTransform(), false, false);
            int width = 0;
            int height = 0;
            int index;
            int newFontSize;
            int characterIndex;
            for(index = 0; index < textSplit.length; ++index) {
                newFontSize = !IGui.isCjk(textSplit[index]) && font.canDisplayUpTo(textSplit[index]) < 0 ? fontSize : fontSizeCjk;
                attributedStrings[index] = new AttributedString(textSplit[index]);
                fontSizes[index] = newFontSize;
                Font fontSized = font.deriveFont(Font.PLAIN, (float)newFontSize);
                Font fontCjkSized = fontCjk.deriveFont(Font.PLAIN, (float)newFontSize);
                for(characterIndex = 0; characterIndex < textSplit[index].length(); ++characterIndex) {
                    char character = textSplit[index].charAt(characterIndex);
                    Font newFont;
                    if (fontSized.canDisplay(character)) {
                        newFont = fontSized;
                    } else if (fontCjkSized.canDisplay(character)) {
                        newFont = fontCjkSized;
                    } else {
                        Font defaultFont = null;
                        Font[] var26 = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
                        for (Font testFont : var26) {
                            if (testFont.canDisplay(character)) {
                                defaultFont = testFont;
                                break;
                            }
                        }
                        newFont = (defaultFont == null ? new Font(null) : defaultFont).deriveFont(Font.PLAIN, (float)newFontSize);
                    }
                    textWidths[index] += newFont.getStringBounds(textSplit[index].substring(characterIndex, characterIndex + 1), context).getBounds().width;
                    attributedStrings[index].addAttribute(TextAttribute.FONT, newFont, characterIndex, characterIndex + 1);
                }
                if (oneRow) {
                    if (index > 0) {
                        width += padding;
                    }
                    width += textWidths[index];
                    height = Math.max(height, (int)((float)fontSizes[index] * 1.25F));
                } else {
                    width = Math.max(width, Math.min(maxWidth, textWidths[index]));
                    height = (int)((float)height + (float)fontSizes[index] * 1.25F);
                }
            }
            index = 0;
            newFontSize = Math.min(height, maxHeight);
            BufferedImage image = new BufferedImage(width + (oneRow ? 0 : padding * 2), newFontSize + (oneRow ? 0 : padding * 2), 10);
            Graphics2D graphics2D = image.createGraphics();
            graphics2D.setColor(Color.WHITE);
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            for(characterIndex = 0; characterIndex < textSplit.length; ++characterIndex) {
                if (oneRow) {
                    graphics2D.drawString(attributedStrings[characterIndex].getIterator(), (float)index, (float)height / 1.25F);
                    index += textWidths[characterIndex] + padding;
                } else {
                    float scaleY = (float)newFontSize / (float)height;
                    float textWidth = Math.min((float)maxWidth, (float)textWidths[characterIndex] * scaleY);
                    float scaleX = textWidth / (float)textWidths[characterIndex];
                    AffineTransform stretch = new AffineTransform();
                    stretch.concatenate(AffineTransform.getScaleInstance(scaleX, scaleY));
                    graphics2D.setTransform(stretch);
                    graphics2D.drawString(attributedStrings[characterIndex].getIterator(), horizontalAlignment.getOffset(0.0F, textWidth - (float)width) / scaleY + (float)padding / scaleX, (float)(index + fontSizes[characterIndex]) + (float)padding / scaleY);
                    index = (int)((float)index + (float)fontSizes[characterIndex] * 1.25F);
                }
            }
            dimensions[0] = width + (oneRow ? 0 : padding * 2);
            dimensions[1] = newFontSize + (oneRow ? 0 : padding * 2);
            byte[] pixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
            graphics2D.dispose();
            image.flush();
            return pixels;
        }
    }

    private DynamicResource getResource(String key, Supplier<NativeImage> supplier, DefaultRenderingColor defaultRenderingColor) {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (font == null || fontCjk == null) {
            ResourceManager resourceManager = minecraftClient.getResourceManager();
            try {
                font = Font.createFont(0, Utilities.getInputStream(resourceManager.getResource(new ResourceLocation("ivr", "font/noto-sans-semibold.ttf"))));
                fontCjk = Font.createFont(0, Utilities.getInputStream(resourceManager.getResource(new ResourceLocation("ivr", "font/noto-serif-cjk-tc-semibold.ttf"))));
            } catch (Exception var7) {
                var7.printStackTrace();
            }
        }
        if (!resourceRegistryQueue.isEmpty()) {
            Runnable runnable = resourceRegistryQueue.remove(0);
            if (runnable != null) {
                runnable.run();
            }
        }
        boolean needsRefresh = resourcesToRefresh.contains(key);
        DynamicResource dynamicResource = dynamicResources.get(key);
        if (dynamicResource != null && !needsRefresh) {
            return dynamicResource;
        } else {
            IVRRouteMapGenerator.setConstants();
            CompletableFuture.supplyAsync(supplier).thenAccept((nativeImage) -> resourceRegistryQueue.add(() -> {
                DynamicResource staticTextureProviderOld = dynamicResources.get(key);
                if (staticTextureProviderOld != null) {
                    staticTextureProviderOld.remove();
                }
                DynamicResource dynamicResourceNew;
                if (nativeImage == null) {
                    dynamicResourceNew = defaultRenderingColor.dynamicResource;
                } else {
                    DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);
                    String newKey = key;
                    try {
                        newKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }
                    String var10003 = newKey.toLowerCase(Locale.ENGLISH);
                    ResourceLocation resourceLocation = new ResourceLocation("ivr", "dynamic_texture_" + var10003.replaceAll("[^0-9a-z_]", "_"));
                    minecraftClient.getTextureManager().register(resourceLocation, dynamicTexture);
                    dynamicResourceNew = new DynamicResource(resourceLocation, dynamicTexture);
                }
                dynamicResources.put(key, dynamicResourceNew);
            }));
            if (needsRefresh) {
                resourcesToRefresh.remove(key);
            }
            if (dynamicResource == null) {
                dynamicResources.put(key, defaultRenderingColor.dynamicResource);
                return defaultRenderingColor.dynamicResource;
            } else {
                return dynamicResource;
            }
        }
    }

    public void clearDataIfNeeded() {
        if (!clearStationIdToPlatforms.isEmpty()) {
            stationIdToPlatforms.remove(clearStationIdToPlatforms.remove(0));
        }
        if (!clearPlatformIdToRoutes.isEmpty()) {
            platformIdToRoutes.remove(clearPlatformIdToRoutes.remove(0));
        }
    }

    public Map<BlockPos, List<KSDPlatform>> getPosToPlatforms(TransportMode transportMode) {
        return posToPlatforms.get(transportMode);
    }

    private static <U extends KSDAreaBase, V extends SavedRailBase> Map<Long, V> areaIdToSavedRails(U area, Set<V> savedRails) {
        final Map<Long, V> savedRailMap = new HashMap<>();
        savedRails.forEach(savedRail -> {
            final BlockPos pos = savedRail.getMidPos();
            if (area.isTransportMode(savedRail.transportMode) && area.inArea(pos.getX(), pos.getY(), pos.getZ())) {
                savedRailMap.put(savedRail.id, savedRail);
            }
        });
        return savedRailMap;
    }

    private static <U extends SavedRailBase> void mapPosToSavedRails(Map<BlockPos, List<U>> posToSavedRails, Set<U> savedRails, TransportMode transportMode) {
        posToSavedRails.clear();
        savedRails.forEach(savedRail -> {
            if (savedRail.isTransportMode(transportMode)) {
                final BlockPos pos = savedRail.getMidPos(false);
                if (!posToSavedRails.containsKey(pos)) {
                    posToSavedRails.put(pos, new ArrayList<>());
                }
                posToSavedRails.get(pos).add(savedRail);
            }
        });
    }

    public static class DynamicResource {

        public final int width;
        public final int height;
        public final ResourceLocation resourceLocation;

        private DynamicResource(ResourceLocation resourceLocation, DynamicTexture dynamicTexture) {
            this.resourceLocation = resourceLocation;
            if (dynamicTexture != null) {
                NativeImage nativeImage = dynamicTexture.getPixels();
                if (nativeImage != null) {
                    this.width = nativeImage.getWidth();
                    this.height = nativeImage.getHeight();
                } else {
                    this.width = 16;
                    this.height = 16;
                }
            } else {
                this.width = 16;
                this.height = 16;
            }

        }

        private void remove() {
            if (!this.resourceLocation.equals(DEFAULT_BLACK_RESOURCE) && !this.resourceLocation.equals(DEFAULT_WHITE_RESOURCE) && !this.resourceLocation.equals(DEFAULT_TRANSPARENT_RESOURCE)) {
                TextureManager textureManager = Minecraft.getInstance().getTextureManager();
                textureManager.release(this.resourceLocation);
                AbstractTexture abstractTexture = textureManager.getTexture(this.resourceLocation);
                if (abstractTexture != null) {
                    abstractTexture.releaseId();
                    abstractTexture.close();
                }
            }

        }
    }

    private enum DefaultRenderingColor {

        BLACK(DEFAULT_BLACK_RESOURCE),
        WHITE(DEFAULT_WHITE_RESOURCE),
        TRANSPARENT(DEFAULT_TRANSPARENT_RESOURCE);

        private final DynamicResource dynamicResource;

        DefaultRenderingColor(ResourceLocation resourceLocation) {
            dynamicResource = new DynamicResource(resourceLocation, null);
        }
    }
}
