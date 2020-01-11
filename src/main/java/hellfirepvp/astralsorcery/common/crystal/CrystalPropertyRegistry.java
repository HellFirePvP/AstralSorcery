/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crystal.property.PropertyConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalPropertyRegistry
 * Created by HellFirePvP
 * Date: 29.01.2019 / 21:34
 */
public class CrystalPropertyRegistry {

    public static final CrystalPropertyRegistry INSTANCE = new CrystalPropertyRegistry();

    private CrystalPropertyRegistry() {}

    @Nullable
    public CrystalProperty getConstellationProperty(IConstellation cst) {
        return RegistriesAS.REGISTRY_CRYSTAL_PROPERTIES.getValues()
                .stream()
                .filter(prop -> prop instanceof PropertyConstellation &&
                        ((PropertyConstellation) prop).getConstellation().equals(cst))
                .findFirst().orElse(null);
    }

}
