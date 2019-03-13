/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyNoKnockBack
 * Created by HellFirePvP
 * Date: 23.11.2018 / 20:13
 */
public class KeyNoKnockBack extends KeyPerk {

    public KeyNoKnockBack(String name, int x, int y) {
        super(name, x, y);
    }

    @SubscribeEvent
    public void onKnockBack(LivingKnockBackEvent event) {
        EntityLivingBase attacked = event.getEntityLiving();
        if (attacked instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attacked;
            Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                event.setCanceled(true);
            }
        }
    }

}
