/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.advancement.instance.ConstellationInstance;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DiscoverConstellationTrigger
 * Created by HellFirePvP
 * Date: 27.10.2018 / 10:54
 */
public class DiscoverConstellationTrigger extends ListenerCriterionTrigger<ConstellationInstance> {

    public static final ResourceLocation ID = new ResourceLocation(AstralSorcery.MODID, "find_constellation");

    public DiscoverConstellationTrigger() {
        super(ID);
    }

    @Override
    public ConstellationInstance deserialize(JsonObject object, ConditionArrayParser conditions) {
        return ConstellationInstance.deserialize(getId(), object);
    }

    public void trigger(ServerPlayerEntity player, IConstellation cst) {
        Listeners<ConstellationInstance> listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger((i) -> i.test(cst));
        }
    }

}
