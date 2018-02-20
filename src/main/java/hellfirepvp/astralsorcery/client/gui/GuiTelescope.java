/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.base.GuiTileBase;
import hellfirepvp.astralsorcery.client.sky.RenderAstralSkybox;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktDiscoverConstellation;
import hellfirepvp.astralsorcery.common.network.packet.client.PktRotateTelescope;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiTelescope
 * Created by HellFirePvP
 * Date: 08.05.2016 / 23:51
 */
public class GuiTelescope extends GuiTileBase<TileTelescope> {

    private static final BindableResource texArrow = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijarrow");
    private static final BindableResource textureGrid = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridtelescope");
    private static final BindableResource textureConnection = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "connectionperks");

    private final EntityPlayer owningPlayer;
    private final TileTelescope guiOwner;
    private TileTelescope.TelescopeRotation rotation;

    private Rectangle rectArrowCW = null, rectArrowCCW = null;

    private SkyConstellationDistribution currentInformation = null;

    public GuiTelescope(EntityPlayer player, TileTelescope e) {
        super(e, 280, 280);
        this.owningPlayer = player;
        this.guiOwner = e;
        this.rotation = e.getRotation();

        setupConstellations();
    }

    private void setupConstellations() {
        WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(guiOwner.getWorld());
        int lastTracked = handle == null ? 5 : handle.lastRecordedDay;
        Random r = new Random(guiOwner.getWorld().getSeed() * 31 + lastTracked * 31);

        currentInformation = new SkyConstellationDistribution();

        for (TileTelescope.TelescopeRotation rot : TileTelescope.TelescopeRotation.values()) {
            currentInformation.informationMap.put(rot, new RotationConstellationInformation());
        }
        if(handle != null) {
            List<IConstellation> constellations = handle.getActiveConstellations();
            List<IWeakConstellation> weakConstellations = new LinkedList<>();
            for (IConstellation c : constellations) {
                if(c instanceof IWeakConstellation && c.canDiscover(ResearchManager.clientProgress)) {
                    weakConstellations.add((IWeakConstellation) c);
                }
            }
            for (IWeakConstellation cst : weakConstellations) {
                Tuple<Point, TileTelescope.TelescopeRotation> foundPoint;
                int counter = 600;
                do {
                    counter--;
                    foundPoint = findEmptyPlace(r, counter <= 0);
                } while (foundPoint == null);
                currentInformation.informationMap.get(foundPoint.value).constellations.put(foundPoint.key, cst);
            }
        }
    }

    private Tuple<Point, TileTelescope.TelescopeRotation> findEmptyPlace(Random rand, boolean ignoreCollision) {
        TileTelescope.TelescopeRotation rot = TileTelescope.TelescopeRotation.values()[rand.nextInt(TileTelescope.TelescopeRotation.values().length)];
        RotationConstellationInformation info = currentInformation.informationMap.get(rot);
        int wh = ((int) SkyConstellationDistribution.constellationWH);
        int wdh = guiWidth  - 6 - wh;
        int hgt = guiHeight - 6 - wh;
        int rX = 6 + rand.nextInt(wdh);
        int rY = 6 + rand.nextInt(hgt);
        Rectangle constellationRect = new Rectangle(rX, rY, wh, wh);
        if(!ignoreCollision) {
            for (Point p : info.constellations.keySet()) {
                Rectangle otherRect = new Rectangle(p.x, p.y, wh, wh);
                if (otherRect.intersects(constellationRect)) {
                    return null;
                }
            }
        }
        return new Tuple<>(new Point(rX, rY), rot);
    }

    public void handleRotationChange(boolean isClockwise) {
        rotation = isClockwise ? rotation.nextClockWise() : rotation.nextCounterClockWise();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        drawWHRect(textureGrid);
        TextureHelper.refreshTextureBindState();

        zLevel -= 5;
        drawCellsWithEffects(partialTicks);
        zLevel += 5;

        drawRotationArrows(partialTicks);

        TextureHelper.refreshTextureBindState();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void drawRotationArrows(float partialTicks) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        Point mouse = getCurrentMousePoint();
        rectArrowCW = null;
        rectArrowCCW = null;

        int width = 30;
        int height = 15;
        rectArrowCCW = new Rectangle(guiLeft - 40, guiTop + (guiHeight / 2), width, height);
        GL11.glPushMatrix();
        GL11.glTranslated(rectArrowCCW.getX() + (width / 2), rectArrowCCW.getY() + (height / 2), 0);
        float uFrom = 0F, vFrom = 0.5F;
        if(rectArrowCCW.contains(mouse)) {
            uFrom = 0.5F;
            GL11.glScaled(1.1, 1.1, 1.1);
        } else {
            double t = ClientScheduler.getClientTick() + partialTicks;
            float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            GL11.glScaled(sin, sin, sin);
        }
        GL11.glColor4f(1F, 1F, 1F, 0.8F);
        GL11.glTranslated(-(width / 2), -(height / 2), 0);
        texArrow.bind();
        drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
        GL11.glPopMatrix();

        rectArrowCW = new Rectangle(guiLeft + guiWidth + 10, guiTop + (guiHeight / 2), width, height);
        GL11.glPushMatrix();
        GL11.glTranslated(rectArrowCW.getX() + (width / 2), rectArrowCW.getY() + (height / 2), 0);
        uFrom = 0F;
        vFrom = 0F;
        if(rectArrowCW.contains(mouse)) {
            uFrom = 0.5F;
            GL11.glScaled(1.1, 1.1, 1.1);
        } else {
            double t = ClientScheduler.getClientTick() + partialTicks;
            float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            GL11.glScaled(sin, sin, sin);
        }
        GL11.glColor4f(1F, 1F, 1F, 0.8F);
        GL11.glTranslated(-(width / 2), -(height / 2), 0);
        texArrow.bind();
        drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void drawCellsWithEffects(float partialTicks) {
        WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(guiOwner.getWorld());
        int lastTracked = handle == null ? 5 : handle.lastRecordedDay;
        Random r = new Random(guiOwner.getWorld().getSeed() * 31 + lastTracked * 31 + rotation.ordinal());
        World world = Minecraft.getMinecraft().world;
        boolean canSeeSky = canTelescopeSeeSky(world);

        /*if(handle != null) {
            LinkedList<IConstellation> active = handle.getSortedActiveConstellations();
            PlayerProgress prog = ResearchManager.clientProgress;
            Iterator<IConstellation> iterator = active.iterator();
            while (iterator.hasNext()) {
                IConstellation c = iterator.next();
                if(!(c instanceof IWeakConstellation)) {
                    iterator.remove();
                    continue;
                }
                if(!c.canDiscover(prog)) {
                    iterator.remove();
                    continue;
                }
                if(handle.getCurrentDistribution((IWeakConstellation) c, (f) -> f) <= 0.5F) {
                    iterator.remove();
                }
            }
            if(active.size() <= 8) {
                active.toArray(constellations);
            } else {
                active.subList(0, 8).toArray(constellations);
            }
        }*/

        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();

        drawGridBackground(partialTicks, canSeeSky);

        if (handle != null && canSeeSky) {
            zLevel += 1;
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glEnable(GL11.GL_BLEND);
            Blending.DEFAULT.apply();

            RenderAstralSkybox.TEX_STAR_1.bind();
            float starSize = 2.5F;
            for (int i = 0; i < 72 + r.nextInt(144); i++) {
                float innerOffsetX = starSize + r.nextInt(MathHelper.floor(guiWidth  - starSize));
                float innerOffsetY = starSize + r.nextInt(MathHelper.floor(guiHeight - starSize));
                float brightness = 0.3F + (RenderConstellation.stdFlicker(ClientScheduler.getClientTick(), partialTicks, 10 + r.nextInt(20))) * 0.6F;
                brightness *= Minecraft.getMinecraft().world.getStarBrightness(1.0F) * 2;
                GL11.glColor4f(brightness, brightness, brightness, brightness);
                drawRectDetailed(guiLeft + innerOffsetX - starSize, guiTop + innerOffsetY - starSize, starSize * 2, starSize * 2);
                GL11.glColor4f(1, 1, 1, 1);
            }
            zLevel -= 1;

            RotationConstellationInformation info = currentInformation.informationMap.get(rotation);
            if (info != null) {
                currentInformation.informationMap.get(rotation).informations.clear();
                for (Map.Entry<Point, IConstellation> entry : info.constellations.entrySet()) {

                    float widthHeight = SkyConstellationDistribution.constellationWH;

                    Point offset = entry.getKey();

                    Map<StarLocation, Rectangle> rectangles = RenderConstellation.renderConstellationIntoGUI(
                            entry.getValue(),
                            offset.x + guiLeft,
                            offset.y + guiTop,
                            zLevel,
                            ((int) widthHeight),
                            ((int) widthHeight),
                            2.5,
                            new RenderConstellation.BrightnessFunction() {
                                @Override
                                public float getBrightness() {
                                    return RenderConstellation.conCFlicker(ClientScheduler.getClientTick(), partialTicks, 5 + r.nextInt(15));
                                }
                            },
                            ResearchManager.clientProgress.hasConstellationDiscovered(entry.getValue().getUnlocalizedName()),
                            true
                    );

                    currentInformation.informationMap.get(rotation).informations.add(new ConstellationInformation(rectangles, entry.getValue()));
                }
            }

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopAttrib();
        }

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();

        zLevel += 2;
        drawDrawnLines(r, partialTicks);
        zLevel -= 2;

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopAttrib();
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
                return RenderConstellation.conCFlicker(ClientScheduler.getClientTick(), pTicks, 5 + r.nextInt(15));
            }
        };

        textureConnection.bind();

        for (int j = 0; j < 2; j++) {
            for (Line l : drawnLines) {
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
        BufferBuilder vb = tes.getBuffer();

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
    private void drawGridBackground(float partialTicks, boolean canSeeSky) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        Blending.PREALPHA.apply();
        World renderWorld = Minecraft.getMinecraft().world;
        int rgbFrom, rgbTo;
        if (canSeeSky) {
            float starBr = renderWorld.getStarBrightness(partialTicks) * 2;
            float rain = renderWorld.getRainStrength(partialTicks);
            rgbFrom = RenderingUtils.clampToColorWithMultiplier(calcRGBFromWithRain(starBr, rain), 1F).getRGB();
            rgbTo   = RenderingUtils.clampToColorWithMultiplier(calcRGBToWithRain  (starBr, rain), 1F).getRGB();
        } else {
            rgbFrom = 0x000000;
            rgbTo =   0x000000;
        }
        int alphaMask = 0xFF000000; //100% opacity.
        RenderingUtils.drawGradientRect(guiLeft + 4, guiTop + 4, zLevel, guiLeft + guiWidth - 4, guiTop + guiHeight - 4, new Color(alphaMask | rgbFrom), new Color(alphaMask | rgbTo));
        Blending.DEFAULT.apply();
        GL11.glPopAttrib();
    }

    private boolean canTelescopeSeeSky(World renderWorld) {
        BlockPos pos = guiOwner.getPos();
        /*int height = 1;
        IBlockState up = renderWorld.getBlockState(pos.up());
        if(up.getBlock().equals(BlocksAS.blockStructural) && up.getValue(BlockStructural.BLOCK_TYPE).equals(BlockStructural.BlockType.TELESCOPE_STRUCT)) {
            height += 1;
        }*/

        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                BlockPos other = pos.add(xx, 0, zz);
                if (!renderWorld.canSeeSky(other)) {
                    return false;
                }
            }
        }
        return renderWorld.canSeeSky(pos.up());
    }

    private static final float THRESHOLD_TO_START = 0.8F;
    private static final float THRESHOLD_TO_SHIFT_BLUEGRAD = 0.5F;
    private static final float THRESHOLD_TO_MAX_BLUEGRAD = 0.2F;

    private int calcRGBToWithRain(float starBr, float rain) {
        int to = calcRGBTo(starBr);
        if(starBr <= THRESHOLD_TO_START) {
            float starMul = 1F;
            if(starBr > THRESHOLD_TO_SHIFT_BLUEGRAD) {
                starMul = 1F - (starBr - THRESHOLD_TO_SHIFT_BLUEGRAD) / (THRESHOLD_TO_START - THRESHOLD_TO_SHIFT_BLUEGRAD);
            }
            float interpDeg = starMul * rain;
            Color safeTo = RenderingUtils.clampToColor(to);
            Vector3 vTo = new Vector3(safeTo.getRed(), safeTo.getGreen(), safeTo.getBlue()).divide(255D);
            Vector3 rainC = new Vector3(102, 114, 137).divide(255D);
            Vector3 interpVec = vTo.copyInterpolateWith(rainC, interpDeg);
            Color newColor = RenderingUtils.clampToColor((int) (interpVec.getX() * 255), (int) (interpVec.getY() * 255), (int) (interpVec.getZ() * 255));
            to = newColor.getRGB();
        }
        return RenderingUtils.clampToColor(to).getRGB();
    }

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

    private int calcRGBFromWithRain(float starBr, float rain) {
        int to = calcRGBFrom(starBr);
        if(starBr <= THRESHOLD_FROM_START) {
            float starMul = 1F;
            if(starBr > THRESHOLD_FROM_SHIFT_BLUEGRAD) {
                starMul = 1F - (starBr - THRESHOLD_FROM_SHIFT_BLUEGRAD) / (THRESHOLD_FROM_START - THRESHOLD_FROM_SHIFT_BLUEGRAD);
            }
            float interpDeg = starMul * rain;
            Color safeTo = RenderingUtils.clampToColor(to);
            Vector3 vTo = new Vector3(safeTo.getRed(), safeTo.getGreen(), safeTo.getBlue()).divide(255D);
            Vector3 rainC = new Vector3(102, 114, 137).divide(255D);
            Vector3 interpVec = vTo.copyInterpolateWith(rainC, interpDeg);
            Color newColor = RenderingUtils.clampToColor((int) (interpVec.getX() * 255), (int) (interpVec.getY() * 255), (int) (interpVec.getZ() * 255));
            to = newColor.getRGB();
        }
        return RenderingUtils.clampToColor(to).getRGB();
    }

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
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            tryStartDrawing(mouseX, mouseY);
        }

        if(mouseX <= guiLeft || mouseX >= guiLeft + guiWidth ||
                mouseY <= guiTop || mouseY >= guiTop + guiHeight) {
            clearLines();
            abortDrawing();
        }

        Point p = new Point(mouseX, mouseY);
        if(rectArrowCW != null && rectArrowCW.contains(p)) {
            PktRotateTelescope pkt = new PktRotateTelescope(true, guiOwner.getWorld().provider.getDimension(), guiOwner.getPos());
            PacketChannel.CHANNEL.sendToServer(pkt);
            return;
        }
        if(rectArrowCCW != null && rectArrowCCW.contains(p)) {
            PktRotateTelescope pkt = new PktRotateTelescope(false, guiOwner.getWorld().provider.getDimension(), guiOwner.getPos());
            PacketChannel.CHANNEL.sendToServer(pkt);
        }

    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (clickedMouseButton == 0) {
            informMovement(mouseX, mouseY);
        }

        if(mouseX <= guiLeft || mouseX >= guiLeft + guiWidth ||
                mouseY <= guiTop || mouseY >= guiTop + guiHeight) {
            clearLines();
            abortDrawing();
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            informRelease(mouseX, mouseY);
        }

        if(mouseX <= guiLeft || mouseX >= guiLeft + guiWidth ||
                mouseY <= guiTop || mouseY >= guiTop + guiHeight) {
            clearLines();
            abortDrawing();
        }
    }

    private LinkedList<Line> drawnLines = new LinkedList<>();
    private Point start, end;

    private void tryStartDrawing(int mouseX, int mouseY) {
        if (!canStartDrawing()) return;

        start = new Point(mouseX, mouseY);
        end = new Point(mouseX, mouseY);
        /*int cell = findCurrentCell(mouseX, mouseY);
        if (currentLinesCell == -1) {
            if (cell != -1) {
                currentDrawCell = cell;
                currentLinesCell = cell;
                start = new Point(mouseX, mouseY);
                end = new Point(mouseX, mouseY); //We want 2 different objects here though.
            }
        } else {
            if (cell != currentLinesCell) {
                abortDrawing();
                clearLines();

                start = new Point(mouseX, mouseY);
                end = new Point(mouseX, mouseY); //We want 2 different objects here though.
            } else {
                start = new Point(mouseX, mouseY);
                end = new Point(mouseX, mouseY); //We want 2 different objects here though.
            }
        }*/
    }

    private boolean canStartDrawing() {
        return Minecraft.getMinecraft().world.getStarBrightness(1.0F) >= 0.35F;
    }

    private void clearLines() {
        drawnLines.clear();
    }

    /*private int findCurrentCell(int x, int y) {
        CellRenderInformation current = currentInformation;
        for (Rectangle r : current.cells.keySet()) {
            if (r.contains(x, y)) {
                return current.cells.get(r);
            }
        }
        return -1;
    }*/

    private void informMovement(int mouseX, int mouseY) {
        if (!canStartDrawing()) return;

        end = new Point(mouseX, mouseY);
    }

    private void informRelease(int mouseX, int mouseY) {
        if (!canStartDrawing()) return;

        if(start == null) return;

        end = new Point(mouseX, mouseY);
        pushDrawnLine(start, end);
        abortDrawing();

        checkConstellation(drawnLines);
    }

    private void checkConstellation(List<Line> drawnLines) {
        RotationConstellationInformation infos = currentInformation.informationMap.get(rotation);
        if(infos == null) return;
        List<ConstellationInformation> renderInfos = infos.informations;
        if(renderInfos.isEmpty()) return;

        lblInfos: for (ConstellationInformation info : renderInfos) {
            IConstellation c = info.constellation;
            if (c == null || ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName())) continue;
            PlayerProgress client = ResearchManager.clientProgress;
            if (client == null) return;

            boolean has = false;
            for (String strConstellation : client.getSeenConstellations()) {
                IConstellation ce = ConstellationRegistry.getConstellationByName(strConstellation);
                if(ce != null && ce.equals(c)) {
                    has = true;
                    break;
                }
            }

            if(!has) continue;

            List<StarConnection> sc = c.getStarConnections();
            if (sc.size() != drawnLines.size()) continue; //Can't match otherwise anyway.
            if (!c.canDiscover(ResearchManager.clientProgress)) continue;

            Map<StarLocation, Rectangle> stars = info.starRectangles;

            for (StarConnection connection : sc) {
                Rectangle fromRect = stars.get(connection.from);
                if (fromRect == null) {
                    AstralSorcery.log.info("[AstralSorcery] Could not check constellation of telescope drawing - starLocation is missing?");
                    continue lblInfos;
                }
                Rectangle toRect = stars.get(connection.to);
                if (toRect == null) {
                    AstralSorcery.log.info("[AstralSorcery] Could not check constellation of telescope drawing - starLocation is missing?");
                    continue lblInfos;
                }
                if (!containsMatch(drawnLines, fromRect, toRect)) {
                    continue lblInfos;
                }
            }

            //We found a match. horray.
            PacketChannel.CHANNEL.sendToServer(new PktDiscoverConstellation(c.getUnlocalizedName()));
            clearLines();
            abortDrawing();
            return;
        }

    }

    private boolean containsMatch(List<Line> drawnLines, Rectangle r1, Rectangle r2) {
        for (Line l : drawnLines) {
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
        Line l = new Line(adjStart, adjEnd);
        this.drawnLines.addLast(l);
    }

    private void abortDrawing() {
        start = null;
        end = null;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static class SkyConstellationDistribution {

        private static final float constellationWH = 150;
        private final Map<TileTelescope.TelescopeRotation, RotationConstellationInformation> informationMap = new HashMap<>();

    }

    public static class RotationConstellationInformation {

        private final List<ConstellationInformation> informations = new LinkedList<>();
        private final Map<Point, IConstellation> constellations = new HashMap<>();

    }

    public static class ConstellationInformation {

        private final Map<StarLocation, Rectangle> starRectangles;
        private final IConstellation constellation;

        public ConstellationInformation(Map<StarLocation, Rectangle> starRectangles, IConstellation c) {
            this.starRectangles = starRectangles;
            this.constellation = c;
        }
    }

    public static class Line {

        public final Point start, end;

        public Line(Point start, Point end) {
            this.start = start;
            this.end = end;
        }
    }

}
