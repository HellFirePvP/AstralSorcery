package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.potion.PotionBleed;
import hellfirepvp.astralsorcery.common.potion.PotionCheatDeath;
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
    public static PotionBleed potionBleed;

    public static void init() {
        potionCheatDeath = registerPotion(new PotionCheatDeath());
        potionBleed = registerPotion(new PotionBleed());
    }

    private static <T extends Potion> T registerPotion(T potion) {
        potion.setRegistryName(potion.getClass().getSimpleName());
        GameRegistry.register(potion);
        return potion;
    }

}
