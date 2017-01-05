/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRequestSeed
 * Created by HellFirePvP
 * Date: 02.12.2016 / 17:40
 */
public class PktRequestSeed implements IMessage, IMessageHandler<PktRequestSeed, PktRequestSeed> {

    private int dimId, session;
    private long seed;

    public PktRequestSeed() {}

    public PktRequestSeed(int session, int dimId) {
        this.dimId = dimId;
        this.session = session;
        this.seed = -1;
    }

    private PktRequestSeed seed(long seed) {
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
            WorldProvider mgr = DimensionManager.getProvider(message.dimId);
            return new PktRequestSeed(message.session, message.dimId).seed(mgr.getSeed());
        } else {
            ConstellationSkyHandler.getInstance().updateSeedCache(message.dimId, message.session, message.seed);
        }
        return null;
    }

}
