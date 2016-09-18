package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.entities.EntityGrindstone;
import hellfirepvp.astralsorcery.common.entities.EntityItemStardust;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktParticleEvent
 * Created by HellFirePvP
 * Date: 02.08.2016 / 12:15
 */
public class PktParticleEvent implements IMessage, IMessageHandler<PktParticleEvent, IMessage> {

    private int typeOrdinal;
    private double xCoord, yCoord, zCoord;

    public PktParticleEvent() {}

    public PktParticleEvent(ParticleEventType type, double x, double y, double z) {
        this.typeOrdinal = type.ordinal();
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.typeOrdinal = buf.readInt();
        this.xCoord = buf.readDouble();
        this.yCoord = buf.readDouble();
        this.zCoord = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.typeOrdinal);
        buf.writeDouble(this.xCoord);
        buf.writeDouble(this.yCoord);
        buf.writeDouble(this.zCoord);
    }

    @Override
    public IMessage onMessage(PktParticleEvent message, MessageContext ctx) {
        try {
            ParticleEventType type = ParticleEventType.values()[message.typeOrdinal];
            type.action.trigger(message);
        } catch (Exception exc) {
            AstralSorcery.log.warn("Error executing ParticleEventType " + message.typeOrdinal + " at " + xCoord + ", " + yCoord + ", " + zCoord);
        }
        return null;
    }

    public BlockPos getPos() {
        return new BlockPos(xCoord, yCoord, zCoord);
    }

    public static enum ParticleEventType {

        COLLECTOR_BURST(TileCollectorCrystal::breakParticles),
        CELESTIAL_CRYSTAL_FORM(EntityItemStardust::spawnFormationParticles);

        private final EventAction action;

        private ParticleEventType(EventAction action) {
            this.action = action;
        }

    }

    private static interface EventAction {

        public void trigger(PktParticleEvent event);

    }

}
