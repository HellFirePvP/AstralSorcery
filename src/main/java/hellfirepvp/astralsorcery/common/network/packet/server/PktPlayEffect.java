/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.common.item.wand.ItemWand;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktPlayEffect
 * Created by HellFirePvP
 * Date: 01.06.2019 / 22:15
 */
public class PktPlayEffect extends ASPacket<PktPlayEffect> {

    private Type type;
    private Vector3 pos = null;
    private Vector3 target = null;
    private Number data = null;

    public PktPlayEffect() {}

    public PktPlayEffect(Type type) {
        this.type = type;
    }

    public PktPlayEffect setPos(Vector3 pos) {
        this.pos = pos;
        return this;
    }

    public Vector3 getPos() {
        return pos;
    }

    public PktPlayEffect setTarget(Vector3 target) {
        this.target = target;
        return this;
    }

    public Vector3 getTarget() {
        return target;
    }

    public PktPlayEffect setColor(Color c) {
        setData(c.getRGB());
        return this;
    }

    public Color getColor() {
        return new Color(getIntData(), true);
    }

    public PktPlayEffect setData(Number nbr) {
        this.data = nbr;
        return this;
    }

    public float getFloatData() {
        return Float.intBitsToFloat(this.data.intValue());
    }

    public double getDoubleData() {
        return Double.longBitsToDouble(this.data.longValue());
    }

    public long getLongData() {
        return this.data.longValue();
    }

    public int getIntData() {
        return this.data.intValue();
    }

    public byte getByteData() {
        return this.data.byteValue();
    }

    @Nonnull
    @Override
    public Encoder<PktPlayEffect> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeEnumValue(buffer, packet.type);
            ByteBufUtils.writeOptional(buffer, packet.pos, ByteBufUtils::writeVector);
            ByteBufUtils.writeOptional(buffer, packet.target, ByteBufUtils::writeVector);
            ByteBufUtils.writeOptional(buffer, packet.data, ByteBufUtils::writeNumber);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktPlayEffect> decoder() {
        return buffer -> {
            Type type = ByteBufUtils.readEnumValue(buffer, Type.class);
            PktPlayEffect pkt = new PktPlayEffect(type);
            pkt.setPos(ByteBufUtils.readOptional(buffer, ByteBufUtils::readVector));
            pkt.setTarget(ByteBufUtils.readOptional(buffer, ByteBufUtils::readVector));
            pkt.setData(ByteBufUtils.readOptional(buffer, ByteBufUtils::readNumber));
            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktPlayEffect> handler() {
        return new Handler<PktPlayEffect>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktPlayEffect packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> packet.type.runEffect().accept(packet));
            }

            @Override
            public void handle(PktPlayEffect packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }

    public static enum Type {

        ROCK_CRYSTAL_HIGHTLIGHT,
        WELL_CATALYST_BREAK;

        @OnlyIn(Dist.CLIENT)
        private Consumer<PktPlayEffect> runEffect() {
            switch (this) {
                case ROCK_CRYSTAL_HIGHTLIGHT:
                    return ItemWand::playEffect;
                case WELL_CATALYST_BREAK:
                    return TileWell::catalystBurst;
            }
            return (pkt) -> {};
        }

    }

}
