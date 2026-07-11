package net.hulan.ivr.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.client.*;
import mtr.data.*;
import mtr.mappings.Utilities;
import net.hulan.ksd.client.KSDClientCache;
import net.hulan.ksd.client.KSDClientData;
import net.hulan.ksd.data.KSDPlatform;
import net.hulan.ksd.data.KSDRoute;
import net.hulan.ksd.data.KSDStation;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

public class IVRRouteMapGenerator implements IGui {

    private static int scale;
    private static int lineSize;
    private static int lineSpacing;
    private static int fontSizeBig;
    private static int fontSizeSmall;
    private static final float lightFactor = 0.5f;
    public static final int PIXEL_SCALE = 4;
    private static final int MIN_VERTICAL_SIZE = 5;

    public static void setConstants() {
        scale = (int)Math.pow(2.0D, Config.dynamicTextureResolution() + 5);
        lineSize = (int) (scale / 6.75);
        lineSpacing = lineSize * 3 / 2;
        fontSizeBig = lineSize * 2;
        fontSizeSmall = fontSizeBig / 2;
    }

    public static NativeImage generatePixelatedText(String text, int textColor, int maxWidth, float cjkSizeRatio, boolean fullPixel) {
        try {
            int scale = fullPixel ? 1 : 4;
            int newMaxWidth = maxWidth / scale;
            int[] dimensions = new int[2];
            byte[] pixels = KSDClientData.DATA_CACHE.getTextPixels(text, dimensions, newMaxWidth, 2147483647, Math.round(24.0F * (cjkSizeRatio > 0.0F ? cjkSizeRatio + 1.0F : 1.0F)), Math.round(24.0F * (cjkSizeRatio < 0.0F ? 1.0F - cjkSizeRatio : 1.0F)), 0, HorizontalAlignment.CENTER);
            int width = Math.min(newMaxWidth, dimensions[0]) * scale;
            int height = dimensions[1] * scale;
            NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
            nativeImage.fillRect(0, 0, width, height, 0);
            drawStringPixelated(nativeImage, pixels, dimensions, textColor, fullPixel);
            return nativeImage;
        } catch (Exception var12) {
            var12.printStackTrace();
            return null;
        }
    }

