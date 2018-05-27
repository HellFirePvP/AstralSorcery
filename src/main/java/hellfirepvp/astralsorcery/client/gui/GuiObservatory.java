/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.base.GuiTileBase;
import hellfirepvp.astralsorcery.client.sky.RenderAstralSkybox;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiObservatory
 * Created by HellFirePvP
 * Date: 27.05.2018 / 07:29
 */
public class GuiObservatory extends GuiTileBase<TileObservatory> {

    private static final Random random = new Random();

    private static final int frameSize = 16;
    private static final AbstractRenderableTexture texPartFrame = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "observatoryframe");

    private static final int randomStars = 220;
    private List<StarPosition> usedStars = new ArrayList<>(randomStars);

    private boolean grabCursor = false;

    public GuiObservatory(TileObservatory te) {
        super(te,
                new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - (frameSize * 2),
                new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() - (frameSize * 2));

        Optional<Long> currSeed = ConstellationSkyHandler.getInstance().getSeedIfPresent(Minecraft.getMinecraft().world);
        currSeed.ifPresent(this::setupInitialStars);
    }

    private void setupInitialStars(long seed) {
        Random rand = new Random(seed);

        int day = (int) (Minecraft.getMinecraft().world.getWorldTime() / 24000);
        for (int i = 0; i < day; i++) {
            rand.nextLong(); //Flush
        }

        for (int i = 0; i < randomStars; i++) {
            usedStars.add(new StarPosition(frameSize + rand.nextFloat() * guiWidth, frameSize + rand.nextFloat() * guiHeight));
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        if (!Minecraft.IS_RUNNING_ON_MAC) {
            KeyBinding.updateKeyBindState();
        }
        mc.mouseHelper.grabMouseCursor();
        mc.inGameHasFocus = true;

        //if (Minecraft.IS_RUNNING_ON_MAC) {
        //    Mouse.setGrabbed(false);
        //    Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2 - 20);
        //    Mouse.setGrabbed(true);
        //}
    }

    @Override
    public void initGui() {
        super.initGui();

        if (!Minecraft.IS_RUNNING_ON_MAC) {
            KeyBinding.updateKeyBindState();
        }
        mc.mouseHelper.grabMouseCursor();
        mc.inGameHasFocus = true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        World w = Minecraft.getMinecraft().world;
        if (w == null) return;

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;

        handleMouseMovement(partialTicks);

        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();

        float pitch = Minecraft.getMinecraft().player.rotationPitch;
        float transparency = 0F;
        if (pitch < 0F) {
            transparency = 1F;
        } else if (pitch < 10F) {
            transparency = (Math.abs(pitch) + 10F) / 10F;
            if (ConstellationSkyHandler.getInstance().isNight(w)) {
                transparency *= transparency;
            }
        }
        boolean canSeeSky = canTelescopeSeeSky(w);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((frameSize - 2) * sr.getScaleFactor(),
                (frameSize - 2) * sr.getScaleFactor(),
                (guiWidth + 2) * sr.getScaleFactor(),
                (guiHeight + 2) * sr.getScaleFactor());
        drawGridBackground(partialTicks, canSeeSky, transparency);

        drawEffectBackground(partialTicks, canSeeSky, transparency);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        zLevel += 10;
        drawFrame(partialTicks);
        zLevel -= 10;
    }

    private void drawEffectBackground(float partialTicks, boolean canSeeSky, float transparency) {
        if (usedStars.isEmpty()) {
            Optional<Long> currSeed = ConstellationSkyHandler.getInstance().getSeedIfPresent(Minecraft.getMinecraft().world);
            if (currSeed.isPresent()) {
                setupInitialStars(currSeed.get());

                zLevel += 5;
                drawCellWithEffects(partialTicks, canSeeSky, transparency);
                zLevel -= 5;
            }
        } else {
            zLevel += 5;
            drawCellWithEffects(partialTicks, canSeeSky, transparency);
            zLevel -= 5;
        }
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

        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.applyStateManager();

        if (canSeeSky) {
            int offsetX = guiLeft;
            int offsetZ = guiTop;
            zLevel += 1;
            Map<IConstellation, Map<StarLocation, Rectangle>> stars = drawCellEffect(offsetX, offsetZ, getGuiWidth(), getGuiHeight(), partialTicks, transparency);
            zLevel -= 1;
        }
    }

    private Map<IConstellation, Map<StarLocation, Rectangle>> drawCellEffect(int offsetX, int offsetY, int guiWidth, int guiHeight, float partialTicks, float transparency) {
        GlStateManager.disableAlpha();

        WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(Minecraft.getMinecraft().world);
        int lastTracked = handle == null ? 5 : handle.lastRecordedDay;
        Random r = new Random();

        GlStateManager.color(1, 1, 1, 1);

        RenderAstralSkybox.TEX_STAR_1.bind();
        for (StarPosition stars : usedStars) {
            r.setSeed(stars.seed);
            GlStateManager.pushMatrix();
            float brightness = 0.3F + (RenderConstellation.stdFlicker(ClientScheduler.getClientTick(), partialTicks, 5 + r.nextInt(15))) * 0.6F;
            brightness *= Minecraft.getMinecraft().world.getStarBrightness(partialTicks) * 2 * transparency;
            brightness *= (1F - Minecraft.getMinecraft().world.getRainStrength(partialTicks));
            GlStateManager.color(brightness, brightness, brightness, brightness);
            int size = r.nextInt(4) + 2;
            drawRect(MathHelper.floor(offsetX + stars.x), MathHelper.floor(offsetY + stars.y), size, size);
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.popMatrix();
        }

        r.setSeed(lastTracked * 31);

        GlStateManager.enableAlpha();
        return null;
    }

    private void drawFrame(float pticks) {
        texPartFrame.bindTexture();

        //Draw corners
        GlStateManager.pushMatrix();
        drawTexturedRectAtCurrentPos(frameSize, frameSize, 0, 0, 8F / 20F, 8F / 20F);
        GlStateManager.translate(guiWidth + frameSize, 0, 0);
        drawTexturedRectAtCurrentPos(frameSize, frameSize, 8F / 20F, 0, 8F / 20F, 8F / 20F);
        GlStateManager.translate(0, (guiHeight + frameSize), 0);
        drawTexturedRectAtCurrentPos(frameSize, frameSize, 8F / 20F, 8F / 20F, 8F / 20F, 8F / 20F);
        GlStateManager.translate(-(guiWidth + frameSize), 0, 0);
        drawTexturedRectAtCurrentPos(frameSize, frameSize, 0, 8F / 20F, 8F / 20F, 8F / 20F);
        GlStateManager.popMatrix();

        //Draw frame border
        GlStateManager.pushMatrix();
        GlStateManager.translate(frameSize, 0, 0);
        drawTexturedRectAtCurrentPos(guiWidth, frameSize, 16F / 20F, 0, 1F / 20F, 8F / 20F);
        GlStateManager.translate(guiWidth, frameSize, 0);
        drawTexturedRectAtCurrentPos(frameSize, guiHeight, 0, 17F / 20F, 8F / 20F, 1F / 20F);
        GlStateManager.translate(-guiWidth, guiHeight, 0);
        drawTexturedRectAtCurrentPos(guiWidth, frameSize, 17F / 20F, 0, 1F / 20F, 8F / 20F);
        GlStateManager.translate(-frameSize, -guiHeight, 0);
        drawTexturedRectAtCurrentPos(frameSize, guiHeight, 0, 16F / 20F, 8F / 20F, 1F / 20F);
        GlStateManager.popMatrix();

        TextureHelper.refreshTextureBindState();
    }

    private void handleMouseMovement(float pticks) {
        boolean ctrl = isShiftKeyDown();

        if (grabCursor && !ctrl) {
            if(!Minecraft.IS_RUNNING_ON_MAC) {
                KeyBinding.updateKeyBindState();
            }
            Minecraft.getMinecraft().mouseHelper.grabMouseCursor();
            Minecraft.getMinecraft().inGameHasFocus = true;
            grabCursor = false;
            //clearLines();
        }
        if (!grabCursor && ctrl) {
            Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
            Minecraft.getMinecraft().inGameHasFocus = false;
            grabCursor = true;
        }

        if (!ctrl) {

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
            if(this.mc.player.rotationPitch >= -10F) {
                this.mc.player.rotationPitch = -10F;
                nullify = true;
            }
            if (nullify) movementY = 0;

            moveIdleStars(MathHelper.floor(movementX), MathHelper.floor(movementY));
        }
    }

    private void moveIdleStars(int changeX, int changeY) {
        int width = guiWidth, height = guiHeight;
        changeX *= 3F;
        changeY *= 2F;

        for (StarPosition sl : usedStars) {
            sl.x -= changeX;
            sl.y += changeY;

            if (Math.abs(changeX) > 0) {
                if (sl.x < 0) {
                    sl.x += width;
                } else if (sl.x > width) {
                    sl.x -= width;
                }
            }
            if (Math.abs(changeY) > 0) {
                if (sl.y < 0) {
                    sl.y += height;
                } else if (sl.y > height) {
                    sl.y -= height;
                }
            }
        }
        /*for (int i = 0; i < (randomStars - usedStars.size()); i++) {
            usedStars.add(new StarPosition(offsetX + random.nextFloat() * width, offsetY + random.nextFloat() * height));
        }*/
    }

    private void drawGridBackground(float partialTicks, boolean canSeeSky, float angleTransparency) {
        Blending.PREALPHA.applyStateManager();
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
        RenderingUtils.drawGradientRect(guiLeft, guiTop, zLevel, guiLeft + guiWidth, guiTop + guiHeight, new Color(alphaMask | rgbFrom), new Color(alphaMask | rgbTo));
        Blending.DEFAULT.applyStateManager();
    }

    private boolean canTelescopeSeeSky(World renderWorld) {
        BlockPos pos = getOwningTileEntity().getPos();
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                if(xx == 0 && zz == 0) continue;
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
