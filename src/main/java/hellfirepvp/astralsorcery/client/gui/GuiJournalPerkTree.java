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
import hellfirepvp.astralsorcery.client.gui.journal.*;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktUnlockPerk;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalPerkTree
 * Created by HellFirePvP
 * Date: 01.07.2018 / 01:14
 */
public class GuiJournalPerkTree extends GuiScreenJournal {

    private static final AbstractRenderableTexture textureResBack = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiresbg2");
    private static final AbstractRenderableTexture texturePerkConnection = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "connectionperks");
    private static final AbstractRenderableTexture textureSearchTextBG = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijtextarea");
    private static final AbstractRenderableTexture textureSearchMark = new SpriteSheetResource(AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "halo4"), 4, 8);

    private SizeHandler sizeHandler;
    private GuiRenderBoundingBox guiBox;

    private ScalingPoint mousePosition, previousMousePosition;
    private int mouseBufferX, mouseBufferY;
    private boolean mouseDragging = false;

    private AbstractPerk unlockPrimed = null;


    private int guiOffsetX, guiOffsetY;

    private Map<AbstractPerk, Rectangle.Double> thisFramePerks = new HashMap<>();
    private Map<AbstractPerk, Long> unlockEffects = new HashMap<>();

    private GuiTextEntry searchTextEntry = new GuiTextEntry();
    private List<AbstractPerk> searchMatches = Lists.newArrayList();

    public GuiJournalPerkTree() {
        super(2);
        this.closeWithInventoryKey = false;
        this.searchTextEntry.setChangeCallback(this::updateSearchHighlight);

        buildTree();
    }

    private void buildTree() {
        this.guiBox = new GuiRenderBoundingBox(10, 10, guiWidth - 10, guiHeight - 10);

        this.sizeHandler = new PerkTreeSizeHandler(this.guiHeight - 40, this.guiWidth - 20);
        this.sizeHandler.setScaleSpeed(0.04F);
        this.sizeHandler.setMaxScale(1F);
        this.sizeHandler.setMinScale(0.1F);
        this.sizeHandler.updateSize();

        this.mousePosition = ScalingPoint.createPoint(0, 0, this.sizeHandler.getScalingFactor(), false);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.guiOffsetX = guiLeft + 10;
        this.guiOffsetY = guiTop + 10;

        boolean shifted = false;
        PlayerProgress progress = ResearchManager.clientProgress;
        if (progress != null) {
            IMajorConstellation attunement = progress.getAttunedConstellation();
            if (attunement != null) {
                AbstractPerk root = PerkTree.PERK_TREE.getRootPerk(attunement);
                if (root != null) {
                    Point.Double shift = this.sizeHandler.evRelativePos(root.getOffset());
                    this.moveMouse(MathHelper.floor(shift.x), MathHelper.floor(shift.y));
                    shifted = true;
                }
            }
        }

        if (!shifted) {
            this.moveMouse(MathHelper.floor(this.sizeHandler.getTotalWidth() / 2),
                    MathHelper.floor(this.sizeHandler.getTotalHeight() / 2));
        }

        this.applyMovedMouseOffset();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.thisFramePerks.clear();

        int dWheelChange = Mouse.getDWheel();
        if(dWheelChange < 0) {
            this.sizeHandler.handleZoomOut();
            this.rescaleMouse();
        }
        if(dWheelChange > 0)  {
            this.sizeHandler.handleZoomIn();
            this.rescaleMouse();
        }

        handleMouseMovement(mouseX, mouseY);

        GlStateManager.pushMatrix();

        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.applyStateManager();
        Blending.DEFAULT.apply();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        drawDefault(textureResShell);
        drawBackground(zLevel - 50);

        ScaledResolution res = new ScaledResolution(mc);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((guiLeft + 27) * res.getScaleFactor(), (guiTop + 27) * res.getScaleFactor(), (guiWidth - 54) * res.getScaleFactor(), (guiHeight - 54) * res.getScaleFactor());
        drawPerkTree(partialTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        drawSearchBox();
        drawMiscInfo();
        drawHoverTooltips(mouseX, mouseY);

        TextureHelper.refreshTextureBindState();
        GlStateManager.popMatrix();
    }

    private void drawMiscInfo() {
        PlayerProgress prog = ResearchManager.clientProgress;

        GlStateManager.color(1F, 1F, 1F, 1F);

        if (prog.getAttunedConstellation() != null && prog.getAvailablePerkPoints() > 0) {
            GlStateManager.disableDepth();
            GlStateManager.pushMatrix();
            GlStateManager.translate(guiLeft + 40, guiTop + 18, 0);
            fontRenderer.drawString(I18n.format("perk.info.points", prog.getAvailablePerkPoints()), 0, 0, new Color(0xCCCCCC).getRGB(), false);

            GlStateManager.color(1F, 1F, 1F, 1F);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            TextureHelper.refreshTextureBindState();
            GlStateManager.popMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
        }
    }

    private void drawSearchBox() {
        GlStateManager.color(1F, 1F, 1F, 1F);

        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft + 300, guiTop + 16, 0);
        textureSearchTextBG.bindTexture();
        drawTexturedRectAtCurrentPos(88.5, 15);

        String text = this.searchTextEntry.getText();

        int length = fontRenderer.getStringWidth(text);
        boolean addDots = length > 75;
        while (length > 75) {
            text = text.substring(1, text.length());
            length = fontRenderer.getStringWidth("..." + text);
        }
        if (addDots) {
            text = "..." + text;
        }

        if ((ClientScheduler.getClientTick() % 20) > 10) {
            text += "_";
        }

        GlStateManager.translate(4, 4, 0);
        fontRenderer.drawString(text, 0, 0, new Color(0xCCCCCC).getRGB(), false);

        GlStateManager.color(1F, 1F, 1F, 1F);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        TextureHelper.refreshTextureBindState();
        GlStateManager.popMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
    }

    private void rescaleMouse() {
        this.mousePosition.rescale(this.sizeHandler.getScalingFactor());
        if(this.previousMousePosition != null) {
            this.previousMousePosition.rescale(this.sizeHandler.getScalingFactor());
        }
        this.moveMouse(0, 0);
    }

    private void drawHoverTooltips(int mouseX, int mouseY) {
        for (Map.Entry<AbstractPerk, Rectangle.Double> rctPerk : this.thisFramePerks.entrySet()) {
            if (rctPerk.getValue().contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                List<String> toolTip = new LinkedList<>();
                AbstractPerk perk = rctPerk.getKey();
                toolTip.add(I18n.format(perk.getUnlocalizedName() + ".name"));
                perk.getLocalizedTooltip().forEach(line -> toolTip.add(TextFormatting.GRAY.toString() + TextFormatting.ITALIC.toString() + line));
                toolTip.add("");
                PlayerProgress prog = ResearchManager.clientProgress;
                String unlockStr;
                if(prog.hasPerkUnlocked(perk)) {
                    unlockStr = "perk.info.active";
                } else if(perk.mayUnlockPerk(prog)) {
                    toolTip.add(I18n.format("perk.info.unlock.click"));
                    unlockStr = "perk.info.available";
                } else {
                    unlockStr = "perk.info.locked";
                }
                toolTip.add(I18n.format(unlockStr));
                if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips) {
                    String loc = perk.getCategory().getLocalizedName();
                    if (loc != null) {
                        toolTip.add(perk.getCategory().getTextFormatting() + "[" + loc + "]");
                    }
                }
                Collection<String> modInfo = perk.getSource();
                if (modInfo != null) {
                    for (String line : modInfo) {
                        toolTip.add(TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString() + line);
                    }
                }
                if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
                    toolTip.add("");
                    toolTip.add(TextFormatting.GRAY + perk.getRegistryName().toString());
                }
                RenderingUtils.renderBlueTooltip(mouseX, mouseY, toolTip, Minecraft.getMinecraft().fontRenderer);
                GlStateManager.color(1F, 1F, 1F, 1F);
                GL11.glColor4f(1F, 1F, 1F, 1F);
                break;
            }
        }
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GlStateManager.enableAlpha();
    }

    private void drawPerkTree(float partialTicks) {
        texturePerkConnection.bindTexture();
        for (Tuple<AbstractPerk, AbstractPerk> perkConnection : PerkTree.PERK_TREE.getConnections()) {
            PerkTreePoint.AllocationStatus status = PerkTreePoint.AllocationStatus.UNALLOCATED;
            PlayerProgress progress = ResearchManager.getProgress(Minecraft.getMinecraft().player, Side.CLIENT);
            if (progress != null) {
                int alloc = 0;
                if (progress.hasPerkUnlocked(perkConnection.key)) {
                    alloc++;
                }
                if (progress.hasPerkUnlocked(perkConnection.value)) {
                    alloc++;
                }
                if (alloc == 2) {
                    status = PerkTreePoint.AllocationStatus.ALLOCATED;
                } else if (alloc == 1 && progress.hasFreeAllocationPoint()) {
                    status = PerkTreePoint.AllocationStatus.UNLOCKABLE;
                } else {
                    status = PerkTreePoint.AllocationStatus.UNALLOCATED;
                }
            }

            Point offsetOne = perkConnection.key.getPoint().getOffset();
            Point offsetTwo = perkConnection.value.getPoint().getOffset();
            Point.Double shiftOne = this.sizeHandler.evRelativePos(offsetOne);
            Point.Double shiftTwo = this.sizeHandler.evRelativePos(offsetTwo);
            drawConnection(status, shiftOne, shiftTwo, partialTicks, ClientScheduler.getClientTick() + offsetOne.x + offsetOne.y + offsetTwo.x + offsetTwo.y);
        }

        List<Runnable> drawHighlight = Lists.newArrayList();
        for (PerkTreePoint perkPoint : PerkTree.PERK_TREE.getPerkPoints()) {
            Point offset = perkPoint.getOffset();
            double x = this.sizeHandler.evRelativePosX(offset.x);
            double y = this.sizeHandler.evRelativePosY(offset.y);
            Rectangle.Double perkRect = drawPerk(perkPoint, x, y, partialTicks, ClientScheduler.getClientTick() + offset.x + offset.y, drawHighlight);
            if (perkRect != null) {
                this.thisFramePerks.put(perkPoint.getPerk(), perkRect);
            }
        }
        drawHighlight.forEach(Runnable::run);
        this.unlockEffects.keySet().removeIf(perk -> !drawPerkUnlock(perk, this.unlockEffects.get(perk)));
        TextureHelper.refreshTextureBindState();
    }

    private boolean drawPerkUnlock(AbstractPerk perk, long tick) {
        int count = (int) (ClientScheduler.getClientTick() - tick);
        SpriteSheetResource sprite = SpriteLibrary.spritePerkActivate;
        if (count >= sprite.getFrameCount()) {
            return false;
        }
        Point.Double oPos = this.sizeHandler.evRelativePos(perk.getOffset());
        Point.Double offset = shift2DOffset(oPos.x, oPos.y);

        double width = 22;
        Rectangle.Double rct;
        if ((rct = thisFramePerks.get(perk)) != null) {
            width = rct.width;
        }
        width *= 2.5;

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        sprite.bindTexture();

        GlStateManager.pushMatrix();
        GlStateManager.translate(offset.x, offset.y, 0);

        Vector3 starVec = new Vector3(-width, -width, 0);
        double uLength = sprite.getUWidth();
        double vLength = sprite.getVWidth();
        Tuple<Double, Double> off = sprite.getUVOffset(count);
        Point.Double frameUV = new Point.Double(off.key, off.value);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone().addX(width * u * 2).addY(width * v * 2);
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(frameUV.x + uLength * u, frameUV.y + vLength * v).endVertex();
        }

        GlStateManager.disableAlpha();
        tes.draw();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();

        TextureHelper.refreshTextureBindState();
        return true;
    }

    private void drawConnection(PerkTreePoint.AllocationStatus status, Point.Double offset, Point.Double target, float pTicks, long effectTick) {
        Point.Double offsetSrc = shift2DOffset(offset.x, offset.y);
        Point.Double offsetDst = shift2DOffset(target.x, target.y);
        Color overlay = Color.WHITE;
        switch (status) {
            case UNALLOCATED:
                overlay = new Color(0xBBBBFF);
                break;
            case ALLOCATED:
                overlay = new Color(0x00EEEE00);
                break;
            case UNLOCKABLE:
                overlay = new Color(0x0071FF);
                break;
        }

        double effectPart = (Math.sin(Math.toRadians(((effectTick) * 8) % 360D)) + 1D) / 4D;
        float br = 0.1F + 0.4F * (2F - ((float) effectPart));
        float rR = (overlay.getRed()   / 255F) * br;
        float rG = (overlay.getGreen() / 255F) * br;
        float rB = (overlay.getBlue()  / 255F) * br;
        float rA = (overlay.getAlpha() / 255F) * br;
        GlStateManager.color(rR, rG, rB, rA);

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        Vector3 fromStar = new Vector3(offsetSrc.x, offsetSrc.y, 0);
        Vector3 toStar   = new Vector3(offsetDst.x, offsetDst.y, 0);

        double width = 4.0D * this.sizeHandler.getScalingFactor();

        Vector3 dir = toStar.clone().subtract(fromStar);
        Vector3 degLot = dir.clone().crossProduct(new Vector3(0, 0, 1)).normalize().multiply(width);//.multiply(j == 0 ? 1 : -1);

        Vector3 vec00 = fromStar.clone().add(degLot);
        Vector3 vecV = degLot.clone().multiply(-2);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = vec00.clone().add(dir.clone().multiply(u)).add(vecV.clone().multiply(v));
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(u, v).endVertex();
        }
        tes.draw();
        GlStateManager.color(1, 1, 1, 1);
    }

    @Nullable
    private Rectangle.Double drawPerk(PerkTreePoint perkPoint, double lowX, double lowY, float pTicks, long effectTick, List<Runnable> outRenderHighlights) {
        Point.Double offset = shift2DOffset(lowX, lowY);

        GlStateManager.pushMatrix();
        GlStateManager.translate(offset.x, offset.y, 0);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.scale(this.sizeHandler.getScalingFactor(), this.sizeHandler.getScalingFactor(), this.sizeHandler.getScalingFactor());
        Rectangle draw = perkPoint.renderAtCurrentPos(perkPoint.getPerk().getPerkStatus(Minecraft.getMinecraft().player, Side.CLIENT), effectTick, pTicks);
        GlStateManager.popMatrix();

        if (draw == null) {
            return null;
        }
        if (this.searchMatches.contains(perkPoint.getPerk())) {
            outRenderHighlights.add(() -> {
                GlStateManager.pushMatrix();
                GlStateManager.translate(offset.x, offset.y, 0);
                GlStateManager.scale(this.sizeHandler.getScalingFactor(), this.sizeHandler.getScalingFactor(), this.sizeHandler.getScalingFactor());
                GlStateManager.color(0.8F, 0.1F, 0.1F, 1F);
                drawSearchMarkHalo(draw, effectTick, pTicks);
                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.popMatrix();
            });
        }

        double rctWidth = draw.width * sizeHandler.getScalingFactor();
        double rctHeight = draw.height * sizeHandler.getScalingFactor();
        return new Rectangle.Double(offset.x - (rctWidth / 2), offset.y - (rctHeight / 2), rctWidth, rctHeight);
    }

    private void drawSearchMarkHalo(Rectangle draw, long effectTick, float pTicks) {
        int size = draw.width;

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        Vector3 starVec = new Vector3(-size, -size, 0);
        textureSearchMark.bindTexture();
        double uLength = textureSearchMark.getUWidth();
        double vLength = textureSearchMark.getVWidth();
        Point.Double frameUV = textureSearchMark.getUVOffset();

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone().addX(size * u * 2).addY(size * v * 2);
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(frameUV.x + uLength * u, frameUV.y + vLength * v).endVertex();
        }

        GlStateManager.disableAlpha();
        tes.draw();
        GlStateManager.enableAlpha();
    }

    private Point.Double shift2DOffset(double x, double y) {
        double scaledLeft = this.mousePosition.getScaledPosX() - sizeHandler.widthToBorder;
        double scaledTop =  this.mousePosition.getScaledPosY() - sizeHandler.heightToBorder;
        double xAdd = x - scaledLeft;
        double yAdd = y - scaledTop;
        double offsetX = guiOffsetX + xAdd;
        double offsetY = guiOffsetY + yAdd;
        return new Point.Double(offsetX, offsetY);
    }

    private void handleMouseMovement(int mouseX, int mouseY) {
        int guiMouseX = mouseX - guiLeft;
        int guiMouseY = mouseY - guiTop;

        if(Mouse.isButtonDown(0) && guiBox.isInBox(guiMouseX, guiMouseY)) {
            if(mouseDragging) {
                moveMouse(-(guiMouseX - mouseBufferX), -(guiMouseY - mouseBufferY));
            } else {
                mouseBufferX = guiMouseX;
                mouseBufferY = guiMouseY;
                mouseDragging = true;
            }
        } else {
            applyMovedMouseOffset();
            mouseDragging = false;
        }
    }

    private void moveMouse(int changeX, int changeY) {
        if (this.previousMousePosition != null) {
            mousePosition.updateScaledPos(
                    sizeHandler.clampX(previousMousePosition.getScaledPosX() + changeX),
                    sizeHandler.clampY(previousMousePosition.getScaledPosY() + changeY),
                    sizeHandler.getScalingFactor());
        } else {
            mousePosition.updateScaledPos(
                    sizeHandler.clampX(changeX),
                    sizeHandler.clampY(changeY),
                    sizeHandler.getScalingFactor());
        }
    }

    private void applyMovedMouseOffset() {
        this.previousMousePosition = ScalingPoint.createPoint(
                this.mousePosition.getScaledPosX(),
                this.mousePosition.getScaledPosY(),
                this.sizeHandler.getScalingFactor(),
                true);
    }

    private void drawBackground(float zLevel) {
        float br = 0.8F;

        GlStateManager.color(br, br, br, 1F);
        GlStateManager.disableBlend();
        textureResBack.bindTexture();
        BufferBuilder vb = Tessellator.getInstance().getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(guiLeft + 10,            guiTop - 10 + guiHeight, zLevel).tex(0, 1).endVertex();
        vb.pos(guiLeft - 10 + guiWidth, guiTop - 10 + guiHeight, zLevel).tex(1, 1).endVertex();
        vb.pos(guiLeft - 10 + guiWidth, guiTop + 10,             zLevel).tex(1, 0).endVertex();
        vb.pos(guiLeft + 10,            guiTop + 10,             zLevel).tex(0, 0).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.enableBlend();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private void updateSearchHighlight() {
        this.searchMatches.clear();

        String matchText = this.searchTextEntry.getText().toLowerCase();
        if (matchText.length() < 3) return;
        for (PerkTreePoint point : PerkTree.PERK_TREE.getPerkPoints()) {
            AbstractPerk perk = point.getPerk();
            String name = I18n.format(perk.getUnlocalizedName() + ".name").toLowerCase();
            if (name.contains(matchText)) {
                this.searchMatches.add(perk);
            } else {
                String catStr = perk.getCategory().getLocalizedName();
                if (catStr != null && catStr.toLowerCase().contains(matchText)) {
                    this.searchMatches.add(perk);
                } else {
                    for (String tooltip : perk.getLocalizedTooltip()) {
                        if (tooltip.toLowerCase().contains(matchText)) {
                            this.searchMatches.add(perk);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        if (this.unlockPrimed == null) {
            return;
        }

        for (Map.Entry<AbstractPerk, Rectangle.Double> rctPerk : this.thisFramePerks.entrySet()) {
            if (this.unlockPrimed.equals(rctPerk.getKey()) && rctPerk.getValue().contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                if (rctPerk.getKey().mayUnlockPerk(ResearchManager.clientProgress)) {
                    PktUnlockPerk pkt = new PktUnlockPerk(false, rctPerk.getKey());
                    PacketChannel.CHANNEL.sendToServer(pkt);
                    break;
                }
            }
        }

        this.unlockPrimed = null;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        this.unlockPrimed = null;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);

        for (Map.Entry<AbstractPerk, Rectangle.Double> rctPerk : this.thisFramePerks.entrySet()) {
            if (rctPerk.getValue().contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                if (rctPerk.getKey().mayUnlockPerk(ResearchManager.clientProgress)) {
                    this.unlockPrimed = rctPerk.getKey();
                    break;
                }
            }
        }

        if (rectResearchBookmark != null && rectResearchBookmark.contains(p)) {
            GuiJournalProgression.resetJournal();
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalProgression.getJournalInstance());
            return;
        }
        if (rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellationCluster.getConstellationScreen());
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (keyCode != Keyboard.KEY_ESCAPE) {
            searchTextEntry.textboxKeyTyped(typedChar, keyCode);
        }
    }

    public void playUnlockAnimation(AbstractPerk perk) {
        this.unlockEffects.put(perk, ClientScheduler.getClientTick());
    }

}
