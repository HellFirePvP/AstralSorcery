/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import net.minecraft.util.DamageSource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationDraconicEvolution
 * Created by HellFirePvP
 * Date: 29.10.2017 / 11:37
 */
public class ModIntegrationDraconicEvolution {

    private static String[] matchingTypes = {
            "de.GuardianFireball",
            "de.GuardianEnergyBall",
            "de.GuardianChaosBall",
            "chaosImplosion",
            "damage.de.fusionExplode",
            "de.islandImplode",
    };

    private static String chaosDamageClassDescr = "com.brandon3055.draconicevolution.lib.DEDamageSources.DamageSourceChaos";
    private static Class<?> chaosDmgClass;

    public static boolean isChaosDamage(DamageSource source) {
        if(chaosDmgClass != null) {
            if(chaosDmgClass.isAssignableFrom(source.getClass())) {
                return true;
            }
        }
        String type = source.damageType;
        if(type != null) {
            for (String match : matchingTypes) {
                if(match != null && match.equalsIgnoreCase(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    static {
        try {
            chaosDmgClass = Class.forName(chaosDamageClassDescr);
        } catch (Exception exc) {
            chaosDmgClass = null;
        }
    }

}
