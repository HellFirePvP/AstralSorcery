/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPerkTree;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktUnlockPerk
 * Created by HellFirePvP
 * Date: 02.06.2019 / 15:08
 */
public class PktUnlockPerk extends ASPacket<PktUnlockPerk> {

    private AbstractPerk perk = null;
    private boolean serverAccept = false;

    public PktUnlockPerk() {}

    public PktUnlockPerk(boolean serverAccepted, AbstractPerk perk) {
        this.serverAccept = serverAccepted;
        this.perk = perk;
    }

    @Nonnull
    @Override
    public Encoder<PktUnlockPerk> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeOptional(buffer, packet.perk, ByteBufUtils::writeRegistryEntry);
            buffer.writeBoolean(packet.serverAccept);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktUnlockPerk> decoder() {
        return buffer -> {
            PktUnlockPerk pkt = new PktUnlockPerk();

            pkt.perk = ByteBufUtils.readOptional(buffer, ByteBufUtils::readRegistryEntry);
            pkt.serverAccept = buffer.readBoolean();

            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktUnlockPerk> handler() {
        return new Handler<PktUnlockPerk>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktUnlockPerk packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    if (packet.serverAccept) {
                        Screen current = Minecraft.getInstance().currentScreen;
                        if (current instanceof ScreenJournalPerkTree) {
                            Minecraft.getInstance().enqueue(() -> ((ScreenJournalPerkTree) current).playUnlockAnimation(packet.perk));
                        }
                    }
                });
            }

            @Override
            public void handle(PktUnlockPerk packet, NetworkEvent.Context context, LogicalSide side) {
                context.enqueueWork(() -> {
                    PlayerEntity player = context.getSender();
                    PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
                    if (!prog.hasPerkUnlocked(packet.perk) && prog.isValid()) {
                        if (packet.perk.mayUnlockPerk(prog, player) && ResearchManager.applyPerk(player, packet.perk)) {
                            packet.replyWith(new PktUnlockPerk(true, packet.perk), context);
                        }
                    }
                });
            }
        };
    }
}
