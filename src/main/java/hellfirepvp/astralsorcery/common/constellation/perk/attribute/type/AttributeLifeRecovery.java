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
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeLifeRecovery
 * Created by HellFirePvP
 * Date: 20.07.2018 / 17:22
 */
public class AttributeLifeRecovery extends PerkAttributeType {

    public AttributeLifeRecovery() {
        super(AttributeTypeRegistry.ATTR_TYPE_LIFE_RECOVERY);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onHeal(LivingHealEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
        if (!hasTypeApplied(player, side)) {
            return;
        }

        float healMultiplier = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(getTypeString(), 1F);
        float val = event.getAmount() * healMultiplier;
        if (val <= 0) {
            event.setCanceled(true);
        } else {
            event.setAmount(val);
        }
    }

}
