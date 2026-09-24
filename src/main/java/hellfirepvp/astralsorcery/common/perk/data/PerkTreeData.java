/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkTreeData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class PerkTreeData {

    private final List<JsonObject> loadedPerkData;
    private final Set<RawPerkData> perks;

    private PerkTreeData(List<JsonObject> loadedPerkData, Set<RawPerkData> perks) {
        this.loadedPerkData = loadedPerkData;
        this.perks = perks;
    }

    public static PerkTreeData load(List<JsonObject> perkData, HolderLookup.Provider registries) {
        RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);
        Set<RawPerkData> perks = new HashSet<>();
        for (JsonObject obj : perkData) {
            RawPerkData.CODEC.parse(ops, obj)
                    .ifError(error -> AstralSorcery.LOG.warn("Failed to load a perk: {}", error.message()))
                    .ifSuccess(perks::add);
        }
        return new PerkTreeData(perkData, perks);
    }

    public Set<RawPerkData> getPerks() {
        return Collections.unmodifiableSet(this.perks);
    }

    public List<JsonObject> getRawData() {
        return Collections.unmodifiableList(this.loadedPerkData);
    }

    public BakedPerkTreeData prepare() {
        return BakedPerkTreeData.create(this.perks);
    }
}
