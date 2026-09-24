/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.modifier;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ConvertedModifierCache
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class ConvertedModifierCache {

    private static final Map<String, Map<PerkAttributeConverter, Table<PerkAttributeType, ModifierType, PerkAttributeModifier>>> cachedConverters = Maps.newHashMap();

    @Nullable
    public static PerkAttributeModifier getCachedResultModifier(String identifier, PerkAttributeConverter converter, PerkAttributeType targetType, ModifierType targetMode) {
        return cachedConverters.computeIfAbsent(identifier, (u) -> new HashMap<>())
                .computeIfAbsent(converter, (c) -> HashBasedTable.create())
                .get(targetType, targetMode);
    }

    public static void addModifierToCache(String identifier, PerkAttributeConverter converter, PerkAttributeType targetType, ModifierType targetMode, PerkAttributeModifier modifier) {
        cachedConverters.computeIfAbsent(identifier, (u) -> new HashMap<>())
                .computeIfAbsent(converter, (c) -> HashBasedTable.create())
                .put(targetType, targetMode, modifier);
    }
}
