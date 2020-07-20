/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import com.google.common.collect.Iterables;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.MouseUtil;
import hellfirepvp.astralsorcery.client.util.RenderingConstellationUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.client.PktDiscoverConstellation;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationDiscoveryScreen
 * Created by HellFirePvP
 * Date: 14.02.2020 / 20:06
 */
public abstract class ConstellationDiscoveryScreen<D extends ConstellationDiscoveryScreen.DrawArea> extends WidthHeightScreen {

    public static final int DEFAULT_CONSTELLATION_SIZE = 150;

    private List<D> drawAreas = new LinkedList<>();

    private D currentDrawArea = null;
    private List<DrawnLine> drawnLines = new LinkedList<>();
    private Point dragStart, dragEnd;

    private boolean initialized = false;

    protected ConstellationDiscoveryScreen(ITextComponent titleIn, int guiHeight, int guiWidth) {
        super(titleIn, guiHeight, guiWidth);
    }

    @Override
    protected void init() {
        super.init();
        this.drawAreas = this.createDrawAreas();

        if (this.getContext() != null) {
            this.fillConstellations(this.getContext(), this.drawAreas);
            this.initialized = true;
        }
    }

    protected boolean isMouseRotatingGui() {
        return true;
    }

    @Nonnull
    protected abstract List<D> createDrawAreas();

    protected abstract void fillConstellations(WorldContext ctx, List<D> drawAreas);

    protected WorldContext getContext() {
        return SkyHandler.getContext(Minecraft.getInstance().world, LogicalSide.CLIENT);
    }

    protected boolean isInitialized() {
        return this.initialized;
    }

    @Nullable
    public D getCurrentDrawArea() {
        return currentDrawArea;
    }

    public List<D> getVisibleDrawAreas() {
        return this.drawAreas.stream().filter(DrawArea::isVisible).collect(Collectors.toList());
    }

