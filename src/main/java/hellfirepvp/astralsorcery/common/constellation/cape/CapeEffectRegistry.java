/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectRegistry
 * Created by HellFirePvP
 * Date: 10.10.2017 / 00:46
 */
public class CapeEffectRegistry {

    private static Map<IConstellation, CapeEffectFactory<? extends CapeArmorEffect>> armorEffectMap = new HashMap<>();

    @Nullable
    public static CapeEffectFactory<? extends CapeArmorEffect> getArmorEffect(IConstellation cst) {
        return armorEffectMap.get(cst);
    }

    public static void registerCapeArmorEffect(IConstellation cst, Class<? extends CapeArmorEffect> armorEffectClass) {
        if(getArmorEffect(cst) != null) return;
        armorEffectMap.put(cst, new CapeEffectFactory<>(armorEffectClass));
    }

}
