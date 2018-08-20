/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.client.gui.GuiJournalPerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.ClientReplyPacket;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktUnlockPerk
 * Created by HellFirePvP
 * Date: 12.12.2016 / 13:07
 */
public class PktUnlockPerk implements IMessage, IMessageHandler<PktUnlockPerk, PktUnlockPerk>, ClientReplyPacket {

    private AbstractPerk perk;

    private boolean serverAccept = false;

    public PktUnlockPerk() {}

    public PktUnlockPerk(boolean serverAccepted, AbstractPerk perk) {
        this.serverAccept = serverAccepted;
        this.perk = perk;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.serverAccept = buf.readBoolean();
        AbstractPerk perk = PerkTree.PERK_TREE.getPerk(new ResourceLocation(ByteBufUtils.readString(buf)));
        if(perk != null) {
            this.perk = perk;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(serverAccept);
        ByteBufUtils.writeString(buf, perk.getRegistryName().toString());
    }

    @Override
    public PktUnlockPerk onMessage(PktUnlockPerk message, MessageContext ctx) {
        if(ctx.side == Side.SERVER) {
            MinecraftServer ms = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (ms != null) {
                ms.addScheduledTask(() -> {
                    EntityPlayer pl = ctx.getServerHandler().player;
                    if(pl != null) {
                        if(message.perk != null) {
                            AbstractPerk perk = message.perk;
                            PlayerProgress prog = ResearchManager.getProgress(pl, ctx.side);
                            if(prog != null) {
                                if(!prog.hasPerkUnlocked(perk)) {
                                    if(perk.mayUnlockPerk(prog) && ResearchManager.applyPerk(pl, message.perk)) {
                                        PacketChannel.CHANNEL.sendTo(new PktUnlockPerk(true, message.perk), (EntityPlayerMP) pl);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        } else {
            recUnlockResultClient(message);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void recUnlockResultClient(PktUnlockPerk message) {
        if(message.serverAccept) {
            AbstractPerk perk = message.perk;
            GuiScreen current = Minecraft.getMinecraft().currentScreen;
            if(current != null && current instanceof GuiJournalPerkTree) {
                Minecraft.getMinecraft().addScheduledTask(() -> ((GuiJournalPerkTree) current).playUnlockAnimation(perk));
            }
        }
    }

}
