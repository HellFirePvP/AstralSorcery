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
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalPerkTree
 * Created by HellFirePvP
 * Date: 01.07.2018 / 01:14
 */
public class GuiJournalPerkTree extends GuiScreenJournal {

    private static final BindableResource textureResBack = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiresbg2");

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

        TextureHelper.refreshTextureBindState();
        GlStateManager.popMatrix();
    }

    private void drawPerkTree(float partialTicks) {
        for (PerkTreePoint perkPoint : PerkTree.INSTANCE.getPerkPoints()) {
            Point offset = perkPoint.getOffset();
            double lX = sizeHandler.evRelativePosX(offset.x - perkPoint.getRenderSize());
            double rX = sizeHandler.evRelativePosX(offset.x + perkPoint.getRenderSize());
            double lY = sizeHandler.evRelativePosY(offset.y - perkPoint.getRenderSize());
            double rY = sizeHandler.evRelativePosY(offset.y + perkPoint.getRenderSize());
            Rectangle.Double perkRect = drawPerk(perkPoint, lX, lY, rX, rY, partialTicks, ClientScheduler.getClientTick() + offset.x + offset.y);
            if (perkRect != null) {
                if (guiBox.isInBox(perkRect.x, perkRect.y) ||
                        guiBox.isInBox(perkRect.x + perkRect.width, perkRect.y + perkRect.height)) {
                    thisFramePerks.put(perkRect, perkPoint.getPerk());
                }
            }
        }
    }

    @Nullable
    private Rectangle.Double drawPerk(PerkTreePoint perkPoint, double lowX, double lowY, double highX, double highY, float pTicks, long effectTick) {
        double scaledLeft = this.mousePosition.x - sizeHandler.widthToBorder;
        double scaledTop =  this.mousePosition.y - sizeHandler.heightToBorder;
        double xAdd = lowX - scaledLeft;
        double yAdd = lowY - scaledTop;
        double offsetX = guiOffsetX + xAdd;
        double offsetY = guiOffsetY + yAdd;

        GlStateManager.pushMatrix();
        GlStateManager.translate(offsetX, offsetY, 0);

        Rectangle draw = perkPoint.renderAtCurrentPos(PerkTreePoint.AllocationStatus.ALLOCATED, effectTick, pTicks);

        GlStateManager.popMatrix();

        if (draw == null) return null;
        return new Rectangle.Double(lowX, lowY, draw.width * sizeHandler.getScalingFactor(), draw.height * sizeHandler.getScalingFactor());
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
        float br = 0.6F;

        GlStateManager.color(br, br, br, 1F);
        GlStateManager.disableBlend();
        textureResBack.bind();
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
