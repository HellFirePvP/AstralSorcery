/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.advancement.instance.PerkLevelInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkLevelTrigger
 * Created by HellFirePvP
 * Date: 11.05.2020 / 20:30
 */
public class PerkLevelTrigger extends ListenerCriterionTrigger<PerkLevelInstance> {

    public static final ResourceLocation ID = AstralSorcery.key("perk_level");

    public PerkLevelTrigger() {
        super(ID);
    }

    @Override
    public PerkLevelInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return PerkLevelInstance.deserialize(getId(), json);
    }

    public void trigger(ServerPlayerEntity player) {
        Listeners<PerkLevelInstance> listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger((i) -> i.test(player));
        }
    }
}
