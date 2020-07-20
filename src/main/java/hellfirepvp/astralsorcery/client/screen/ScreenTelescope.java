/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.base.NavigationArrowScreen;
import hellfirepvp.astralsorcery.client.screen.base.SkyScreen;
import hellfirepvp.astralsorcery.client.screen.base.TileConstellationDiscoveryScreen;
import hellfirepvp.astralsorcery.client.screen.telescope.TelescopeRotationDrawArea;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.client.PktRotateTelescope;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Tuple;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenTelescope
 * Created by HellFirePvP
 * Date: 15.01.2020 / 17:16
 */
public class ScreenTelescope extends TileConstellationDiscoveryScreen<TileTelescope, TelescopeRotationDrawArea> implements SkyScreen, NavigationArrowScreen {

    private TileTelescope.TelescopeRotation rotation;

    private Rectangle rectArrowCW = null, rectArrowCCW = null;

    public ScreenTelescope(TileTelescope telescope) {
        super(telescope, 280, 280);
        this.rotation = telescope.getRotation();
    }

    @Nonnull
    @Override
    protected List<TelescopeRotationDrawArea> createDrawAreas() {
        List<TelescopeRotationDrawArea> areas = new LinkedList<>();
        for (TileTelescope.TelescopeRotation r : TileTelescope.TelescopeRotation.values()) {
            areas.add(new TelescopeRotationDrawArea(this, r, this.getGuiBox()));
        }
        return areas;
    }

    @Override
    protected void fillConstellations(WorldContext ctx, List<TelescopeRotationDrawArea> drawAreas) {
        Random gen = ctx.getDayRandom();
        PlayerProgress prog = ResearchHelper.getClientProgress();

        List<IWeakConstellation> cst = new ArrayList<>();
        for (IConstellation active : ctx.getActiveCelestialsHandler().getCurrentRenderPositions().keySet()) {
            if (active instanceof IWeakConstellation && active.canDiscover(Minecraft.getInstance().player, prog)) {
                cst.add((IWeakConstellation) active);
            }
        }
        Collections.shuffle(cst, gen);
        cst = cst.subList(0, Math.min(drawAreas.size(), cst.size()));
        for (IWeakConstellation constellation : cst) {
            Point foundPoint;
            TelescopeRotationDrawArea associatedArea;
            do {
                associatedArea = MiscUtils.getRandomEntry(drawAreas, gen);
                foundPoint = findEmptySpace(gen, associatedArea);
            } while (foundPoint == null);
            associatedArea.addConstellationToArea(constellation, foundPoint, DEFAULT_CONSTELLATION_SIZE);
        }
    }

    private Point findEmptySpace(Random rand, TelescopeRotationDrawArea area) {
        int size = DEFAULT_CONSTELLATION_SIZE;
        int wdh = guiWidth  - 6 - size;
        int hgt = guiHeight - 6 - size;
        int rX = 6 + rand.nextInt(wdh);
        int rY = 6 + rand.nextInt(hgt);
        Rectangle constellationRect = new Rectangle(rX, rY, size, size);
        for (ConstellationDisplayInformation info : area.getDisplayMap().values()) {
            Point offset = info.getRenderPosition();
            Rectangle otherRect = new Rectangle(offset.x, offset.y, size, size);
            if (otherRect.intersects(constellationRect)) {
                return null;
            }
        }
        return new Point(rX, rY);
    }

    @Override
    public void render(int mouseX, int mouseY, float pTicks) {
        super.render(mouseX, mouseY, pTicks);

        this.drawWHRect(TexturesAS.TEX_GUI_TELESCOPE);

        this.changeZLevel(-10);
        this.drawConstellationCell(pTicks);
        this.changeZLevel(10);

        this.drawNavArrows(mouseX, mouseY, pTicks);
    }

    private void drawNavArrows(int mouseX, int mouseY, float pTicks) {
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();

        this.rectArrowCCW = this.drawArrow(guiLeft - 40, guiTop + (guiHeight / 2), this.getGuiZLevel(), Type.LEFT, mouseX, mouseY, pTicks);
        this.rectArrowCW = this.drawArrow(guiLeft + guiWidth + 10, guiTop + (guiHeight / 2), this.getGuiZLevel(), Type.RIGHT, mouseX, mouseY, pTicks);

        RenderSystem.disableBlend();
    }