    public List<D> getContainingDrawAreas(double mouseX, double mouseY) {
        return this.drawAreas.stream()
                .filter(area -> area.contains(mouseX, mouseY))
                .collect(Collectors.toList());
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.initialized && this.getContext() != null) {
            this.fillConstellations(this.getContext(), this.drawAreas);
            this.initialized = true;
        }
    }

    protected float multiplyStarBrightness(float pTicks, float brightnessIn) {
        brightnessIn *= Minecraft.getInstance().world.getStarBrightness(pTicks) * 2;
        return MathHelper.clamp(brightnessIn * (1F - Minecraft.getInstance().world.getRainStrength(pTicks)), 0, 1);
    }

    @Override
    public void render(int mouseX, int mouseY, float pTicks) {
        if (this.isMouseRotatingGui()) {
            if (hasShiftDown() && Minecraft.getInstance().mouseHelper.isMouseGrabbed()) {
                MouseUtil.ungrab();
            }
            if (!hasShiftDown() && !Minecraft.getInstance().mouseHelper.isMouseGrabbed()) {
                MouseUtil.grab();
            }
        }

        super.render(mouseX, mouseY, pTicks);
    }

    protected void renderDrawnLines(Random rand, float pTicks) {
        if (!canDraw()) {
            this.clearDrawing();
            return;
        }

        float lineBreadth = 2.0F;
        Supplier<Float> brightnessFn = () -> RenderingConstellationUtils.conCFlicker(ClientScheduler.getClientTick(), pTicks, 5 + rand.nextInt(10));
        TexturesAS.TEX_STAR_CONNECTION.bindTexture();

        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            for (DrawnLine line : drawnLines) {
                drawLine(buf, pTicks, line.from, line.to, brightnessFn, lineBreadth);
            }

            if (this.dragStart != null && this.dragEnd != null) {
                Point adjStart = new Point(this.dragStart.x - guiLeft, this.dragStart.y - guiTop);
                Point adjEnd = new Point(this.dragEnd.x - guiLeft, this.dragEnd.y - guiTop);
                drawLine(buf, pTicks, adjStart, adjEnd, () -> 0.8F, lineBreadth);
            }
        });
    }

    private void drawLine(BufferBuilder buf, float pTicks, Point from, Point to, Supplier<Float> brightnessFn, float lineBreadth) {
        float brightness = brightnessFn.get();
        float starBr = this.multiplyStarBrightness(pTicks, brightness);
        if (starBr <= 0.0F) {
            return;
        }
        starBr = starBr * 0.75F + 0.25F;

        Vector3 fromStar = new Vector3(guiLeft + from.getX(), guiTop + from.getY(), this.getGuiZLevel());
        Vector3 toStar = new Vector3(guiLeft + to.getX(), guiTop + to.getY(), this.getGuiZLevel());

        Vector3 dir = toStar.clone().subtract(fromStar);
        Vector3 degLot = dir.clone().crossProduct(Vector3.RotAxis.Z_AXIS).normalize().multiply(lineBreadth);//.multiply(j == 0 ? 1 : -1);

        Vector3 vec00 = fromStar.clone().add(degLot);
        Vector3 vecV = degLot.clone().multiply(-2);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = vec00.clone().add(dir.clone().multiply(u)).add(vecV.clone().multiply(v));
            pos.drawPos(buf).color(starBr, starBr, starBr, Math.max(0, starBr)).tex(u, v).endVertex();
        }
    }

    @Override
    protected void mouseDragStart(double mouseX, double mouseY) {
        if (!canDraw()) {
            return;
        }
        if (this.currentDrawArea != null && !this.currentDrawArea.contains(mouseX, mouseY)) {
            this.clearDrawing();
        }
        if (this.currentDrawArea == null) {
            this.currentDrawArea = Iterables.getFirst(this.getContainingDrawAreas(mouseX, mouseY), null);
        }
        if (this.currentDrawArea == null) {
            this.clearDrawing();
            return;
        }

        this.dragStart = new Point((int) mouseX, (int) mouseY);
        this.dragEnd = new Point((int) mouseX, (int) mouseY);
    }

    @Override
    protected void mouseDragTick(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY, double mouseOffsetX, double mouseOffsetY) {
        if (!canDraw() || this.dragStart == null || this.currentDrawArea == null) {
            return;
        }

        if (!this.currentDrawArea.contains(mouseX, mouseY)) {
            this.clearDrawing();
            return;
        }

        this.dragEnd = new Point((int) mouseX, (int) mouseY);
    }

    @Override
    protected void mouseDragStop(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY) {
        if (!canDraw() || this.dragStart == null || this.currentDrawArea == null) {
            return;
        }

        if (!this.currentDrawArea.contains(mouseX, mouseY)) {
            this.clearDrawing();
            return;
        }

        this.dragEnd = new Point((int) mouseX, (int) mouseY);
        this.finishDrawingLine();
        this.stopCurrentDrawing();

        this.checkConstellationMatch();
    }

    protected void finishDrawingLine() {
        if (Math.abs(this.dragStart.getX() - this.dragEnd.getX()) <= 2 &&
                Math.abs(this.dragStart.getY() - this.dragEnd.getY()) <= 2) {
            return; //Rather a point than a line. probably not the users intention...
        }

        Point adjStart = new Point(this.dragStart.x - guiLeft, this.dragStart.y - guiTop);
        Point adjEnd = new Point(this.dragEnd.x - guiLeft, this.dragEnd.y - guiTop);
        DrawnLine l = new DrawnLine(adjStart, adjEnd);
        this.drawnLines.add(l);
    }

    protected boolean canDraw() {
        return !Minecraft.getInstance().mouseHelper.isMouseGrabbed() &&
                DayTimeHelper.isNight(Minecraft.getInstance().world) &&
                Minecraft.getInstance().world.getRainStrength(1.0F) <= 0.1F;
    }

    protected void clearDrawing() {
        this.currentDrawArea = null;
        this.drawnLines.clear();
        this.stopCurrentDrawing();
    }

    private void stopCurrentDrawing() {
        this.dragStart = null;
        this.dragEnd  = null;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void checkConstellationMatch() {
        if (this.currentDrawArea == null || this.currentDrawArea.cstDisplay.isEmpty()) {
            return;
        }
        PlayerProgress progress = ResearchHelper.getClientProgress();
        for (IConstellation cst : this.currentDrawArea.cstDisplay.keySet()) {
            ConstellationDisplayInformation info = this.currentDrawArea.cstDisplay.get(cst);
            if (progress.hasConstellationDiscovered(cst) || !progress.hasSeenConstellation(cst)) {
                continue;
            }
            if (cst.getStarConnections().size() != this.drawnLines.size()) {
                continue;
            }
            if (!cst.canDiscover(Minecraft.getInstance().player, progress)) {
                continue;
            }

            boolean didMatch = true;
            for (StarConnection cstConnection : cst.getStarConnections()) {
                Rectangle rctFrom = info.frameDrawInformation.get(cstConnection.from);
                Rectangle rctTo = info.frameDrawInformation.get(cstConnection.to);
                if (rctFrom == null || rctTo == null) {
                    didMatch = false;
                    break;
                }
                if (!hasMatchingDrawnLine(rctFrom, rctTo)) {
                    didMatch = false;
                    break;
                }
            }
            if (didMatch) {
                PacketChannel.CHANNEL.sendToServer(new PktDiscoverConstellation(cst));

                this.clearDrawing();
                return;
            }
        }
    }

    private boolean hasMatchingDrawnLine(Rectangle rctFrom, Rectangle rctTo) {
        for (DrawnLine line : this.drawnLines) {
            Point start = line.from;
            Point end = line.to;
            start = new Point(start.x + guiLeft, start.y + guiTop);
            end = new Point(end.x + guiLeft, end.y + guiTop);
            if ((rctFrom.contains(start) && rctTo.contains(end)) ||
                    (rctTo.contains(start) && rctFrom.contains(end))) {
                return true;
            }
        }
        return false;
    }

    protected boolean canObserverSeeSky(BlockPos pos, int xzWidth) {
        World world = Minecraft.getInstance().world;
        if (world == null) {
            return false;
        }
        for (int xx = -xzWidth; xx <= xzWidth; xx++) {
            for (int zz = -xzWidth; zz <= xzWidth; zz++) {
                BlockPos other = pos.add(xx, 0, zz);
                if (xx == 0 && zz == 0) {
                    continue;
                }
                if (!MiscUtils.canSeeSky(world, other, true, false)) {
                    return false;
                }
            }
        }
        return MiscUtils.canSeeSky(world, pos.up(), true, false);
    }

    public static class DrawArea {

        protected final Rectangle area;
        protected final Map<IConstellation, ConstellationDisplayInformation> cstDisplay = new HashMap<>();

        public DrawArea(Rectangle area) {
            this.area = area;
        }

        public void addConstellationToArea(IConstellation cst, Point drawPoint, float size) {
            this.addConstellationToArea(cst, new ConstellationDisplayInformation(drawPoint, size));
        }

        public void addConstellationToArea(IConstellation cst, ConstellationDisplayInformation info) {
            this.cstDisplay.put(cst, info);
        }

        public Map<IConstellation, ConstellationDisplayInformation> getDisplayMap() {
            return Collections.unmodifiableMap(this.cstDisplay);
        }

        public boolean contains(double mouseX, double mouseY) {
            return this.isVisible() && this.area.contains(mouseX, mouseY);
        }

        public boolean isVisible() {
            return true;
        }
    }

    public static class ConstellationDisplayInformation {

        private final Point renderPosition;
        private final float renderSize;

        private final Map<StarLocation, Rectangle> frameDrawInformation = new HashMap<>();

        protected ConstellationDisplayInformation(Point renderPosition, float renderSize) {
            this.renderPosition = renderPosition;
            this.renderSize = renderSize;
        }

        public Point getRenderPosition() {
            return renderPosition;
        }

        public float getRenderSize() {
            return renderSize;
        }

        public Map<StarLocation, Rectangle> getFrameDrawInformation() {
            return frameDrawInformation;
        }
    }

    private static class DrawnLine {

        private final Point from, to;

        private DrawnLine(Point from, Point to) {
            this.from = from;
            this.to = to;
        }

        public Point getFrom() {
            return from;
        }

        public Point getTo() {
            return to;
        }
    }
}
