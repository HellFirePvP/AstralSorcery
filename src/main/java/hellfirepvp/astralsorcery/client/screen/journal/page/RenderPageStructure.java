/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.common.data.journal.JournalPage;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.util.MapStream;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import hellfirepvp.observerlib.api.client.StructureRenderer;
import hellfirepvp.observerlib.api.structure.Structure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderPageStructure
 * Created by HellFirePvP
 * Date: 22.08.2019 / 21:18
 */
public class RenderPageStructure extends RenderablePage {

    private final StructureRenderer structureRenderer;
    private final Structure structure;
    private final Vector3 shift;
    private final List<Tuple<ItemStack, ITextComponent>> contentStacks;
    private final ITextComponent name;

    private Optional<Integer> drawSlice = Optional.empty();
    private Rectangle switchView = null, sliceUp = null, sliceDown = null;
    private long totalRenderFrame = 0;

    public RenderPageStructure(@Nullable ResearchNode node, int nodePage, Structure structure, @Nullable ITextComponent name, @Nonnull Vector3 shift) {
        super(node, nodePage);
        this.structure = structure;
        this.structureRenderer = new StructureRenderer(this.structure).setIsolateIndividualBlock(true);
        this.name = name;
        this.shift = shift;
        this.contentStacks = MapStream.ofKeys(
                structure.getAsStacks(this.structureRenderer.getRenderWorld(), Minecraft.getInstance().player),
                stack -> new StringTextComponent(stack.getCount() + "x ").appendSibling(stack.getDisplayName()))
        .toTupleList();
    }

    @Override
    public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
        this.totalRenderFrame++;

        this.renderStructure(offsetX, offsetY, pTicks);
        GlStateManager.color4f(1F, 1F, 1F, 1F);
        float shift = this.renderSizeDescription(offsetX, offsetY + 5);

        if (this.name != null) {
            renderHeadline(offsetX + shift, offsetY + 5, this.name);
        }
    }

    private void renderHeadline(float offsetX, float offsetY, ITextComponent title) {
        float scale = 1.3F;
        GlStateManager.pushMatrix();
        GlStateManager.translated(offsetX, offsetY, 0);
        GlStateManager.scaled(scale, scale, scale);
        GlStateManager.disableDepthTest();
        RenderingDrawUtils.renderStringAtPos(0, 0, null, title.getFormattedText(), 0x00DDDDDD, true);
        GlStateManager.enableDepthTest();
        GlStateManager.popMatrix();
    }

    private float renderSizeDescription(float offsetX, float offsetY) {
        Vector3 size = new Vector3(this.structure.getMaximumOffset()).subtract(this.structure.getMinimumOffset());
        FontRenderer fr = RenderablePage.getFontRenderer();
        float scale = 1.3F;
        String desc = (int) size.getX() + " - " + (int) size.getY() + " - " + (int) size.getZ();
        float length = fr.getStringWidth(desc) * scale;
        GlStateManager.pushMatrix();
        GlStateManager.translated(offsetX, offsetY, 0);
        GlStateManager.scaled(scale, scale, scale);
        GlStateManager.disableDepthTest();
        RenderingDrawUtils.renderStringAtPos(0, 0, fr, desc, 0x00DDDDDD, true);
        GlStateManager.enableDepthTest();
        GlStateManager.popMatrix();
        return length + 8F;
    }

    private void renderStructure(float offsetX, float offsetY, float pTicks) {
        Point.Double renderOffset = renderOffset(offsetX + 8, offsetY);
        this.structureRenderer.render3DSliceGUI(renderOffset.x + shift.getX(), renderOffset.y + shift.getY(), pTicks, drawSlice);
    }

    private Point.Double renderOffset(float stdPageOffsetX, float stdPageOffsetY) {
        return new Point.Double(stdPageOffsetX + JournalPage.DEFAULT_WIDTH * 0.45, stdPageOffsetY + JournalPage.DEFAULT_HEIGHT * 0.6);
    }

    @Override
    public void postRender(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
        Rectangle rect = RenderingDrawUtils.drawInfoStar(offsetX + 160, offsetY + 10, zLevel, 15, pTicks);
        if (rect.contains(mouseX, mouseY)) {
            RenderingDrawUtils.renderBlueTooltip((int) offsetX + 160, (int) offsetY + 10, this.contentStacks, RenderablePage.getFontRenderer(), false);
        }
    }

    @Override
    public boolean propagateMouseDrag(double mouseDX, double mouseDZ) {
        this.structureRenderer.rotateFromMouseDrag((float) mouseDX, (float) mouseDZ);
        return true;
    }

    @Override
    public boolean propagateMouseClick(double mouseX, double mouseZ) {
        if (switchView != null && switchView.contains(mouseX, mouseZ)) {
            if (drawSlice.isPresent()) {
                drawSlice = Optional.empty();
            } else {
                drawSlice = Optional.of(this.structureRenderer.getDefaultSlice());
            }
            SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE, 1F, 1F);
            return true;
        }
        if (sliceUp != null && drawSlice.isPresent() && sliceUp.contains(mouseX, mouseZ)) {
            drawSlice = Optional.of(drawSlice.get() + 1);
            SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE, 1F, 1F);
            return true;
        }
        if (sliceDown != null && drawSlice.isPresent() && sliceDown.contains(mouseX, mouseZ)) {
            drawSlice = Optional.of(drawSlice.get() - 1);
            SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE, 1F, 1F);
            return true;
        }
        return false;
    }
}
