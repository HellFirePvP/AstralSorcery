package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.sky.RenderAstralSkybox;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.ItemConstellationPaper;
import hellfirepvp.astralsorcery.common.item.ItemJournal;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktDiscoverConstellation;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiHandTelescope
 * Created by HellFirePvP
 * Date: 28.12.2016 / 12:18
 */
public class GuiHandTelescope extends GuiWHScreen {

    private static final BindableResource textureGrid = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridHandTelescope");
    private static final BindableResource textureConnection = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "connectionPerks");
    private static final Rectangle rectDrawing = new Rectangle(6, 6, 120, 120);

    private IMajorConstellation drawnConstellation = null;
    private Map<StarLocation, Rectangle> drawnStars = null;

    public GuiHandTelescope() {
        super(144, 144);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        drawWHRect(textureGrid);
        TextureHelper.refreshTextureBindState();

        World w = Minecraft.getMinecraft().world;
        float pitch = Minecraft.getMinecraft().player.rotationPitch;
        float transparency = 0F;
        if(pitch < -60F) {
            transparency = 1F;
        } else if(pitch < -10F) {
            transparency = (Math.abs(pitch) - 10F) / 50F;
        }
        boolean canSeeSky = canTelescopeSeeSky(w);

        zLevel -= 5;
        drawCellWithEffects(partialTicks, canSeeSky, transparency);
        zLevel += 5;

        TextureHelper.refreshTextureBindState();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void drawCellWithEffects(float partialTicks, boolean canSeeSky, float transparency) {
        WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(Minecraft.getMinecraft().world);
        int lastTracked = handle == null ? 5 : handle.lastRecordedDay;
        Random r = new Random(Minecraft.getMinecraft().world.getSeed() * 31 + lastTracked * 31);

        drawnConstellation = null;
        drawnStars = null;

        IMajorConstellation top = null;
        if(handle != null) {
            LinkedList<IConstellation> active = handle.getSortedActiveConstellations();
            Iterator<IConstellation> iterator = active.iterator();
            while (iterator.hasNext()) {
                IConstellation c = iterator.next();
                if(!(c instanceof IMajorConstellation)) {
                    iterator.remove();
                }
            }
            if(!active.isEmpty()) {
                top = (IMajorConstellation) active.getFirst();
            }
        }

        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();

        drawGridBackground(partialTicks, canSeeSky, transparency);

        if (canSeeSky) {
            int offsetX = guiLeft;
            int offsetZ = guiTop ;
            zLevel += 1;
            Optional<Map<StarLocation, Rectangle>> stars = drawCellEffect(r, top, offsetX, offsetZ, getGuiWidth(), getGuiHeight(), partialTicks, transparency);
            zLevel -= 1;

            if(stars.isPresent()) {
                drawnConstellation = top;
                drawnStars = stars.get();
            }
        } else {
            abortDrawing();
            clearLines();
        }

        zLevel += 2;
        drawDrawnLines(r, partialTicks);
        zLevel -= 2;

    }

    private void drawDrawnLines(final Random r, final float pTicks) {
        if (!canStartDrawing()) {
            clearLines();
            abortDrawing();
            return;
        }

        float linebreadth = 2F;
        RenderConstellation.BrightnessFunction func = new RenderConstellation.BrightnessFunction() {
            @Override
            public float getBrightness() {
                return RenderConstellation.conCFlicker(Minecraft.getMinecraft().world.getWorldTime(), pTicks, 5 + r.nextInt(15));
            }
        };

        textureConnection.bind();

        for (int j = 0; j < 2; j++) {
            for (GuiTelescope.Line l : drawnLines) {
                drawLine(l.start, l.end, func, linebreadth, true);
            }

            if (start != null && end != null) {
                Point adjStart = new Point(start.x - guiLeft, start.y - guiTop);
                Point adjEnd = new Point(end.x - guiLeft, end.y - guiTop);
                drawLine(adjStart, adjEnd, func, linebreadth, false);
            }
        }
    }

    private void drawLine(Point start, Point end, RenderConstellation.BrightnessFunction func, float linebreadth, boolean applyFunc) {
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();

        float brightness;
        if (applyFunc) {
            brightness = func.getBrightness();
        } else {
            brightness = 1F;
        }
        float starBr = Minecraft.getMinecraft().world.getStarBrightness(1.0F);
        if (starBr <= 0.0F) {
            return;
        }
        brightness *= (starBr * 2);
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        GL11.glColor4f(brightness, brightness, brightness, brightness < 0 ? 0 : brightness);

        Vector3 fromStar = new Vector3(guiLeft + start.getX(), guiTop + start.getY(), zLevel);
        Vector3 toStar = new Vector3(guiLeft + end.getX(), guiTop + end.getY(), zLevel);

        Vector3 dir = toStar.clone().subtract(fromStar);
        Vector3 degLot = dir.clone().crossProduct(new Vector3(0, 0, 1)).normalize().multiply(linebreadth);//.multiply(j == 0 ? 1 : -1);

        Vector3 vec00 = fromStar.clone().add(degLot);
        Vector3 vecV = degLot.clone().multiply(-2);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = vec00.clone().add(dir.clone().multiply(u)).add(vecV.clone().multiply(v));
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(u, v).endVertex();
        }

        tes.draw();
    }

    private Optional<Map<StarLocation, Rectangle>> drawCellEffect(Random rand, IConstellation c, int offsetX, int offsetY, int width, int height, float partialTicks, float transparency) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();

        RenderAstralSkybox.TEX_STAR_1.bind();
        int starSize = 2;
        for (int i = 0; i < 17 + rand.nextInt(18); i++) {
            int innerOffsetX = starSize + rand.nextInt(width - starSize);
            int innerOffsetY = starSize + rand.nextInt(height - starSize);
            GL11.glPushMatrix();
            float brightness = RenderConstellation.stdFlicker(Minecraft.getMinecraft().world.getWorldTime(), partialTicks, 10 + rand.nextInt(20));
            brightness *= Minecraft.getMinecraft().world.getStarBrightness(1.0F) * 2 * transparency;
            GL11.glColor4f(brightness, brightness, brightness, brightness);
            drawRect(offsetX + innerOffsetX - starSize, offsetY + innerOffsetY - starSize, starSize * 2, starSize * 2);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glPopMatrix();
        }

        Map<StarLocation, Rectangle> rectangles = null;
        if (c != null) {
            zLevel += 1;

            int wPart = ((int) (((float) width) * 0.1F));
            int hPart = ((int) (((float) height) * 0.1F));

            rectangles = RenderConstellation.renderConstellationIntoGUI(
                    c,
                    offsetX + wPart,
                    offsetY + hPart,
                    zLevel,
                    width - (((int) (wPart * 1.5F))),
                    height - (((int) (hPart * 1.5F))),
                    2,
                    new RenderConstellation.BrightnessFunction() {
                        @Override
                        public float getBrightness() {
                            return RenderConstellation.conCFlicker(Minecraft.getMinecraft().world.getWorldTime(), partialTicks, 5 + rand.nextInt(15)) * transparency;
                        }
                    },
                    ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName()),
                    true
            );

            zLevel -= 1;
        }

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopAttrib();
        return Optional.ofNullable(rectangles);
    }

    private void drawGridBackground(float partialTicks, boolean canSeeSky, float angleTransparency) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        World renderWorld = Minecraft.getMinecraft().world;
        int rgbFrom, rgbTo;
        if (canSeeSky && angleTransparency > 1.0E-4) {
            float starBr = renderWorld.getStarBrightness(partialTicks) * 2;
            rgbFrom = calcRGBFrom(starBr);
            rgbTo =   calcRGBTo  (starBr);
            Color from = new Color(rgbFrom);
            Color to = new Color(rgbTo);
            rgbFrom = new Color(
                    MathHelper.clamp((int) (((float) from.getRed())   * angleTransparency), 0, 255),
                    MathHelper.clamp((int) (((float) from.getGreen()) * angleTransparency), 0, 255),
                    MathHelper.clamp((int) (((float) from.getBlue())  * angleTransparency), 0, 255))
                    .getRGB();
            rgbTo = new Color(
                    MathHelper.clamp((int) (((float) to.getRed())     * angleTransparency), 0, 255),
                    MathHelper.clamp((int) (((float) to.getGreen())   * angleTransparency), 0, 255),
                    MathHelper.clamp((int) (((float) to.getBlue())    * angleTransparency), 0, 255))
                    .getRGB();
        } else {
            rgbFrom = 0x000000;
            rgbTo =   0x000000;
        }
        int alphaMask = 0xFF000000; //100% opacity.
        RenderingUtils.drawGradientRect(guiLeft + 4, guiTop + 4, zLevel, guiLeft + guiWidth - 4, guiTop + guiHeight - 4, new Color(alphaMask | rgbFrom), new Color(alphaMask | rgbTo));
        GL11.glPopAttrib();
    }

    private boolean canTelescopeSeeSky(World renderWorld) {
        BlockPos pos = Minecraft.getMinecraft().player.getPosition();
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                BlockPos other = pos.add(xx, 0, zz);
                if (!renderWorld.canSeeSky(other)) {
                    return false;
                }
            }
        }
        return renderWorld.canSeeSky(pos);
    }

    private static final float THRESHOLD_TO_START = 0.8F;
    private static final float THRESHOLD_TO_SHIFT_BLUEGRAD = 0.5F;
    private static final float THRESHOLD_TO_MAX_BLUEGRAD = 0.2F;

    private int calcRGBTo(float starBr) {
        if (starBr >= THRESHOLD_TO_START) { //Blue ranges from 0 (1.0F starBr) to 170 (0.7F starBr)
            return 0; //Black.
        } else if (starBr >= THRESHOLD_TO_SHIFT_BLUEGRAD) { //Blue ranges from 170/AA (0.7F) to 255 (0.4F), green from 0 (0.7F) to 85(0.4F)
            float partSize = (THRESHOLD_TO_START - THRESHOLD_TO_SHIFT_BLUEGRAD);
            float perc = 1F - (starBr - THRESHOLD_TO_SHIFT_BLUEGRAD) / partSize;
            return (int) (perc * 170F);
        } else if (starBr >= THRESHOLD_TO_MAX_BLUEGRAD) {
            float partSize = (THRESHOLD_TO_SHIFT_BLUEGRAD - THRESHOLD_TO_MAX_BLUEGRAD);
            float perc = 1F - (starBr - THRESHOLD_TO_MAX_BLUEGRAD) / partSize;
            int green = (int) (perc * 85F);
            int blue = green + 0xAA; //LUL
            return (green << 8) | blue;
        } else {
            float partSize = (THRESHOLD_TO_MAX_BLUEGRAD - 0.0F); //Blue is 255, green from 85 (0.4F) to 175 (0.0F)
            float perc = 1F - (starBr - 0) / partSize;
            int green = 85 + ((int) (perc * 90));
            int red = (int) (perc * 140);
            return (red << 16) | (green << 8) | 0xFF;
        }
    }

    private static final float THRESHOLD_FROM_START = 1.0F;
    private static final float THRESHOLD_FROM_SHIFT_BLUEGRAD = 0.6F;
    private static final float THRESHOLD_FROM_MAX_BLUEGRAD = 0.3F;

    private int calcRGBFrom(float starBr) {
        if (starBr >= THRESHOLD_FROM_START) { //Blue ranges from 0 (1.0F starBr) to 170 (0.7F starBr)
            return 0; //Black.
        } else if (starBr >= THRESHOLD_FROM_SHIFT_BLUEGRAD) { //Blue ranges from 170/AA (0.7F) to 255 (0.4F), green from 0 (0.7F) to 85(0.4F)
            float partSize = (THRESHOLD_FROM_START - THRESHOLD_FROM_SHIFT_BLUEGRAD);
            float perc = 1F - (starBr - THRESHOLD_FROM_SHIFT_BLUEGRAD) / partSize;
            return (int) (perc * 170F);
        } else if (starBr >= THRESHOLD_FROM_MAX_BLUEGRAD) {
            float partSize = (THRESHOLD_FROM_SHIFT_BLUEGRAD - THRESHOLD_FROM_MAX_BLUEGRAD);
            float perc = 1F - (starBr - THRESHOLD_FROM_MAX_BLUEGRAD) / partSize;
            int green = (int) (perc * 85F);
            int blue = green + 0xAA; //LUL
            return (green << 8) | blue;
        } else {
            float partSize = (THRESHOLD_FROM_MAX_BLUEGRAD - 0.0F); //Blue is 255, green from 85 (0.4F) to 175 (0.0F)
            float perc = 1F - (starBr - 0) / partSize;
            int green = 85 + ((int) (perc * 90));
            int red = (int) (perc * 140);
            return (red << 16) | (green << 8) | 0xFF;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            tryStartDrawing(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (clickedMouseButton == 0) {
            informMovement(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            informRelease(mouseX, mouseY);
        }
    }

    private LinkedList<GuiTelescope.Line> drawnLines = new LinkedList<>();
    private Point start, end;

    private void tryStartDrawing(int mouseX, int mouseY) {
        if (!canStartDrawing()) return;

        if(isInDrawingCell(mouseX, mouseY)) {
            start = new Point(mouseX, mouseY);
            end = new Point(mouseX, mouseY);
        } else {
            abortDrawing();
            clearLines();
        }
    }

    private boolean canStartDrawing() {
        return Minecraft.getMinecraft().world.getStarBrightness(1.0F) >= 0.35F &&
                Minecraft.getMinecraft().player.rotationPitch <= -45F;
    }

    private void clearLines() {
        drawnLines.clear();
    }

    private boolean isInDrawingCell(int x, int y) {
        return rectDrawing.contains(x - guiLeft, y - guiTop);
    }

    private void informMovement(int mouseX, int mouseY) {
        if (!isInDrawingCell(mouseX, mouseY)) {
            abortDrawing();
            clearLines();
        } else {
            end = new Point(mouseX, mouseY);
        }
    }

    private void informRelease(int mouseX, int mouseY) {
        if (!isInDrawingCell(mouseX, mouseY)) {
            abortDrawing();
            clearLines();
        } else {
            if(start != null) {
                end = new Point(mouseX, mouseY);
                pushDrawnLine(start, end);
            } else {
                start = null;
                end = null;
            }
            abortDrawing();

            checkConstellation(drawnLines);
        }
    }

    private void checkConstellation(List<GuiTelescope.Line> drawnLines) {
        IConstellation c = drawnConstellation;
        if (c == null || ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName())) return;

        boolean has = false;
        IItemHandler handle = Minecraft.getMinecraft().player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        List<ItemStack> papers = ItemUtils.scanInventoryFor(handle, ItemsAS.constellationPaper);
        for (ItemStack stack : papers) {
            IConstellation con = ItemConstellationPaper.getConstellation(stack);
            if(con.equals(c)) {
                has = true;
                break;
            }
        }
        if(!has) {
            List<ItemStack> journals = ItemUtils.scanInventoryFor(handle, ItemsAS.journal);
            lblJournals:
            for (ItemStack stack : journals) {
                for (IConstellation con : ItemJournal.getStoredConstellations(stack)) {
                    if(con.equals(c)) {
                        has = true;
                        break lblJournals;
                    }
                }
            }
        }

        if(!has) return;

        List<StarConnection> sc = c.getStarConnections();
        if (sc.size() != drawnLines.size()) return; //Can't match otherwise anyway.

        for (StarConnection connection : sc) {
            Rectangle fromRect = drawnStars.get(connection.from);
            if (fromRect == null) {
                AstralSorcery.log.info("Could not check constellation of telescope drawing - starLocation is missing?");
                return;
            }
            Rectangle toRect = drawnStars.get(connection.to);
            if (toRect == null) {
                AstralSorcery.log.info("Could not check constellation of telescope drawing - starLocation is missing?");
                return;
            }
            if (!containsMatch(drawnLines, fromRect, toRect)) {
                return;
            }
        }

        //We found a match. horray.
        PacketChannel.CHANNEL.sendToServer(new PktDiscoverConstellation(c.getUnlocalizedName()));
        clearLines();
        abortDrawing();
    }

    private boolean containsMatch(List<GuiTelescope.Line> drawnLines, Rectangle r1, Rectangle r2) {
        for (GuiTelescope.Line l : drawnLines) {
            Point start = l.start;
            Point end = l.end;
            start = new Point(start.x + guiLeft, start.y + guiTop);
            end = new Point(end.x + guiLeft, end.y + guiTop);
            if ((r1.contains(start) && r2.contains(end)) ||
                    (r2.contains(start) && r1.contains(end))) {
                return true;
            }
        }
        return false;
    }

    private void pushDrawnLine(Point start, Point end) {
        if (Math.abs(start.getX() - end.getX()) <= 2 &&
                Math.abs(start.getY() - end.getY()) <= 2) {
            return; //Rather a point than a line. probably not the users intention...
        }
        Point adjStart = new Point(start.x - guiLeft, start.y - guiTop);
        Point adjEnd = new Point(end.x - guiLeft, end.y - guiTop);
        GuiTelescope.Line l = new GuiTelescope.Line(adjStart, adjEnd);
        this.drawnLines.addLast(l);
    }

    private void abortDrawing() {
        start = null;
        end = null;
    }

}
