/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer;
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
        float shift = this.renderSizeDescription(offsetX, offsetY + 5, zLevel);

        if (this.name != null) {
            renderHeadline(offsetX + shift, offsetY + 5, zLevel, this.name);
        }
    }

    private void renderHeadline(float offsetX, float offsetY, float zLevel, ITextComponent title) {
        float scale = 1.3F;

        RenderSystem.pushMatrix();
        RenderSystem.translated(offsetX, offsetY, 0);
        RenderSystem.scaled(scale, scale, scale);
        RenderSystem.disableDepthTest();

        RenderingDrawUtils.renderStringAtPos(0, 0, zLevel, null, title.getFormattedText(), 0x00DDDDDD, true);

        RenderSystem.enableDepthTest();
        RenderSystem.popMatrix();
    }

    private float renderSizeDescription(float offsetX, float offsetY, float zLevel) {
        Vector3 size = new Vector3(this.structure.getMaximumOffset()).subtract(this.structure.getMinimumOffset());
        FontRenderer fr = RenderablePage.getFontRenderer();
        float scale = 1.3F;
        String desc = (int) size.getX() + " - " + (int) size.getY() + " - " + (int) size.getZ();
        float length = fr.getStringWidth(desc) * scale;

        RenderSystem.pushMatrix();
        RenderSystem.translated(offsetX, offsetY, 0);
        RenderSystem.scaled(scale, scale, scale);
        RenderSystem.disableDepthTest();

        RenderingDrawUtils.renderStringAtPos(0, 0, zLevel, fr, desc, 0x00DDDDDD, true);

        RenderSystem.enableDepthTest();
        RenderSystem.popMatrix();
        return length + 8F;
    }

    private void renderStructure(float offsetX, float offsetY, float pTicks) {
        Point.Double renderOffset = renderOffset(offsetX + 8, offsetY);
        this.structureRenderer.render3DSliceGUI(new MatrixStack(), renderOffset.x + shift.getX(), renderOffset.y + shift.getY(), pTicks, drawSlice);
    }

    private Point.Double renderOffset(float stdPageOffsetX, float stdPageOffsetY) {
        return new Point.Double(stdPageOffsetX + JournalPage.DEFAULT_WIDTH * 0.45, stdPageOffsetY + JournalPage.DEFAULT_HEIGHT * 0.6);
    }

    @Override
    public void postRender(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
        MatrixStack renderStack = new MatrixStack();
        renderStack.translate(offsetX + 160, offsetY + 10, zLevel);
        Rectangle rect = RenderingDrawUtils.drawInfoStar(renderStack, IDrawRenderTypeBuffer.defaultBuffer(), 15, pTicks);
        rect.translate((int) (offsetX + 160), (int) (offsetY + 10));
        if (rect.contains(mouseX, mouseY)) {
            RenderingDrawUtils.renderBlueTooltip(offsetX + 160, offsetY + 10, zLevel + 150, this.contentStacks, RenderablePage.getFontRenderer(), false);
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
