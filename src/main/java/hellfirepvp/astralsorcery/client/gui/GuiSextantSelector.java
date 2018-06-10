/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.base.GuiSkyScreen;
import hellfirepvp.astralsorcery.client.gui.base.GuiWHScreen;
import hellfirepvp.astralsorcery.client.sky.RenderAstralSkybox;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.tool.sextant.ItemSextant;
import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiSextantSelector
 * Created by HellFirePvP
 * Date: 31.05.2018 / 14:36
 */
public class GuiSextantSelector extends GuiWHScreen implements GuiSkyScreen {

    private static final Random rand = new Random();

    private static final AbstractRenderableTexture textureSextant = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridsextant");

    private static final Rectangle.Float partFrame = new Rectangle.Float(0, 0, 140F / 172F, 1F);
    private static final Rectangle.Float partSelectFrame = new Rectangle.Float(140F / 172F, 110F / 140F, 30F / 172F, 30F / 140F);
    private static final Rectangle.Float partIconSize = new Rectangle2D.Float(0F, 0F, 16F / 172F, 16F / 140F);
    private static final Rectangle.Float partArrowDown = new Rectangle2D.Float(140F / 172F, 80F / 140F, 12F / 172F, 8F / 140F);
    private static final Rectangle.Float partArrowUp = new Rectangle2D.Float(140F / 172F, 88F / 140F, 12F / 172F, 8F / 140F);

    private static final Rectangle.Float partFrameCornerUpLeft = new Rectangle2D.Float(140F / 172F, 97F / 140F, 6F / 172F, 6F / 140F);
    private static final Rectangle.Float partFrameCornerUpRight = new Rectangle2D.Float(147F / 172F, 97F / 140F, 6F / 172F, 6F / 140F);
    private static final Rectangle.Float partFrameCornerDownLeft = new Rectangle2D.Float(140F / 172F, 104F / 140F, 6F / 172F, 6F / 140F);
    private static final Rectangle.Float partFrameCornerDownRight = new Rectangle2D.Float(147F / 172F, 104F / 140F, 6F / 172F, 6F / 140F);

    private static final Rectangle.Float partFrameEdgeLeft = new Rectangle2D.Float(140F / 172F, 103F / 140F, 6F / 172F, 1F / 140F);
    private static final Rectangle.Float partFrameEdgeUp = new Rectangle2D.Float(146F / 172F, 97F / 140F, 1F / 172F, 6F / 140F);
    private static final Rectangle.Float partFrameEdgeRight = new Rectangle2D.Float(147F / 172F, 103F / 140F, 6F / 172F, 1F / 140F);
    private static final Rectangle.Float partFrameEdgeDown = new Rectangle2D.Float(146F / 172F, 104F / 140F, 1F / 172F, 6F / 140F);

    private static final int selectionsPerFrame = 4;
    private static final int randomStars = 85;
    private List<StarPosition> usedStars = new ArrayList<>(randomStars);

    private static final int showupDelay = 20;
    private Map<SextantFinder.TargetObject, Tuple<BlockPos, Integer>> showupTargets = new HashMap<>();

    private List<SextantFinder.TargetObject> availableTargets = new LinkedList<>();
    private SextantFinder.TargetObject selectedTarget = null;
    private int selectionOffset;

    private boolean grabCursor = false;

