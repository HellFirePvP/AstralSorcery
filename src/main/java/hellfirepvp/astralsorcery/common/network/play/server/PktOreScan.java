/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import hellfirepvp.astralsorcery.client.util.MiscPlayEffect;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktOreScan
 * Created by HellFirePvP
 * Date: 01.06.2019 / 23:55
 */
public class PktOreScan extends ASPacket<PktOreScan> {

    private List<BlockPos> positions = new ArrayList<>();

    public PktOreScan() {}

    public PktOreScan(List<BlockPos> positions) {
        this.positions = positions;
    }

    @Nonnull
    @Override
    public Encoder<PktOreScan> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeList(buffer, packet.positions, ByteBufUtils::writePos);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktOreScan> decoder() {
        return buffer -> {
            return new PktOreScan(ByteBufUtils.readList(buffer, ByteBufUtils::readPos));
        };
    }

    @Nonnull
    @Override
    public Handler<PktOreScan> handler() {
        return new Handler<PktOreScan>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktOreScan packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    PlayerEntity player = Minecraft.getInstance().player;
                    if (player == null) {
                        return;
                    }

                    for (BlockPos at : packet.positions) {
                        Vector3 atPos = new Vector3(at).add(0.5, 0.5, 0.5);
                        atPos.add(rand.nextFloat() - rand.nextFloat(), rand.nextFloat() - rand.nextFloat(), rand.nextFloat() - rand.nextFloat());
                        BlockState state = Minecraft.getInstance().world.getBlockState(at);
                        //if (Mods.ORESTAGES.isPresent()) {
                        //    if(changed.contains(state) || !ModIntegrationOreStages.canSeeOreClient(state)) {
                        //        changed.add(state);
                        //        continue;
                        //    }
                        //}
                        MiscPlayEffect.playSingleBlockTumbleDepthEffect(atPos, state);
                    }
                });
            }

            @Override
            public void handle(PktOreScan packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
