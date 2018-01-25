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
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktDiscoverConstellation;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
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

    private static final Random random = new Random();

    private static final BindableResource textureGrid = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridhandtelescope");
    private static final BindableResource textureConnection = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "connectionperks");
    private static final Rectangle rectDrawing = new Rectangle(6, 6, 210, 210);

    private IMajorConstellation drawnConstellation = null;
    private Map<StarLocation, Rectangle> drawnStars = null;

    private static final int randomStars = 40;
    private List<StarPosition> usedStars = new ArrayList<>(randomStars);

    private IMajorConstellation topFound = null;
    private float selectedYaw = 0, selectedPitch = 0;

    private boolean grabCursor = false;

    public GuiHandTelescope() {
        super(216, 216);

        Optional<Long> currSeed = ConstellationSkyHandler.getInstance().getSeedIfPresent(Minecraft.getMinecraft().world);
        if (currSeed.isPresent()) {
            setupInitialStars(currSeed.get());
        }
    }

    private void setupInitialStars(long seed) {
        int offsetX = 6, offsetY = 6;
        int width = guiWidth - 6, height = guiHeight - 6;
        Random rand = new Random(seed);

        int day = (int) (Minecraft.getMinecraft().world.getWorldTime() / 24000);
        for (int i = 0; i < day; i++) {
            rand.nextLong(); //Flush
        }

        WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(Minecraft.getMinecraft().world);
        if (handle != null) {
            IMajorConstellation bestGuess = (IMajorConstellation) handle.getHighestDistributionConstellation(rand, (c) -> c instanceof IMajorConstellation);
            if (bestGuess != null && handle.getCurrentDistribution(bestGuess, (f) -> 1F) >= 0.8F &&
                    bestGuess.canDiscover(ResearchManager.clientProgress)) {
                topFound = bestGuess;
                selectedYaw = (rand.nextFloat() * 360F) - 180F;
                selectedPitch = -90F + rand.nextFloat() * 25F;
            }
        }

        for (int i = 0; i < randomStars; i++) {
            usedStars.add(new StarPosition(offsetX + rand.nextFloat() * width, offsetY + rand.nextFloat() * height));
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        mc.setIngameFocus();

        if (Minecraft.IS_RUNNING_ON_MAC) {
            Mouse.setGrabbed(false);
            Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2 - 20);
            Mouse.setGrabbed(true);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        mc.mouseHelper.grabMouseCursor();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        drawWHRect(textureGrid);
        TextureHelper.refreshTextureBindState();

        handleMouseMovement(partialTicks);

        World w = Minecraft.getMinecraft().world;
        float pitch = Minecraft.getMinecraft().player.rotationPitch;
        float transparency = 0F;
        if (pitch < -60F) {
            transparency = 1F;
        } else if (pitch < -10F) {
            transparency = (Math.abs(pitch) - 10F) / 50F;
            if (ConstellationSkyHandler.getInstance().isNight(w)) {
                transparency *= transparency;
            }
        }
        boolean canSeeSky = canTelescopeSeeSky(w);

        if (usedStars.isEmpty()) {
            Optional<Long> currSeed = ConstellationSkyHandler.getInstance().getSeedIfPresent(Minecraft.getMinecraft().world);
            if (currSeed.isPresent()) {
                setupInitialStars(currSeed.get());

                zLevel -= 5;
                drawCellWithEffects(partialTicks, canSeeSky, transparency);
                zLevel += 5;
            }
        } else {
            zLevel -= 5;
            drawCellWithEffects(partialTicks, canSeeSky, transparency);
            zLevel += 5;
        }

        TextureHelper.refreshTextureBindState();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void handleMouseMovement(float pticks) {
        boolean ctrl = isShiftKeyDown();
        if (grabCursor && !ctrl) {
            Minecraft.getMinecraft().mouseHelper.grabMouseCursor();
            grabCursor = false;
            clearLines();
        }
        if (!grabCursor && ctrl) {
            Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
            grabCursor = true;
        }

        if (!ctrl) {

            this.mc.mouseHelper.mouseXYChange();
            float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f1 = f * f * f * 8.0F;
            float f2 = (float) this.mc.mouseHelper.deltaX * f1;
            float f3 = (float) this.mc.mouseHelper.deltaY * f1;
            int i = 1;

            if (this.mc.gameSettings.invertMouse) {
                i = -1;
            }

            float movementX;
            float movementY;
            EntityRenderer er = Minecraft.getMinecraft().entityRenderer;
            if (this.mc.gameSettings.smoothCamera) {
                er.smoothCamYaw += f2;
                er.smoothCamPitch += f3;
                float f4 = pticks - er.smoothCamPartialTicks;
                er.smoothCamPartialTicks = pticks;
                f2 = er.smoothCamFilterX * f4;
                f3 = er.smoothCamFilterY * f4;
                movementX = f2;
                movementY = f3 * i;
            } else {
                er.smoothCamYaw = 0.0F;
                er.smoothCamPitch = 0.0F;
                movementX = f2;
                movementY = f3 * i;
            }
            boolean nullify = this.mc.player.rotationPitch <= -89.99F && Math.abs(movementY) == movementY;
            this.mc.player.turn(movementX, movementY);
            if (nullify) movementY = 0;
            handleHandMovement(MathHelper.floor(movementX), MathHelper.floor(movementY));
        }
    }

    private void handleHandMovement(int changeX, int changeY) {
        int offsetX = 6, offsetY = 6;
        int width = guiWidth - 12, height = guiHeight - 12;

        Iterator<StarPosition> iterator = usedStars.iterator();
        while (iterator.hasNext()) {
            StarPosition sl = iterator.next();
            sl.x -= changeX;
            sl.y += changeY;

            if (sl.x < offsetX) {
                sl.x += width;
            } else if (sl.x > (offsetX + width)) {
                sl.x -= width;
            }
            if (sl.y < offsetY) {
                sl.y += height;
            } else if (sl.y > (offsetY + height)) {
                sl.y -= height;
            }
        }
        /*for (int i = 0; i < (randomStars - usedStars.size()); i++) {
            usedStars.add(new StarPosition(offsetX + random.nextFloat() * width, offsetY + random.nextFloat() * height));
        }*/
    }

    private void drawCellWithEffects(float partialTicks, boolean canSeeSky, float transparency) {
        WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(Minecraft.getMinecraft().world);
        int lastTracked = handle == null ? 5 : handle.lastRecordedDay;
        Optional<Long> seed = ConstellationSkyHandler.getInstance().getSeedIfPresent(Minecraft.getMinecraft().world);
        long s = 0;
        if(seed.isPresent()) {
            s = seed.get();
        }
        Random r = new Random(s * 31 + lastTracked * 31);

        drawnConstellation = null;
        drawnStars = null;

        if (handle != null) {
            IMajorConstellation bestGuess = (IMajorConstellation) handle.getHighestDistributionConstellation(r, (c) -> c instanceof IMajorConstellation);
            if ((topFound == null || !topFound.equals(bestGuess)) && handle.getCurrentDistribution(bestGuess, (f) -> 1F) >= 0.8F) {
                topFound = bestGuess;
                selectedYaw = (r.nextFloat() * 360F) - 180F;
                selectedPitch = -90F + r.nextFloat() * 45F;
            }
        }

        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();

        drawGridBackground(partialTicks, canSeeSky, transparency);

        if (canSeeSky) {
            int offsetX = guiLeft;
            int offsetZ = guiTop;
            zLevel += 1;
            Optional<Map<StarLocation, Rectangle>> stars = drawCellEffect(offsetX, offsetZ, getGuiWidth(), getGuiHeight(), partialTicks, transparency);
            zLevel -= 1;

            if (stars.isPresent()) {
                drawnConstellation = topFound;
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
                return RenderConstellation.conCFlicker(ClientScheduler.getClientTick(), pTicks, 5 + r.nextInt(15));
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

    private Optional<Map<StarLocation, Rectangle>> drawCellEffect(int offsetX, int offsetY, int width, int height, float partialTicks, float transparency) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(Minecraft.getMinecraft().world);
        int lastTracked = handle == null ? 5 : handle.lastRecordedDay;
        Random r = new Random();

        RenderAstralSkybox.TEX_STAR_1.bind();
        for (StarPosition stars : usedStars) {
            r.setSeed(stars.seed);
            GL11.glPushMatrix();
            float brightness = 0.3F + (RenderConstellation.stdFlicker(ClientScheduler.getClientTick(), partialTicks, 10 + r.nextInt(20))) * 0.6F;
            brightness *= Minecraft.getMinecraft().world.getStarBrightness(partialTicks) * 2 * transparency;
            brightness *= (1F - Minecraft.getMinecraft().world.getRainStrength(partialTicks));
            GL11.glColor4f(brightness, brightness, brightness, brightness);
            drawRect(MathHelper.floor(offsetX + stars.x), MathHelper.floor(offsetY + stars.y), 5, 5);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glPopMatrix();
        }

        r.setSeed(lastTracked * 31);

        Map<StarLocation, Rectangle> rectangles = null;
        if (topFound != null) {
            zLevel += 1;

            float playerYaw = Minecraft.getMinecraft().player.rotationYaw % 360F;
            if (playerYaw < 0) {
                playerYaw += 360F;
            }
            if (playerYaw >= 180F) {
                playerYaw -= 360F;
            }
            float playerPitch = Minecraft.getMinecraft().player.rotationPitch;

            float diffYaw = playerYaw - selectedYaw;
            float diffPitch = playerPitch - selectedPitch;

            float sFactor = 35F;
            if ((Math.abs(diffYaw) <= sFactor || Math.abs(playerYaw + 360F) <= sFactor) &&
                    Math.abs(diffPitch) <= sFactor) {

                ScaledResolution res = new ScaledResolution(mc);
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GL11.glScissor((guiLeft + 5) * res.getScaleFactor(), (guiTop + 5) * res.getScaleFactor(), (guiWidth - 10) * res.getScaleFactor(), (guiHeight - 10) * res.getScaleFactor());

                int wPart = ((int) (((float) width) * 0.1F));
                int hPart = ((int) (((float) height) * 0.1F));

                rectangles = RenderConstellation.renderConstellationIntoGUI(
                        topFound,
                        offsetX + wPart + MathHelper.floor((diffYaw / sFactor) * width),
                        offsetY + hPart + MathHelper.floor((diffPitch / sFactor) * height),
                        zLevel,
                        width - (((int) (wPart * 1.5F))),
                        height - (((int) (hPart * 1.5F))),
                        2,
                        new RenderConstellation.BrightnessFunction() {
                            @Override
                            public float getBrightness() {
                                return (0.3F + 0.7F * RenderConstellation.conCFlicker(ClientScheduler.getClientTick(), partialTicks, 5 + r.nextInt(15))) * transparency;
                            }
                        },
                        ResearchManager.clientProgress.hasConstellationDiscovered(topFound.getUnlocalizedName()),
                        true
                );


                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }

            zLevel -= 1;
        }

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopAttrib();
        return Optional.ofNullable(rectangles);
    }

    private void drawGridBackground(float partialTicks, boolean canSeeSky, float angleTransparency) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        Blending.PREALPHA.apply();
        World renderWorld = Minecraft.getMinecraft().world;
        int rgbFrom, rgbTo;
        if (canSeeSky && angleTransparency > 1.0E-4) {
            float starBr = renderWorld.getStarBrightness(partialTicks) * 2;
            float rain = renderWorld.getRainStrength(partialTicks);
            rgbFrom = RenderingUtils.clampToColorWithMultiplier(calcRGBFromWithRain(starBr, rain), angleTransparency).getRGB();
            rgbTo = RenderingUtils.clampToColorWithMultiplier(calcRGBToWithRain(starBr, rain), angleTransparency).getRGB();
        } else {
            rgbFrom = 0x000000;
            rgbTo = 0x000000;
        }
        int alphaMask = 0xFF000000; //100% opacity.
        RenderingUtils.drawGradientRect(guiLeft + 4, guiTop + 4, zLevel, guiLeft + guiWidth - 4, guiTop + guiHeight - 4, new Color(alphaMask | rgbFrom), new Color(alphaMask | rgbTo));
        Blending.DEFAULT.apply();
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

    private int calcRGBToWithRain(float starBr, float rain) {
        int to = calcRGBTo(starBr);
        if (starBr <= THRESHOLD_TO_START) {
            float starMul = 1F;
            if (starBr > THRESHOLD_TO_SHIFT_BLUEGRAD) {
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
        if (starBr <= THRESHOLD_FROM_START) {
            float starMul = 1F;
            if (starBr > THRESHOLD_FROM_SHIFT_BLUEGRAD) {
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

        if (isInDrawingCell(mouseX, mouseY)) {
            start = new Point(mouseX, mouseY);
            end = new Point(mouseX, mouseY);
        } else {
            abortDrawing();
            clearLines();
        }
    }

    private boolean canStartDrawing() {
        return Minecraft.getMinecraft().world.getStarBrightness(1.0F) >= 0.35F &&
                Minecraft.getMinecraft().world.getRainStrength(1.0F) <= 0.1F &&
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
            if (start != null) {
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

        if (!has) return;

        List<StarConnection> sc = c.getStarConnections();
        if (sc.size() != drawnLines.size()) return; //Can't match otherwise anyway.
        if (!c.canDiscover(ResearchManager.clientProgress)) return;

        for (StarConnection connection : sc) {
            Rectangle fromRect = drawnStars.get(connection.from);
            if (fromRect == null) {
                AstralSorcery.log.info("[AstralSorcery] Could not check constellation of telescope drawing - starLocation is missing?");
                return;
            }
            Rectangle toRect = drawnStars.get(connection.to);
            if (toRect == null) {
                AstralSorcery.log.info("[AstralSorcery] Could not check constellation of telescope drawing - starLocation is missing?");
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

    private static class StarPosition {

        private float x;
        private float y;
        private long seed = random.nextLong(); //Bad on performance i know i know.

        private StarPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

}
