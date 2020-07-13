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
import hellfirepvp.astralsorcery.common.event.PotionApplyEvent;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.math.MathHelper;
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
        eventBus.addListener(this::onNewEffect);
        eventBus.addListener(this::onChangeEffect);
    }

    private void onNewEffect(PotionApplyEvent.New event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            modifyPotionDuration((PlayerEntity) event.getEntityLiving(), event.getPotionEffect(), event.getPotionEffect());
        }
    }

    private void onChangeEffect(PotionApplyEvent.Changed event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            modifyPotionDuration((PlayerEntity) event.getEntityLiving(), event.getNewCombinedEffect(), event.getAddedEffect());
        }
    }

    private void modifyPotionDuration(PlayerEntity player, EffectInstance newSetEffect, EffectInstance addedEffect) {
        if (player.getEntityWorld().isRemote() ||
                newSetEffect.getPotion().getEffectType().equals(EffectType.HARMFUL) ||
                addedEffect.getAmplifier() < newSetEffect.getAmplifier()) {
            return;
        }

        float newDuration = addedEffect.getDuration();
        newDuration = PerkAttributeHelper.getOrCreateMap(player, LogicalSide.SERVER)
                .modifyValue(player, ResearchHelper.getProgress(player, LogicalSide.SERVER), this, newDuration);
        newDuration = AttributeEvent.postProcessModded(player, this, newDuration);

        if (newSetEffect.getDuration() < newDuration) {
            newSetEffect.duration = MathHelper.floor(newDuration);
        }
    }

}
