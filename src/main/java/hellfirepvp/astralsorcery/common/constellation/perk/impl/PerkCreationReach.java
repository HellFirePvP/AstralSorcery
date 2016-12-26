package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.util.ExtendedChainingPlayerController;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktUpdateReach;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkCreationReach
 * Created by HellFirePvP
 * Date: 23.12.2016 / 11:16
 */
public class PerkCreationReach extends ConstellationPerk {

    private float reachModifier = 3.5F;

    public PerkCreationReach() {
        super("CRE_REACH", Target.PLAYER_TICK);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if(side == Side.SERVER) {
            if(!isCooldownActiveForPlayer(player)) {
                if(player instanceof EntityPlayerMP) {
                    double reach = Math.max(5D, ((EntityPlayerMP) player).interactionManager.getBlockReachDistance() + reachModifier);
                    ((EntityPlayerMP) player).interactionManager.setBlockReachDistance(reach);
                    PktUpdateReach pkt = new PktUpdateReach(true, reachModifier);
                    PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);
                }
            }
            setCooldownActiveForPlayer(player, 30);
        }
    }

    @Override
    public void onTimeout(EntityPlayer player) {
        if(player instanceof EntityPlayerMP) {
            double reach = Math.max(5D, ((EntityPlayerMP) player).interactionManager.getBlockReachDistance() - reachModifier);
            ((EntityPlayerMP) player).interactionManager.setBlockReachDistance(reach);
            PktUpdateReach pkt = new PktUpdateReach(false, 0F);
            PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void updateReach(boolean apply, float modifierIn) {
        EntityPlayer pl = Minecraft.getMinecraft().player;
        PlayerControllerMP ctrl = Minecraft.getMinecraft().playerController;
        if(pl != null && ctrl != null) {
            if(!(ctrl instanceof ExtendedChainingPlayerController)) {
                ExtendedChainingPlayerController ovr = new ExtendedChainingPlayerController(ctrl);
                boolean isFlying = pl.capabilities.isFlying;
                boolean allowFlight = pl.capabilities.allowFlying;
                ovr.setGameType(ctrl.getCurrentGameType()); //Overwrites fly states depending on gametype. Please. no.
                pl.capabilities.isFlying = isFlying;
                pl.capabilities.allowFlying = allowFlight;
                Minecraft.getMinecraft().playerController = ovr;
            }

            ExtendedChainingPlayerController ctr = (ExtendedChainingPlayerController) Minecraft.getMinecraft().playerController;
            if(apply) {
                ctr.setReachModifier(modifierIn);
            } else {
                ctr.setReachModifier(0F);
            }
        } else {
            AstralSorcery.proxy.scheduleClientside(() -> updateReach(apply, modifierIn)); //Retry.
        }
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        reachModifier = cfg.getFloat(getKey() + "ReachModifier", getConfigurationSection(), 3.5F, 0F, 200F, "Sets the reach modifier that gets applied when the player has this perk. (Too high values might cause issues.)");
    }

}
