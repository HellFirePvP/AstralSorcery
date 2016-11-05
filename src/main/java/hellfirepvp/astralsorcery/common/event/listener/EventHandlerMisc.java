package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.common.util.SwordSharpenHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
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
                mod.add(1, I18n.translateToLocal("misc.sword.sharpened"));
            } else {
                mod.add(I18n.translateToLocal("misc.sword.sharpened"));
            }
            toolTip.clear();
            toolTip.addAll(mod);
        }
    }

}
