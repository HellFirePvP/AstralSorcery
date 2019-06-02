/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.sextant.TargetObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRequestSextantTarget
 * Created by HellFirePvP
 * Date: 02.06.2019 / 14:46
 */
public class PktRequestSextantTarget extends ASPacket<PktRequestSextantTarget> {

    private TargetObject target = null;

    private BlockPos resultPos = null;
    private DimensionType type = null;

    public PktRequestSextantTarget() {}

    public PktRequestSextantTarget(TargetObject target) {
        this.target = target;
    }

    public PktRequestSextantTarget(TargetObject target, @Nullable BlockPos result, DimensionType type) {
        this.target = target;
        this.resultPos = result;
        this.type = type;
    }

    @Nonnull
    @Override
    public Encoder<PktRequestSextantTarget> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeRegistryEntry(buffer, packet.target);
            ByteBufUtils.writeOptional(buffer, packet.resultPos, ByteBufUtils::writePos);
            ByteBufUtils.writeOptional(buffer, packet.type, ByteBufUtils::writeRegistryEntry);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktRequestSextantTarget> decoder() {
        return buffer -> {
            PktRequestSextantTarget pkt = new PktRequestSextantTarget();

            pkt.target = ByteBufUtils.readRegistryEntry(buffer);
            pkt.resultPos = ByteBufUtils.readOptional(buffer, ByteBufUtils::readPos);
            pkt.type = ByteBufUtils.readOptional(buffer, ByteBufUtils::readRegistryEntry);

            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktRequestSextantTarget> handler() {
        return new Handler<PktRequestSextantTarget>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktRequestSextantTarget packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> UISextantCache.addTarget(packet.target, packet.resultPos, packet.type));
            }

            @Override
            public void handle(PktRequestSextantTarget packet, NetworkEvent.Context context, LogicalSide side) {
                context.enqueueWork(() -> {
                    TargetObject to = packet.target;
                    EntityPlayerMP player = context.getSender();
                    if (!MiscUtils.isPlayerFakeMP(player)) {
                        Tuple<EnumHand, ItemStack> heldStack = MiscUtils.getMainOrOffHand(player, ItemsAS.sextant,
                                (st) -> to.isSelectable(st, ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER)));
                        if (heldStack == null) {
                            return;
                        }

                        ExecutorService exec = Executors.newSingleThreadExecutor();
                        try {
                            exec.invokeAll(Collections.singletonList(
                                    () -> {
                                        BlockPos result = to.searchFor((WorldServer) player.world, player.getPosition());

                                        PktRequestSextantTarget target = new PktRequestSextantTarget(to, result, player.world.getDimension().getType());
                                        packet.replyWith(target, context);
                                        return null;
                                    }
                            ), 5, TimeUnit.SECONDS);
                        } catch (InterruptedException ignored) {
                            // No-Op, drop the task if it fails.
                        } finally {
                            exec.shutdown();
                        }
                    }
                });
            }
        };
    }
}
