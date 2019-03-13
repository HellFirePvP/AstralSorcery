/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.event.APIRegistryEvent;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.BaseTweaker;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTree
 * Created by HellFirePvP
 * Date: 19.08.2018 / 21:49
 */
@ZenClass("mods.astralsorcery.PerkTree")
public class PerkTree extends BaseTweaker {

    private static List<String> removedPerks = Lists.newLinkedList();
    private static List<String> disabledPerks = Lists.newLinkedList();
    private static Map<String, Double> perkModifiers = Maps.newHashMap();

    @ZenMethod
    public static void disablePerk(String perkRegistryName) {
        disabledPerks.add(perkRegistryName);
    }

    @ZenMethod
    public static void removePerk(String perkRegistryName) {
        removedPerks.add(perkRegistryName);
    }

    @ZenMethod
    public static void modifyPerk(String perkRegistryName, double multiplier) {
        perkModifiers.put(perkRegistryName, multiplier);
    }

    @SubscribeEvent
    public void onPerkRemoval(APIRegistryEvent.PerkPostRemove event) {
        if (removedPerks.contains(event.getPerk().getRegistryName().toString())) {
            event.setRemoved(true);
        }
    }

    @SubscribeEvent
    public void onPerkDisable(APIRegistryEvent.PerkDisable event) {
        if (disabledPerks.contains(event.getPerk().getRegistryName().toString())) {
            event.setPerkDisabled(true);
        }
    }

    public static double getMultiplier(AbstractPerk perk) {
        return perkModifiers.getOrDefault(perk.getRegistryName().toString(), 1.0D);
    }

}
