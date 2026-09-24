/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.page;

import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.screen.element.ScaledStringWidget;
import hellfirepvp.astralsorcery.client.screen.element.TomeInfoStarElement;
import hellfirepvp.astralsorcery.client.screen.element.TomeSliceArrowElement;
import hellfirepvp.astralsorcery.client.screen.element.TomeToggleButtonElement;
import hellfirepvp.astralsorcery.client.screen.tome.TomePagesScreen;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.client.util.structure.StructureRenderer;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import hellfirepvp.astralsorcery.common.util.tooltip.ItemStackTooltip;
import hellfirepvp.observerlib.api.util.StructureBlockArray;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPageStructure
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@OnlyIn(Dist.CLIENT)
public class RenderPageStructure extends RenderPage {

    private final StructureBlockArray structure;
    private final AABB structureBounds;
    private StructureRenderer structureRenderer = null;

    private TomeToggleButtonElement sliceToggle;
    private TomeSliceArrowElement sliceUp, sliceDown;
    private StringWidget sliceDisplay;

    public RenderPageStructure(@Nullable ResearchNode node, int nodePage, StructureBlockArray structure) {
        super(node, nodePage);
        this.structure = structure;
        this.structureBounds = AABB.of(BoundingBox.fromCorners(this.structure.getMinimumOffset(), this.structure.getMaximumOffset()));
    }

    @Override
    public void init(TomePagesScreen parent, int pageX, int pageY) {
        super.init(parent, pageX, pageY);

        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        this.structureRenderer = new StructureRenderer(level.registryAccess(), this.structure);

        this.initSliceInputs(parent);
        this.initStructureDescription(parent);
        this.updateSliceDescription();
    }

    private void initSliceInputs(TomePagesScreen parent) {
        this.sliceToggle = new TomeToggleButtonElement(this.pageX + 154, this.pageY + 10, showSlice -> {
            if (showSlice) {
                this.structureRenderer.switchToSliceRender(this.structureRenderer.getDefaultSlice());
            } else {
                this.structureRenderer.switchToFullRender();
            }
            PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_PAGE).forUI().play();
            this.updateSliceInputVisibility();
            this.updateSliceDescription();
        });
        this.sliceToggle.setIsSet(this.structureRenderer.rendersAsSlice());
        this.sliceDown = new TomeSliceArrowElement(this.pageX + 160, this.pageY + 28, false, () -> {
            this.structureRenderer.switchToSliceRender(this.structureRenderer.getCurrentSlice() - 1);
            PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_PAGE).forUI().play();
            this.updateSliceInputVisibility();
            this.updateSliceDescription();
        });
        this.sliceUp = new TomeSliceArrowElement(this.pageX + 148, this.pageY + 28, true, () -> {
            this.structureRenderer.switchToSliceRender(this.structureRenderer.getCurrentSlice() + 1);
            PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_PAGE).forUI().play();
            this.updateSliceInputVisibility();
            this.updateSliceDescription();
        });
        this.sliceDown.visible = false;
        this.sliceUp.visible = false;

        parent.addRenderableWidget(this.sliceToggle);
        parent.addRenderableWidget(this.sliceDown);
        parent.addRenderableWidget(this.sliceUp);
    }

    private void updateSliceInputVisibility() {
        if (this.sliceToggle == null) return;

        if (!this.structureRenderer.rendersAsSlice()) {
            this.sliceDown.visible = false;
            this.sliceUp.visible = false;
            return;
        }

        int slice = this.structureRenderer.getCurrentSlice();
        this.sliceDown.visible = this.structureRenderer.hasSlice(slice - 1);
        this.sliceUp.visible = this.structureRenderer.hasSlice(slice + 1);
    }

    private void initStructureDescription(TomePagesScreen parent) {
        Vec3i size = this.structure.getMaximumOffset().subtract(this.structure.getMinimumOffset()).offset(1, 1, 1);
        Component sizeCmp = Component.literal(String.format("%s - %s - %s", size.getX(), size.getY(), size.getZ()));
        Font font = Minecraft.getInstance().font;
        StringWidget sizeWidget = new ScaledStringWidget(this.pageX + 10, this.pageY, font.width(sizeCmp), font.lineHeight, 1.3F, sizeCmp, font);
        sizeWidget.setColor(ColorsAS.TOME_TEXT_COLOR.getColor());
        sizeWidget.alignLeft();
        parent.addRenderableWidget(sizeWidget);

        Component templateStr = Component.literal(String.format("%s / %s", -999, -999));
        this.sliceDisplay = new ScaledStringWidget(this.pageX + 10, this.pageY + 14, font.width(templateStr), font.lineHeight, 1.3F, templateStr, font);
        this.sliceDisplay.setColor(ColorsAS.TOME_TEXT_COLOR.getColor());
        this.sliceDisplay.alignLeft();
        this.sliceDisplay.visible = false;
        parent.addRenderableWidget(this.sliceDisplay);

        List<ItemStack> stacks = this.structure.getAsStacks(Minecraft.getInstance().level, Minecraft.getInstance().player);
        List<ItemStackTooltip> stackDisplays = stacks.stream()
                .filter(s -> !s.isEmpty())
                .map(stack -> new ItemStackTooltip(stack, stack.getCount()))
                .toList();
        TomeInfoStarElement infoStar = TomeInfoStarElement.ofTooltipComponents(this.pageX + 126, this.pageY + 2, () -> stackDisplays);
        parent.addRenderableWidget(infoStar);
    }

    private void updateSliceDescription() {
        if (this.sliceDisplay == null) return;

        if (!this.structureRenderer.rendersAsSlice()) {
            this.sliceDisplay.visible = false;
            return;
        }

        int slice = this.structureRenderer.getCurrentSlice();
        Vec3i min = this.structure.getMinimumOffset();
        Vec3i max = this.structure.getMaximumOffset();
        Component sliceCmp = Component.literal(String.format("%s / %s", slice - min.getY() + 1, max.getY() - min.getY() + 1));
        this.sliceDisplay.setMessage(sliceCmp);
        this.sliceDisplay.visible = true;
    }

    @Override
    public boolean propagateMouseDrag(double mouseX, double mouseY, double mouseDX, double mouseDZ) {
        this.structureRenderer.rotateFromMouseDrag((float) mouseDX, (float) mouseDZ);
        return super.propagateMouseDrag(mouseX, mouseY, mouseDX, mouseDZ);
    }

    @Override
    public boolean propagateMouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.structureRenderer.zoom((float) scrollY);
        return super.propagateMouseScroll(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {
        int size = 140;
        guiGraphics.enableScissor(x + TomePage.DEFAULT_WIDTH / 2 - size / 2, y + TomePage.DEFAULT_HEIGHT - size - 20,
                x + TomePage.DEFAULT_WIDTH / 2 + size / 2, y + TomePage.DEFAULT_HEIGHT - 20);

        this.structureRenderer.render(new PoseStack(),
                x + TomePage.DEFAULT_WIDTH / 2F, y + TomePage.DEFAULT_HEIGHT - size / 2F - 20, pTicks);
        guiGraphics.disableScissor();
    }
}
