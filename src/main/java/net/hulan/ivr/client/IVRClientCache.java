package net.hulan.ivr.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import mtr.client.ClientData;
import mtr.client.Config;
import mtr.data.DataCache;
import mtr.data.IGui;
import mtr.mappings.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

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
    private final Object2ObjectLinkedOpenHashMap<String, IVRClientCache.DynamicResource> dynamicResources = new Object2ObjectLinkedOpenHashMap<>();
    private final ObjectLinkedOpenHashSet<String> resourcesToRefresh = new ObjectLinkedOpenHashSet<>();
    private final java.util.List<Runnable> resourceRegistryQueue = new ArrayList<>();
    private static final Identifier DEFAULT_BLACK_RESOURCE = new Identifier("mtr", "textures/block/black.png");
    private static final Identifier DEFAULT_WHITE_RESOURCE = new Identifier("mtr", "textures/block/white.png");
    private static final Identifier DEFAULT_TRANSPARENT_RESOURCE = new Identifier("mtr", "textures/block/transparent.png");

    public IVRClientCache() {
        super(ClientData.STATIONS, ClientData.PLATFORMS, ClientData.SIDINGS, ClientData.ROUTES, ClientData.DEPOTS, new HashSet<>());
    }

    protected void syncAdditional() {
        ClientData.DATA_CACHE.sync();
    }

    public void resetFonts() {
        this.font = null;
        this.fontCjk = null;
        this.refreshDynamicResources();
    }

    public void refreshDynamicResources() {
        System.out.println("Refreshing dynamic resources");
        ClientData.DATA_CACHE.refreshDynamicResources();
        this.resourcesToRefresh.addAll(this.dynamicResources.keySet());
    }

    public IVRClientCache.DynamicResource getPixelatedText(String text, int textColor, int maxWidth, float cjkSizeRatio, boolean fullPixel) {
        return this.getResource(String.format("pixelated_text_%s_%s_%s_%s_%s", text, textColor, maxWidth, cjkSizeRatio, fullPixel), () -> IVRRouteMapGenerator.generatePixelatedText(text, textColor, maxWidth, cjkSizeRatio, fullPixel), IVRClientCache.DefaultRenderingColor.TRANSPARENT);
    }

    public IVRClientCache.DynamicResource getColorStrip(long platformId) {
        return this.getResource(String.format("color_%s", platformId), () -> IVRRouteMapGenerator.generateColorStrip(platformId), IVRClientCache.DefaultRenderingColor.TRANSPARENT);
    }

    public IVRClientCache.DynamicResource getStationName(String stationName, float aspectRatio) {
        return this.getResource(String.format("station_name_%s_%s", stationName, aspectRatio), () -> IVRRouteMapGenerator.generateStationName(stationName, aspectRatio), IVRClientCache.DefaultRenderingColor.TRANSPARENT);
    }

    public IVRClientCache.DynamicResource getStationNameEntrance(int textColor, String stationName, float aspectRatio) {
        return this.getResource(String.format("station_name_entrance_%s_%s_%s", textColor, stationName, aspectRatio), () -> IVRRouteMapGenerator.generateStationNameEntrance(textColor, stationName, aspectRatio), IVRClientCache.DefaultRenderingColor.TRANSPARENT);
    }

    public IVRClientCache.DynamicResource getSingleRowStationName(long platformId, float aspectRatio) {
        return this.getResource(String.format("single_row_station_name_%s_%s", platformId, aspectRatio), () -> IVRRouteMapGenerator.generateSingleRowStationName(platformId, aspectRatio), IVRClientCache.DefaultRenderingColor.WHITE);
    }

    public IVRClientCache.DynamicResource getSignText(String text, HorizontalAlignment horizontalAlignment, float paddingScale, int backgroundColor, int textColor) {
        return this.getResource(String.format("sign_text_%s_%s_%s_%s_%s", text, horizontalAlignment, paddingScale, backgroundColor, textColor), () -> IVRRouteMapGenerator.generateSignText(text, horizontalAlignment, paddingScale, backgroundColor, textColor), IVRClientCache.DefaultRenderingColor.TRANSPARENT);
    }

    public IVRClientCache.DynamicResource getLiftPanelDisplay(String originalText, int textColor) {
        return this.getResource(String.format("lift_panel_display_%s", originalText), () -> IVRRouteMapGenerator.generateLiftPanel(originalText, textColor), IVRClientCache.DefaultRenderingColor.BLACK);
    }

    public IVRClientCache.DynamicResource getExitSignLetter(String exitLetter, String exitNumber, int backgroundColor) {
        return this.getResource(String.format("exit_sign_letter_%s_%s", exitLetter, exitNumber),
                () -> IVRRouteMapGenerator.generateExitSignLetter(exitLetter, exitNumber, backgroundColor),
                IVRClientCache.DefaultRenderingColor.TRANSPARENT);
    }

    public IVRClientCache.DynamicResource getRouteSquare(int color, String routeName, HorizontalAlignment horizontalAlignment) {
        return this.getResource(String.format("route_square_%s_%s_%s", color, routeName, horizontalAlignment),
                () -> IVRRouteMapGenerator.generateRouteSquare(color, routeName, horizontalAlignment),
                IVRClientCache.DefaultRenderingColor.TRANSPARENT);
    }

    public IVRClientCache.DynamicResource getDirectionArrow(long platformId,
                                                            boolean hasLeft,
                                                            boolean hasRight,
                                                            HorizontalAlignment horizontalAlignment,
                                                            boolean showToString,
                                                            float paddingScale,
                                                            float aspectRatio,
                                                            int backgroundColor,
                                                            int textColor,
                                                            int transparentColor) {
        return this.getResource(String.format("direction_arrow_%s_%s_%s_%s_%s_%s_%s_%s_%s_%s",
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
                transparentColor == 0 && backgroundColor == -1 ? IVRClientCache.DefaultRenderingColor.WHITE : IVRClientCache.DefaultRenderingColor.TRANSPARENT);
    }

    public IVRClientCache.DynamicResource getDirectionArrowForRS(long platformId,
                                                                 boolean hasLeft,
                                                                 boolean hasRight,
                                                                 HorizontalAlignment horizontalAlignment,
                                                                 float paddingScale,
                                                                 float aspectRatio,
                                                                 int backgroundColor,
                                                                 int textColor,
                                                                 int transparentColor) {
        return this.getResource(String.format("direction_arrow_for_rs_%s_%s_%s_%s_%s_%s_%s_%s",
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
                transparentColor == 0 && backgroundColor == -1 ? IVRClientCache.DefaultRenderingColor.WHITE : IVRClientCache.DefaultRenderingColor.TRANSPARENT);
    }

    public IVRClientCache.DynamicResource getRouteMap(long platformId,
                                                      boolean vertical,
                                                      boolean flip,
                                                      float aspectRatio,
                                                      boolean transparentWhite) {
        return this.getResource(String.format("route_map_%s_%s_%s_%s_%s",
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
                transparentWhite ? IVRClientCache.DefaultRenderingColor.TRANSPARENT : IVRClientCache.DefaultRenderingColor.WHITE);
    }

    public IVRClientCache.DynamicResource getRouteMapForRS(long platformId,
                                                      boolean vertical,
                                                      boolean flip,
                                                      float aspectRatio,
                                                      boolean transparentWhite) {
        return this.getResource(String.format("route_map_for_rs_%s_%s_%s_%s_%s",
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
                transparentWhite ? IVRClientCache.DefaultRenderingColor.TRANSPARENT : IVRClientCache.DefaultRenderingColor.WHITE);
    }

    public byte[] getTextPixels(String text, int[] dimensions, int fontSizeCjk, int fontSize) {
        return this.getTextPixels(text, dimensions, 2147483647, (int)((float)Math.max(fontSizeCjk, fontSize) * 1.25F), fontSizeCjk, fontSize, 0, null);
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
                newFontSize = !IGui.isCjk(textSplit[index]) && this.font.canDisplayUpTo(textSplit[index]) < 0 ? fontSize : fontSizeCjk;
                attributedStrings[index] = new AttributedString(textSplit[index]);
                fontSizes[index] = newFontSize;
                Font fontSized = this.font.deriveFont(Font.PLAIN, (float)newFontSize);
                Font fontCjkSized = this.fontCjk.deriveFont(Font.PLAIN, (float)newFontSize);

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

    private IVRClientCache.DynamicResource getResource(String key, Supplier<NativeImage> supplier, IVRClientCache.DefaultRenderingColor defaultRenderingColor) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (this.font == null || this.fontCjk == null) {
            ResourceManager resourceManager = minecraftClient.getResourceManager();

            try {
                this.font = Font.createFont(0, Utilities.getInputStream(resourceManager.getResource(new Identifier("ivr", "font/noto-sans-semibold.ttf"))));
                this.fontCjk = Font.createFont(0, Utilities.getInputStream(resourceManager.getResource(new Identifier("ivr", "font/noto-serif-cjk-tc-semibold.ttf"))));
            } catch (Exception var7) {
                var7.printStackTrace();
            }
        }

        if (!this.resourceRegistryQueue.isEmpty()) {
            Runnable runnable = this.resourceRegistryQueue.remove(0);
            if (runnable != null) {
                runnable.run();
            }
        }

        boolean needsRefresh = this.resourcesToRefresh.contains(key);
        IVRClientCache.DynamicResource dynamicResource = this.dynamicResources.get(key);
        if (dynamicResource != null && !needsRefresh) {
            return dynamicResource;
        } else {
            IVRRouteMapGenerator.setConstants();
            CompletableFuture.supplyAsync(supplier).thenAccept((nativeImage) -> this.resourceRegistryQueue.add(() -> {
                DynamicResource staticTextureProviderOld = this.dynamicResources.get(key);
                if (staticTextureProviderOld != null) {
                    staticTextureProviderOld.remove();
                }

                DynamicResource dynamicResourceNew;
                if (nativeImage == null) {
                    dynamicResourceNew = defaultRenderingColor.dynamicResource;
                } else {
                    NativeImageBackedTexture dynamicTexture = new NativeImageBackedTexture(nativeImage);
                    String newKey = key;

                    try {
                        newKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                    String var10003 = newKey.toLowerCase(Locale.ENGLISH);
                    Identifier resourceLocation = new Identifier("ivr", "dynamic_texture_" + var10003.replaceAll("[^0-9a-z_]", "_"));
                    minecraftClient.getTextureManager().registerTexture(resourceLocation, dynamicTexture);
                    dynamicResourceNew = new DynamicResource(resourceLocation, dynamicTexture);
                }

                this.dynamicResources.put(key, dynamicResourceNew);
            }));
            if (needsRefresh) {
                this.resourcesToRefresh.remove(key);
            }

            if (dynamicResource == null) {
                this.dynamicResources.put(key, defaultRenderingColor.dynamicResource);
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

        private final IVRClientCache.DynamicResource dynamicResource;

        DefaultRenderingColor(Identifier resourceLocation) {
            this.dynamicResource = new IVRClientCache.DynamicResource(resourceLocation, null);
        }
    }

    public static class DynamicResource {
        public final int width;
        public final int height;
        public final Identifier resourceLocation;

        private DynamicResource(Identifier resourceLocation, NativeImageBackedTexture dynamicTexture) {
            this.resourceLocation = resourceLocation;
            if (dynamicTexture != null) {
                NativeImage nativeImage = dynamicTexture.getImage();
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
            if (!this.resourceLocation.equals(IVRClientCache.DEFAULT_BLACK_RESOURCE) && !this.resourceLocation.equals(IVRClientCache.DEFAULT_WHITE_RESOURCE) && !this.resourceLocation.equals(IVRClientCache.DEFAULT_TRANSPARENT_RESOURCE)) {
                TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
                textureManager.destroyTexture(this.resourceLocation);
                AbstractTexture abstractTexture = textureManager.getTexture(this.resourceLocation);
                if (abstractTexture != null) {
                    abstractTexture.clearGlId();
                    abstractTexture.close();
                }
            }

        }
    }

}
