/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.common.constellation.mantle.effect.MantleEffectVicio;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.perk.node.key.KeyMantleFlight;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktElytraCapeState
 * Created by HellFirePvP
 * Date: 02.06.2019 / 13:44
 */
public class PktElytraCapeState extends ASPacket<PktElytraCapeState> {

    private byte type = 0;

    public PktElytraCapeState() {}

    private PktElytraCapeState(int type) {
        this.type = (byte) type;
    }

    public static PktElytraCapeState resetFallDistance() {
        return new PktElytraCapeState(0);
    }

    public static PktElytraCapeState setFlying() {
        return new PktElytraCapeState(1);
    }

    public static PktElytraCapeState resetFlying() {
        return new PktElytraCapeState(2);
    }

    @Nonnull
    @Override
    public Encoder<PktElytraCapeState> encoder() {
        return (packet, buffer) -> buffer.writeByte(packet.type);
    }

    @Nonnull
    @Override
    public Decoder<PktElytraCapeState> decoder() {
        return buffer -> new PktElytraCapeState(buffer.readByte());
    }

    @Nonnull
    @Override
    public Handler<PktElytraCapeState> handler() {
        return (packet, context, side) -> {
            context.enqueueWork(() -> {
                ServerPlayerEntity player = context.getSender();

                MantleEffectVicio vic = ItemMantle.getEffect(player, ConstellationsAS.vicio);
                if (vic == null) {
                    return;
                }

                PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
                boolean hasFlightPerk = progress.hasPerkEffect(p -> p instanceof KeyMantleFlight);
                if (!progress.doPerkAbilities()) {
                    hasFlightPerk = false;
                }

                switch (packet.type) {
                    case 0: {
                        if (player.isElytraFlying()) {
                            player.fallDistance = 0F;
                        }
                        break;
                    }
                    case 1: {
                        if (!hasFlightPerk) {
                            player.startFallFlying();
                        }
                        break;
                    }
                    case 2: {
                        player.stopFallFlying();
                        break;
                    }
                }
            });
        };
    }
}
