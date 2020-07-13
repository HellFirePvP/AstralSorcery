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
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeProjectileAttackDamage
 * Created by HellFirePvP
 * Date: 25.08.2019 / 00:42
 */
public class AttributeTypeProjectileAttackDamage extends PerkAttributeType {

    public AttributeTypeProjectileAttackDamage() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_PROJ_DAMAGE, true);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(EventPriority.LOW, this::onProjectileDamage);
    }

    private void onProjectileDamage(LivingHurtEvent event) {
        if (event.getSource().isProjectile()) {
            DamageSource source = event.getSource();
            if (source.getTrueSource() != null && source.getTrueSource() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) source.getTrueSource();
                LogicalSide side = this.getSide(player);
                if (!hasTypeApplied(player, side)) {
                    return;
                }

                float amt = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(player, ResearchHelper.getProgress(player, side), this, event.getAmount());
                amt = AttributeEvent.postProcessModded(player, this, amt);
                event.setAmount(amt);
            }
        }
    }
}
