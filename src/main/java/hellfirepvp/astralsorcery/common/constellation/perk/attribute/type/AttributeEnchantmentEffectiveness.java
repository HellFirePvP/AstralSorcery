/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantment;
import hellfirepvp.astralsorcery.common.event.DynamicEnchantmentEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeEnchantmentEffectiveness
 * Created by HellFirePvP
 * Date: 12.08.2018 / 10:22
 */
public class AttributeEnchantmentEffectiveness extends PerkAttributeType {

    public AttributeEnchantmentEffectiveness() {
        super(AttributeTypeRegistry.ATTR_TYPE_INC_ENCH_EFFECT);
    }

    @SubscribeEvent
    public void onDynEnchantmentModify(DynamicEnchantmentEvent.Modify event) {
        if (event.getResolvedPlayer() != null) {
            EntityPlayer player = event.getResolvedPlayer();
            Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
            if (!hasTypeApplied(player, side)) {
                return;
            }
            float inc = PerkAttributeHelper.getOrCreateMap(player, side)
                    .getModifier(AttributeTypeRegistry.ATTR_TYPE_INC_ENCH_EFFECT);
            inc *= PerkAttributeHelper.getOrCreateMap(player, side)
                    .getModifier(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT);
            for (DynamicEnchantment ench : event.getEnchantmentsToApply()) {
                float lvl = ench.getLevelAddition();
                lvl *= inc;
                ench.setLevelAddition(Math.round(lvl));
            }
        }
    }

}
