/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;

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

    private static Map<IMajorConstellation, ConstellationPerkMap> registeredPerks = new HashMap<>();

    public static void registerPerkMap(IMajorConstellation constellation, ConstellationPerkMap perkMap) {
        if(registeredPerks.containsKey(constellation)) {
            AstralSorcery.log.info("[AstralSorcery] PerkMapRegistry already contains mapping for '" + constellation.getUnlocalizedName() + "' - overwriting...");
        }
        registeredPerks.put(constellation, perkMap);
    }

    @Nullable
    public static ConstellationPerkMap getPerkMap(IMajorConstellation constellation) {
        return registeredPerks.get(constellation);
    }

}
