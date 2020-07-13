/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.loot;

import hellfirepvp.astralsorcery.common.base.Mods;
import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityLootTableProvider
 * Created by HellFirePvP
 * Date: 07.03.2020 / 07:51
 */
public class EntityLootTableProvider extends EntityLootTables {

    @Override
    protected void addTables() {

    }

    @Override
    protected Iterable<EntityType<?>> getKnownEntities() {
        return ForgeRegistries.ENTITIES.getValues().stream()
                .filter(Mods.ASTRAL_SORCERY::owns)
                .collect(Collectors.toList());
    }
}
