/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.helper;

import com.mojang.datafixers.util.Either;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityLumenDisplay;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderLumenDisplayOverlay
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderLumenDisplayOverlay {

    public static final ResourceLocation LUMEN_LAYER_ID = AstralSorcery.key("lumen_display_overlay");
    public static final LayerRenderer RENDERER = new LayerRenderer();

    public static void registerLayers(RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.CROSSHAIR, LUMEN_LAYER_ID, RENDERER);
    }

    public static class LayerRenderer implements LayeredDraw.Layer {

        @Override
        public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
            if (Minecraft.getInstance().options.hideGui) return;
            if (Minecraft.getInstance().level == null) return;

            HitResult hit = Minecraft.getInstance().hitResult;
            if (!(hit instanceof BlockHitResult blockHitResult) || blockHitResult.getType() == HitResult.Type.MISS) {
                return;
            }

            Level level = Minecraft.getInstance().level;
            MiscUtil.getTileAt(level, blockHitResult.getBlockPos(), TileEntityLumenDisplay.class, true).ifPresent(tile -> {
                tile.getDisplayTooltip().ifPresent(cmp -> {
                    Font font = Minecraft.getInstance().font;
                    List<ClientTooltipComponent> components = ClientHooks.gatherTooltipComponentsFromElements(ItemStack.EMPTY,
                            List.of(Either.right(cmp)), 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), font);

                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);
                    int x = guiGraphics.guiWidth() / 2;
                    int y = guiGraphics.guiHeight() / 2;

                    for (int i = 0; i < components.size(); i++) {
                        ClientTooltipComponent tooltip = components.get(i);
                        tooltip.renderText(font, x, y, guiGraphics.pose().last().pose(), guiGraphics.bufferSource());
                        y += tooltip.getHeight() + (i == 0 ? 2 : 0);
                    }

                    y = guiGraphics.guiHeight() / 2;

                    for (int i = 0; i < components.size(); i++) {
                        ClientTooltipComponent tooltip = components.get(i);
                        tooltip.renderImage(font, x, y, guiGraphics);
                        y += tooltip.getHeight() + (i == 0 ? 2 : 0);
                    }
                    guiGraphics.pose().popPose();
                });
            });
        }
    }
}
