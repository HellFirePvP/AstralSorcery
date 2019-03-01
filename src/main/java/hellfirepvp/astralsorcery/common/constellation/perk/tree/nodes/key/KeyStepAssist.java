/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkEffectHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.types.ICooldownPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncStepAssist;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyStepAssist
 * Created by HellFirePvP
 * Date: 02.08.2018 / 22:54
 */
public class KeyStepAssist extends KeyPerk implements IPlayerTickPerk, ICooldownPerk {

    public KeyStepAssist(String name, int x, int y) {
        super(name, x, y);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if (side == Side.SERVER) {
            if(!PerkEffectHelper.EVENT_INSTANCE.isCooldownActiveForPlayer(player, this)) {
                player.stepHeight += 0.5F;
            } else {
                if(player.stepHeight < 1.1F) {
                    player.stepHeight = 1.1F;
                }
            }
            PerkEffectHelper.EVENT_INSTANCE.forceSetCooldownForPlayer(player, this, 20);
            if(MiscUtils.isConnectionEstablished((EntityPlayerMP) player)) {
                PktSyncStepAssist sync = new PktSyncStepAssist(player.stepHeight);
                PacketChannel.CHANNEL.sendTo(sync, (EntityPlayerMP) player);
            }
        }
    }

    @Override
    public void handleCooldownTimeout(EntityPlayer player) {
        player.stepHeight -= 0.5F;
        if(player.stepHeight < 0.6F) {
            player.stepHeight = 0.6F;
        }

        if(MiscUtils.isConnectionEstablished((EntityPlayerMP) player)) {
            PktSyncStepAssist sync = new PktSyncStepAssist(player.stepHeight);
            PacketChannel.CHANNEL.sendTo(sync, (EntityPlayerMP) player);
        }
    }

}
