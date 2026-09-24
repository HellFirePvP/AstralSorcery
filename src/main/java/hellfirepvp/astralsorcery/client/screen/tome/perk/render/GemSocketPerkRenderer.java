/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.perk.render;

import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.perk.tree.perk.socket.GemSocketPerk;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocationStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GemSocketPerkRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GemSocketPerkRenderer<T extends PerkTreePoint<A>, A extends AbstractPerk<?>> extends MajorPerkRenderer<T, A> {

    public static final GemSocketPerkRenderer<?, ?> GEM_SOCKET = new GemSocketPerkRenderer<>();

    protected GemSocketPerkRenderer() {}

    @Override
    public boolean needsImmediateRender(PerkTreePoint<?> point) {
        return point.getPerk() instanceof GemSocketPerk;
    }

    @Override
    public void renderImmediate(GuiGraphics graphics, T point, PerkAllocationStatus status, float pTicks, float x, float y, float scale) {
        super.renderImmediate(graphics, point, status, pTicks, x, y, scale);

        if (point.getPerk() instanceof GemSocketPerk gemSocketPerk) {
            ItemStack socketedStack = gemSocketPerk.getGemStack(ResearchManager.getClientProgress());
            if (!socketedStack.isEmpty()) {
                Font fr = IClientItemExtensions.of(socketedStack).getFont(socketedStack, IClientItemExtensions.FontContext.ITEM_COUNT);
                if (fr == null) fr = Minecraft.getInstance().font;

                float socketX = x - (8 * scale);
                float socketY = y - (8 * scale);

                graphics.pose().pushPose();
                graphics.pose().translate(socketX, socketY, 0);
                graphics.pose().scale(scale, scale, 1F);
                graphics.renderItem(socketedStack, 0, 0);
                graphics.renderItemDecorations(fr, socketedStack, 0, 0);
                graphics.pose().popPose();
            }
        }
    }
}