    public static NativeImage generateColorStrip(long platformId) {
        try {
            List<Integer> colors = getRouteStream(platformId, (route, currentStationIndex) -> {
            });
            if (colors.isEmpty()) {
                return null;
            } else {
                NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, 1, colors.size(), false);
                for(int i = 0; i < colors.size(); ++i) {
                    drawPixelSafe(nativeImage, 0, i, -16777216 | colors.get(i));
                }
                return nativeImage;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static NativeImage generateStationName(String stationName, float aspectRatio) {
        if (aspectRatio <= 0.0F) {
            return null;
        } else {
            try {
                int height = scale * 2;
                int width = Math.round((float)height * aspectRatio);
                int[] dimensions = new int[2];
                byte[] pixels = KSDClientData.DATA_CACHE.getTextPixels(IGui.formatVerticalChinese(stationName), dimensions, width, height, fontSizeBig * 2, fontSizeSmall * 2, 0, HorizontalAlignment.CENTER);
                NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
                nativeImage.fillRect(0, 0, width, height, 0);
                drawString(nativeImage, pixels, width / 2, height / 2, dimensions, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, -1, false);
                return nativeImage;
            } catch (Exception var8) {
                var8.printStackTrace();
                return null;
            }
        }
    }

    public static NativeImage generateStationNameEntrance(int textColor, String stationName, float aspectRatio) {
        if (aspectRatio <= 0.0F) {
            return null;
        } else {
            try {
                int size = scale * 2;
                int width = Math.round((float)size * aspectRatio);
                int padding = scale / 16;
                int[] dimensions = new int[2];
                byte[] pixels = KSDClientData.DATA_CACHE.getTextPixels(stationName, dimensions, width - size - padding, size - padding * 2, fontSizeBig * 3, fontSizeSmall * 3, padding, HorizontalAlignment.LEFT);
                int xOffset = (width - dimensions[0] - size) / 2;
                int fakeBackgroundColor = textColor == -16777216 ? textColor + 65793 : 0;
                NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, size, false);
                nativeImage.fillRect(0, 0, width, size, fakeBackgroundColor);
                drawResource(nativeImage, "textures/block/sign/ivr_logo.png", xOffset, 0, size, size, false, 0.0F, 1.0F, 0.0F, 1.0F, 0, true);
                drawString(nativeImage, pixels, size + xOffset, size / 2, dimensions, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, fakeBackgroundColor, textColor, false);
                clearColor(nativeImage, invertColor(fakeBackgroundColor));
                return nativeImage;
            } catch (Exception var11) {
                var11.printStackTrace();
                return null;
            }
        }
    }

    public static NativeImage generateSingleRowStationName(long platformId, float aspectRatio) {
        if (aspectRatio <= 0.0F) {
            return null;
        } else {
            try {
                int[] dimensions = new int[2];
                byte[] pixels = KSDClientData.DATA_CACHE.getTextPixels(getStationName(platformId).replace("|", " | "), dimensions, fontSizeBig, fontSizeSmall);
                int padding = dimensions[1] / 2;
                int height = dimensions[1] + padding;
                int width = Math.max(Math.round((float)height * aspectRatio), dimensions[0] + padding);
                NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
                nativeImage.fillRect(0, 0, width, height, -1);
                drawString(nativeImage, pixels, width / 2, height / 2, dimensions, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, -16777216, false);
                return nativeImage;
            } catch (Exception var9) {
                var9.printStackTrace();
                return null;
            }
        }
    }

    public static NativeImage generateSignText(String text, HorizontalAlignment horizontalAlignment, float paddingScale, int backgroundColor, int textColor) {
        try {
            int height = scale;
            int padding = Math.round((float)height * paddingScale);
            int tileSize = height - padding * 2;
            int tilePadding = tileSize / 4;
            int[] dimensions = new int[2];
            byte[] pixels = KSDClientData.DATA_CACHE.getTextPixels(text, dimensions, 2147483647, (int)((float)tileSize * 1.25F), tileSize * 3 / 5, tileSize * 3 / 10, tilePadding, horizontalAlignment);
            int width = dimensions[0] - tilePadding * 2;
            if (width > 0 && height > 0) {
                NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
                nativeImage.fillRect(0, 0, width, height, 0);
                drawString(nativeImage, pixels, width / 2, height / 2, dimensions, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, backgroundColor, textColor, false);
                clearColor(nativeImage, invertColor(backgroundColor));
                return nativeImage;
            } else {
                return null;
            }
        } catch (Exception var13) {
            var13.printStackTrace();
            return null;
        }
    }

    public static NativeImage generateLiftPanel(String text, int textColor) {
        try {
            int width = Math.round((float)scale * 1.5F);
            int height = fontSizeSmall * 2 * text.split("\\|").length;
            int[] dimensions = new int[2];
            byte[] pixels = KSDClientData.DATA_CACHE.getTextPixels(text.toUpperCase(Locale.ENGLISH), dimensions, width, height, fontSizeSmall * 2, fontSizeSmall * 2, 0, HorizontalAlignment.CENTER);
            NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
            nativeImage.fillRect(0, 0, width, height, 0);
            drawString(nativeImage, pixels, width / 2, height / 2, dimensions, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, -16777216, textColor, false);
            clearColor(nativeImage, invertColor(-16777216));
            return nativeImage;
        } catch (Exception var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public static NativeImage generateExitSignLetter(String exitLetter, String exitNumber, int backgroundColor) {
        try {
            int size = scale / 2;
            boolean noNumber = exitNumber.isEmpty();
            int textSize = size * 7 / 8;
            int[] dimensions1 = new int[2];
            byte[] pixels1 = KSDClientData.DATA_CACHE.getTextPixels(exitLetter, dimensions1, noNumber ? textSize : textSize * 2 / 3, textSize, textSize, size, size, HorizontalAlignment.CENTER);
            int[] dimensions2 = new int[2];
            byte[] pixels2 = noNumber ? null : KSDClientData.DATA_CACHE.getTextPixels(exitNumber, dimensions2, textSize / 3, textSize, textSize / 2, textSize / 2, size, HorizontalAlignment.CENTER);
            NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, size, size, false);
            nativeImage.fillRect(0, 0, size, size, backgroundColor);
            drawResource(nativeImage, "textures/block/classical/sign/exit_letter_blank.png", 0, 0, size, size, false, 0.0F, 1.0F, 0.0F, 1.0F, 0, true);
            drawString(nativeImage, pixels1, size / 2 - (noNumber ? 0 : textSize / 6 - size / 32), size / 2, dimensions1, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, -1, false);
            if (!noNumber) {
                drawString(nativeImage, pixels2, size / 2 + textSize / 3 - size / 32, size / 2 + textSize / 8, dimensions2, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, -1, false);
            }
            return nativeImage;
        } catch (Exception var11) {
            var11.printStackTrace();
            return null;
        }
    }

    public static NativeImage generateRouteSquare(int color, String routeName, HorizontalAlignment horizontalAlignment) {
        try {
            int padding = scale / 32;
            int[] dimensions = new int[2];
            byte[] pixels = KSDClientData.DATA_CACHE.getTextPixels(routeName, dimensions, 2147483647, (int)((float)(fontSizeBig + fontSizeSmall) * 1.25F), fontSizeBig, fontSizeSmall, padding, horizontalAlignment);
            int width = dimensions[0] + padding * 2;
            int height = dimensions[1] + padding * 2;
            NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
            nativeImage.fillRect(0, 0, width, height, invertColor(-16777216 | color));
            drawString(nativeImage, pixels, width / 2, height / 2, dimensions, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, -1, false);
            return nativeImage;
        } catch (Exception var9) {
            var9.printStackTrace();
            return null;
        }
    }

    public static NativeImage generateDirectionArrow(long platformId,
                                                     boolean hasLeft,
                                                     boolean hasRight,
                                                     HorizontalAlignment horizontalAlignment,
                                                     boolean showToString,
                                                     float paddingScale,
                                                     float aspectRatio,
                                                     int backgroundColor,
                                                     int textColor,
                                                     int transparentColor) {
        if (aspectRatio <= 0.0F) {
            return null;
        } else {
            try {
                List<String> destinations = new ArrayList<>();
                List<Integer> colors = getRouteStream(platformId, (route, currentStationIndex) -> destinations.add(KSDClientData.DATA_CACHE.getFormattedRouteDestination(route, currentStationIndex, "temp_circular_marker")));
                boolean isTerminating = destinations.isEmpty();
                boolean leftToRight = horizontalAlignment == HorizontalAlignment.CENTER ? hasLeft || !hasRight : horizontalAlignment != HorizontalAlignment.RIGHT;
                int height = scale;
                int width = Math.round((float)height * aspectRatio);
                int padding = Math.round((float)height * paddingScale);
                int tileSize = height - padding * 2;
                if (width > 0 && height > 0) {
                    KSDClientCache clientCache = KSDClientData.DATA_CACHE;
                    NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
                    nativeImage.fillRect(0, 0, width, height, invertColor(backgroundColor));
                    int circleX;
                    if (isTerminating) {
                        circleX = (int)horizontalAlignment.getOffset(0.0F, (float)(tileSize - width));
                    } else {
                        String destinationString = IGui.mergeStations(destinations);
                        boolean noToString = destinationString.startsWith("temp_circular_marker");
                        destinationString = destinationString.replace("temp_circular_marker", "");
                        if (!destinationString.isEmpty() && showToString && !noToString) {
                            destinationString = IGui.insertTranslation("gui.mtr.to_cjk", "gui.mtr.to", 1, destinationString);
                        }
                        int tilePadding = tileSize / 4;
                        int leftSize = ((hasLeft ? 1 : 0) + (leftToRight ? 1 : 0)) * (tileSize + tilePadding);
                        int rightSize = ((hasRight ? 1 : 0) + (leftToRight ? 0 : 1)) * (tileSize + tilePadding);
                        int[] dimensionsDestination = new int[2];
                        byte[] pixelsDestination = clientCache.getTextPixels(destinationString, dimensionsDestination, width - leftSize - rightSize - padding * (showToString ? 2 : 1), (int)((float)tileSize * 1.25F), tileSize * 3 / 5, tileSize * 3 / 10, tilePadding, leftToRight ? HorizontalAlignment.LEFT : HorizontalAlignment.RIGHT);
                        int leftPadding = (int)horizontalAlignment.getOffset(0.0F, (float)(leftSize + rightSize + dimensionsDestination[0] - tilePadding * 2 - width));
                        drawString(nativeImage, pixelsDestination, leftPadding + leftSize - tilePadding, height / 2, dimensionsDestination, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, backgroundColor, textColor, false);
                        if (hasLeft) {
                            drawResource(nativeImage, "textures/block/sign/arrow.png", leftPadding, padding, tileSize, tileSize, false, 0.0F, 1.0F, 0.0F, 1.0F, textColor, false);
                        }
                        if (hasRight) {
                            drawResource(nativeImage, "textures/block/sign/arrow.png", leftPadding + leftSize + dimensionsDestination[0] - tilePadding * 2 + rightSize - tileSize, padding, tileSize, tileSize, true, 0.0F, 1.0F, 0.0F, 1.0F, textColor, false);
                        }
                        circleX = leftPadding + leftSize + (leftToRight ? -tileSize - tilePadding : dimensionsDestination[0] - tilePadding);
                    }
                    for(int i = 0; i < colors.size(); ++i) {
                        drawResource(nativeImage, "textures/block/sign/circle.png", circleX, padding, tileSize, tileSize, false, 0.0F, 1.0F, (float)i / (float)colors.size(), ((float)i + 1.0F) / (float)colors.size(), colors.get(i), false);
                    }
                    KSDPlatform platform = KSDClientData.DATA_CACHE.platformIdMap.get(platformId);
                    if (platform != null) {
                        int[] dimensionsPlatformNumber = new int[2];
                        byte[] pixelsPlatformNumber = clientCache.getTextPixels(platform.name, dimensionsPlatformNumber, tileSize, (int)((float)tileSize * 1.25F * 3.0F / 4.0F), tileSize * 3 / 4, tileSize * 3 / 4, 0, HorizontalAlignment.CENTER);
                        drawString(nativeImage, pixelsPlatformNumber, circleX + tileSize / 2, padding + tileSize / 2, dimensionsPlatformNumber, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, -1, false);
                    }
                    if (transparentColor != 0) {
                        clearColor(nativeImage, invertColor(transparentColor));
                    }
                    return nativeImage;
                } else {
                    return null;
                }
            } catch (Exception var30) {
                var30.printStackTrace();
                return null;
            }
        }
    }

    public static NativeImage generateDirectionArrowForRS(long platformId,
                                                          boolean hasLeft,
                                                          boolean hasRight,
                                                          HorizontalAlignment horizontalAlignment,
                                                          float paddingScale,
                                                          float aspectRatio,
                                                          int textColor,
                                                          int transparentColor) {
        if (aspectRatio > 0.0F) {
            try {
                List<String> destinations = new ArrayList<>();
                List<Integer> colors = getRouteStream(platformId, (route, currentStationIndex) -> destinations.add(KSDClientData.DATA_CACHE.getFormattedRouteDestination(route, currentStationIndex, "temp_circular_marker")));
                int routeColor = KSDClientData.DATA_CACHE.requestPlatformIdToRoutes(platformId).get(0).routeColor;
                boolean isTerminating = destinations.isEmpty();
                boolean leftToRight = horizontalAlignment == HorizontalAlignment.CENTER ? hasLeft || !hasRight : horizontalAlignment != HorizontalAlignment.RIGHT;
                int height = scale;
                int width = Math.round((float)height * aspectRatio);
                int padding = Math.round((float)height * paddingScale);
                int tileSize = height - padding * 2;
                if (width > 0 && height > 0) {
                    KSDClientCache clientCache = KSDClientData.DATA_CACHE;
                    NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
                    nativeImage.fillRect(0, 0, width, height, invertColor(routeColor | ARGB_BLACK));
                    int circleX;
                    if (isTerminating) {
                        circleX = (int)horizontalAlignment.getOffset(0.0F, (float)(tileSize - width));
                    } else {
                        int tilePadding = tileSize / 4;
                        int leftSize = ((hasLeft ? 1 : 0) + (leftToRight ? 1 : 0)) * (tileSize + tilePadding);
                        int rightSize = ((hasRight ? 1 : 0) + (leftToRight ? 0 : 1)) * (tileSize + tilePadding);
                        int leftPadding = (int)horizontalAlignment.getOffset(0.0F, (float)(leftSize + rightSize - tilePadding * 2 - width));
                        if (hasLeft) {
                            drawResource(nativeImage, "textures/block/sign/arrow.png", leftPadding, padding, tileSize, tileSize, false, 0.0F, 1.0F, 0.0F, 1.0F, textColor, false);
                        }
                        if (hasRight) {
                            drawResource(nativeImage, "textures/block/sign/arrow.png", leftPadding + leftSize - tilePadding * 2 + rightSize - tileSize, padding, tileSize, tileSize, true, 0.0F, 1.0F, 0.0F, 1.0F, textColor, false);
                        }
                        if (Boolean.compare(hasLeft, hasRight) == 0) {
                            circleX = (int)horizontalAlignment.getOffset(0.0F, (float)(tileSize - width));
                        } else {
                            circleX = leftPadding + leftSize + (leftToRight ? - tileSize - tilePadding : - tilePadding);
                        }
                    }
                    for(int i = 0; i < colors.size(); ++i) {
                        drawResource(nativeImage, "textures/block/sign/circle.png", circleX, padding, tileSize, tileSize, false, 0.0F, 1.0F, (float)i / (float)colors.size(), ((float)i + 1.0F) / (float)colors.size(), colors.get(i), false);
                    }
                    KSDPlatform platform = KSDClientData.DATA_CACHE.platformIdMap.get(platformId);
                    if (platform != null) {
                        int[] dimensionsPlatformNumber = new int[2];
                        byte[] pixelsPlatformNumber = clientCache.getTextPixels(platform.name, dimensionsPlatformNumber, tileSize, (int)((float)tileSize * 1.25F * 3.0F / 4.0F), tileSize * 3 / 4, tileSize * 3 / 4, 0, HorizontalAlignment.CENTER);
                        drawString(nativeImage, pixelsPlatformNumber, circleX + tileSize / 2, padding + tileSize / 2, dimensionsPlatformNumber, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, -1, false);
                    }
                    if (transparentColor != 0) {
                        clearColor(nativeImage, invertColor(transparentColor));
                    }
                    return nativeImage;
                } else {
                    return null;
                }
            } catch (Exception var30) {
                var30.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static NativeImage generateRouteMap(long platformId,
                                               boolean vertical,
                                               boolean flip,
                                               float aspectRatio,
                                               boolean transparentWhite) {
        if (!(aspectRatio <= 0.0F)) {
            try {
                List<Tuple<KSDRoute, Integer>> routeDetails = new ArrayList<>();
                getRouteStream(platformId, (routeX, currentStationIndex) -> routeDetails.add(new Tuple<>(routeX, currentStationIndex)));
                int routeCount = routeDetails.size();
                if (routeCount > 0) {
                    final KSDClientCache clientCache = KSDClientData.DATA_CACHE;
                    final List<List<Long>> stationsIdsBefore = new ArrayList<>();
                    final List<List<Long>> stationsIdsAfter = new ArrayList<>();
                    final List<Map<Integer, StationPosition>> stationPositions = new ArrayList<>();
                    final int[] colorIndices = new int[routeCount];
                    final Set<Integer> currentRouteColors = new HashSet<>();
                    final Set<String> currentRouteNames = new HashSet<>();
                    int colorIndex = -1;
                    int previousColor = -1;
                    for (int routeIndex = 0; routeIndex < routeCount; routeIndex++) {
                        stationsIdsBefore.add(new ArrayList<>());
                        stationsIdsAfter.add(new ArrayList<>());
                        stationPositions.add(new HashMap<>());
                        final Tuple<KSDRoute, Integer> routeDetail = routeDetails.get(routeIndex);
                        final List<Route.RoutePlatform> platformIds = routeDetail.getA().platformIds;
                        final int currentIndex = routeDetail.getB();
                        for (int stationIndex = 0; stationIndex < platformIds.size(); stationIndex++) {
                            if (stationIndex != currentIndex) {
                                final long stationId = getStationId(platformIds.get(stationIndex).platformId);
                                if (stationIndex < currentIndex) {
                                    stationsIdsBefore.get(stationsIdsBefore.size() - 1).add(0, stationId);
                                } else {
                                    stationsIdsAfter.get(stationsIdsAfter.size() - 1).add(stationId);
                                }
                            }
                        }
                        final int color = routeDetail.getA().color;
                        if (color != previousColor) {
                            colorIndex++;
                            previousColor = color;
                        }
                        colorIndices[routeIndex] = colorIndex;
                        currentRouteColors.add(color);
                        currentRouteNames.add(routeDetail.getA().name.split("\\|\\|")[0]);
                    }
                    for (int routeIndex = 0; routeIndex < routeCount; routeIndex++) {
                        stationPositions.get(routeIndex).put(0, new StationPosition(0, getLineOffset(routeIndex, colorIndices), true));
                    }
                    final float[] bounds = new float[3];
                    setup(stationPositions, flip ? stationsIdsBefore : stationsIdsAfter, colorIndices, bounds, flip, true);
                    final float xOffset = bounds[0] + 0.5F;
                    setup(stationPositions, flip ? stationsIdsAfter : stationsIdsBefore, colorIndices, bounds, !flip, false);
                    final float rawHeightPart = Math.abs(bounds[1]) + (vertical ? 0.6F : 1);
                    final float rawWidth = xOffset + bounds[0] + 0.5F;
                    final float rawHeightTotal = rawHeightPart + bounds[2] + (vertical ? 0.6F : 1);
                    final float rawHeight;
                    final float yOffset;
                    final float extraPadding;
                    if (vertical && rawHeightTotal < MIN_VERTICAL_SIZE) {
                        rawHeight = MIN_VERTICAL_SIZE;
                        extraPadding = (MIN_VERTICAL_SIZE - rawHeightTotal) / 2;
                        yOffset = rawHeightPart + extraPadding;
                    } else {
                        rawHeight = rawHeightTotal;
                        extraPadding = 0;
                        yOffset = rawHeightPart;
                    }
                    final int height;
                    final int width;
                    final float widthScale;
                    final float heightScale;
                    if (rawWidth / rawHeight > aspectRatio) {
                        width = Math.round(rawWidth * scale);
                        height = Math.round(width / aspectRatio);
                        widthScale = 1;
                        heightScale = height / rawHeight / scale;
                    } else {
                        height = Math.round(rawHeight * scale);
                        width = Math.round(height * aspectRatio);
                        heightScale = 1;
                        widthScale = width / rawWidth / scale;
                    }
                    if (width > 0 && height > 0) {
                        final NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
                        nativeImage.fillRect(0, 0, width, height, ARGB_WHITE);
                        final Map<Long, Set<StationPositionGrouped>> stationPositionsGrouped = new HashMap<>();
                        for (int routeIndex = 0; routeIndex < routeCount; routeIndex++) {
                            final KSDRoute route = routeDetails.get(routeIndex).getA();
                            final int currentIndex = routeDetails.get(routeIndex).getB();
                            final Map<Integer, StationPosition> routeStationPositions = stationPositions.get(routeIndex);
                            for (int stationIndex = 0; stationIndex < route.platformIds.size(); stationIndex++) {
                                final StationPosition stationPosition = routeStationPositions.get(stationIndex - currentIndex);
                                if (stationIndex < route.platformIds.size() - 1) {
                                    drawLine(nativeImage, stationPosition, routeStationPositions.get(stationIndex + 1 - currentIndex), widthScale, heightScale, xOffset, yOffset, stationIndex < currentIndex ? lightenColor(ARGB_BLACK | route.color) : ARGB_BLACK | route.color);
                                }
                                final long stationId = getStationId(route.platformIds.get(stationIndex).platformId);
                                if (!stationPositionsGrouped.containsKey(stationId)) {
                                    stationPositionsGrouped.put(stationId, new HashSet<>());
                                }
                                if (!stationPosition.isCommon || stationPositionsGrouped.get(stationId).stream().noneMatch(stationPosition2 -> stationPosition2.stationPosition.x == stationPosition.x)) {
                                    final Map<Integer, ClientCache.ColorNameTuple> interchangeRoutes = getInterchangeRoutes(stationId);
                                    final List<Integer> allColors = new ArrayList<>(interchangeRoutes.keySet());
                                    allColors.sort(Integer::compareTo);
                                    final List<Integer> interchangeColors = new ArrayList<>();
                                    final List<String> interchangeNames = new ArrayList<>();
                                    allColors.forEach(color -> {
                                        final String name = interchangeRoutes.get(color).name;
                                        if (!currentRouteColors.contains(color) && !currentRouteNames.contains(name)) {
                                            if (!interchangeColors.contains(color)) {
                                                interchangeColors.add(color);
                                            }
                                            if (!interchangeNames.contains(name)) {
                                                interchangeNames.add(name);
                                            }
                                        }
                                    });
                                    stationPositionsGrouped.get(stationId).add(new StationPositionGrouped(stationPosition, stationIndex - currentIndex, interchangeColors, interchangeNames, routeIndex, stationIndex));
                                }
                            }
                        }
                        final int maxStringWidth = (int) (scale * 0.9 * ((vertical ? heightScale : widthScale) / 2 + extraPadding / routeCount));
                        stationPositionsGrouped.forEach((stationId, stationPositionGroupedSet) -> stationPositionGroupedSet.forEach((stationPositionGrouped) -> {
                            try {
                                final int x = Math.round((stationPositionGrouped.stationPosition.x + xOffset) * scale * widthScale);
                                final int y = Math.round((stationPositionGrouped.stationPosition.y + yOffset) * scale * heightScale);
                                final int lines = stationPositionGrouped.stationPosition.isCommon ? colorIndices[colorIndices.length - 1] : 0;
                                final boolean textBelow = vertical || (stationPositionGrouped.stationPosition.isCommon ? Math.abs(stationPositionGrouped.stationOffset) % 2 == 0 : y >= yOffset * scale);
                                final boolean currentStation = stationPositionGrouped.stationOffset == 0;
                                final boolean passed = stationPositionGrouped.stationOffset < 0;
                                final List<Integer> interchangeColors = stationPositionGrouped.interchangeColors;
                                int routeIdx = stationPositionGrouped.routeIndex();
                                KSDRoute route = routeDetails.get(routeIdx).getA();
                                if (!interchangeColors.isEmpty() && !currentStation && !passed) {
                                    int lineHeight = lineSize * 2;
                                    int lineWidth = (int) Math.ceil((float) lineSize / 4 / (float) interchangeColors.size());
                                    int iconX, iconY;
                                    if (vertical) {
                                        iconX = x - 76 - lineSize / 2;
                                        iconY = y - 108 / 2;
                                    } else {
                                        iconX = x - 76 / 2;
                                        iconY = y - 108 - lineSize / 2 - (textBelow ? 0 : lines * lineSpacing);
                                    }
                                    for (int i = 0; i < interchangeColors.size(); ++i) {
                                        for (int drawX = 0; drawX < lineWidth; ++drawX) {
                                            for (int drawY = 0; drawY < lineHeight; ++drawY) {
                                                drawPixelSafe(
                                                        nativeImage,
                                                        x + drawX + lineWidth * i - lineWidth * interchangeColors.size() / 2,
                                                        y + (textBelow ? -1 : lines * lineSpacing) + (textBelow ? -drawY : drawY),
                                                        -16777216 | interchangeColors.get(i));
                                                drawResource(
                                                        nativeImage,
                                                        "textures/block/sign/train.png",
                                                        x + drawX + 76 * i - 76 * interchangeColors.size() / 2,
                                                        y + (textBelow ? -1 : lines * lineSpacing) + (textBelow ? -drawY : drawY) + lineHeight,
                                                        76,
                                                        108,
                                                        true,
                                                        (float) i / (float) interchangeColors.size(),
                                                        ((float) i + 1.0F) / (float) interchangeColors.size(),
                                                        0.0F,
                                                        1.0F,
                                                        -16777216 | interchangeColors.get(i),
                                                        false);
                                            }
                                        }
                                    }
                                    int[] dimensionsX = new int[2];
                                    byte[] pixelsX = clientCache.getTextPixels(IGui.mergeStations(stationPositionGrouped.interchangeNames), dimensionsX, maxStringWidth - (vertical ? lineHeight : 0), (int) ((float) (fontSizeBig + fontSizeSmall) * 1.25F / 2.0F), fontSizeBig / 2, fontSizeSmall / 2, 0, vertical ? HorizontalAlignment.LEFT : HorizontalAlignment.CENTER);
                                    drawString(nativeImage, pixelsX, x, y + (textBelow ? -1 - lineHeight : lines * lineSpacing + lineHeight), dimensionsX, HorizontalAlignment.CENTER, textBelow ? VerticalAlignment.BOTTOM : VerticalAlignment.TOP, 0, -16777216, vertical);
                                }
                                drawStation(nativeImage, x, y, heightScale, lines, getLineColor(currentStation, passed, route.color), currentStation ? 1.25F : 1.0F);
                                if (currentStation) {
                                    int stationIdx = stationPositionGrouped.stationIndex();
                                    Tuple<KSDRoute, Integer> routeDetail = routeDetails.get(routeIdx);
                                    int currentIndex = routeDetail.getB();
                                    Map<Integer, StationPosition> routePositions = stationPositions.get(routeIdx);
                                    StationPosition nextPos = routePositions.get(stationIdx + 1 - currentIndex);
                                    if (nextPos != null) {
                                        float curX = stationPositionGrouped.stationPosition().x;
                                        float nextX = nextPos.x;
                                        int arrowSize = lineSize;
                                        int drawX = x - arrowSize / 2;
                                        int drawY = y - arrowSize / 2;
                                        drawResource(
                                                nativeImage,
                                                "textures/block/sign/arrow.png",
                                                drawX,
                                                drawY,
                                                arrowSize,
                                                arrowSize,
                                                nextX > curX,
                                                0.0F,
                                                1.0F,
                                                0.0F,
                                                1.0F,
                                                ARGB_BLACK | route.color,
                                                false);
                                    }
                                }
                                if (!passed) {
                                    KSDStation station = KSDClientData.DATA_CACHE.stationIdMap.get(stationId);
                                    int[] dimensions = new int[2];
                                    byte[] pixels = clientCache.getTextPixels(
                                            station == null ? "" : station.name,
                                            dimensions,
                                            maxStringWidth,
                                            (int) ((float) (fontSizeBig + fontSizeSmall) * 1.25F),
                                            fontSizeBig,
                                            fontSizeSmall,
                                            fontSizeSmall / 4,
                                            vertical ? HorizontalAlignment.RIGHT : HorizontalAlignment.CENTER);
                                    drawString(
                                            nativeImage,
                                            pixels,
                                            x,
                                            y + (textBelow ? lines * lineSpacing : -1) + (textBelow ? 1 : -1) * lineSize * 5 / 4,
                                            dimensions,
                                            HorizontalAlignment.CENTER,
                                            textBelow ? VerticalAlignment.TOP : VerticalAlignment.BOTTOM,
                                            0,
                                            currentStation ? ARGB_BLACK : -5592406,
                                            vertical);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }));
                        if (transparentWhite) {
                            clearColor(nativeImage, -1);
                        }
                        return nativeImage;
                    }
                    return null;
                }
            } catch (Exception var43) {
                var43.printStackTrace();
            }
        }
        return null;
    }

    private static int darkenColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        r = (int) (r * lightFactor);
        g = (int) (g * lightFactor);
        b = (int) (b * lightFactor);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static int lightenColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        r = (int) (r + (255 - r) * lightFactor);
        g = (int) (g + (255 - g) * lightFactor);
        b = (int) (b + (255 - b) * lightFactor);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static int getLineColor(boolean current, boolean passed, int color) {
        if (current) {
            return darkenColor(color);
        } else if (passed) {
            return lightenColor(color);
        } else {
            return  -16777216 | color;
        }
    }

    public static void scrollTextLightRail(PoseStack matrices, VertexConsumer vertexConsumer, int rows, float availableWidth, float availableHeight, int imageWidth, int imageHeight) {
        float scale = availableHeight / (float)imageHeight * (float)rows;
        int totalTime = 3000 + (int)Math.floor(availableWidth / scale) * 8;
        int totalStep = (int)(System.currentTimeMillis() % ((long) totalTime * rows));
        int step = totalStep % totalTime;
        int row = totalStep / totalTime;
        float xOffset = (availableWidth - (float)imageWidth * scale) / 2.0F;
        float x = xOffset - (float)Math.max(0, step - 3000) * scale / 8.0F;
        IDrawing.drawTexture(matrices, vertexConsumer, Math.max(x, 0.0F), 0.0F, (float)imageWidth * scale + Math.min(x, 0.0F), availableHeight, Math.max(-x, 0.0F) / (float)imageWidth / scale, (float)row / (float)rows, 1.0F, (float)(row + 1) / (float)rows, Direction.UP, -1, 15728880);
    }

    private static void setup(List<Map<Integer, StationPosition>> stationPositions, List<List<Long>> stationsIdLists, int[] colorIndices, float[] bounds, boolean passed, boolean reverse) {
        int passedMultiplier = passed ? -1 : 1;
        int reverseMultiplier = reverse ? -1 : 1;
        bounds[0] = 0.0F;
        List<Long> commonStationIds = new ArrayList<>();
        stationsIdLists.get(0).forEach((stationId) -> {
            if (stationId != 0L && !commonStationIds.contains(stationId) && stationsIdLists.stream().allMatch((stationsIds) -> stationsIds.contains(stationId))) {
                commonStationIds.add(stationId);
            }
        });
        int positionXOffset = 0;
        int routeCount = stationsIdLists.size();
        int[] traverseIndex = new int[routeCount];
        for(int commonStationIndex = 0; commonStationIndex <= commonStationIds.size(); ++commonStationIndex) {
            boolean lastStation = commonStationIndex == commonStationIds.size();
            long commonStationId = lastStation ? -1L : commonStationIds.get(commonStationIndex);
            int intermediateSegmentsMaxCount = 0;
            int[] intermediateSegmentsCounts = new int[routeCount];
            for(int routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
                intermediateSegmentsCounts[routeIndex] = (lastStation ? stationsIdLists.get(routeIndex).size() : stationsIdLists.get(routeIndex).indexOf(commonStationId) + 1) - traverseIndex[routeIndex];
                intermediateSegmentsMaxCount = Math.max(intermediateSegmentsMaxCount, intermediateSegmentsCounts[routeIndex]);
            }
            List<Integer> routesIndicesInSection = new ArrayList<>();
            int routeIndex;
            for(routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
                if (!lastStation || intermediateSegmentsCounts[routeIndex] > 0) {
                    routesIndicesInSection.add(routeIndex);
                }
            }
            float stationY;
            for(routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
                if (intermediateSegmentsCounts[routeIndex] > 0) {
                    stationY = (float)intermediateSegmentsMaxCount / (float)intermediateSegmentsCounts[routeIndex];
                    for(int j = 0; j < intermediateSegmentsCounts[routeIndex] - (lastStation ? 0 : 1); ++j) {
                        float stationX = (float)positionXOffset + stationY * (float)(j + 1);
                        bounds[0] = Math.max(bounds[0], stationX / 2.0F);
                        float stationY1 = (float)routesIndicesInSection.indexOf(routeIndex) - (float)(routesIndicesInSection.size() - 1) / 2.0F + getLineOffset(routeIndex, colorIndices);
                        bounds[1] = Math.min(bounds[1], stationY1);
                        bounds[2] = Math.max(bounds[2], stationY1);
                        stationPositions.get(routeIndex).put(passedMultiplier * (j + traverseIndex[routeIndex] + 1), new StationPosition((float)reverseMultiplier * stationX / 2.0F, stationY1, false));
                    }
                    traverseIndex[routeIndex] += intermediateSegmentsCounts[routeIndex];
                }
            }
            if (!lastStation) {
                positionXOffset += intermediateSegmentsMaxCount;
                for(routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
                    stationY = getLineOffset(routeIndex, colorIndices);
                    bounds[1] = Math.min(bounds[1], stationY);
                    bounds[2] = Math.max(bounds[2], stationY);
                    stationPositions.get(routeIndex).put(passedMultiplier * traverseIndex[routeIndex], new StationPosition((float)(reverseMultiplier * positionXOffset) / 2.0F, stationY, true));
                }
                bounds[0] = (float)positionXOffset / 2.0F;
            }
        }

    }

    private static float getLineOffset(int routeIndex, int[] colorIndices) {
        return (float)lineSpacing / (float)scale * ((float)colorIndices[routeIndex] - (float)colorIndices[colorIndices.length - 1] / 2.0F);
    }

    private static List<Integer> getRouteStream(long platformId, BiConsumer<KSDRoute, Integer> nonTerminatingCallback) {
        List<Integer> colors = new ArrayList<>();
        List<Integer> terminatingColors = new ArrayList<>();
        KSDClientData.ROUTES.stream().filter((route) -> route.containsPlatformId(platformId) && !route.isHidden).sorted((a, b) -> a.color == b.color ? a.compareTo(b) : a.color - b.color).forEach((route) -> {
            int currentStationIndex = route.getPlatformIdIndex(platformId);
            if (currentStationIndex < route.platformIds.size() - 1) {
                nonTerminatingCallback.accept(route, currentStationIndex);
                if (!colors.contains(route.color)) {
                    colors.add(route.color);
                }
            } else if (!terminatingColors.contains(route.color)) {
                terminatingColors.add(route.color);
            }
        });
        if (colors.isEmpty()) {
            colors.addAll(terminatingColors);
        }
        return colors;
    }

    private static long getStationId(long platformId) {
        KSDStation station = KSDClientData.DATA_CACHE.platformIdToStation.get(platformId);
        return station == null ? -1L : station.id;
    }

    private static String getStationName(long platformId) {
        KSDStation station = KSDClientData.DATA_CACHE.platformIdToStation.get(platformId);
        return station == null ? "" : station.name;
    }

    private static Map<Integer, ClientCache.ColorNameTuple> getInterchangeRoutes(long stationId) {
        Map<Integer, ClientCache.ColorNameTuple> interChangeRoutes = KSDClientData.DATA_CACHE.stationIdToRoutes.get(stationId);
        return interChangeRoutes == null ? new HashMap<>() : interChangeRoutes;
    }

    private static void drawLine(NativeImage nativeImage, StationPosition stationPosition1, StationPosition stationPosition2, float widthScale, float heightScale, float xOffset, float yOffset, int color) {
        int x1 = Math.round((stationPosition1.x + xOffset) * (float)scale * widthScale);
        int x2 = Math.round((stationPosition2.x + xOffset) * (float)scale * widthScale);
        int y1 = Math.round((stationPosition1.y + yOffset) * (float)scale * heightScale);
        int y2 = Math.round((stationPosition2.y + yOffset) * (float)scale * heightScale);
        int xChange = x2 - x1;
        int yChange = y2 - y1;
        int xChangeAbs = Math.abs(xChange);
        int yChangeAbs = Math.abs(yChange);
        int changeDifference = Math.abs(yChangeAbs - xChangeAbs);
        if (xChangeAbs > yChangeAbs) {
            boolean y1OffsetGreater = Math.abs((float)y1 - yOffset * (float)scale) > Math.abs((float)y2 - yOffset * (float)scale);
            drawLine(nativeImage, x1, y1, x2 - x1, y1OffsetGreater ? 0 : y2 - y1, y1OffsetGreater ? changeDifference : yChangeAbs, color);
            drawLine(nativeImage, x2, y2, x1 - x2, y1OffsetGreater ? y1 - y2 : 0, y1OffsetGreater ? yChangeAbs : changeDifference, color);
        } else {
            int halfXChangeAbs = xChangeAbs / 2;
            drawLine(nativeImage, x1, y1, x2 - x1, y2 - y1, halfXChangeAbs, color);
            drawLine(nativeImage, x2, y2, x1 - x2, y1 - y2, halfXChangeAbs, color);
            drawLine(nativeImage, (x1 + x2) / 2, y1 + (int)Math.copySign((float)halfXChangeAbs, (float)(y2 - y1)), 0, y2 - y1, changeDifference, color);
        }

    }

    private static void drawLine(NativeImage nativeImage, int x, int y, int directionX, int directionY, int length, int color) {
        int halfLineHeight = lineSize / 2;
        int xWidth = directionX == 0 ? halfLineHeight : 0;
        int yWidth = directionX == 0 ? 0 : (directionY == 0 ? halfLineHeight : Math.round((float)lineSize * Mth.SQRT_OF_TWO / 2.0F));
        int yMin = y - halfLineHeight - (directionY < 0 ? length : 0) + 1;
        int yMax = y + halfLineHeight + (directionY > 0 ? length : 0) - 1;
        int drawOffset = directionX != 0 && directionY != 0 ? halfLineHeight : 0;
        for(int i = -drawOffset; i < Math.abs(length) + drawOffset; ++i) {
            int drawX = x + (directionX == 0 ? 0 : (int)Math.copySign((float)i, (float)directionX)) + (directionX < 0 ? -1 : 0);
            int drawY = y + (directionY == 0 ? 0 : (int)Math.copySign((float)i, (float)directionY)) + (directionY < 0 ? -1 : 0);
            int yOffset;
            for(yOffset = 0; yOffset < xWidth; ++yOffset) {
                drawPixelSafe(nativeImage, drawX - yOffset - 1, drawY, color);
                drawPixelSafe(nativeImage, drawX + yOffset, drawY, color);
            }
            for(yOffset = 0; yOffset < yWidth; ++yOffset) {
                drawPixelSafe(nativeImage, drawX, Math.max(drawY - yOffset, yMin) - 1, color);
                drawPixelSafe(nativeImage, drawX, Math.min(drawY + yOffset, yMax), color);
            }
        }
    }

    private static void drawStation(NativeImage nativeImage, int x, int y, float heightScale, int lines, int borderColor, float multiplier) {
        int radius = Math.round(lineSize * multiplier);
        for(int offsetX = -radius; offsetX < radius; ++offsetX) {
            for(int offsetY = -radius; offsetY < radius; ++offsetY) {
                int extraOffsetY = offsetY > 0 ? (int)((float)(lines * lineSpacing) * heightScale) : 0;
                int repeatDraw = offsetY == 0 ? (int)((float)(lines * lineSpacing) * heightScale) : 0;
                double squareSum = ((double)offsetX + 0.5D) * ((double)offsetX + 0.5D) + ((double)offsetY + 0.5D) * ((double)offsetY + 0.5D);
                int i;
                if (squareSum <= 0.5D * (double) radius * (double) radius) {
                    for(i = 0; i <= repeatDraw; ++i) {
                        drawPixelSafe(nativeImage, x + offsetX, y + offsetY + extraOffsetY + i, -1);
                    }
                } else if (squareSum <= (double) (radius * radius)) {
                    for(i = 0; i <= repeatDraw; ++i) {
                        drawPixelSafe(nativeImage, x + offsetX, y + offsetY + extraOffsetY + i, borderColor);
                    }
                }
            }
        }
    }

    private static void drawString(NativeImage nativeImage, byte[] pixels, int x, int y, int[] textDimensions, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, int backgroundColor, int textColor, boolean rotate90) {
        int drawX;
        int drawY;
        if ((backgroundColor >> 24 & 255) > 0) {
            for(drawX = 0; drawX < textDimensions[rotate90 ? 1 : 0]; ++drawX) {
                for(drawY = 0; drawY < textDimensions[rotate90 ? 0 : 1]; ++drawY) {
                    drawPixelSafe(nativeImage, (int)horizontalAlignment.getOffset((float)(drawX + x), (float)textDimensions[rotate90 ? 1 : 0]), (int)verticalAlignment.getOffset((float)(drawY + y), (float)textDimensions[rotate90 ? 0 : 1]), backgroundColor);
                }
            }
        }
        drawX = 0;
        drawY = rotate90 ? textDimensions[0] - 1 : 0;
        for(int i = 0; i < textDimensions[0] * textDimensions[1]; ++i) {
            blendPixel(nativeImage, (int)horizontalAlignment.getOffset((float)(x + drawX), (float)textDimensions[rotate90 ? 1 : 0]), (int)verticalAlignment.getOffset((float)(y + drawY), (float)textDimensions[rotate90 ? 0 : 1]), ((pixels[i] & 255) << 24) + (textColor & 16777215));
            if (rotate90) {
                --drawY;
                if (drawY < 0) {
                    drawY = textDimensions[0] - 1;
                    ++drawX;
                }
            } else {
                ++drawX;
                if (drawX == textDimensions[0]) {
                    drawX = 0;
                    ++drawY;
                }
            }
        }
    }

    private static void drawStringPixelated(NativeImage nativeImage, byte[] pixels, int[] textDimensions, int textColor, boolean fullPixel) {
        int yOffset = (textDimensions[1] * (fullPixel ? 1 : 4) - nativeImage.getHeight()) / 2;
        int drawX = 0;
        int drawY = 0;
        for(int i = 0; i < textDimensions[0] * textDimensions[1]; ++i) {
            if ((pixels[i] & 255) > 127) {
                if (fullPixel) {
                    drawPixelSafe(nativeImage, drawX, drawY - yOffset, textColor);
                } else {
                    for(int j = 0; j < 3; ++j) {
                        for(int k = 0; k < 3; ++k) {
                            drawPixelSafe(nativeImage, drawX * 4 + j, drawY * 4 + k - yOffset, textColor);
                        }
                    }
                }
            }
            ++drawX;
            if (drawX == textDimensions[0]) {
                drawX = 0;
                ++drawY;
            }
        }
    }

    private static void drawResource(NativeImage nativeImage, String resource, int x, int y, int width, int height, boolean flipX, float x1, float x2, float y1, float y2, int color, boolean useActualColor) throws IOException {
        NativeImage nativeImageResource = NativeImage.read(NativeImage.Format.RGBA, Utilities.getInputStream(Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation("ivr", resource))));
        int resourceWidth = nativeImageResource.getWidth();
        int resourceHeight = nativeImageResource.getHeight();
        for(int drawX = Math.round(x1 * (float)width); drawX < Math.round(x2 * (float)width); ++drawX) {
            for(int drawY = Math.round(y1 * (float)height); drawY < Math.round(y2 * (float)height); ++drawY) {
                float pixelX = (float)drawX / (float)width * (float)resourceWidth;
                float pixelY = (float)drawY / (float)height * (float)resourceHeight;
                int floorX = (int)pixelX;
                int floorY = (int)pixelY;
                int ceilX = floorX + 1;
                int ceilY = floorY + 1;
                float percentX1 = (float)ceilX - pixelX;
                float percentY1 = (float)ceilY - pixelY;
                float percentX2 = pixelX - (float)floorX;
                float percentY2 = pixelY - (float)floorY;
                int pixel1 = nativeImageResource.getPixelRGBA(Mth.clamp(floorX, 0, resourceWidth - 1), Mth.clamp(floorY, 0, resourceHeight - 1));
                int pixel2 = nativeImageResource.getPixelRGBA(Mth.clamp(ceilX, 0, resourceWidth - 1), Mth.clamp(floorY, 0, resourceHeight - 1));
                int pixel3 = nativeImageResource.getPixelRGBA(Mth.clamp(floorX, 0, resourceWidth - 1), Mth.clamp(ceilY, 0, resourceHeight - 1));
                int pixel4 = nativeImageResource.getPixelRGBA(Mth.clamp(ceilX, 0, resourceWidth - 1), Mth.clamp(ceilY, 0, resourceHeight - 1));
                int newColor;
                if (useActualColor) {
                    newColor = invertColor(pixel1);
                } else {
                    float luminance1 = (float)(pixel1 >> 24 & 255) * percentX1 * percentY1;
                    float luminance2 = (float)(pixel2 >> 24 & 255) * percentX2 * percentY1;
                    float luminance3 = (float)(pixel3 >> 24 & 255) * percentX1 * percentY2;
                    float luminance4 = (float)(pixel4 >> 24 & 255) * percentX2 * percentY2;
                    newColor = (color & 16777215) + ((int)(luminance1 + luminance2 + luminance3 + luminance4) << 24);
                }
                blendPixel(nativeImage, (flipX ? width - drawX - 1 : drawX) + x, drawY + y, newColor);
            }
        }
    }

    private static void blendPixel(NativeImage nativeImage, int x, int y, int color) {
        if (RailwayData.isBetween(x, 0.0D, nativeImage.getWidth() - 1) && RailwayData.isBetween(y, 0.0D, nativeImage.getHeight() - 1)) {
            float percent = (float)(color >> 24 & 255) / 255.0F;
            if (percent > 0.0F) {
                int existingPixel = nativeImage.getPixelRGBA(x, y);
                boolean existingTransparent = (existingPixel >> 24 & 255) == 0;
                int r1 = existingTransparent ? 255 : existingPixel & 255;
                int g1 = existingTransparent ? 255 : existingPixel >> 8 & 255;
                int b1 = existingTransparent ? 255 : existingPixel >> 16 & 255;
                int r2 = color >> 16 & 255;
                int g2 = color >> 8 & 255;
                int b2 = color & 255;
                float inversePercent = 1.0F - percent;
                int finalColor = -16777216 | ((int)((float)r1 * inversePercent + (float)r2 * percent) << 16) + ((int)((float)g1 * inversePercent + (float)g2 * percent) << 8) + (int)((float)b1 * inversePercent + (float)b2 * percent);
                drawPixelSafe(nativeImage, x, y, finalColor);
            }
        }
    }

    private static void drawPixelSafe(NativeImage nativeImage, int x, int y, int color) {
        if (RailwayData.isBetween(x, 0.0D, nativeImage.getWidth() - 1) && RailwayData.isBetween(y, 0.0D, nativeImage.getHeight() - 1)) {
            nativeImage.setPixelRGBA(x, y, invertColor(color));
        }
    }

    private static int invertColor(int color) {
        return ((color & -16777216) != 0 ? -16777216 : 0) + ((color & 255) << 16) + (color & '\uff00') + ((color & 16711680) >> 16);
    }

    private static void clearColor(NativeImage nativeImage, int color) {
        for(int x = 0; x < nativeImage.getWidth(); ++x) {
            for(int y = 0; y < nativeImage.getHeight(); ++y) {
                if (nativeImage.getPixelRGBA(x, y) == color) {
                    nativeImage.setPixelRGBA(x, y, 0);
                }
            }
        }
    }

    private record StationPosition(float x, float y, boolean isCommon) {
    }

    private record StationPositionGrouped(StationPosition stationPosition,
                                          int stationOffset, List<Integer> interchangeColors,
                                          List<String> interchangeNames,
                                          int routeIndex, int stationIndex) {
    }
}
