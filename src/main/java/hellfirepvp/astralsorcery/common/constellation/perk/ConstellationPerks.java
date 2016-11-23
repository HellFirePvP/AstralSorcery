package hellfirepvp.astralsorcery.common.constellation.perk;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationPerks
 * Created by HellFirePvP
 * Date: 23.11.2016 / 01:49
 */
public enum ConstellationPerks {

    OFF_DMG_INCREASE(() -> new ConstellationPerk("dmg_inc"));

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

    private static interface PerkProvider {

        public ConstellationPerk providePerk();

    }

}
