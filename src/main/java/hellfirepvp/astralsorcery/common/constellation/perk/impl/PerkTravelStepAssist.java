/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncStepAssist;
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
        if(side == Side.SERVER) {
            if(!isCooldownActiveForPlayer(player)) {
                player.stepHeight += 0.5F;

                PktSyncStepAssist sync = new PktSyncStepAssist(player.stepHeight);
                PacketChannel.CHANNEL.sendTo(sync, (EntityPlayerMP) player);
            } else {
                if(player.stepHeight < 1.1F) {
                    player.stepHeight = 1.1F;

                    PktSyncStepAssist sync = new PktSyncStepAssist(player.stepHeight);
                    PacketChannel.CHANNEL.sendTo(sync, (EntityPlayerMP) player);
                }
            }
            forceSetCooldownForPlayer(player, 20);
        }
    }

    @Override
    public void onTimeout(EntityPlayer player) {
        player.stepHeight -= 0.5F;
        if(player.stepHeight < 0.6F) {
            player.stepHeight = 0.6F;
        }

        PktSyncStepAssist sync = new PktSyncStepAssist(player.stepHeight);
        PacketChannel.CHANNEL.sendTo(sync, (EntityPlayerMP) player);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {}

}
