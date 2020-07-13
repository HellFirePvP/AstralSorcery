/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.advancement.instance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationInstance
 * Created by HellFirePvP
 * Date: 11.05.2020 / 20:26
 */
public class ConstellationInstance extends CriterionInstance {

    private boolean constellationMajor = false;
    private boolean constellationWeak = false;
    private boolean constellationMinor = false;
    private Set<IConstellation> constellations = new HashSet<>();

    private ConstellationInstance(ResourceLocation id) {
        super(id);
    }

    public static ConstellationInstance any(ResourceLocation type) {
        return new ConstellationInstance(type);
    }

    public static ConstellationInstance anyMajor(ResourceLocation type) {
        ConstellationInstance instance = new ConstellationInstance(type);
        instance.constellationMajor = true;
        return instance;
    }

    public static ConstellationInstance anyWeak(ResourceLocation type) {
        ConstellationInstance instance = new ConstellationInstance(type);
        instance.constellationWeak = true;
        return instance;
    }

    public static ConstellationInstance anyMinor(ResourceLocation type) {
        ConstellationInstance instance = new ConstellationInstance(type);
        instance.constellationMinor = true;
        return instance;
    }

    public static ConstellationInstance anyOf(ResourceLocation type, IConstellation... cst) {
        ConstellationInstance instance = new ConstellationInstance(type);
        instance.constellations.addAll(Arrays.asList(cst));
        return instance;
    }

    @Override
    public JsonElement serialize() {
        JsonObject out = new JsonObject();
        if (this.constellationMajor) {
            out.addProperty("major", true);
        }
        if (this.constellationWeak) {
            out.addProperty("weak", true);
        }
        if (this.constellationMinor) {
            out.addProperty("minor", true);
        }
        if (!this.constellations.isEmpty()) {
            JsonArray names = new JsonArray();
            for (IConstellation cst : this.constellations) {
                names.add(cst.getRegistryName().toString());
            }
            out.add("constellations", names);
        }
        return out;
    }

    public static ConstellationInstance deserialize(ResourceLocation id, JsonObject json) {
        ConstellationInstance instance = new ConstellationInstance(id);
        instance.constellationMajor = JSONUtils.getBoolean(json, "major", false);
        instance.constellationWeak  = JSONUtils.getBoolean(json, "weak", false);
        instance.constellationMinor = JSONUtils.getBoolean(json, "minor", false);
        JsonArray constellationNames = JSONUtils.getJsonArray(json, "constellations", new JsonArray());
        for (int idx = 0; idx < constellationNames.size(); idx++) {
            JsonElement element = constellationNames.get(idx);
            String key = JSONUtils.getString(element, String.format("constellations[%s]", idx));
            IConstellation cst = RegistriesAS.REGISTRY_CONSTELLATIONS.getValue(new ResourceLocation(key));
            if (cst == null) {
                throw new IllegalArgumentException(String.format("Unknown constellation: %s - at constellations[%s]", key, idx));
            }
            instance.constellations.add(cst);
        }
        return instance;
    }

    public boolean test(IConstellation discovered) {
        if (constellationMajor && !(discovered instanceof IMajorConstellation)) {
            return false;
        }
        if (constellationWeak && (!(discovered instanceof IWeakConstellation) || discovered instanceof IMajorConstellation)) {
            return false;
        }
        if (constellationMinor && !(discovered instanceof IMinorConstellation)) {
            return false;
        }
        return constellations.isEmpty() || constellations.contains(discovered);
    }
}
