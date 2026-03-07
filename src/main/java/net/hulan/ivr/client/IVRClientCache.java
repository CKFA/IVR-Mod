package net.hulan.ivr.client;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import mtr.client.ClientData;
import mtr.client.Config;
import mtr.data.DataCache;
import mtr.data.IGui;
import mtr.mappings.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class IVRClientCache extends DataCache implements IGui {
    
    private Font font;
    private Font fontCjk;
    private final Object2ObjectLinkedOpenHashMap<String, DynamicResource> dynamicResources = new Object2ObjectLinkedOpenHashMap<>();
    private final ObjectLinkedOpenHashSet<String> resourcesToRefresh = new ObjectLinkedOpenHashSet<>();
    private final java.util.List<Runnable> resourceRegistryQueue = new ArrayList<>();
    private static final ResourceLocation DEFAULT_BLACK_RESOURCE = new ResourceLocation("mtr", "textures/block/black.png");
    private static final ResourceLocation DEFAULT_WHITE_RESOURCE = new ResourceLocation("mtr", "textures/block/white.png");
    private static final ResourceLocation DEFAULT_TRANSPARENT_RESOURCE = new ResourceLocation("mtr", "textures/block/transparent.png");

    public IVRClientCache() {
        super(ClientData.STATIONS, ClientData.PLATFORMS, ClientData.SIDINGS, ClientData.ROUTES, ClientData.DEPOTS, new HashSet<>());
    }

    protected void syncAdditional() {
        ClientData.DATA_CACHE.sync();
    }

    public void resetFonts() {
        font = null;
        fontCjk = null;
        refreshDynamicResources();
    }

    public void refreshDynamicResources() {
        System.out.println("Refreshing dynamic resources");
        ClientData.DATA_CACHE.refreshDynamicResources();
        resourcesToRefresh.addAll(dynamicResources.keySet());
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

    public DynamicResource getSignText(String text, HorizontalAlignment horizontalAlignment, float paddingScale, int backgroundColor, int textColor) {
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

    public DynamicResource getRouteSquare(int color, String routeName, HorizontalAlignment horizontalAlignment) {
        return getResource(String.format("route_square_%s_%s_%s", color, routeName, horizontalAlignment),
                () -> IVRRouteMapGenerator.generateRouteSquare(color, routeName, horizontalAlignment),
                DefaultRenderingColor.TRANSPARENT);
    }

    public DynamicResource getDirectionArrow(long platformId,
                                                            boolean hasLeft,
                                                            boolean hasRight,
                                                            HorizontalAlignment horizontalAlignment,
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
                                                                 HorizontalAlignment horizontalAlignment,
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

    public DynamicResource getRouteMapForRS(long platformId,
                                                      boolean vertical,
                                                      boolean flip,
                                                      float aspectRatio,
                                                      boolean transparentWhite) {
        return getResource(String.format("route_map_for_rs_%s_%s_%s_%s_%s",
                        platformId,
                        vertical,
                        flip,
                        aspectRatio,
                        transparentWhite),
                () -> IVRRouteMapGenerator.generateRouteMapForRS(platformId,
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
                                HorizontalAlignment horizontalAlignment) {
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

    private enum DefaultRenderingColor {
        BLACK(IVRClientCache.DEFAULT_BLACK_RESOURCE),
        WHITE(IVRClientCache.DEFAULT_WHITE_RESOURCE),
        TRANSPARENT(IVRClientCache.DEFAULT_TRANSPARENT_RESOURCE);

        private final DynamicResource dynamicResource;

        DefaultRenderingColor(ResourceLocation resourceLocation) {
            dynamicResource = new DynamicResource(resourceLocation, null);
        }
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
                    width = nativeImage.getWidth();
                    height = nativeImage.getHeight();
                } else {
                    width = 16;
                    height = 16;
                }
            } else {
                width = 16;
                height = 16;
            }
        }

        private void remove() {
            if (!resourceLocation.equals(IVRClientCache.DEFAULT_BLACK_RESOURCE) && !resourceLocation.equals(IVRClientCache.DEFAULT_WHITE_RESOURCE) && !resourceLocation.equals(IVRClientCache.DEFAULT_TRANSPARENT_RESOURCE)) {
                TextureManager textureManager = Minecraft.getInstance().getTextureManager();
                textureManager.release(resourceLocation);
                AbstractTexture abstractTexture = textureManager.getTexture(resourceLocation);
                abstractTexture.releaseId();
                abstractTexture.close();
            }
        }
    }
}
