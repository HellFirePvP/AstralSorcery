package hellfirepvp.astralsorcery.common.constellation.perk;

import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDamageDistance;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDamageIncrease;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDamageKnockedback;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDamageOnKill;
import hellfirepvp.astralsorcery.common.data.config.Config;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationPerks
 * Created by HellFirePvP
 * Date: 23.11.2016 / 01:49
 */
public enum ConstellationPerks {

    OFF_DMG_INCREASE(PerkDamageIncrease::new),
    OFF_DMG_AFTERKILL(PerkDamageOnKill::new),
    OFF_DMG_DISTANCE(PerkDamageDistance::new),
    OFF_DMG_KNOCKBACK(PerkDamageKnockedback::new);

    private final PerkProvider provider;
    private final ConstellationPerk singleInstance;

    private ConstellationPerks(PerkProvider provider) {
        this.provider = provider;
        this.singleInstance = createPerk();
    }

    public ConstellationPerk getSingleInstance() {
        return singleInstance;
    }

    public ConstellationPerk createPerk() {
        ConstellationPerk newPerk = provider.providePerk();
        newPerk.setId(ordinal());
        return newPerk;
    }

    public boolean isInstanceOfThis(ConstellationPerk perk) {
        return perk.getId() == ordinal();
    }

    public static void addDynamicConfigEntries() {
        for (ConstellationPerks p : values()) {
            ConstellationPerk perk = p.getSingleInstance();
            if(perk.hasConfigEntries()) {
                Config.addDynamicEntry(perk);
            }
        }
    }

    private static interface PerkProvider {

        public ConstellationPerk providePerk();

    }

}
