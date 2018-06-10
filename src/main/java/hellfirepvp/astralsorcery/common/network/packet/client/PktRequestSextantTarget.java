/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.client.util.UISextantCache;
import hellfirepvp.astralsorcery.common.item.tool.sextant.ItemSextant;
import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.ClientReplyPacket;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRequestSextantTarget
 * Created by HellFirePvP
 * Date: 08.06.2018 / 16:48
 */
public class PktRequestSextantTarget implements IMessageHandler<PktRequestSextantTarget, IMessage>, IMessage, ClientReplyPacket {

    private String regNameExpected = null;

    private BlockPos resultPos = null;
    private Integer resultDim = null;

    public PktRequestSextantTarget() {}

    public PktRequestSextantTarget(SextantFinder.TargetObject object) {
        this.regNameExpected = object.getRegistryName();
    }

    public PktRequestSextantTarget(SextantFinder.TargetObject to, @Nullable BlockPos result, Integer dimension) {
        this.regNameExpected = to.getRegistryName();
        this.resultPos = result;
        this.resultDim = dimension;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.regNameExpected = ByteBufUtils.readString(buf);
        if (buf.readBoolean()) {
            this.resultPos = ByteBufUtils.readPos(buf);
        }
        if (buf.readBoolean()) {
            this.resultDim = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeString(buf, this.regNameExpected);

        buf.writeBoolean(resultPos != null);
        if (resultPos != null) {
            ByteBufUtils.writePos(buf, resultPos);
        }

        buf.writeBoolean(resultDim != null);
        if (resultDim != null) {
            buf.writeInt(resultDim);
        }
    }

    @Override
    public IMessage onMessage(PktRequestSextantTarget message, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                SextantFinder.TargetObject to = SextantFinder.getByName(message.regNameExpected);
                if (to == null) return;
                EntityPlayerMP player = ctx.getServerHandler().player;
                if (!MiscUtils.isPlayerFakeMP(player)) {
                    Tuple<EnumHand, ItemStack> heldStack = MiscUtils.getMainOrOffHand(player, ItemsAS.sextant, to::isSelectable);
                    if (heldStack == null) {
                        return;
                    }
                    Thread tr = new Thread(() -> {
                        //May be null; In that case, tell that to the client as well so it won't ask the server any longer.
                        BlockPos result = to.searchFor((WorldServer) player.world, player.getPosition());

                        PktRequestSextantTarget target = new PktRequestSextantTarget(to, result, player.world.provider.getDimension());
                        PacketChannel.CHANNEL.sendTo(target, player);
                    });
                    tr.setName("SextantTargetFinder ThreadId=" + tr.getId());
                    tr.start();
                }
            });
        } else {
            handlePacketClient(message, ctx);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void handlePacketClient(PktRequestSextantTarget pkt, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().player == null ||
                    Minecraft.getMinecraft().world == null) {
                return;
            }
            SextantFinder.TargetObject to = SextantFinder.getByName(pkt.regNameExpected);
            if (to == null) return;
            UISextantCache.addTarget(to, pkt.resultPos, pkt.resultDim);
        });
    }

}