    public GuiSextantSelector(ItemStack sextant) {
        super(280, 280);

        Optional<Long> currSeed = ConstellationSkyHandler.getInstance().getSeedIfPresent(Minecraft.getMinecraft().world);
        currSeed.ifPresent(this::setupInitialStars);

        Tuple<BlockPos, Integer> target = ItemSextant.getCurrentTargetInformation(sextant);
        if (target != null && Minecraft.getMinecraft().world != null && target.value == Minecraft.getMinecraft().world.provider.getDimension()) {
            SextantFinder.TargetObject selectedTarget = ItemSextant.getTarget(sextant);
            if (selectedTarget != null) {
                this.selectedTarget = selectedTarget;
            }
        }
        for (SextantFinder.TargetObject to : SextantFinder.getSelectableTargets()) {
            if (to.isSelectable(sextant, ResearchManager.clientProgress)) {
                availableTargets.add(to);
            }
        }

        if (this.selectedTarget == null) {
            this.selectionOffset = 0;
        } else {
            this.selectionOffset = this.availableTargets.indexOf(this.selectedTarget);
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

        for (int i = 0; i < randomStars; i++) {
            usedStars.add(new StarPosition(offsetX + rand.nextFloat() * width, offsetY + rand.nextFloat() * height));
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (Minecraft.getMinecraft().player == null ||
                Minecraft.getMinecraft().world == null) {
            return;
        }

        for (SextantFinder.TargetObject to : this.availableTargets) {
            if (!this.showupTargets.containsKey(to)) {
                BlockPos target = UISextantCache.queryLocation(Minecraft.getMinecraft().player.getPosition(),
                        Minecraft.getMinecraft().world.provider.getDimension(), to);
                if (target != null) {
                    this.showupTargets.put(to, new Tuple<>(target, 0));
                }
            }
        }

        for (SextantFinder.TargetObject to : this.showupTargets.keySet()) {
            Tuple<BlockPos, Integer> showupTpl = this.showupTargets.get(to);
            if (showupTpl.value < showupDelay) {
                this.showupTargets.put(to, new Tuple<>(showupTpl.key, showupTpl.value + 1));
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        if (!Minecraft.IS_RUNNING_ON_MAC) {
            KeyBinding.updateKeyBindState();
        }
        mc.mouseHelper.grabMouseCursor();
        mc.inGameHasFocus = true;
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
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().world == null) {
            return;
        }
        GlStateManager.pushMatrix();

        handleMouseMovement(partialTicks);

        ScaledResolution res = new ScaledResolution(mc);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                (guiLeft + 16) * res.getScaleFactor(),
                (guiTop + 17) * res.getScaleFactor(),
                (guiWidth - 32) * res.getScaleFactor(),
                (guiHeight - 33) * res.getScaleFactor());

        drawSkyScreen(partialTicks);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        textureSextant.bindTexture();
        zLevel += 5;
        drawTexturedRect(guiLeft, guiTop, guiWidth, guiHeight, partFrame);
        zLevel -= 5;
        TextureHelper.refreshTextureBindState();

        drawSelectorBox(guiLeft + guiWidth + 10, guiTop + (guiHeight / 2D - (selectionsPerFrame / 2D) * 16 - 6),
                availableTargets, this.selectedTarget == null ? -1 : availableTargets.indexOf(this.selectedTarget));

        TextureHelper.refreshTextureBindState();
        GlStateManager.popMatrix();
    }

    private void drawSkyScreen(float partialTicks) {
        World w = Minecraft.getMinecraft().world;
        float pitch = Minecraft.getMinecraft().player.rotationPitch;
        float transparency = 0F;
        if (pitch < -20F) {
            transparency = 1F;
        } else if (pitch < 10F) {
            transparency = 1F - (Math.abs(pitch + 20) / 30F);
            if (ConstellationSkyHandler.getInstance().isNight(w)) {
                transparency *= transparency;
            }
        }
        boolean canSeeSky = canSeeSky(w);

        drawGridBackground(partialTicks, canSeeSky, transparency);
        drawEffectBackground(partialTicks, canSeeSky, transparency);
    }

    private void drawEffectBackground(float partialTicks, boolean canSeeSky, float transparency) {
        if (usedStars.isEmpty()) {
            Optional<Long> currSeed = ConstellationSkyHandler.getInstance().getSeedIfPresent(Minecraft.getMinecraft().world);
            if (!currSeed.isPresent()) {
                return;
            }
            setupInitialStars(currSeed.get());
        }

        zLevel += 5;
        drawCellWithEffects(partialTicks, canSeeSky, transparency);
        zLevel -= 5;
    }

    private Map<Rectangle, SextantFinder.TargetObject> drawCellWithEffects(float partialTicks, boolean canSeeSky, float transparency) {
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
            drawCellEffect(offsetX, offsetZ, getGuiWidth(), getGuiHeight(), partialTicks, transparency);
            zLevel -= 1;
        }

        Vector3 iPosPlayer = RenderingUtils.interpolatePosition(Minecraft.getMinecraft().player, partialTicks).setY(0);

        zLevel += 10;

        Map<Rectangle, SextantFinder.TargetObject> targets = new HashMap<>();
        for (Map.Entry<SextantFinder.TargetObject, Tuple<BlockPos, Integer>> visibleTarget : this.showupTargets.entrySet()) {
            float dayMultiplier = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(Minecraft.getMinecraft().world);
            if(dayMultiplier <= 0.1F) {
                continue;
            }

            SextantFinder.TargetObject target = visibleTarget.getKey();
            BlockPos actualPos = visibleTarget.getValue().key;
            float alphaShowup = MathHelper.clamp(((float) visibleTarget.getValue().value + partialTicks) / ((float) showupDelay), 0F, 1F);

            Vector3 dir = new Vector3(actualPos).setY(0).subtract(iPosPlayer);
            //length, yaw, pitch
            Vector3 polar = dir.clone().copyToPolar();
            if(polar.getX() <= 20D) {
                continue;
            }
            //float proximity = polar.getX() >= 350D ? 1F : MathHelper.sqrt(((float) polar.getX()) / 350F);
            double yaw = 180D - polar.getZ();
            double pitch = polar.getX() >= 350D ? -20D : Math.min(-20D, -20D - (70D - (70D * (polar.getX() / 350D))));

            float playerYaw = Minecraft.getMinecraft().player.rotationYaw % 360F;
            if (playerYaw < 0) {
                playerYaw += 360F;
            }
            if (playerYaw >= 180F) {
                playerYaw -= 360F;
            }
            float playerPitch = Minecraft.getMinecraft().player.rotationPitch;

            double diffYaw = playerYaw - yaw;
            double diffPitch = playerPitch - pitch;

            if (diffYaw >= 180F) {
                diffYaw -= 360F;
            }

            float xsFactor = 100F;
            float ysFactor = 60F;
            int wPart = ((int) (((float) width) * 0.1F));
            int hPart = ((int) (((float) height) * 0.1F));

            GlStateManager.disableAlpha();
            float alpha = RenderConstellation.conCFlicker(ClientScheduler.getClientTick(), partialTicks, 8);
            alpha = (0.2F + 0.8F * alpha) * dayMultiplier * alphaShowup * transparency;
            Color c = new Color(target.getColorTheme(), false);
            GlStateManager.color(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, alpha);
            RenderAstralSkybox.TEX_STAR_1.bind();

            double offsetX = guiLeft + wPart - MathHelper.floor((diffYaw / xsFactor) * width);
            double offsetY = guiTop + hPart - MathHelper.floor((diffPitch / ysFactor) * height);
            drawTexturedRect(
                    offsetX,
                    offsetY,
                    16,
                    16,
                    0, 0, 1, 1);
            TextureHelper.refreshTextureBindState();

            targets.put(new Rectangle(MathHelper.ceil(offsetX), MathHelper.ceil(offsetY), 16, 16), target);
        }

        zLevel -= 10;

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableBlend();
        return targets;
    }

    private void drawCellEffect(int offsetX, int offsetY, int guiWidth, int guiHeight, float partialTicks, float transparency) {
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
            int size = r.nextInt(4) + 3;
            drawRect(MathHelper.floor(offsetX + stars.x), MathHelper.floor(offsetY + stars.y), size, size);
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.popMatrix();
        }

        Random cstRand = new Random(lastTracked * 31);
        for (int i = 0; i < 5 + cstRand.nextInt(10); i++) {
            cstRand.nextLong();
        }

        GlStateManager.enableAlpha();
    }

