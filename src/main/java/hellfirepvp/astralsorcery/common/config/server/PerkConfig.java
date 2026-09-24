/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config.server;

import hellfirepvp.astralsorcery.common.config.ConfigEntry;
import hellfirepvp.astralsorcery.common.perk.tree.perk.key.*;
import hellfirepvp.astralsorcery.common.perk.tree.perk.root.*;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkConfig
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkConfig extends ConfigEntry {

    public static final PerkConfig CONFIG = new PerkConfig();

    public ModConfigSpec.IntValue perkLevelCap;

    private PerkConfig() {
        super("perks");
    }

    @Override
    public void createEntries(ModConfigSpec.Builder cfgBuilder) {
        this.perkLevelCap = cfgBuilder
                .comment("Sets the max level for the perk tree levels.")
                .translation(translationKey("perkLevelCap"))
                .defineInRange("perkLevelCap", 30, 1, 100);
    }

    public static void addPerkConfigs() {
        CONFIG.newSubSection(RootPerkAevitas.CONFIG);
        CONFIG.newSubSection(RootPerkArmara.CONFIG);
        CONFIG.newSubSection(RootPerkDiscidia.CONFIG);
        CONFIG.newSubSection(RootPerkEvorsio.CONFIG);
        CONFIG.newSubSection(RootPerkVicio.CONFIG);

        CONFIG.newSubSection(KeyPerkMendArmor.CONFIG);
        CONFIG.newSubSection(KeyPerkCullingAttack.CONFIG);
        CONFIG.newSubSection(KeyPerkDamageArmor.CONFIG);
        CONFIG.newSubSection(KeyPerkDisarm.CONFIG);
        CONFIG.newSubSection(KeyPerkCheatDeath.CONFIG);
        CONFIG.newSubSection(KeyPerkDamageEffects.CONFIG);
        CONFIG.newSubSection(KeyPerkProjectileDistance.CONFIG);
        CONFIG.newSubSection(KeyPerkProjectileProximity.CONFIG);
        CONFIG.newSubSection(KeyPerkLastBreath.CONFIG);
        CONFIG.newSubSection(KeyPerkNoArmor.CONFIG);
        CONFIG.newSubSection(KeyPerkGrowPlants.CONFIG);
    }
}
