/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import hellfirepvp.astralsorcery.common.constellation.perk.impl.*;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerServer;
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
    DMG_REFLECT      (5,  PerkDamageReflect::new),

    TRV_PLACELIGHTS  (20, PerkTravelPlaceLight::new),
    TRV_LAVAPROTECT  (21, PerkTravelLavaProtection::new),
    TRV_MOVESPEED    (22, PerkTravelMovespeed::new),
    TRV_SWIMSPEED    (23, PerkTravelWaterMovement::new),
    TRV_REDFOODNEED  (24, PerkTravelReduceFoodNeed::new),
    TRV_STEPASSIST   (25, PerkTravelStepAssist::new),

    CRE_GROWTH       (40, PerkCreationGrowables::new),
    CRE_BREEDING     (41, PerkCreationBreedables::new),
    CRE_OREGEN       (42, PerkCreationStoneEnrichment::new),
    CRE_REACH        (43, PerkCreationReach::new),
    CRE_MEND         (44, PerkCreationMending::new),
    CRE_FLARES       (45, PerkCreationFlares::new),

    DEF_DMGREDUCTION (60, PerkDefensiveDamageReduction::new),
    DEF_DODGE        (61, PerkDefensiveDamageDodge::new),
    DEF_NOARMOR      (62, PerkDefensiveNoArmor::new),
    DEF_ELEMENTAL    (63, PerkDefensiveElemental::new),
    DEF_FALLREDUCTION(64, PerkDefensiveReduceFall::new),
    DEF_CHEATDEATH   (65, PerkDefensiveCheatDeath::new),

    DTR_DIG_SPEED    (80, PerkDestructionDigSpeed::new),
    DTR_DIG_TYPES    (81, PerkDestructionDigTypes::new),
    DTR_ARMOR_BREAK  (82, PerkDestructionArmor::new),
    DTR_DAMAGE_ARC   (83, PerkDestructionDamageArc::new),
    DTR_LOW_HEALTH   (84, PerkDestructionLowHealth::new),
    DTR_STACK        (85, PerkDestructionStack::new);

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

    public static class PerkTimeoutHandler implements
            TimeoutListContainer.ContainerTimeoutDelegate<EventHandlerServer.PlayerWrapperContainer, Integer> {

        @Override
        public void onContainerTimeout(EventHandlerServer.PlayerWrapperContainer key, Integer id) {
            ConstellationPerks timedOut = getById(id);
            if(timedOut != null) {
                timedOut.getSingleInstance().onTimeout(key.player);
            }
        }
    }

}
