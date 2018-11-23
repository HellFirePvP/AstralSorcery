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
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
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
            float multiplier = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), 1F);
            multiplier -= 1F;
            multiplier = AttributeEvent.postProcessModded(player, this, multiplier);
            multiplier = 1F - MathHelper.clamp(multiplier, 0F, 1F);
            event.setAmount(event.getAmount() * multiplier);
        }
    }

    private boolean isMaybeElementalDamage(DamageSource source) {
        // "Magic" is often used for any kinds of damages... poison for example
        if (source.isFireDamage() || source.isMagicDamage()) {
            return true;
        }
        String key = source.getDamageType();
        if (key == null) {
            return false;
        }
        key = key.toLowerCase();
        return key.contains("fire") || key.contains("heat") || key.contains("lightning") ||
                key.contains("cold") || key.contains("freeze") ||
                key.contains("electr") || key.contains("froze") || key.contains("ice");
    }

}
