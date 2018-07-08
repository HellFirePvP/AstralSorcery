/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.journal.GuiRenderBoundingBox;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.journal.PerkTreeSizeHandler;
import hellfirepvp.astralsorcery.client.gui.journal.SizeHandler;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
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
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    private SizeHandler sizeHandler;
    private GuiRenderBoundingBox guiBox;

    private Point mousePosition = new Point(), previousMousePosition = new Point();
    private int mouseBufferX, mouseBufferY;
    private boolean mouseDragging = false;

    private int guiOffsetX, guiOffsetY;

    private Map<Rectangle.Double, AbstractPerk> thisFramePerks = new HashMap<>();

    public GuiJournalPerkTree() {
        super(2);

        buildTree();
    }

    private void buildTree() {
        this.guiBox = new GuiRenderBoundingBox(10, 10, guiWidth - 10, guiHeight - 10);

        this.sizeHandler = new PerkTreeSizeHandler(this.guiHeight - 20, this.guiWidth - 20);
        this.sizeHandler.setMaxScale(1F);
        this.sizeHandler.setMinScale(1F);
        this.sizeHandler.updateSize();
    }

    @Override
    public void initGui() {
        super.initGui();

        this.guiOffsetX = guiLeft + 10;
        this.guiOffsetY = guiTop + 10;

        this.moveMouse(MathHelper.floor(this.sizeHandler.getTotalWidth() / 2),
                MathHelper.floor(this.sizeHandler.getTotalHeight() / 2));
        this.applyMovedMouseOffset();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.thisFramePerks.clear();

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

        drawHoverTooltips(mouseX, mouseY);

        TextureHelper.refreshTextureBindState();
        GlStateManager.popMatrix();
    }

    private void drawHoverTooltips(int mouseX, int mouseY) {
        for (Rectangle.Double r : this.thisFramePerks.keySet()) {
            if (r.contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                List<String> toolTip = new LinkedList<>();
                AbstractPerk perk = this.thisFramePerks.get(r);
                toolTip.add(I18n.format(perk.getUnlocalizedName()));
                toolTip.addAll(perk.getLocalizedTooltip());
                toolTip.add("");
                PlayerProgress prog = ResearchManager.clientProgress;
                String unlockStr;
                if(prog.hasPerkUnlocked(perk)) {
                    int unlockLevel = prog.getAppliedPerks().get(perk);
                    if(unlockLevel > 0) {
                        toolTip.add(I18n.format("perk.info.unlocked.level", unlockLevel));
                    } else {
                        toolTip.add(I18n.format("perk.info.unlocked.free"));
                    }
                    unlockStr = "perk.info.active";
                } else if(mayUnlockClient(prog, perk)) {
                    toolTip.add(I18n.format("perk.info.unlock.level", prog.getNextFreeLevel()));
                    unlockStr = "perk.info.available";
                } else {
                    unlockStr = "perk.info.locked";
                }
                toolTip.add(I18n.format(unlockStr));
                RenderingUtils.renderBlueTooltip(mouseX, mouseY, toolTip, Minecraft.getMinecraft().fontRenderer);
                GlStateManager.color(1F, 1F, 1F, 1F);
                GL11.glColor4f(1F, 1F, 1F, 1F);
            }
        }
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GlStateManager.enableAlpha();
    }

    private boolean mayUnlockClient(PlayerProgress prog, AbstractPerk perk) {
        if(!prog.hasFreeAlignmentLevel()) return false;

        boolean hasConnection = false;
        for (AbstractPerk otherPerks : PerkTree.INSTANCE.getConnectedPerks(perk)) {
            if (prog.hasPerkUnlocked(otherPerks)) {
                hasConnection = true;
                break;
            }
        }
        return hasConnection;
    }

    private void drawPerkTree(float partialTicks) {
        texturePerkConnection.bindTexture();
        for (Tuple<AbstractPerk, AbstractPerk> perkConnection : PerkTree.INSTANCE.getConnections()) {
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
                } else if (alloc == 1 && progress.hasFreeAlignmentLevel()) {
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

        for (PerkTreePoint perkPoint : PerkTree.INSTANCE.getPerkPoints()) {
            Point offset = perkPoint.getOffset();
            double x = this.sizeHandler.evRelativePosX(offset.x);
            double y = this.sizeHandler.evRelativePosY(offset.y);
            Rectangle.Double perkRect = drawPerk(perkPoint, x, y, partialTicks, ClientScheduler.getClientTick() + offset.x + offset.y);
            if (perkRect != null) {
                if (this.guiBox.isInBox(perkRect.x, perkRect.y) ||
                        this.guiBox.isInBox(perkRect.x + perkRect.width, perkRect.y + perkRect.height)) {
                    this.thisFramePerks.put(perkRect, perkPoint.getPerk());
                }
            }
        }

        TextureHelper.refreshTextureBindState();
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
                overlay = new Color(0x33BBFF);
                break;
        }

        double effectPart = (Math.sin(Math.toRadians(((effectTick) * 8) % 360D)) + 1D) / 4D;
        float br = 0.5F + 0.2F * (2F - ((float) effectPart));
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

        Vector3 dir = toStar.clone().subtract(fromStar);
        Vector3 degLot = dir.clone().crossProduct(new Vector3(0, 0, 1)).normalize().multiply(3D);//.multiply(j == 0 ? 1 : -1);

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
    private Rectangle.Double drawPerk(PerkTreePoint perkPoint, double lowX, double lowY, float pTicks, long effectTick) {
        Point.Double offset = shift2DOffset(lowX, lowY);

        GlStateManager.pushMatrix();
        GlStateManager.translate(offset.x, offset.y, 0);
        GlStateManager.color(1, 1, 1, 1);

        Rectangle draw = perkPoint.renderAtCurrentPos(perkPoint.getPerk().getPerkStatus(Minecraft.getMinecraft().player, Side.CLIENT), effectTick, pTicks);

        GlStateManager.popMatrix();

        if (draw == null) return null;
        double rctWidth = draw.width * sizeHandler.getScalingFactor();
        double rctHeight = draw.height * sizeHandler.getScalingFactor();
        return new Rectangle.Double(offset.x - (rctWidth / 2), offset.y - (rctHeight / 2), rctWidth, rctHeight);
    }

    private Point.Double shift2DOffset(double x, double y) {
        double scaledLeft = this.mousePosition.x - sizeHandler.widthToBorder;
        double scaledTop =  this.mousePosition.y - sizeHandler.heightToBorder;
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
            this.mousePosition = new Point(
                    MathHelper.floor(this.sizeHandler.clampX(this.previousMousePosition.getX() + changeX)),
                    MathHelper.floor(this.sizeHandler.clampY(this.previousMousePosition.getY() + changeY)));
        } else {
            this.mousePosition = new Point(
                    MathHelper.floor(this.sizeHandler.clampX(changeX)),
                    MathHelper.floor(this.sizeHandler.clampY(changeY)));
        }
    }

    private void applyMovedMouseOffset() {
        this.previousMousePosition = new Point(this.mousePosition);
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

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(rectResearchBookmark != null && rectResearchBookmark.contains(p)) {
            GuiJournalProgression.resetJournal();
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalProgression.getJournalInstance());
            return;
        }
        if(rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellationCluster.getConstellationScreen());
        }
    }
}
