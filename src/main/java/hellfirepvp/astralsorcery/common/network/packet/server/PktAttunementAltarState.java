/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktAttunementAltarState
 * Created by HellFirePvP
 * Date: 01.06.2019 / 18:33
 */
public class PktAttunementAltarState extends ASPacket<PktAttunementAltarState> {

    private DimensionType type;
    private int entityId = -1;
    private BlockPos at = BlockPos.ORIGIN;
    private boolean started = false;

    public PktAttunementAltarState() {}

    public PktAttunementAltarState(int entityId, DimensionType type, BlockPos at) {
        this.entityId = entityId;
        this.type = type;
        this.at = at;
    }

    public PktAttunementAltarState(boolean started, DimensionType type, BlockPos at) {
        this.started = started;
        this.type = type;
        this.at = at;
    }

    private PktAttunementAltarState(int entityId, DimensionType type, BlockPos at, boolean started) {
        this.entityId = entityId;
        this.type = type;
        this.at = at;
        this.started = started;
    }

    @Nonnull
    @Override
    public Encoder<PktAttunementAltarState> encoder() {
        return (pktAttunementAltarState, buffer) -> {
            buffer.writeInt(entityId);
            buffer.writeBoolean(started);
            ByteBufUtils.writeResourceLocation(buffer, type.getRegistryName());
            ByteBufUtils.writePos(buffer, at);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktAttunementAltarState> decoder() {
        return buffer -> {
            int entityId = buffer.readInt();
            boolean started = buffer.readBoolean();
            DimensionType type = DimensionType.byName(ByteBufUtils.readResourceLocation(buffer));
            BlockPos at = ByteBufUtils.readPos(buffer);
            return new PktAttunementAltarState(entityId, type, at, started);
        };
    }

    @Nonnull
    @Override
    public Handler<PktAttunementAltarState> handler() {
        return new Handler<PktAttunementAltarState>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktAttunementAltarState packet, NetworkEvent.Context context) {
                World mcWorld = LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT);
                if (mcWorld.getDimension().getType().equals(packet.type)) {
                    EntityPlayer player = Minecraft.getInstance().player;
                    if (player != null && player.getEntityId() == packet.entityId) {
                        TileAttunementAltar ta = MiscUtils.getTileAt(mcWorld, packet.at, TileAttunementAltar.class, true);
                        if (ta != null) {
                            if(ta.tryStartCameraFlight()) {
                                replyWith(new PktAttunementAltarState(true, packet.type, packet.at), context);
                            }
                        }
                    }
                }
                context.setPacketHandled(true);
            }

            @Override
            public void handle(PktAttunementAltarState packet, NetworkEvent.Context context, LogicalSide side) {
                context.enqueueWork(() -> {
                    if (packet.started) {
                        MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(side);
                        World w = DimensionManager.getWorld(srv, packet.type, true, true);
                        TileAttunementAltar ta = MiscUtils.getTileAt(w, packet.at, TileAttunementAltar.class, true);
                        if(ta != null) {
                            EntityPlayer pl = context.getSender();
                            ta.markPlayerStartCameraFlight(pl);
                        }
                    }
                });
                context.setPacketHandled(true);
            }
        };
    }
}
