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
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeLifeLeech
 * Created by HellFirePvP
 * Date: 01.08.2018 / 21:04
 */
public class AttributeLifeLeech extends PerkAttributeType {

    public AttributeLifeLeech() {
        super(AttributeTypeRegistry.ATTR_TYPE_ATTACK_LIFE_LEECH);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAttack(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getTrueSource();
            Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
            if (side == Side.SERVER && hasTypeApplied(player, side)) {
                float leechPerc = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(AttributeTypeRegistry.ATTR_TYPE_ATTACK_LIFE_LEECH, 0F);
                leechPerc /= 100.0F;
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
