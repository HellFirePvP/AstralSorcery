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
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypePotionDuration
 * Created by HellFirePvP
 * Date: 25.08.2019 / 00:36
 */
public class AttributeTypePotionDuration extends PerkAttributeType {

    public AttributeTypePotionDuration() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_POTION_DURATION, true);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onEffect);
    }

    private void onEffect(PotionEvent.PotionAddedEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            if (event.getOldPotionEffect() == null) {
                //New effect
                modifyPotionDuration((PlayerEntity) event.getEntityLiving(), event.getPotionEffect(), event.getPotionEffect());
            } else {
                //Existing effect
                if (new EffectInstance(event.getOldPotionEffect()).combine(event.getPotionEffect())) {
                    modifyPotionDuration((PlayerEntity) event.getEntityLiving(), event.getPotionEffect(), event.getOldPotionEffect());
                }
            }
        }
    }

    private void modifyPotionDuration(PlayerEntity player, EffectInstance newSetEffect, EffectInstance existingEffect) {
        if (player.getEntityWorld().isRemote() ||
                newSetEffect.getPotion().getEffectType().equals(EffectType.HARMFUL) ||
                existingEffect.getAmplifier() < newSetEffect.getAmplifier()) {
            return;
        }

        float newDuration = existingEffect.getDuration();
        newDuration = PerkAttributeHelper.getOrCreateMap(player, LogicalSide.SERVER)
                .modifyValue(player, ResearchHelper.getProgress(player, LogicalSide.SERVER), this, newDuration);
        newDuration = AttributeEvent.postProcessModded(player, this, newDuration);

        if (newSetEffect.getDuration() < newDuration) {
            newSetEffect.duration = MathHelper.floor(newDuration);
        }
    }

}
