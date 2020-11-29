/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPerkTree;
import hellfirepvp.astralsorcery.common.data.research.*;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
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

    private ResourceLocation perkKey = null;
    private boolean serverAccept = false;

    public PktUnlockPerk() {}

    public PktUnlockPerk(boolean serverAccepted, AbstractPerk perk) {
        this.serverAccept = serverAccepted;
        this.perkKey = perk.getRegistryName();
    }

    @Nonnull
    @Override
    public Encoder<PktUnlockPerk> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeOptional(buffer, packet.perkKey, ByteBufUtils::writeResourceLocation);
            buffer.writeBoolean(packet.serverAccept);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktUnlockPerk> decoder() {
        return buffer -> {
            PktUnlockPerk pkt = new PktUnlockPerk();

            pkt.perkKey = ByteBufUtils.readOptional(buffer, ByteBufUtils::readResourceLocation);
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
                        PerkTree.PERK_TREE.getPerk(LogicalSide.CLIENT, packet.perkKey).ifPresent(perk -> {
                            Screen current = Minecraft.getInstance().currentScreen;
                            if (current instanceof ScreenJournalPerkTree) {
                                Minecraft.getInstance().enqueue(() -> ((ScreenJournalPerkTree) current).playUnlockAnimation(perk));
                            }
                        });
                    }
                });
            }

            @Override
            public void handle(PktUnlockPerk packet, NetworkEvent.Context context, LogicalSide side) {
                context.enqueueWork(() -> {
                    PerkTree.PERK_TREE.getPerk(side, packet.perkKey).ifPresent(perk -> {
                        PlayerEntity player = context.getSender();
                        PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
                        if (prog.isValid()) {
                            PlayerPerkData perkData = prog.getPerkData();
                            if (!perkData.hasPerkAllocation(perk)) {
                                if (perk.mayUnlockPerk(prog, player) && ResearchManager.applyPerk(player, perk, PlayerPerkAllocation.unlock())) {
                                    packet.replyWith(new PktUnlockPerk(true, perk), context);
                                }
                            }
                        }
                    });
                });
            }
        };
    }
}
