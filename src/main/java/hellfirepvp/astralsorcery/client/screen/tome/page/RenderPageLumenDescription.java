/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.page;

import com.mojang.datafixers.util.Either;
import hellfirepvp.astralsorcery.client.util.tooltip.StoredLumenClientComponent;
import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBinding;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBindingType;
import hellfirepvp.astralsorcery.common.lumen.binding.data.LumenBindingTypeLoader;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import hellfirepvp.astralsorcery.common.util.tooltip.StoredLumenDisplayTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.client.ClientHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPageLumenDescription
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderPageLumenDescription extends RenderPage {

    private final long uuidSeed;
    private final Lumen lumen;
    private final List<LumenBindingType.SlotType> slotTypes;

    public RenderPageLumenDescription(@Nullable ResearchNode node, int nodePage, Lumen lumen, List<LumenBindingType.SlotType> slotTypes) {
        super(node, nodePage);
        this.uuidSeed = RandomSource.create().nextLong();
        this.lumen = lumen;
        this.slotTypes = slotTypes;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {
        this.lumen.getRegistryKey().ifPresent(key -> {
            LumenBindingTypeLoader.getInstance().getLumenBindingType(LogicalSide.CLIENT, key).ifPresent(bindingKey -> {
                LumenBindingTypeLoader.getInstance().getBindingType(LogicalSide.CLIENT, bindingKey).ifPresent(bindingType -> {
                    int offsetY = y;
                    for (LumenBindingType.SlotType slotType : slotTypes) {
                        LumenBinding binding = bindingType.getBinding(slotType).orElse(null);
                        if (binding != null) {
                            StoredLumenComponent cmp = StoredLumenComponent.EMPTY
                                    .updateLumenStack(this.lumen, StoredLumenComponent.DEFAULT_CAPACITY, StoredLumenComponent.DEFAULT_CAPACITY)
                                    .applyBinding(this.lumen, bindingKey);

                            long seed = this.uuidSeed ^ (long) bindingKey.hashCode() << 32 | slotType.hashCode();
                            UUID effectId = new UUID(seed, seed);
                            StoredLumenDisplayTooltip tooltip = StoredLumenDisplayTooltip.of(slotType.getDisplayStack(), effectId, cmp, TomePage.DEFAULT_WIDTH);
                            guiGraphics.renderItem(slotType.getDisplayStack(), x + 68, offsetY - 3);
                            offsetY = renderComponent(guiGraphics, tooltip, x, offsetY) + 2;
                        }
                    }
                });
            });
        });
    }

    private int renderComponent(GuiGraphics guiGraphics, StoredLumenDisplayTooltip cmp, int x, int y) {
        Font font = Minecraft.getInstance().font;
        List<ClientTooltipComponent> components = ClientHooks.gatherTooltipComponentsFromElements(ItemStack.EMPTY,
                List.of(Either.right(cmp)), 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), font);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);

        int textY = y;
        for (int i = 0; i < components.size(); i++) {
            ClientTooltipComponent tooltip = components.get(i);
            tooltip.renderText(font, x, textY, guiGraphics.pose().last().pose(), guiGraphics.bufferSource());
            textY += tooltip.getHeight() + (i == 0 ? 2 : 0);
        }

        int cmpY = y;
        for (int i = 0; i < components.size(); i++) {
            ClientTooltipComponent tooltip = components.get(i);
            tooltip.renderImage(font, x, cmpY, guiGraphics);
            cmpY += tooltip.getHeight() + (i == 0 ? 2 : 0);
        }
        guiGraphics.pose().popPose();
        return Math.max(textY, cmpY);
    }
}
