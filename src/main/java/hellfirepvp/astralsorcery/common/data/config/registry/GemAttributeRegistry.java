/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataAdapter;
import hellfirepvp.astralsorcery.common.data.config.registry.sets.GemAttributeEntry;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GemAttributeRegistry
 * Created by HellFirePvP
 * Date: 01.09.2019 / 19:22
 */
public class GemAttributeRegistry extends ConfigDataAdapter<GemAttributeEntry> {

    public static final GemAttributeRegistry INSTANCE = new GemAttributeRegistry();

    private GemAttributeRegistry() {}

    @Override
    public List<GemAttributeEntry> getDefaultValues() {
        return new ArrayList<GemAttributeEntry>() {
            {
                add(new GemAttributeEntry(PerkAttributeTypesAS.ATTR_TYPE_HEALTH,                    12));
                add(new GemAttributeEntry(PerkAttributeTypesAS.ATTR_TYPE_MOVESPEED,                 8));
                add(new GemAttributeEntry(PerkAttributeTypesAS.ATTR_TYPE_ARMOR,                     8));
                add(new GemAttributeEntry(PerkAttributeTypesAS.ATTR_TYPE_REACH,                     4));
                add(new GemAttributeEntry(PerkAttributeTypesAS.ATTR_TYPE_ATTACK_SPEED,              2));
                add(new GemAttributeEntry(PerkAttributeTypesAS.ATTR_TYPE_MELEE_DAMAGE,              8));
                add(new GemAttributeEntry(PerkAttributeTypesAS.ATTR_TYPE_PROJ_DAMAGE,               8));
                add(new GemAttributeEntry(PerkAttributeTypesAS.ATTR_TYPE_LIFE_RECOVERY,             2));
                add(new GemAttributeEntry(PerkAttributeTypesAS.ATTR_TYPE_INC_HARVEST_SPEED,         2));
                add(new GemAttributeEntry(PerkAttributeTypesAS.ATTR_TYPE_INC_CRIT_CHANCE,           4));
                add(new GemAttributeEntry(PerkAttributeTypesAS.ATTR_TYPE_INC_CRIT_MULTIPLIER,       4));
                add(new GemAttributeEntry(PerkAttributeTypesAS.ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST,  2));
                add(new GemAttributeEntry(PerkAttributeTypesAS.ATTR_TYPE_INC_DODGE,                 2));
                add(new GemAttributeEntry(PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EXP,              1));
            }
        };
    }

    @Override
    public String getFileName() {
        return "gem_attributes";
    }

    @Override
    public String getCommentDescription() {
        return "Format: '<attributeRegistryName>;<integerWeight>' Defines the attributes Perk Gems can roll.";
    }

    @Override
    public String getTranslationKey() {
        return translationKey("data");
    }

    @Override
    public Predicate<Object> getValidator() {
        return obj -> obj instanceof String && GemAttributeEntry.deserialize((String) obj) != null;
    }

    @Nullable
    @Override
    public GemAttributeEntry deserialize(String string) throws IllegalArgumentException {
        return GemAttributeEntry.deserialize(string);
    }
}
