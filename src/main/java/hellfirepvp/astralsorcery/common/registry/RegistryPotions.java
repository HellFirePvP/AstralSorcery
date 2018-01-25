/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.potion.PotionBleed;
import hellfirepvp.astralsorcery.common.potion.PotionCheatDeath;
import hellfirepvp.astralsorcery.common.potion.PotionSpellPlague;
import net.minecraft.potion.Potion;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryPotions
 * Created by HellFirePvP
 * Date: 13.11.2016 / 01:32
 */
public class RegistryPotions {

    public static PotionCheatDeath potionCheatDeath;
    public static PotionBleed potionBleed;
    public static PotionSpellPlague potionSpellPlague;

    public static void init() {
        potionCheatDeath = registerPotion(new PotionCheatDeath());
        potionBleed = registerPotion(new PotionBleed());
        potionSpellPlague = registerPotion(new PotionSpellPlague());
    }

    private static <T extends Potion> T registerPotion(T potion) {
        potion.setRegistryName(potion.getClass().getSimpleName());
        CommonProxy.registryPrimer.register(potion);
        return potion;
    }

}
