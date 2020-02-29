/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.common.item.base.render.ItemOverlayRender;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OverlayRenderHelper
 * Created by HellFirePvP
 * Date: 23.02.2020 / 17:15
 */
public class OverlayRenderHelper {

    public static final OverlayRenderHelper INSTANCE = new OverlayRenderHelper();

    private OverlayRenderHelper() {}

    public void attachEventListeners(IEventBus bus) {
        bus.addListener(EventPriority.LOW, this::onOverlayRender);
    }

    private void onOverlayRender(RenderGameOverlayEvent.Post event) {
        float pTicks = event.getPartialTicks();
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if (Minecraft.getInstance().player == null || Minecraft.getInstance().world == null) {
            return;
        }

        if (!doHudRender(Minecraft.getInstance().player.getHeldItem(Hand.MAIN_HAND), pTicks)) {
            doHudRender(Minecraft.getInstance().player.getHeldItem(Hand.OFF_HAND), pTicks);
        }
    }

    private boolean doHudRender(ItemStack heldItem, float pTicks) {
        if (heldItem.isEmpty()) {
            return false;
        }
        Item held = heldItem.getItem();
        if (held instanceof ItemOverlayRender) {
            return ((ItemOverlayRender) held).renderOverlay(heldItem, pTicks);
        }
        return false;
    }
}
