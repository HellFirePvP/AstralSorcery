/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournal;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPerkTree;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalProgression;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktProgressionUpdate
 * Created by HellFirePvP
 * Date: 02.06.2019 / 00:06
 */
public class PktProgressionUpdate extends ASPacket<PktProgressionUpdate> {

    public ProgressionTier tier = null;
    public ResearchProgression prog = null;

    public PktProgressionUpdate() {}

    public PktProgressionUpdate(ResearchProgression prog) {
        this.prog = prog;
    }

    public PktProgressionUpdate(ProgressionTier tier) {
        this.tier = tier;
    }

    private PktProgressionUpdate(ProgressionTier tier, ResearchProgression prog) {
        this.tier = tier;
        this.prog = prog;
    }

    @Nonnull
    @Override
    public Encoder<PktProgressionUpdate> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeOptional(buffer, packet.tier, ByteBufUtils::writeEnumValue);
            ByteBufUtils.writeOptional(buffer, packet.prog, ByteBufUtils::writeEnumValue);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktProgressionUpdate> decoder() {
        return buffer ->
                new PktProgressionUpdate(
                        ByteBufUtils.readOptional(buffer, buf -> ByteBufUtils.readEnumValue(buf, ProgressionTier.class)),
                        ByteBufUtils.readOptional(buffer, buf -> ByteBufUtils.readEnumValue(buf, ResearchProgression.class)));
    }

    @Nonnull
    @Override
    public Handler<PktProgressionUpdate> handler() {
        return new Handler<PktProgressionUpdate>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktProgressionUpdate packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    if (packet.tier != null) {
                        Minecraft.getInstance().player.sendMessage(
                                new TranslationTextComponent("astralsorcery.progress.gain.progress.chat")
                                        .applyTextStyle(TextFormatting.BLUE));
                    }
                    if (packet.prog != null) {
                        Minecraft.getInstance().player.sendMessage(
                                new TranslationTextComponent("astralsorcery.progress.gain.research.chat", packet.prog.getName())
                                        .applyTextStyle(TextFormatting.AQUA));
                    }
                    packet.refreshJournal();
                });
            }

            @Override
            public void handle(PktProgressionUpdate packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }

    @OnlyIn(Dist.CLIENT)
    private void refreshJournal() {
        Screen open = Minecraft.getInstance().currentScreen;
        if (open != null) {
            if (open instanceof ScreenJournal && !(open instanceof ScreenJournalPerkTree)) {
                Minecraft.getInstance().displayGuiScreen(null);
            }
        }
        ScreenJournalProgression.resetJournal();
    }
}
