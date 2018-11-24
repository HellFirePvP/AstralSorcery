/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyAreaOfEffect
 * Created by HellFirePvP
 * Date: 24.11.2018 / 21:50
 */
public class KeyAreaOfEffect extends KeyAddEnchantment {

    private static boolean inSweepAttack = false;

    public KeyAreaOfEffect(String name, int x, int y) {
        super(name, x, y);
        addEnchantment(Enchantments.SWEEPING, 2);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onDmg(LivingHurtEvent event) {
        if (inSweepAttack) {
            return;
        }

        DamageSource source = event.getSource();
        if (source instanceof EntityDamageSourceIndirect &&
                source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getTrueSource();
            Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                EntityLivingBase attacked = event.getEntityLiving();
                float sweepPerc = EnchantmentHelper.getSweepingDamageRatio(player);
                if (sweepPerc > 0) {
                    sweepPerc = PerkAttributeHelper.getOrCreateMap(player, side)
                            .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, sweepPerc);
                    float toApply = event.getAmount() * sweepPerc;
                    inSweepAttack = true;
                    try {
                        for (EntityLivingBase target : attacked.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class,
                                attacked.getEntityBoundingBox().grow(1, 0.25, 1))) {
                            if (MiscUtils.canPlayerAttackServer(player, target)) {
                                DamageUtil.attackEntityFrom(target, source, toApply);
                            }
                        }
                    } finally {
                        inSweepAttack = false;
                    }
                }
            }
        }
    }

}
