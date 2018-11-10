/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeCritMultiplier
 * Created by HellFirePvP
 * Date: 13.07.2018 / 19:22
 */
public class AttributeCritMultiplier extends PerkAttributeType {

    public AttributeCritMultiplier() {
        super(AttributeTypeRegistry.ATTR_TYPE_INC_CRIT_MULTIPLIER);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onArrowCt(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) event.getEntity();
            if (!arrow.getIsCritical()) return; //No crit

            if (arrow.shootingEntity != null && arrow.shootingEntity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) arrow.shootingEntity;
                Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
                if (!hasTypeApplied(player, side)) {
                    return;
                }

                float dmgMod = PerkAttributeHelper.getOrCreateMap(player, side).modifyValue(getTypeString(), 1F);
                arrow.setDamage(arrow.getDamage() * dmgMod);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCrit(CriticalHitEvent event) {
        if (!event.isVanillaCritical() && event.getResult() != Event.Result.ALLOW) {
            return; //No crit
        }

        EntityPlayer player = event.getEntityPlayer();
        Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
        if (!hasTypeApplied(player, side)) {
            return;
        }

        float dmgMod = PerkAttributeHelper.getOrCreateMap(event.getEntityPlayer(), side)
                .modifyValue(getTypeString(), event.getDamageModifier());
        event.setDamageModifier(dmgMod);
    }

}
