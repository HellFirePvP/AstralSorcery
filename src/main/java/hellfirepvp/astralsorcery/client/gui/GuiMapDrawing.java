/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.gui.base.GuiTileBase;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.constellation.DrawnConstellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.starmap.ActiveStarMap;
import hellfirepvp.astralsorcery.common.data.DataActiveCelestials;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.ItemInfusedGlass;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktBurnParchment;
import hellfirepvp.astralsorcery.common.network.packet.client.PktEngraveGlass;
import hellfirepvp.astralsorcery.common.tile.TileMapDrawingTable;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiMapDrawing
 * Created by HellFirePvP
 * Date: 18.03.2017 / 17:29
 */
public class GuiMapDrawing extends GuiTileBase<TileMapDrawingTable> {

    public static final BindableResource texMapDrawing = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guidrawing");
    public static final BindableResource texMapDrawingEmpty = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guidrawing_empty");

    private static final Rectangle rctDrawingGrid = new Rectangle(
            68 + DrawnConstellation.CONSTELLATION_DRAW_SIZE, 45 + DrawnConstellation.CONSTELLATION_DRAW_SIZE,
            120 - (DrawnConstellation.CONSTELLATION_DRAW_SIZE * 2), 120 - (DrawnConstellation.CONSTELLATION_DRAW_SIZE * 2));
    private Map<Rectangle, IConstellation> mapRenderedConstellations = new HashMap<>();

    private List<DrawnConstellation> drawnConstellations = new LinkedList<>();

    private IConstellation dragging = null;
    private int dragRequested = 0;

