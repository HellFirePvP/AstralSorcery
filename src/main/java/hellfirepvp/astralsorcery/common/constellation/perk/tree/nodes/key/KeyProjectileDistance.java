/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
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
 * Class: KeyProjectileDistance
 * Created by HellFirePvP
 * Date: 28.07.2018 / 21:14
 */
public class KeyProjectileDistance extends KeyPerk {

    public KeyProjectileDistance(String name, int x, int y) {
        super(name, x, y);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onProjDamage(LivingHurtEvent event) {
        if (event.getSource().isProjectile()) {
            DamageSource source = event.getSource();
            if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) source.getTrueSource();
                Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
                PlayerProgress prog = ResearchManager.getProgress(player, side);
                if (prog.hasPerkEffect(this)) {
                    float added = 0.75F;
                    added *= PerkAttributeHelper.getOrCreateMap(player, side).getModifier(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT);

                    float capDstSq = 6400; //80 * 80
                    float mul = ((float) (player.getDistanceSq(event.getEntityLiving()))) / capDstSq;
                    added *= (mul > 1 ? 1 : mul);

                    float amt = event.getAmount();
                    amt *= (1 + added);
                    event.setAmount(amt);
                }
            }
        }
    }

}
