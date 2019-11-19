/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.client.util.UISextantCache;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        this.resultPos = ByteBufUtils.readOptional(buf, ByteBufUtils::readPos);
        this.resultDim = ByteBufUtils.readOptional(buf, ByteBuf::readInt);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeString(buf, this.regNameExpected);
        ByteBufUtils.writeOptional(buf, resultPos, ByteBufUtils::writePos);
        ByteBufUtils.writeOptional(buf, resultDim, ByteBuf::writeInt);
    }

    @Override
    public IMessage onMessage(PktRequestSextantTarget pkt, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                SextantFinder.TargetObject to = SextantFinder.getByName(pkt.regNameExpected);
                if (to == null) return;
                EntityPlayerMP player = ctx.getServerHandler().player;
                if (!MiscUtils.isPlayerFakeMP(player)) {
                    Tuple<EnumHand, ItemStack> heldStack = MiscUtils.getMainOrOffHand(player, ItemsAS.sextant,
                            (st) -> to.isSelectable(st, ResearchManager.getProgress(player, Side.SERVER)));
                    if (heldStack == null) {
                        return;
                    }

                    ExecutorService exec = Executors.newSingleThreadExecutor();
                    try {
                        exec.invokeAll(Collections.singletonList(
                                () -> {
                                    BlockPos result = to.searchFor((WorldServer) player.world, player.getPosition());

                                    PktRequestSextantTarget target = new PktRequestSextantTarget(to, result, player.world.provider.getDimension());
                                    PacketChannel.CHANNEL.sendTo(target, player);
                                    return null;
                                }
                        ), 5, TimeUnit.SECONDS);
                    } catch (InterruptedException ignored) {
                        // No-Op, drop the task if it fails.
                    } finally {
                        exec.shutdown();
                    }
                }
            });
        } else {
            handlePacketClient(pkt);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void handlePacketClient(PktRequestSextantTarget pkt) {
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
