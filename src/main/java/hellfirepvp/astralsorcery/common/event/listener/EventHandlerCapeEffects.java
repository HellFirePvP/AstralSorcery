/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectDiscidia;
import hellfirepvp.astralsorcery.common.item.wearable.ItemCape;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerCapeEffects
 * Created by HellFirePvP
 * Date: 10.10.2017 / 00:34
 */
public class EventHandlerCapeEffects {

    //Prevent damage overflow for discidia
    private static boolean chainingAttack = false;

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        if(event.getEntityLiving() != null && event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer pl = (EntityPlayer) event.getEntityLiving();
            CapeEffectDiscidia cd = ItemCape.getCapeEffect(pl, Constellations.discidia);
            if(cd != null) {
                cd.writeLastAttackDamage(event.getAmount());
            }
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        if(chainingAttack) return;

        DamageSource ds = event.getSource();
        if(ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) ds.getTrueSource();
            CapeEffectDiscidia cd = ItemCape.getCapeEffect(attacker, Constellations.discidia);
            if(cd != null) {
                double added = cd.getLastAttackDamage();

                chainingAttack = true;
                attacker.attackEntityFrom(DamageSource.causePlayerDamage(attacker), (float) added);
                attacker.attackEntityFrom(CommonProxy.dmgSourceStellar, (float) (added / 2));
                chainingAttack = false;
            }
        }
    }

}
