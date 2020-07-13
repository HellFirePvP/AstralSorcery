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
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeModifierLifeLeech;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeLifeLeech
 * Created by HellFirePvP
 * Date: 25.08.2019 / 00:32
 */
public class AttributeTypeLifeLeech extends PerkAttributeType {

    public AttributeTypeLifeLeech() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_ATTACK_LIFE_LEECH);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(EventPriority.LOWEST, this::onLeech);
    }

    @Nonnull
    @Override
    public PerkAttributeModifier createModifier(float modifier, ModifierType mode) {
        return new AttributeModifierLifeLeech(this, mode, modifier);
    }

    private void onLeech(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source.getTrueSource();
            LogicalSide side = this.getSide(player);
            if (side.isServer() && hasTypeApplied(player, side)) {

                float leechPerc = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(player, ResearchHelper.getProgress(player, side), this, 0F);
                leechPerc /= 100.0F;
                leechPerc = AttributeEvent.postProcessModded(player, this, leechPerc);
                if (leechPerc > 0) {
                    float leech = event.getAmount() * leechPerc;
                    if (leech > 0) {
                        player.heal(leech);
                    }
                }
            }
        }
    }

}
