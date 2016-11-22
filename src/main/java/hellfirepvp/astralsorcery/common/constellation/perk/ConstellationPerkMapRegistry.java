package hellfirepvp.astralsorcery.common.constellation.perk;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.ConstellationBase;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationPerkMapRegistry
 * Created by HellFirePvP
 * Date: 22.11.2016 / 12:33
 */
public class ConstellationPerkMapRegistry {

    private static Map<ConstellationBase.Major, ConstellationPerkMap> registeredPerks = new HashMap<>();

    public static void registerPerkMap(ConstellationBase.Major constellation, ConstellationPerkMap perkMap) {
        if(registeredPerks.containsKey(constellation)) {
            AstralSorcery.log.info("[AstralSorcery] PerkMapRegistry already contains mapping for '" + constellation.getUnlocalizedName() + "' - overwriting...");
        }
        registeredPerks.put(constellation, perkMap);
    }

    @Nullable
    public static ConstellationPerkMap getPerkMap(ConstellationBase.Major constellation) {
        return registeredPerks.get(constellation);
    }

}
