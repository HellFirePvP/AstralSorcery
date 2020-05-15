/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
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
 * Class: PktToggleClientOption
 * Created by HellFirePvP
 * Date: 13.05.2020 / 19:25
 */
public class PktToggleClientOption extends ASPacket<PktToggleClientOption> {

    private Option option;

    public PktToggleClientOption() {}

    public PktToggleClientOption(Option option) {
        this.option = option;
    }

    @Nonnull
    @Override
    public Encoder<PktToggleClientOption> encoder() {
        return (pkt, buf) -> ByteBufUtils.writeEnumValue(buf, pkt.option);
    }

    @Nonnull
    @Override
    public Decoder<PktToggleClientOption> decoder() {
        return buf -> new PktToggleClientOption(ByteBufUtils.readEnumValue(buf, Option.class));
    }

    @Nonnull
    @Override
    public Handler<PktToggleClientOption> handler() {
        return new Handler<PktToggleClientOption>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktToggleClientOption packet, NetworkEvent.Context context) {

            }

            @Override
            public void handleServer(PktToggleClientOption packet, NetworkEvent.Context context) {
                ServerPlayerEntity player = context.getSender();
                switch (packet.option) {
                    case DISABLE_PERK_ABILITIES:
                        if (ResearchManager.togglePerkAbilities(player)) {
                            PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
                            if (prog.isValid()) {
                                ITextComponent status;
                                if (prog.doPerkAbilities()) {
                                    status = new TranslationTextComponent("astralsorcery.progress.perk_abilities.enable").applyTextStyle(TextFormatting.GREEN);
                                } else {
                                    status = new TranslationTextComponent("astralsorcery.progress.perk_abilities.disable").applyTextStyle(TextFormatting.RED);
                                }
                                player.sendMessage(new TranslationTextComponent("astralsorcery.progress.perk_abilities", status).applyTextStyle(TextFormatting.GRAY));
                            }
                        }
                        break;
                }
            }

            @Override
            public void handle(PktToggleClientOption packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }

    public static enum Option {

        DISABLE_PERK_ABILITIES

    }
}
