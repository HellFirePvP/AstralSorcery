/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderQuadUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FakeSlotElement
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FakeSlotElement extends AbstractWidget {

    private final AbstractRenderTexture texture;
    private final Supplier<ItemStack> itemStackDisplaySupplier;
    private Consumer<Integer> onClick = slot -> {};

    public FakeSlotElement(int x, int y, AbstractRenderTexture texture, Supplier<ItemStack> itemStackDisplaySupplier) {
        super(x, y, 18, 18, Component.empty());
        this.texture = texture;
        this.itemStackDisplaySupplier = itemStackDisplaySupplier;
    }

    public FakeSlotElement setOnClick(Consumer<Integer> onClick) {
        this.onClick = onClick;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        this.texture.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            RenderQuadUtil.rect(buf, guiGraphics.pose(), this.getX(), this.getY(), this.getWidth(), this.getHeight())
                    .draw();
        });
        RenderSystem.disableBlend();

        ItemStack stackToRender = this.itemStackDisplaySupplier.get();
        if (!stackToRender.isEmpty()) {
            Font fr = IClientItemExtensions.of(stackToRender).getFont(stackToRender, IClientItemExtensions.FontContext.TOOLTIP);
            if (fr == null) fr = Minecraft.getInstance().font;
            guiGraphics.renderItem(stackToRender, this.getX() + 1, this.getY() + 1);
            guiGraphics.renderItemDecorations(fr, stackToRender, this.getX() + 1, this.getY() + 1);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isActive()) {
            if (this.isValidClickButton(button)) {
                boolean flag = this.clicked(mouseX, mouseY);
                if (flag) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    this.onClick(mouseX, mouseY, button);
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        super.onClick(mouseX, mouseY, button);
        this.onClick.accept(button);
    }

    @Override
    public void playDownSound(SoundManager handler) {
        if (!this.itemStackDisplaySupplier.get().isEmpty()) {
            super.playDownSound(handler);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
