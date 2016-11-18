package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.perk.IPlayerCapabilityPerks;
import hellfirepvp.astralsorcery.common.constellation.perk.PlayerPerkHelper;
import hellfirepvp.astralsorcery.common.util.SwordSharpenHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
    public void onToolTip(ItemTooltipEvent event) {
        List<String> toolTip = event.getToolTip();
        ItemStack stack = event.getItemStack();
        if(SwordSharpenHelper.isSwordSharpened(stack)) {
            LinkedList<String> mod = new LinkedList<>();
            mod.addAll(toolTip);
            if(mod.size() >= 2) {
                mod.add(1, I18n.format("misc.sword.sharpened"));
            } else {
                mod.add(I18n.format("misc.sword.sharpened"));
            }
            toolTip.clear();
            toolTip.addAll(mod);
        }
    }

    //Player CAP stuffs.

    @SubscribeEvent
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
            cloned.updatePerks(current.getCurrentPlayerPerks());
        }
    }

}
