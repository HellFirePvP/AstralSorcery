/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.item.tool.ItemChargedCrystalAxe;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktDualParticleEvent
 * Created by HellFirePvP
 * Date: 11.03.2017 / 22:05
 */
public class PktDualParticleEvent implements IMessage, IMessageHandler<PktDualParticleEvent, IMessage> {

    private int typeOrdinal;
    private double originX, originY, originZ;
    private double targetX, targetY, targetZ;
    private double additionalData = 0;

    public PktDualParticleEvent() {}

    public PktDualParticleEvent(DualParticleEventType type, Vector3 origin, Vector3 target) {
        this.typeOrdinal = type.ordinal();
        this.originX = origin.getX();
        this.originY = origin.getY();
        this.originZ = origin.getZ();
        this.targetX = target.getX();
        this.targetY = target.getY();
        this.targetZ = target.getZ();
    }

    public void setAdditionalData(double additionalData) {
        this.additionalData = additionalData;
    }

    public double getAdditionalData() {
        return additionalData;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.typeOrdinal = buf.readInt();
        this.originX = buf.readDouble();
        this.originY = buf.readDouble();
        this.originZ = buf.readDouble();
        this.targetX = buf.readDouble();
        this.targetY = buf.readDouble();
        this.targetZ = buf.readDouble();
        this.additionalData = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(typeOrdinal);
        buf.writeDouble(originX);
        buf.writeDouble(originY);
        buf.writeDouble(originZ);
        buf.writeDouble(targetX);
        buf.writeDouble(targetY);
        buf.writeDouble(targetZ);
        buf.writeDouble(additionalData);
    }

    @Override
    public IMessage onMessage(PktDualParticleEvent message, MessageContext ctx) {
        try {
            DualParticleEventType type = DualParticleEventType.values()[message.typeOrdinal];
            EventAction trigger = type.getTrigger(ctx.side);
            if(trigger != null) {
                trigger.trigger(message);
            }
        } catch (Exception exc) {
            AstralSorcery.log.warn("[AstralSorcery] Error executing DualParticleEventType " + message.typeOrdinal + " from " + getOriginVec() + " to " + getTargetVec());
        }
        return null;
    }

    public Vector3 getOriginVec() {
        return new Vector3(originX, originY, originZ);
    }

    public Vector3 getTargetVec() {
        return new Vector3(targetX, targetY, targetZ);
    }

    public static enum DualParticleEventType {

        CHARGE_HARVEST;

        @SideOnly(Side.CLIENT)
        private static EventAction getClientTrigger(DualParticleEventType type) {
            switch (type) {
                case CHARGE_HARVEST:
                    return ItemChargedCrystalAxe::playDrainParticles;
            }
            return null;
        }

        public EventAction getTrigger(Side side) {
            if(!side.isClient()) return null;
            return getClientTrigger(this);
        }

    }

    private static interface EventAction {

        public void trigger(PktDualParticleEvent event);

    }

}
