/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktAttunementAltarState
 * Created by HellFirePvP
 * Date: 28.12.2016 / 01:53
 */
public class PktAttunementAltarState implements IMessage, IMessageHandler<PktAttunementAltarState, PktAttunementAltarState> {

    private int entityId = -1;
    private int worldId = -1;
    private BlockPos at = BlockPos.ORIGIN;
    private boolean started = false;

    public PktAttunementAltarState() {}

    public PktAttunementAltarState(int entityId, int worldId, BlockPos at) {
        this.entityId = entityId;
        this.worldId = worldId;
        this.at = at;
    }

    public PktAttunementAltarState(boolean started, int worldId, BlockPos at) {
        this.started = started;
        this.worldId = worldId;
        this.at = at;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        started = buf.readBoolean();
        worldId = buf.readInt();
        at = ByteBufUtils.readPos(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeBoolean(started);
        buf.writeInt(worldId);
        ByteBufUtils.writePos(buf, at);
    }

    @Override
    public PktAttunementAltarState onMessage(PktAttunementAltarState message, MessageContext ctx) {
        if(ctx.side == Side.SERVER) {
            if(message.started) {
                World w = DimensionManager.getWorld(message.worldId);
                TileAttunementAltar ta = MiscUtils.getTileAt(w, message.at, TileAttunementAltar.class, true);
                if(ta != null) {
                    EntityPlayer pl = ctx.getServerHandler().player;
                    ta.markPlayerStartCameraFlight(pl);
                }
            }
        } else {
            return recClient(message);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private PktAttunementAltarState recClient(PktAttunementAltarState message) {
        if(Minecraft.getMinecraft().world != null &&
                Minecraft.getMinecraft().world.provider.getDimension() == message.worldId &&
                Minecraft.getMinecraft().player != null &&
                Minecraft.getMinecraft().player.getEntityId() == message.entityId) {
            TileAttunementAltar ta = MiscUtils.getTileAt(Minecraft.getMinecraft().world, message.at, TileAttunementAltar.class, true);
            if (ta != null) {
                if(ta.tryStartCameraFlight()) {
                    return new PktAttunementAltarState(true, message.worldId, message.at);
                }
            }
        }
        return new PktAttunementAltarState(false, -1, BlockPos.ORIGIN);
    }
}
