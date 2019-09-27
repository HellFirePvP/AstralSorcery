/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import hellfirepvp.astralsorcery.client.util.MiscPlayEffect;
import hellfirepvp.astralsorcery.common.auxiliary.BlockBreakHelper;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectAevitas;
import hellfirepvp.astralsorcery.common.item.lens.ItemColoredLensFire;
import hellfirepvp.astralsorcery.common.item.wand.ItemWand;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.time.TimeStopEffectHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktPlayEffect
 * Created by HellFirePvP
 * Date: 01.06.2019 / 22:15
 */
public class PktPlayEffect extends ASPacket<PktPlayEffect> {

    private Type type;
    private Consumer<PacketBuffer> encoder = (buf) -> {};

    private PacketBuffer data = null;

    public PktPlayEffect() {}

    public PktPlayEffect(Type type) {
        this.type = type;
    }

    public PktPlayEffect addData(Consumer<PacketBuffer> encoder) {
        this.encoder = this.encoder.andThen(encoder);
        return this;
    }

    public PacketBuffer getExtraData() {
        return data;
    }

    @Nonnull
    @Override
    public Encoder<PktPlayEffect> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeEnumValue(buffer, packet.type);
            packet.encoder.accept(buffer);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktPlayEffect> decoder() {
        return buffer -> {
            Type type = ByteBufUtils.readEnumValue(buffer, Type.class);
            PktPlayEffect pkt = new PktPlayEffect(type);
            ByteBuf buf = Unpooled.buffer(buffer.readableBytes());
            buffer.readBytes(buf);
            pkt.data = new PacketBuffer(buf);
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

        LIGHTNING,
        BEAM_BREAK,
        ROCK_CRYSTAL_COLUMN,
        ROCK_CRYSTAL_SPARKS,
        WELL_CATALYST_BREAK,
        CROP_GROWTH,
        MELT_BLOCK,
        ALTAR_RECIPE_FINISH,
        TIME_FREEZE_EFFECT;

        @OnlyIn(Dist.CLIENT)
        private Consumer<PktPlayEffect> runEffect() {
            switch (this) {
                case LIGHTNING:
                    return MiscPlayEffect::fireLightning;
                case ROCK_CRYSTAL_COLUMN:
                    return ItemWand::playEffect;
                case ROCK_CRYSTAL_SPARKS:
                    return ItemWand::playUndergroundEffect;
                case WELL_CATALYST_BREAK:
                    return TileWell::catalystBurst;
                case CROP_GROWTH:
                    return CEffectAevitas::playParticles;
                case TIME_FREEZE_EFFECT:
                    return TimeStopEffectHelper::playEntityParticles;
                case BEAM_BREAK:
                    return BlockBreakHelper::blockBreakAnimation;
                case MELT_BLOCK:
                    return ItemColoredLensFire::playParticles;
                case ALTAR_RECIPE_FINISH:
                    return TileAltar::finishCraftingEffects;
            }
            return (pkt) -> {};
        }

    }

}
