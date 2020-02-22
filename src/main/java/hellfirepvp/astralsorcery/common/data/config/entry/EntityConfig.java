/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.entry;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityConfig
 * Created by HellFirePvP
 * Date: 20.04.2019 / 13:38
 */
public class EntityConfig extends ConfigEntry {

    public static final EntityConfig CONFIG = new EntityConfig();

    public ForgeConfigSpec.IntValue flareAmbientSpawnChance;
    public ForgeConfigSpec.BooleanValue flareAttackBats;
    public ForgeConfigSpec.BooleanValue flareAttackPhantoms;

    private EntityConfig() {
        super("entities");
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        flareAmbientSpawnChance = cfgBuilder
                .comment("Defines how common ***ambient*** flares are. the lower the more common. 0 = ambient ones don't appear/disable")
                .translation(translationKey("flareAmbientSpawnChance"))
                .defineInRange("flareAmbientSpawnChance", 10, 0, 200_000);

        flareAttackBats = cfgBuilder
                .comment("If this is set to true, occasionally, a spawned flare will (attempt to) kill bats close to it.")
                .translation(translationKey("flareAttackBats"))
                .define("flareAttackBats", true);
        flareAttackPhantoms = cfgBuilder
                .comment("If this is set to true, occasionally, a spawned flare will (attempt to) kill phantoms close to it.")
                .translation(translationKey("flareAttackPhantoms"))
                .define("flareAttackPhantoms", true);
    }

}
