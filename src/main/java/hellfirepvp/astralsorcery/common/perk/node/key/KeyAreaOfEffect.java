/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyAreaOfEffect
 * Created by HellFirePvP
 * Date: 25.08.2019 / 19:10
 */
public class KeyAreaOfEffect extends KeyAddEnchantment {

    public KeyAreaOfEffect(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.addEnchantment(Enchantments.SWEEPING, 2);
    }

    @Override
    public void attachListeners(IEventBus bus) {
        super.attachListeners(bus);
        bus.addListener(EventPriority.HIGH, this::onDamage);
    }

    private void onDamage(LivingHurtEvent event) {
        if (EventFlags.SWEEP_ATTACK.isSet()) {
            return;
        }

        DamageSource source = event.getSource();
        if (source instanceof IndirectEntityDamageSource &&
                source.getTrueSource() != null && source.getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source.getTrueSource();
            LogicalSide side = this.getSide(player);
            PlayerProgress prog = ResearchHelper.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                LivingEntity attacked = event.getEntityLiving();
                float sweepPerc = EnchantmentHelper.getSweepingDamageRatio(player);
                if (sweepPerc > 0) {
                    sweepPerc = PerkAttributeHelper.getOrCreateMap(player, side)
                            .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, sweepPerc);
                    float toApply = event.getAmount() * sweepPerc;

                    float range = 2.5F * PerkAttributeHelper.getOrCreateMap(player, side).getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT);
                    EventFlags.SWEEP_ATTACK.executeWithFlag(() -> {
                        for (LivingEntity target : attacked.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class,
                                attacked.getBoundingBox().grow(range, range / 2F, range))) {
                            if (MiscUtils.canPlayerAttackServer(player, target) && !player.equals(target)) {
                                DamageUtil.attackEntityFrom(target, source, toApply);
                            }
                        }
                    });
                }
            }
        }
    }
}