    private void drawGridBackground(float partialTicks, boolean canSeeSky, float transparency) {
        Blending.PREALPHA.applyStateManager();
        Tuple<Color, Color> fromTo = GuiSkyScreen.getRBGFromTo(canSeeSky, transparency, partialTicks);
        RenderingUtils.drawGradientRect(guiLeft, guiTop, zLevel, guiLeft + guiWidth, guiTop + guiHeight, fromTo.key, fromTo.value);
        Blending.DEFAULT.applyStateManager();
    }

    private void drawSelectorBox(double offsetX, double offsetY, List<SextantFinder.TargetObject> objects, int selectedOption) {
        textureSextant.bindTexture();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableDepth();
        int totalInnerHeight = Math.min(selectionsPerFrame, objects.size()) * 16;

        //Re-center selection
        int yShift = objects.size() >= selectionsPerFrame ? 0 : (selectionsPerFrame - objects.size()) * 8;
        offsetY += yShift;

        if (objects.size() <= selectionsPerFrame) {
            this.selectionOffset = 0;
        } else if (this.selectionOffset + selectionsPerFrame > objects.size()) {
            this.selectionOffset = objects.size() - selectionsPerFrame;
        }

        boolean drawPrevArrow = this.selectionOffset > 0;
        boolean drawNextArrow = this.selectionOffset + selectionsPerFrame < objects.size();

        //Draw corners
        drawTexturedRect(offsetX, offsetY, 6, 6,
                partFrameCornerUpLeft);
        drawTexturedRect(offsetX + 6 + 16, offsetY, 6, 6,
                partFrameCornerUpRight);
        drawTexturedRect(offsetX, offsetY + 6 + totalInnerHeight, 6, 6,
                partFrameCornerDownLeft);
        drawTexturedRect(offsetX + 6 + 16, offsetY + 6 + totalInnerHeight, 6, 6,
                partFrameCornerDownRight);

        //Draw Edges
        drawTexturedRect(offsetX + 6, offsetY, 16, 6,
                partFrameEdgeUp);
        drawTexturedRect(offsetX + 6, offsetY + 6 + totalInnerHeight, 16, 6,
                partFrameEdgeDown);
        drawTexturedRect(offsetX, offsetY + 6, 6, totalInnerHeight,
                partFrameEdgeLeft);
        drawTexturedRect(offsetX + 6 + 16, offsetY + 6, 6, totalInnerHeight,
                partFrameEdgeRight);

        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.applyStateManager();

        //"Blur" center
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorMaterial();
        GlStateManager.color(0F, 0F, 0F, 0.3F);

        drawTexturedRect(offsetX + 6, offsetY + 6, 16, totalInnerHeight,
                0F, 0F, 1F, 1F);

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableColorMaterial();
        GlStateManager.enableTexture2D();

        if (drawPrevArrow) {
            drawTexturedRect(offsetX + 8D, offsetY - 10, 12, 7,
                    partArrowUp);
        }
        if (drawNextArrow) {
            drawTexturedRect(offsetX + 8, offsetY + 3 + 12 + totalInnerHeight, 12, 7,
                    partArrowDown);
        }

        int internalOffset = 0;
        int selectedInternal = -1;
        for (int i = this.selectionOffset; i < this.selectionOffset + Math.min(selectionsPerFrame, objects.size()); i++) {
            SextantFinder.TargetObject to = objects.get(i);
            AbstractRenderableTexture tex = to.getRenderable();
            tex.bindTexture();
            drawTexturedRect(offsetX + 6, offsetY + 6 + (internalOffset * 16), 16, 16, tex);

            if(i == selectedOption) {
                selectedInternal = internalOffset;
            }
            internalOffset += 1;
        }

        if (selectedInternal >= 0) {
            drawTexturedRect(offsetX, offsetY + selectedInternal * 16, 28, 28, partSelectFrame);
        }

        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        TextureHelper.refreshTextureBindState();
    }

    private boolean canSeeSky(World renderWorld) {
        BlockPos playerPos = Minecraft.getMinecraft().player.getPosition();

        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                BlockPos other = playerPos.add(xx, 0, zz);
                if (!renderWorld.canSeeSky(other)) {
                    return false;
                }
            }
        }
        return renderWorld.canSeeSky(playerPos.up());
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
            if (nullify) movementY = 0;
            handleHandMovement(MathHelper.floor(movementX), MathHelper.floor(movementY));
        }
    }

    private void handleHandMovement(int changeX, int changeY) {
        int offsetX = 6, offsetY = 6;
        int width = guiWidth - 12, height = guiHeight - 12;

        for (StarPosition sl : usedStars) {
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
    }

    private static class StarPosition {

        private float x;
        private float y;
        private long seed = rand.nextLong(); //Bad on performance i know i know.

        private StarPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
