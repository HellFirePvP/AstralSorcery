/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.common.item.base.render.ItemHeldRender;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemHeldRenderHelper
 * Created by HellFirePvP
 * Date: 28.02.2020 / 20:01
 */
public class ItemHeldEffectRenderer {

    public static final ItemHeldEffectRenderer INSTANCE = new ItemHeldEffectRenderer();

    private ItemHeldEffectRenderer() {}

    public void attachEventListeners(IEventBus bus) {
        bus.addListener(EventPriority.LOWEST, this::onHeldRender);
    }

    private void onHeldRender(RenderWorldLastEvent event) {
        float pTicks = event.getPartialTicks();

        if (Minecraft.getInstance().player == null || Minecraft.getInstance().world == null) {
            return;
        }

        if (!doHeldRender(Minecraft.getInstance().player.getHeldItem(Hand.MAIN_HAND), pTicks)) {
            doHeldRender(Minecraft.getInstance().player.getHeldItem(Hand.OFF_HAND), pTicks);
        }
    }

    private boolean doHeldRender(ItemStack heldItem, float pTicks) {
        if (heldItem.isEmpty()) {
            return false;
        }
        Item held = heldItem.getItem();
        if (held instanceof ItemHeldRender) {
            return ((ItemHeldRender) held).renderInHand(heldItem, pTicks);
        }
        return false;
    }
}
