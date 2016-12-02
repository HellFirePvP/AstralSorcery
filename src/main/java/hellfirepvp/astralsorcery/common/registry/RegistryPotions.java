package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.potion.PotionCheatDeath;
import hellfirepvp.astralsorcery.common.potion.PotionHunt;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryPotions
 * Created by HellFirePvP
 * Date: 13.11.2016 / 01:32
 */
public class RegistryPotions {

    public static PotionCheatDeath potionCheatDeath;
    //public static PotionHunt potionHunt;

    public static void init() {
        potionCheatDeath = registerPotion(new PotionCheatDeath());
        //potionHunt = registerPotion(new PotionHunt());
    }

    private static <T extends Potion> T registerPotion(T potion) {
        potion.setRegistryName(potion.getClass().getSimpleName());
        GameRegistry.register(potion);
        return potion;
    }

}
