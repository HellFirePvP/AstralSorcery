package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktAttenuationState
 * Created by HellFirePvP
 * Date: 19.12.2016 / 12:25
 */
public class PktAttenuationState implements IMessage, IMessageHandler<PktAttenuationState, IMessage> {

    public BlockPos tePos = null;
    public int dimId = 0;
    public int state = 0;

    public PktAttenuationState() {}

    public PktAttenuationState(BlockPos tePos, int dimId, int state) {
        this.tePos = tePos;
        this.dimId = dimId;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tePos = ByteBufUtils.readPos(buf);
        state = buf.readInt();
        dimId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writePos(buf, tePos);
        buf.writeInt(state);
        buf.writeInt(dimId);
    }

    @Override
    public IMessage onMessage(PktAttenuationState message, MessageContext ctx) {
        if(ctx.side == Side.SERVER) {
            WorldServer ws = DimensionManager.getWorld(message.dimId);
            if(ws != null) {
                TileAttunementAltar ta = MiscUtils.getTileAt(ws, message.tePos, TileAttunementAltar.class, true);
                if(ta != null) {
                    //ta.updateState(message.state, ctx.getServerHandler().playerEntity.getUniqueID());
                }
            }
        }
        return null;
    }

}
