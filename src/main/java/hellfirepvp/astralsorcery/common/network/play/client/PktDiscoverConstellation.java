/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktDiscoverConstellation
 * Created by HellFirePvP
 * Date: 02.06.2019 / 13:40
 */
public class PktDiscoverConstellation extends ASPacket<PktDiscoverConstellation> {

    private IConstellation constellation;

    public PktDiscoverConstellation() {}

    public PktDiscoverConstellation(IConstellation constellation) {
        this.constellation = constellation;
    }

    @Nonnull
    @Override
    public Encoder<PktDiscoverConstellation> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeRegistryEntry(buffer, packet.constellation);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktDiscoverConstellation> decoder() {
        return buffer -> new PktDiscoverConstellation(ByteBufUtils.readRegistryEntry(buffer));
    }

    @Nonnull
    @Override
    public Handler<PktDiscoverConstellation> handler() {
        return (packet, context, side) -> {
            context.enqueueWork(() -> {
                if (side == LogicalSide.SERVER) {
                    PlayerEntity player = context.getSender();
                    PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
                    if (prog.isValid() &&
                            packet.constellation.canDiscover(player, prog) &&
                            ResearchManager.discoverConstellation(packet.constellation, player)) {
                        player.sendMessage(new TranslationTextComponent("astralsorcery.progress.constellation.discover.chat",
                                packet.constellation.getConstellationName().applyTextStyle(TextFormatting.GRAY))
                                .applyTextStyle(TextFormatting.BLUE));
                    }
                }
            });
        };
    }
}
