/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.advancements.instances.PerkLevelInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkLevelTrigger
 * Created by HellFirePvP
 * Date: 27.10.2018 / 13:18
 */
public class PerkLevelTrigger extends ListenerCriterionTrigger<PerkLevelInstance> {

    public static final ResourceLocation ID = new ResourceLocation(AstralSorcery.MODID, "perk_level");

    public PerkLevelTrigger() {
        super(ID);
    }

    @Override
    public PerkLevelInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return PerkLevelInstance.deserialize(getId(), json);
    }

    public void trigger(EntityPlayerMP player) {
        Listeners<PerkLevelInstance> listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger((i) -> i.test(player));
        }
    }

}
