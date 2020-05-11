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
import hellfirepvp.astralsorcery.common.advancement.instance.ConstellationInstance;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttuneCrystalTrigger
 * Created by HellFirePvP
 * Date: 27.10.2018 / 14:02
 */
public class AttuneCrystalTrigger extends ListenerCriterionTrigger<ConstellationInstance> {

    public static final ResourceLocation ID = AstralSorcery.key("attune_crystal");

    public AttuneCrystalTrigger() {
        super(ID);
    }

    @Override
    public ConstellationInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return ConstellationInstance.deserialize(getId(), json);
    }

    public void trigger(ServerPlayerEntity player, IConstellation attuned) {
        Listeners<ConstellationInstance> listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger((i) -> i.test(attuned));
        }
    }

}
