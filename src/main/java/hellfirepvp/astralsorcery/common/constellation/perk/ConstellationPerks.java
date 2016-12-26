package hellfirepvp.astralsorcery.common.constellation.perk;

import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkCreationBreedables;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkCreationGrowables;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkCreationReach;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkCreationStoneEnrichment;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDamageBleed;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDamageDistance;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDamageIncrease;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDamageKnockedback;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDamageOnKill;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDefensiveDamageDodge;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDefensiveDamageReduction;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDefensiveElemental;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDefensiveNoArmor;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkTravelLavaProtection;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkTravelMovespeed;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkTravelPlaceLight;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDefensiveReduceFall;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkTravelReduceFoodNeed;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkTravelWaterMovement;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.util.data.TimeoutListContainer;
import net.minecraft.entity.player.EntityPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationPerks
 * Created by HellFirePvP
 * Date: 23.11.2016 / 01:49
 */
public enum ConstellationPerks {

    DMG_INCREASE     (0,  PerkDamageIncrease::new),
    DMG_AFTERKILL    (1,  PerkDamageOnKill::new),
    DMG_DISTANCE     (2,  PerkDamageDistance::new),
    DMG_KNOCKBACK    (3,  PerkDamageKnockedback::new),
    DMG_BLEED        (4,  PerkDamageBleed::new),

    TRV_PLACELIGHTS  (20, PerkTravelPlaceLight::new),
    TRV_LAVAPROTECT  (21, PerkTravelLavaProtection::new),
    TRV_MOVESPEED    (22, PerkTravelMovespeed::new),
    TRV_SWIMSPEED    (23, PerkTravelWaterMovement::new),
    TRV_REDFOODNEED  (24, PerkTravelReduceFoodNeed::new),

    CRE_GROWTH       (40, PerkCreationGrowables::new),
    CRE_BREEDING     (41, PerkCreationBreedables::new),
    CRE_OREGEN       (42, PerkCreationStoneEnrichment::new),
    CRE_REACH        (43, PerkCreationReach::new),

    DEF_DMGREDUCTION (60, PerkDefensiveDamageReduction::new),
    DEF_DODGE        (61, PerkDefensiveDamageDodge::new),
    DEF_NOARMOR      (62, PerkDefensiveNoArmor::new),
    DEF_ELEMENTAL    (63, PerkDefensiveElemental::new),
    DEF_FALLREDUCTION(64, PerkDefensiveReduceFall::new);

    private final int id;
    private final PerkProvider provider;
    private final ConstellationPerk singleInstance;

    private ConstellationPerks(int id, PerkProvider provider) {
        this.id = id;
        this.provider = provider;
        this.singleInstance = createPerk();
    }

    public ConstellationPerk getSingleInstance() {
        return singleInstance;
    }

    public int getId() {
        return id;
    }

    public ConstellationPerk createPerk() {
        ConstellationPerk newPerk = provider.providePerk();
        newPerk.setId(id);
        return newPerk;
    }

    public boolean isInstanceOfThis(ConstellationPerk perk) {
        return perk.getId() == id;
    }

    public static void addDynamicConfigEntries() {
        for (ConstellationPerks p : values()) {
            ConstellationPerk perk = p.getSingleInstance();
            if(perk.hasConfigEntries()) {
                Config.addDynamicEntry(perk);
            }
        }
    }

    public static ConstellationPerks getById(int id) {
        for (ConstellationPerks perk : values()) {
            if(perk.id == id) return perk;
        }
        return null;
    }

    private static interface PerkProvider {

        public ConstellationPerk providePerk();

    }

    public static class PerkTimeoutHandler implements TimeoutListContainer.ContainerTimeoutDelegate<EntityPlayer, Integer> {

        @Override
        public void onContainerTimeout(EntityPlayer key, Integer id) {
            ConstellationPerks timedOut = getById(id);
            if(timedOut != null) {
                timedOut.getSingleInstance().onTimeout(key);
            }
        }
    }

}
