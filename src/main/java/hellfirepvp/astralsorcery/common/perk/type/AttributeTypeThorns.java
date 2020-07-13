/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeModifierThorns;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeThorns
 * Created by HellFirePvP
 * Date: 25.08.2019 / 00:43
 */
public class AttributeTypeThorns extends PerkAttributeType {

    public AttributeTypeThorns() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_INC_THORNS);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onThronsReflect);
    }

    @Nonnull
    @Override
    public PerkAttributeModifier createModifier(float modifier, ModifierType mode) {
        return new AttributeModifierThorns(this, mode, modifier);
    }

    private void onThronsReflect(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        LogicalSide side = this.getSide(player);
        if (!hasTypeApplied(player, side)) {
            return;
        }

        PlayerProgress prog = ResearchHelper.getProgress(player, side);

        float reflectAmount = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, prog, this, 0F);
        reflectAmount = AttributeEvent.postProcessModded(player, this, reflectAmount);
        reflectAmount /= 100.0F;
        if (reflectAmount <= 0) {
            return;
        }
        reflectAmount = MathHelper.clamp(reflectAmount, 0F, 1F);

        DamageSource source = event.getSource();
        LivingEntity reflectTarget = null;
        if (source.getImmediateSource() != null &&
                source.getImmediateSource() instanceof LivingEntity &&
                source.getImmediateSource().isAlive()) {
            reflectTarget = (LivingEntity) source.getImmediateSource();
        }

        if (reflectTarget == null &&
                AttributeEvent.postProcessModded(player, this,
                        PerkAttributeHelper.getOrCreateMap(player, side)
                                .getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_THORNS_RANGED)) > 1) {
            if (source.getTrueSource() != null &&
                    source.getTrueSource() instanceof LivingEntity &&
                    source.getTrueSource().isAlive()) {
                reflectTarget = (LivingEntity) source.getTrueSource();
            }
        }

        if (reflectTarget != null) {
            float dmgReflected = event.getAmount() * reflectAmount;
            if (dmgReflected > 0 && !event.getEntityLiving().equals(reflectTarget)) {
                if (MiscUtils.canPlayerAttackServer(event.getEntityLiving(), reflectTarget)) {
                    DamageUtil.attackEntityFrom(reflectTarget, CommonProxy.DAMAGE_SOURCE_REFLECT, dmgReflected, player);
                }
            }
        }
    }
}
