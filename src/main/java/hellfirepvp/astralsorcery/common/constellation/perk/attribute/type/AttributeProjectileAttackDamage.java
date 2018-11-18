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
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeProjectileAttackDamage
 * Created by HellFirePvP
 * Date: 14.07.2018 / 13:11
 */
public class AttributeProjectileAttackDamage extends PerkAttributeType {

    public AttributeProjectileAttackDamage() {
        super(AttributeTypeRegistry.ATTR_TYPE_PROJ_DAMAGE);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onProjDamage(LivingHurtEvent event) {
        if (event.getSource().isProjectile()) {
            DamageSource source = event.getSource();
            if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) source.getTrueSource();
                Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
                if (!hasTypeApplied(player, side)) {
                    return;
                }

                float amt = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(ResearchManager.getProgress(player, side), getTypeString(), event.getAmount());
                event.setAmount(amt);
            }
        }
    }

}
