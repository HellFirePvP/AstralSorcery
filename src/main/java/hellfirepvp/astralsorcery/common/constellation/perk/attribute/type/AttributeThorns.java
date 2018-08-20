/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeThorns
 * Created by HellFirePvP
 * Date: 11.08.2018 / 18:24
 */
public class AttributeThorns extends PerkAttributeType {

    public AttributeThorns() {
        super(AttributeTypeRegistry.ATTR_TYPE_INC_THORNS);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
        if (!hasTypeApplied(player, side)) {
            return;
        }

        float reflectAmount = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(AttributeTypeRegistry.ATTR_TYPE_INC_THORNS, 0F);
        reflectAmount *= PerkAttributeHelper.getOrCreateMap(player, side)
                .getModifier(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT);
        if (reflectAmount <= 0) {
            return;
        }

        DamageSource source = event.getSource();
        EntityLivingBase reflectTarget = null;
        if (source.getImmediateSource() != null &&
                source.getImmediateSource() instanceof EntityLivingBase &&
                !source.getImmediateSource().isDead) {
            reflectTarget = (EntityLivingBase) source.getImmediateSource();
        }

        if (reflectTarget == null && PerkAttributeHelper.getOrCreateMap(player, side).getModifier(AttributeTypeRegistry.ATTR_TYPE_INC_THORNS_RANGED) > 1) {
            if (source.getTrueSource() != null &&
                    source.getTrueSource() instanceof EntityLivingBase &&
                    !source.getTrueSource().isDead) {
                reflectTarget = (EntityLivingBase) source.getTrueSource();
            }
        }

        if (reflectTarget != null) {
            float dmgReflected = event.getAmount() * reflectAmount;
            if (dmgReflected > 0 && !event.getEntityLiving().equals(reflectTarget) &&
                    (!(event.getEntityLiving() instanceof EntityPlayer)) ||
                        (!((EntityPlayer) event.getEntityLiving()).isSpectator() && !((EntityPlayer) event.getEntityLiving()).isCreative())) {
                reflectTarget.attackEntityFrom(CommonProxy.dmgSourceReflect.setSource(player), dmgReflected);
            }
        }
    }

}
