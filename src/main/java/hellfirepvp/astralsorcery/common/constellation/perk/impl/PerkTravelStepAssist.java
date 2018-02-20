/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncStepAssist;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTravelStepAssist
 * Created by HellFirePvP
 * Date: 06.04.2017 / 23:40
 */
public class PerkTravelStepAssist extends ConstellationPerk {

    public PerkTravelStepAssist() {
        super("TRV_STEPASSIST", Target.PLAYER_TICK);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if(side == Side.SERVER && player instanceof EntityPlayerMP) {
            if(!isCooldownActiveForPlayer(player)) {
                player.stepHeight += 0.5F;
            } else {
                if(player.stepHeight < 1.1F) {
                    player.stepHeight = 1.1F;
                }
            }
            forceSetCooldownForPlayer(player, 20);
            if(MiscUtils.isConnectionEstablished((EntityPlayerMP) player)) {
                PktSyncStepAssist sync = new PktSyncStepAssist(player.stepHeight);
                PacketChannel.CHANNEL.sendTo(sync, (EntityPlayerMP) player);
            }
        }
    }

    @Override
    public void onTimeout(EntityPlayer player) {
        player.stepHeight -= 0.5F;
        if(player.stepHeight < 0.6F) {
            player.stepHeight = 0.6F;
        }

        if(MiscUtils.isConnectionEstablished((EntityPlayerMP) player)) {
            PktSyncStepAssist sync = new PktSyncStepAssist(player.stepHeight);
            PacketChannel.CHANNEL.sendTo(sync, (EntityPlayerMP) player);
        }
    }

    @Override
    public void loadFromConfig(Configuration cfg) {}

}
