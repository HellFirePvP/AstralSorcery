/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.perk.type.vanilla.VanillaPerkAttributeType;
import net.minecraft.entity.ai.attributes.IAttribute;

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

    private static Map<IAttribute, PerkAttributeType> vanillaTypes = new HashMap<>();

    public static void register(PerkAttributeType type) {
        if (type instanceof VanillaPerkAttributeType) {
            vanillaTypes.put(((VanillaPerkAttributeType) type).getAttribute(), type);
        }
    }

    @Nullable
    public static PerkAttributeType findVanillaType(IAttribute attribute) {
        return vanillaTypes.get(attribute);
    }
}
