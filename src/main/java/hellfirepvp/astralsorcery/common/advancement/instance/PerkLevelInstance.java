/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.advancement.instance;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.advancement.PerkLevelTrigger;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkLevelInstance
 * Created by HellFirePvP
 * Date: 11.05.2020 / 20:23
 */
public class PerkLevelInstance extends CriterionInstance {

    private int levelNeeded = 0;

    private PerkLevelInstance(ResourceLocation criterionIn) {
        super(criterionIn);
    }

    public static PerkLevelInstance reachLevel(int level) {
        PerkLevelInstance instance = new PerkLevelInstance(PerkLevelTrigger.ID);
        instance.levelNeeded = level;
        return instance;
    }

    @Override
    public JsonElement serialize() {
        JsonObject out = new JsonObject();
        out.addProperty("levelNeeded", this.levelNeeded);
        return out;
    }

    public static PerkLevelInstance deserialize(ResourceLocation id, JsonObject json) {
        PerkLevelInstance instance = new PerkLevelInstance(id);
        instance.levelNeeded = JSONUtils.getInt(json, "levelNeeded");
        return instance;
    }

    public boolean test(ServerPlayerEntity player) {
        return ResearchHelper.getProgress(player, LogicalSide.SERVER).getPerkLevel(player) >= this.levelNeeded;
    }
}
