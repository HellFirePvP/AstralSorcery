/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LootTablesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LootTablesAS {

    public static final ResourceKey<LootTable> SHOOTING_STAR = key("gameplay/shooting_star");
    public static final ResourceKey<LootTable> SHRINE_CHEST_SMALL = key("gameplay/shrine_chest_small");
    public static final ResourceKey<LootTable> SHRINE_CHEST = key("gameplay/shrine_chest");
    public static final ResourceKey<LootTable> DIG_SITE_ARCHAEOLOGY = key("gameplay/dig_site_archaeology");

    private static ResourceKey<LootTable> key(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, AstralSorcery.key(name));
    }

}
