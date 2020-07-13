/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeCritMultiplier
 * Created by HellFirePvP
 * Date: 25.08.2019 / 00:23
 */
public class AttributeTypeCritMultiplier extends PerkAttributeType {

    public AttributeTypeCritMultiplier() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_INC_CRIT_MULTIPLIER, true);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(EventPriority.LOWEST, this::onArrowCrit);
        eventBus.addListener(EventPriority.LOWEST, this::onHitCrit);
    }

    private void onArrowCrit(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ArrowEntity) {
            ArrowEntity arrow = (ArrowEntity) event.getEntity();
            if (!arrow.getIsCritical()) {
                return;
            }

            if (arrow.shootingEntity != null) {
                PlayerEntity player = event.getWorld().getPlayerByUuid(arrow.shootingEntity);
                if (player != null) {
                    LogicalSide side = this.getSide(player);
                    if (!hasTypeApplied(player, side)) {
                        return;
                    }
                    float dmgMod = PerkAttributeHelper.getOrCreateMap(player, side)
                            .modifyValue(player, ResearchHelper.getProgress(player, side), this, 1F);
                    dmgMod = AttributeEvent.postProcessModded(player, this, dmgMod);
                    arrow.setDamage(arrow.getDamage() * dmgMod);
                }
            }
        }
    }

    private void onHitCrit(CriticalHitEvent event) {
        if (!event.isVanillaCritical() && event.getResult() != Event.Result.ALLOW) {
            return; //No crit
        }

        PlayerEntity player = event.getPlayer();
        LogicalSide side = this.getSide(player);
        if (!hasTypeApplied(player, side)) {
            return;
        }

        float dmgMod = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, ResearchHelper.getProgress(player, side), this, 1F);
        dmgMod = AttributeEvent.postProcessModded(player, this, dmgMod);
        event.setDamageModifier(event.getDamageModifier() * dmgMod);
    }
}
