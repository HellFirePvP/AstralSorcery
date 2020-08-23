package hellfirepvp.astralsorcery.common.perk.data;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeData
 * Created by HellFirePvP
 * Date: 12.08.2020 / 22:17
 */
public class PerkTreeData {

    private final Set<LoadedPerkData> perks = new HashSet<>();

    LoadedPerkData addPerk(AbstractPerk perk, JsonObject perkDataObject) {
        LoadedPerkData data = new LoadedPerkData(perk, perkDataObject);
        this.perks.add(data);
        return data;
    }

    public PreparedPerkTreeData prepare() {
        return PreparedPerkTreeData.create(this.perks);
    }

    public Collection<JsonObject> getAsDataTree() {
        return this.perks.stream().map(LoadedPerkData::getPerkDataObject).collect(Collectors.toList());
    }
}
