package hellfirepvp.astralsorcery.common.constellation.perk;

import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDamageDistance;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDamageIncrease;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDamageKnockedback;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkDamageOnKill;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkTravelLavaProtection;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkTravelMovespeed;
import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkTravelPlaceLight;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.util.data.TimeoutList;
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

    DMG_INCREASE   (0,  PerkDamageIncrease::new),
    DMG_AFTERKILL  (1,  PerkDamageOnKill::new),
    DMG_DISTANCE   (2,  PerkDamageDistance::new),
    DMG_KNOCKBACK  (3,  PerkDamageKnockedback::new),

    TRV_PLACELIGHTS(20, PerkTravelPlaceLight::new),
    TRV_LAVAPROTECT(21, PerkTravelLavaProtection::new),
    TRV_MOVESPEED  (22, PerkTravelMovespeed::new);

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
