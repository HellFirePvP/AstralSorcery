/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantment;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.event.DynamicEnchantmentEvent;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeEnchantmentEffectiveness
 * Created by HellFirePvP
 * Date: 25.08.2019 / 00:30
 */
public class AttributeTypeEnchantmentEffectiveness extends PerkAttributeType {

    public AttributeTypeEnchantmentEffectiveness() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_INC_ENCH_EFFECT, true);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onModify);
    }

    private void onModify(DynamicEnchantmentEvent.Modify event) {
        PlayerEntity player = event.getResolvedPlayer();
        LogicalSide side = this.getSide(player);
        if (!hasTypeApplied(player, side)) {
            return;
        }
        float inc = PerkAttributeHelper.getOrCreateMap(player, side)
                .getModifier(player, ResearchHelper.getProgress(player, side), this);
        for (DynamicEnchantment ench : event.getEnchantmentsToApply()) {
            float lvl = ench.getLevelAddition();
            lvl *= inc;
            float post = AttributeEvent.postProcessModded(player, this, lvl);
            ench.setLevelAddition(Math.round(post));
        }
    }
}
