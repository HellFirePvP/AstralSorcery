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
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeLifeRecovery
 * Created by HellFirePvP
 * Date: 25.08.2019 / 00:34
 */
public class AttributeTypeLifeRecovery extends PerkAttributeType {

    public AttributeTypeLifeRecovery() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_LIFE_RECOVERY, true);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(EventPriority.LOW, this::onHeal);
    }

    private void onHeal(LivingHealEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        LogicalSide side = this.getSide(player);
        if (!hasTypeApplied(player, side)) {
            return;
        }

        float heal = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, ResearchHelper.getProgress(player, side), this, event.getAmount());
        heal = AttributeEvent.postProcessModded(player, this, heal);
        float val = heal;
        if (val <= 0) {
            event.setCanceled(true);
        } else {
            event.setAmount(val);
        }
    }
}