    private void drawConstellationCell(float pTicks) {
        boolean canSeeSky = this.canObserverSeeSky(this.getTile().getPos(), 1);

        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();

        this.drawSkyBackground(pTicks, canSeeSky);

        if (!this.isInitialized()) {
            Blending.DEFAULT.apply();
            RenderSystem.disableBlend();
            RenderSystem.enableAlphaTest();
            return;
        }

        WorldContext ctx = this.getContext();
        if (ctx != null && canSeeSky) {
            Random gen = ctx.getDayRandom();
            PlayerProgress prog = ResearchHelper.getClientProgress();

            for (int i = 0; i < this.rotation.ordinal(); i++) {
                gen.nextFloat(); //Flush
            }

            this.changeZLevel(1);
            float starSize = 5F;
            TexturesAS.TEX_STAR_1.bindTexture();
            RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
                for (int i = 0; i < 72 + gen.nextInt(108); i++) {
                    float innerOffsetX = starSize + gen.nextFloat() * (guiWidth  - starSize) + this.getGuiLeft();
                    float innerOffsetY = starSize + gen.nextFloat() * (guiHeight - starSize) + this.getGuiTop();
                    float brightness = 0.4F + (RenderingConstellationUtils.stdFlicker(ClientScheduler.getClientTick(), pTicks, 10 + gen.nextInt(20))) * 0.5F;
                    brightness = this.multiplyStarBrightness(pTicks, brightness);

                    RenderingGuiUtils.rect(buf, this)
                            .at(innerOffsetX, innerOffsetY)
                            .dim(starSize, starSize)
                            .color(brightness, brightness, brightness, brightness)
                            .draw();
                }
            });
            this.changeZLevel(-1);

            this.changeZLevel(3);
            for (TelescopeRotationDrawArea area : this.getVisibleDrawAreas()) {
                for (IConstellation cst : area.getDisplayMap().keySet()) {
                    ConstellationDisplayInformation info = area.getDisplayMap().get(cst);
                    info.getFrameDrawInformation().clear();

                    Point pos = info.getRenderPosition();
                    int size = (int) info.getRenderSize();

                    float rainBr = 1F - Minecraft.getInstance().world.getRainStrength(pTicks);
                    Map<StarLocation, Rectangle> cstRenderInfo = RenderingConstellationUtils.renderConstellationIntoGUI(
                            cst,
                            pos.x + guiLeft,
                            pos.y + guiTop,
                            this.getGuiZLevel(),
                            size, size,
                            2.5F,
                            () -> RenderingConstellationUtils.conCFlicker(ClientScheduler.getClientTick(), pTicks, 5 + gen.nextInt(15)) * rainBr,
                            prog.hasConstellationDiscovered(cst),
                            true);

                    info.getFrameDrawInformation().putAll(cstRenderInfo);
                }
            }
            this.changeZLevel(-3);

            this.changeZLevel(5);
            this.renderDrawnLines(gen, pTicks);
            this.changeZLevel(-5);
        }

        Blending.DEFAULT.apply();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    private void drawSkyBackground(float pTicks, boolean canSeeSky) {
        Tuple<Color, Color> rgbFromTo = SkyScreen.getSkyGradient(canSeeSky, 1F, pTicks);
        RenderingDrawUtils.drawGradientRect(this.getGuiZLevel(),
                this.guiLeft, this.guiTop,
                this.guiLeft + this.guiWidth, this.guiTop + this.guiHeight,
                rgbFromTo.getA().getRGB(), rgbFromTo.getB().getRGB());
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        Point p = new Point((int) mouseX, (int) mouseY);
        if (rectArrowCW != null && rectArrowCW.contains(p)) {
            PktRotateTelescope pkt = new PktRotateTelescope(true, this.getTile().getWorld().getDimension().getType(), this.getTile().getPos());
            PacketChannel.CHANNEL.sendToServer(pkt);
            return true;
        }
        if (rectArrowCCW != null && rectArrowCCW.contains(p)) {
            PktRotateTelescope pkt = new PktRotateTelescope(false, this.getTile().getWorld().getDimension().getType(), this.getTile().getPos());
            PacketChannel.CHANNEL.sendToServer(pkt);
            return true;
        }
        return false;
    }

    public void handleRotationChange(boolean isClockwise) {
        this.rotation = isClockwise ? rotation.nextClockWise() : rotation.nextCounterClockWise();
        this.clearDrawing();
    }

    public TileTelescope.TelescopeRotation getRotation() {
        return rotation;
    }

    @Override
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        return true;
    }

    @Override
    protected boolean isMouseRotatingGui() {
        return false;
    }
}
