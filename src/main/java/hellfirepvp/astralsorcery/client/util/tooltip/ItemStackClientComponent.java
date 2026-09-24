/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.common.util.tooltip.ItemStackTooltip;
import hellfirepvp.observerlib.client.util.LightmapUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemStackClientComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ItemStackClientComponent implements ClientTooltipComponent {

    private final ItemStack stack;
    private final Component display;

    public ItemStackClientComponent(ItemStack stack) {
        this(stack, stack.getCount());
    }

    public ItemStackClientComponent(ItemStack stack, int count) {
        this.stack = stack.copyWithCount(1);
        this.display = Component.literal(count + "x ")
                .append(stack.getHoverName()).withStyle(stack.getRarity().getStyleModifier());
    }

    public static ItemStackClientComponent create(ItemStackTooltip component) {
        return new ItemStackClientComponent(component.stack(), component.count());
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public int getWidth(Font font) {
        // Stack, padding, name
        return 18 + 4 + font.width(this.display);
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
        font.drawInBatch(this.display, x + 18 + 4, y + 2,
                0xFFFFFFFF, true, matrix, bufferSource,
                Font.DisplayMode.NORMAL, 0x00000000, LightmapUtil.getPackedFullbrightCoords());
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        RenderSystem.enableDepthTest();

        guiGraphics.renderFakeItem(this.stack, x + 1, y + 1);
        guiGraphics.renderItemDecorations(font, this.stack, x + 1, y + 1);
        RenderSystem.disableBlend();
        guiGraphics.bufferSource().endBatch();
    }
}
