package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.entities.EntityGrindstone;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktPlayEntityEffect
 * Created by HellFirePvP
 * Date: 10.11.2016 / 12:47
 */
public class PktEntityEffect implements IMessage, IMessageHandler<PktEntityEffect, IMessage> {

    private byte typeOrdinal;
    public int entityId;

    public PktEntityEffect() {}

    public PktEntityEffect(EntityEffectType type, Entity entity) {
        this.typeOrdinal = (byte) type.ordinal();
        this.entityId = entity.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.typeOrdinal = buf.readByte();
        this.entityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(typeOrdinal);
        buf.writeInt(entityId);
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public Entity getClientWorldEntity() {
        return Minecraft.getMinecraft().theWorld.getEntityByID(entityId);
    }

    @Override
    public IMessage onMessage(PktEntityEffect message, MessageContext ctx) {
        try {
            EntityEffectType type = EntityEffectType.values()[message.typeOrdinal];
            EventAction trigger = type.getTrigger(ctx.side);
            if(trigger != null) {
                trigger.trigger(message);
            }
        } catch (Exception exc) {
            AstralSorcery.log.warn("Error executing ParticleEventType " + message.typeOrdinal + " for entityId " + message.entityId);
        }
        return null;
    }

    public static enum EntityEffectType {

        //DEFINE EVENT TRIGGER IN THE FCKING HUGE SWITCH STATEMENT DOWN TEHRE.
        GRINDSTONE_WHEEL;

        //GOD I HATE THIS PART
        //But i can't do this in the ctor because server-client stuffs.
        @SideOnly(Side.CLIENT)
        private static EventAction getClientTrigger(EntityEffectType type) {
            switch (type) {
                case GRINDSTONE_WHEEL:
                    return EntityGrindstone::playWheelAnimation;
            }
            return null;
        }

        public EventAction getTrigger(Side side) {
            if(!side.isClient()) return null;
            return getClientTrigger(this);
        }

    }

    private static interface EventAction {

        public void trigger(PktEntityEffect event);

    }

}
