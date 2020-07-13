/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataAdapter;
import hellfirepvp.astralsorcery.common.data.config.registry.sets.EntityTechnicalEntry;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TechnicalEntityRegistry
 * Created by HellFirePvP
 * Date: 28.07.2019 / 09:40
 */
public class TechnicalEntityRegistry extends ConfigDataAdapter<EntityTechnicalEntry> {

    public static final TechnicalEntityRegistry INSTANCE = new TechnicalEntityRegistry();

    public boolean canAffect(Entity entity) {
        return canAffect(entity.getType());
    }

    public boolean canAffect(EntityType<?> type) {
        return MiscUtils.contains(this.getConfiguredValues(), e -> e.getEntityType().equals(type));
    }

    private TechnicalEntityRegistry() {}

    @Override
    public List<EntityTechnicalEntry> getDefaultValues() {
        return Lists.newArrayList(
                new EntityTechnicalEntry(Mods.MINECRAFT, "ender_pearl"),
                new EntityTechnicalEntry(Mods.MINECRAFT, "firework_rocket"),
                new EntityTechnicalEntry(Mods.ASTRAL_SORCERY, "observatory_helper"),
                new EntityTechnicalEntry(Mods.BOTANIA, "mana_burst"),
                new EntityTechnicalEntry(Mods.BOTANIA, "spark"),
                new EntityTechnicalEntry(Mods.BOTANIA, "corporea_spark")
        );
    }

    @Override
    public String getSectionName() {
        return "technical_entities";
    }

    @Override
    public String getCommentDescription() {
        return "Defines entities whose purpose is mostly technical and less gameplay impactful. Those will be excluded from effects that manipulate entities. " +
                "Add entities by their entity type name." +
                "Format: <EntityTypeName>";
    }

    @Override
    public String getTranslationKey() {
        return translationKey("data");
    }

    @Nullable
    @Override
    public EntityTechnicalEntry deserialize(String string) throws IllegalArgumentException {
        return EntityTechnicalEntry.deserialize(string);
    }

    @Override
    public Predicate<Object> getValidator() {
        return obj -> obj instanceof String;
    }
}
