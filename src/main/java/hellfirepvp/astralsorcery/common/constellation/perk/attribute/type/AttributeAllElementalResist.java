/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeAllElementalResist
 * Created by HellFirePvP
 * Date: 13.07.2018 / 23:34
 */
public class AttributeAllElementalResist extends PerkAttributeType {

    public AttributeAllElementalResist() {
        super(AttributeTypeRegistry.ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
    }

    @SubscribeEvent
    public void onDamageTaken(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
        if (!hasTypeApplied(player, side)) {
            return;
        }
        DamageSource ds = event.getSource();
        if (isMaybeElementalDamage(ds)) {
            float multiplier = 1F;
            multiplier = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(getTypeString(), multiplier);
            multiplier = Math.max(0, multiplier);
            multiplier = 1F - ((1F - multiplier) * PerkAttributeHelper.getOrCreateMap(player, side).getModifier(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT));
            event.setAmount(event.getAmount() * multiplier);
        }
    }

    private boolean isMaybeElementalDamage(DamageSource source) {
        // "Magic" is often used for any kinds of damages... poison for example
        if (source.isFireDamage() || source.isMagicDamage() || source.isExplosion()) {
            return true;
        }
        String key = source.getDamageType();
        if (key == null) {
            return false;
        }
        key = key.toLowerCase();
        return key.contains("fire") || key.contains("lightning") || key.contains("cold");
    }

}
