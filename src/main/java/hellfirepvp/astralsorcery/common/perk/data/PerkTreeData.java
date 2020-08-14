package hellfirepvp.astralsorcery.common.perk.data;

import hellfirepvp.astralsorcery.common.perk.AbstractPerk;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeData
 * Created by HellFirePvP
 * Date: 12.08.2020 / 22:17
 */
public class PerkTreeData {

    private final Set<ConnectedPerkData> perks = new HashSet<>();

    ConnectedPerkData addPerk(AbstractPerk perk) {
        ConnectedPerkData data = new ConnectedPerkData(perk);
        this.perks.add(data);
        return data;
    }

    public Set<ConnectedPerkData> getLoadedPerks() {
        return Collections.unmodifiableSet(this.perks);
    }
}
