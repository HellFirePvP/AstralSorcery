/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.advancements.instances;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.constellation.*;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationInstance
 * Created by HellFirePvP
 * Date: 27.10.2018 / 13:09
 */
public class ConstellationInstance extends AbstractCriterionInstance {

    private boolean constellationMajor = false;
    private boolean constellationWeak = false;
    private boolean constellationMinor = false;
    private List<String> constellationNames = Lists.newArrayList();

    private ConstellationInstance(ResourceLocation id) {
        super(id);
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
        return constellationNames.isEmpty() || constellationNames.contains(discovered.getUnlocalizedName());
    }

    public static ConstellationInstance deserialize(ResourceLocation id, JsonObject json) {
        ConstellationInstance ci = new ConstellationInstance(id);
        ci.constellationMajor = JsonUtils.getBoolean(json, "major", false);
        ci.constellationWeak  = JsonUtils.getBoolean(json, "weak", false);
        ci.constellationMinor = JsonUtils.getBoolean(json, "minor", false);
        for (JsonElement je : JsonUtils.getJsonArray(json, "constellations", new JsonArray())) {
            if (!je.isJsonPrimitive() || je.getAsJsonPrimitive().isString()) {
                continue;
            }
            IConstellation cst = ConstellationRegistry.getConstellationByName(je.getAsString());
            if (cst != null) {
                ci.constellationNames.add(cst.getUnlocalizedName());
            }
        }
        return ci;
    }

}
