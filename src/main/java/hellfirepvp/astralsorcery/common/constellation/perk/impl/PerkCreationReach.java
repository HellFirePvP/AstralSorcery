/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktUpdateReach;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkCreationReach
 * Created by HellFirePvP
 * Date: 23.12.2016 / 11:16
 */
public class PerkCreationReach extends ConstellationPerk {

    private static float reachModifier = 3.5F;
    private static AttributeModifier MODIFIER_EXTEND_REACH;

    public PerkCreationReach() {
        super("CRE_REACH", Target.PLAYER_TICK);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if(side == Side.SERVER) {
            if(!isCooldownActiveForPlayer(player)) {
                if(!player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).hasModifier(MODIFIER_EXTEND_REACH)) {
                    player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).applyModifier(MODIFIER_EXTEND_REACH);
                }
                PktUpdateReach pkt = new PktUpdateReach(true);
                PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);
            }
            setCooldownActiveForPlayer(player, 30);
        }
    }

    @Override
    public void onTimeout(EntityPlayer player) {
        if(player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).hasModifier(MODIFIER_EXTEND_REACH)) {
            player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).removeModifier(MODIFIER_EXTEND_REACH);
        }
        PktUpdateReach pkt = new PktUpdateReach(false);
        PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);
    }

    @SideOnly(Side.CLIENT)
    public static void updateReach(boolean apply) {
        EntityPlayer pl = Minecraft.getMinecraft().player;
        if(pl != null) {
            if(!apply) {
                if(pl.getEntityAttribute(EntityPlayer.REACH_DISTANCE).hasModifier(MODIFIER_EXTEND_REACH)) {
                    pl.getEntityAttribute(EntityPlayer.REACH_DISTANCE).removeModifier(MODIFIER_EXTEND_REACH);
                }
            } else {
                if(!pl.getEntityAttribute(EntityPlayer.REACH_DISTANCE).hasModifier(MODIFIER_EXTEND_REACH)) {
                    pl.getEntityAttribute(EntityPlayer.REACH_DISTANCE).applyModifier(MODIFIER_EXTEND_REACH);
                }
            }
        } else {
            AstralSorcery.proxy.scheduleClientside(() -> updateReach(apply)); //Retry.
        }
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        reachModifier = cfg.getFloat(getKey() + "ReachModifier", getConfigurationSection(), reachModifier, 0F, 200F, "Sets the reach modifier that gets applied when the player has this perk. (Too high values might cause issues.)");

        MODIFIER_EXTEND_REACH = new AttributeModifier(UUID.fromString("2f36e6fc-ddfe-11e7-80c1-9a214cf093ae"), "AS_REACH_EXTEND", reachModifier, 0);
    }

}
