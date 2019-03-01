/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.network.packet.ClientReplyPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRequestSeed
 * Created by HellFirePvP
 * Date: 02.12.2016 / 17:40
 */
public class PktRequestSeed implements IMessage, IMessageHandler<PktRequestSeed, PktRequestSeed>, ClientReplyPacket {

    private Integer dimId, session;
    private Long seed;

    public PktRequestSeed() {}

    public PktRequestSeed(Integer session, Integer dimId) {
        this.dimId = dimId;
        this.session = session;
        this.seed = -1L;
    }

    private PktRequestSeed seed(Long seed) {
        this.seed = seed;
        return this;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.dimId = buf.readInt();
        this.session = buf.readInt();
        this.seed = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimId);
        buf.writeInt(session);
        buf.writeLong(seed);
    }

    @Override
    public PktRequestSeed onMessage(PktRequestSeed message, MessageContext ctx) {
        if(ctx.side == Side.SERVER) {
            Long seed;
            try {
                WorldProvider mgr = DimensionManager.getProvider(message.dimId);
                seed = new Random(mgr.getSeed()).nextLong();
            } catch (Exception exc) {
                World plWorld = ctx.getServerHandler().player.world;
                if(plWorld.provider.getDimension() == message.dimId) {
                    seed = ctx.getServerHandler().player.world.getSeed();
                    seed = new Random(seed).nextLong();
                } else {
                    return null; //Who sent that packet? World desync between server and client?...
                }
            }
            return new PktRequestSeed(message.session, message.dimId).seed(seed);
        } else {
            updateSeedClient(message.dimId, message.session, message.seed);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void updateSeedClient(int dimId, int session, long seed) {
        Minecraft.getMinecraft().addScheduledTask(() ->
                ConstellationSkyHandler.getInstance().updateSeedCache(dimId, session, seed));
    }

}
