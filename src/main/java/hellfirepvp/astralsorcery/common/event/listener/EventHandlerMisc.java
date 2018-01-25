/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.auxiliary.SwordSharpenHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerMisc
 * Created by HellFirePvP
 * Date: 04.11.2016 / 23:42
 */
public class EventHandlerMisc {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onToolTip(ItemTooltipEvent event) {
        List<String> toolTip = event.getToolTip();
        ItemStack stack = event.getItemStack();
        if(SwordSharpenHelper.isSwordSharpened(stack)) {
            List<String> newTooltip = new LinkedList<>();
            if(toolTip.size() > 1) {
                newTooltip.addAll(toolTip);
                newTooltip.add(1, I18n.format("misc.sword.sharpened", String.valueOf(Math.round(Config.swordSharpMultiplier * 100)) + "%"));
            } else {
                newTooltip.add(I18n.format("misc.sword.sharpened", String.valueOf(Math.round(Config.swordSharpMultiplier * 100)) + "%"));
                newTooltip.addAll(toolTip);
            }
            toolTip.clear();
            toolTip.addAll(newTooltip);
        }
    }

    //Player CAP stuffs.

    /*@SubscribeEvent
    public void onAttach(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(AstralSorcery.MODID, "constellationperks"), new IPlayerCapabilityPerks.Provider());
        }
    }

    @SubscribeEvent
    public void onClone(PlayerEvent.Clone event) {
        IPlayerCapabilityPerks current = PlayerPerkHelper.getPerks(event.getEntityPlayer());
        IPlayerCapabilityPerks cloned = PlayerPerkHelper.getPerks(event.getEntityPlayer());
        if(cloned != null && current != null) {
            cloned.updatePerks(current.getAttunedConstellation(), current.getCurrentPlayerPerks());
        }
    }*/

}
