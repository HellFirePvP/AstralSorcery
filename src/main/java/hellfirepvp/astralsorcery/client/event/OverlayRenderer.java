/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.common.item.base.client.ItemOverlayRender;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
public class OverlayRenderer {

    public static final OverlayRenderer INSTANCE = new OverlayRenderer();

    private OverlayRenderer() {}

    public void attachEventListeners(IEventBus bus) {
        bus.addListener(EventPriority.LOW, this::onOverlayRender);
    }

    private void onOverlayRender(RenderGameOverlayEvent.Post event) {
        float pTicks = event.getPartialTicks();
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null || Minecraft.getInstance().world == null) {
            return;
        }

        MatrixStack renderStack = event.getMatrixStack();
        for (EquipmentSlotType type : EquipmentSlotType.values()) {
            if (doHudRender(renderStack, player.getItemStackFromSlot(type), pTicks)) {
                break;
            }
        }
    }

    private boolean doHudRender(MatrixStack renderStack, ItemStack heldItem, float pTicks) {
        if (heldItem.isEmpty()) {
            return false;
        }
        Item held = heldItem.getItem();
        if (held instanceof ItemOverlayRender) {
            return ((ItemOverlayRender) held).renderOverlay(renderStack, heldItem, pTicks);
        }
        return false;
    }
}