    public GuiMapDrawing(TileMapDrawingTable table) {
        super(table, 188, 256);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        TileMapDrawingTable tile = getOwningTileEntity();
        GlStateManager.color(1F, 1F, 1F, 1F);
        if (!tile.hasParchment()) {
            drawWHRect(texMapDrawingEmpty);
        } else {
            drawWHRect(texMapDrawing);
        }

        mapRenderedConstellations.clear();

        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();

        List<String> tooltip = null;
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        boolean hasLens = false;
        if(itemRender != null) {
            float prev = zLevel;
            float itemPrev = itemRender.zLevel;
            zLevel += 100;
            itemRender.zLevel += 100;

            ItemStack in = tile.getSlotIn();
            if(!in.isEmpty()) {
                Rectangle rc = new Rectangle(guiLeft + 111, guiTop + 8, 16, 16);
                GlStateManager.pushMatrix();
                GlStateManager.translate(rc.x, rc.y, 0);
                GlStateManager.enableDepth();
                itemRender.renderItemAndEffectIntoGUI(this.mc.player, in, 0, 0);
                itemRender.renderItemOverlayIntoGUI(this.fontRenderer, in, 0, 0, null);
                GlStateManager.popMatrix();
                if(rc.contains(mouseX, mouseY)) {
                    FontRenderer custom = in.getItem().getFontRenderer(in);
                    if(custom != null) {
                        fr = custom;
                    }
                    tooltip = in.getTooltip(Minecraft.getMinecraft().player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
                }
            }
            in = tile.getSlotGlassLens();
            if(!in.isEmpty()) {
                if(in.getItem() instanceof ItemInfusedGlass) {
                    hasLens = true;
                }

                Rectangle rc = new Rectangle(guiLeft + 129, guiTop + 8, 16, 16);
                GlStateManager.pushMatrix();
                GlStateManager.translate(rc.x, rc.y, 0);
                GlStateManager.enableDepth();
                itemRender.renderItemAndEffectIntoGUI(this.mc.player, in, 0, 0);
                itemRender.renderItemOverlayIntoGUI(this.fontRenderer, in, 0, 0, null);
                GlStateManager.popMatrix();
                if(rc.contains(mouseX, mouseY)) {
                    FontRenderer custom = in.getItem().getFontRenderer(in);
                    if(custom != null) {
                        fr = custom;
                    }
                    tooltip = in.getTooltip(Minecraft.getMinecraft().player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
                }
            }

            zLevel = prev;
            itemRender.zLevel = itemPrev;
        }

        RenderConstellation.BrightnessFunction f = new RenderConstellation.BrightnessFunction() {
            @Override
            public float getBrightness() {
                return ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(Minecraft.getMinecraft().world);
            }
        };
        if(hasLens) {
            WorldSkyHandler wsh = ConstellationSkyHandler.getInstance().getWorldHandler(tile.getWorld());
            if(wsh != null && tile.doesSeeSky()) {

                if(f.getBrightness() > 1E-4) {
                    DataActiveCelestials dac = SyncDataHolder.getDataClient(SyncDataHolder.DATA_CONSTELLATIONS);
                    Collection<IConstellation> cst = dac.getActiveConstellations(Minecraft.getMinecraft().world.provider.getDimension());

                    if(cst != null) {
                        List<IConstellation> filtered = cst.stream()
                                .filter((c) -> ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName()))
                                .collect(Collectors.toList());

                        for (int i = 0; i < filtered.size(); i++) {
                            IConstellation c = filtered.get(i);
                            int offsetX = i % 2 == 0 ? 8 : 232;
                            int offsetY = 40 + (i / 2) * 23;

                            mapRenderedConstellations.put(new Rectangle(offsetX, offsetY, 16, 16), c);

                            drawConstellation(c, new Point(offsetX, offsetY), f);
                        }
                    }
                }
            }

            ActiveStarMap map = ItemInfusedGlass.getMapEngravingInformations(tile.getSlotGlassLens());
            if(map != null && tile.doesSeeSky()) {
                RenderConstellation.BrightnessFunction dim = new RenderConstellation.BrightnessFunction() {
                    @Override
                    public float getBrightness() {
                        return f.getBrightness() * 0.8F;
                    }
                };
                Map<IConstellation, List<Point>> constellationMap = map.getMapOffsets();
                for (IConstellation c : constellationMap.keySet()) {
                    for (Point p : constellationMap.get(c)) {
                        int whDrawn = DrawnConstellation.CONSTELLATION_DRAW_SIZE;
                        Point offset = new Point(p.x, p.y);
                        offset.translate(guiLeft, guiTop);
                        offset.translate(rctDrawingGrid.x, rctDrawingGrid.y);
                        offset.translate(-whDrawn, -whDrawn);

                        RenderConstellation.renderConstellationIntoGUI(c, offset.x, offset.y, zLevel, whDrawn * 2, whDrawn * 2,
                                1.6F, dim, true, false);
                    }
                }
            }
        }

        if(tile.getPercRunning() > 1E-4) {
            SpriteSheetResource halo = SpriteLibrary.spriteHalo2;
            halo.getResource().bind();
            Tuple<Double, Double> uvFrame = halo.getUVOffset(ClientScheduler.getClientTick());
            GlStateManager.pushMatrix();

            float rot =     ((float) (ClientScheduler.getClientTick()     % 2000) / 2000F * 360F);

            float scale = 160F;

            GlStateManager.translate(guiLeft + guiWidth / 2, guiTop + guiHeight / 2 + 10, 0);
            GlStateManager.rotate(rot, 0, 0, 1);
            GlStateManager.translate(-scale / 2, -scale / 2, 0);

            GlStateManager.color(1F, 1F, 1F, tile.getPercRunning());
            GlStateManager.enableBlend();
            Blending.DEFAULT.applyStateManager();
            GlStateManager.disableAlpha();

            drawTexturedRectAtCurrentPos(scale, scale,
                    (float) (double) uvFrame.key, (float) (double) uvFrame.value, //Jeeez. Double -> float is not a thing.
                    (float)  halo.getULength(), (float) halo.getVLength());

            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            TextureHelper.refreshTextureBindState();
        }

        if(!tile.getSlotIn().isEmpty() && !tile.hasParchment() && itemRender != null) {
            TextureHelper.refreshTextureBindState();
            TextureHelper.setActiveTextureToAtlasSprite();
            float prev = zLevel;
            float itemPrev = itemRender.zLevel;
            zLevel += 100;
            itemRender.zLevel += 100;

            ItemStack in = tile.getSlotIn();
            GlStateManager.pushMatrix();
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.translate(guiLeft + 63 + 16.25, guiTop + 42 + 16.25, 0); //-> +130, +130
            GlStateManager.scale(6, 6, 0);
            GlStateManager.enableDepth();

            itemRender.renderItemAndEffectIntoGUI(this.mc.player, in, 0, 0);
            itemRender.renderItemOverlayIntoGUI(this.fontRenderer, in, 0, 0, null);

            GlStateManager.popMatrix();

            zLevel = prev;
            itemRender.zLevel = itemPrev;
        }

        if(f.getBrightness() <= 1E-4 || !tile.hasParchment()) {
            drawnConstellations.clear();
            dragging = null;
            dragRequested = 0;
        }

        for (DrawnConstellation cst : drawnConstellations) {
            int whDrawn = DrawnConstellation.CONSTELLATION_DRAW_SIZE;
            Point offset = new Point(cst.point.x, cst.point.y);
            offset.translate(guiLeft, guiTop);
            offset.translate(-whDrawn, -whDrawn);

            RenderConstellation.renderConstellationIntoGUI(cst.constellation, offset.x, offset.y, zLevel, whDrawn * 2, whDrawn * 2,
                    1.6F, f, true, false);
        }

        if(dragging != null) {
            int whDragging = DrawnConstellation.CONSTELLATION_DRAW_SIZE;
            Point offset = new Point(mouseX, mouseY);
            offset.translate(-whDragging, -whDragging);

            RenderConstellation.renderConstellationIntoGUI(dragging, offset.x, offset.y, zLevel, whDragging * 2, whDragging * 2,
                    1.6F, f, true, false);

            if(ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(Minecraft.getMinecraft().world) <= 1E-4) {
                dragging = null;
            }
        }

        for (Rectangle r : mapRenderedConstellations.keySet()) {
            if(r.contains(mouseX - guiLeft, mouseY - guiTop)) {
                tooltip = Lists.newArrayList(I18n.format(mapRenderedConstellations.get(r).getUnlocalizedName()));
            }
        }

        if(tooltip != null) {
            RenderingUtils.renderBlueTooltip(mouseX, mouseY, tooltip, fr);
        }
        TextureHelper.refreshTextureBindState();
    }

    private void drawConstellation(IConstellation c, Point p, RenderConstellation.BrightnessFunction fct) {
        RenderConstellation.renderConstellationIntoGUI(Color.WHITE, c,
                guiLeft + p.x, guiTop + p.y, zLevel,
                16, 16, 0.5, fct, true, false);
    }

    @Nullable
    private Point translatePointToGrid(Point mouse) {
        mouse = new Point(mouse.x, mouse.y);
        mouse.translate(-guiLeft, -guiTop);
        if(rctDrawingGrid.contains(mouse)) {
            return mouse;
        }
        return null;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if(dragRequested > 0) {
            if(!Mouse.isButtonDown(0)) {
                dragRequested--;
                if (dragRequested <= 0) {
                    dragging = null;
                    dragRequested = 0;
                }
            }
        }

        if(drawnConstellations.size() >= 3) {
            LinkedList<DrawnConstellation> filtered = new LinkedList<>();
            for (int i = 0; i < 3; i++) {
                DrawnConstellation c = drawnConstellations.get(i);
                Point at = new Point(c.point.x, c.point.y);
                at.translate(-rctDrawingGrid.x, -rctDrawingGrid.y);
                filtered.add(new DrawnConstellation(at, c.constellation));
            }
            PktEngraveGlass pkt = new PktEngraveGlass(getOwningTileEntity().getWorld().provider.getDimension(),
                    getOwningTileEntity().getPos(), filtered);
            PacketChannel.CHANNEL.sendToServer(pkt);
            drawnConstellations.clear();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton == 0 && getOwningTileEntity().hasParchment() &&
                 drawnConstellations.size() < 3 && getOwningTileEntity().hasUnengravedGlass()) {
            tryPickUp(new Point(mouseX, mouseY));
        }

    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        if(clickedMouseButton == 0 && dragging != null && getOwningTileEntity().hasParchment() &&
                drawnConstellations.size() < 3 && getOwningTileEntity().hasUnengravedGlass()) {
            dragRequested = 10;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        if(getOwningTileEntity().hasParchment() &&
                drawnConstellations.size() < 3 && getOwningTileEntity().hasUnengravedGlass()) {
            tryDrop(new Point(mouseX, mouseY));
        }
    }

    private void tryDrop(Point mouse) {
        if(dragging != null && dragRequested > 0) {

            Point gridPoint = translatePointToGrid(mouse);
            if(gridPoint != null) {
                if(!tryBurnParchment()) {
                    drawnConstellations.add(new DrawnConstellation(gridPoint, dragging));
                }
            }

            dragging = null;
            dragRequested = 0;
        }
    }

    private boolean tryBurnParchment() {
        for (int i = 0; i < drawnConstellations.size() + 1; i++) {
            if(EffectHandler.STATIC_EFFECT_RAND.nextInt(Math.max(1, MathHelper.ceil(7 * ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(Minecraft.getMinecraft().world)))) == 0) {
                PktBurnParchment pkt = new PktBurnParchment(Minecraft.getMinecraft().world.provider.getDimension(), getOwningTileEntity().getPos());
                PacketChannel.CHANNEL.sendToServer(pkt);
                return true;
            }
        }
        return false;
    }

    private void tryPickUp(Point mouse) {
        mouse.translate(-guiLeft, -guiTop);
        for (Rectangle r : mapRenderedConstellations.keySet()) {
            if(r.contains(mouse)) {
                dragging = mapRenderedConstellations.get(r);
                dragRequested = 10;
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}

