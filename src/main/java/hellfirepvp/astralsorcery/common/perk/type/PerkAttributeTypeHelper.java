/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.type.vanilla.VanillaPerkAttributeType;
import net.minecraft.entity.ai.attributes.Attribute;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeTypeHelper
 * Created by HellFirePvP
 * Date: 08.08.2019 / 17:06
 */
public class PerkAttributeTypeHelper {

    @Nullable
    public static PerkAttributeType findVanillaType(Attribute attribute) {
        return RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.getValues().stream()
                .filter(type -> type instanceof VanillaPerkAttributeType)
                .map(type -> (VanillaPerkAttributeType) type)
                .filter(type -> type.getAttribute().equals(attribute))
                .findFirst()
                .map(type -> (PerkAttributeType) type)
                .orElse(null);
    }
}
