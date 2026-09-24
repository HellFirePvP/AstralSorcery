/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchMessageHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ResearchMessageHelper {

    private ResearchMessageHelper() {}

    public static void sendConstellationMemorization(ServerPlayer sPlayer, PlayerProgress progress, BaseConstellation constellation) {
        Component constellationName = constellation.getColoredName();
        Component memorizationCmp = Component.translatable("message.astralsorcery.research.see_constellation", constellationName)
                .withStyle(ChatFormatting.GRAY);
        sPlayer.sendSystemMessage(memorizationCmp);
        if (progress.getSeenConstellations().size() == 1) {
            sPlayer.sendSystemMessage(Component.translatable("message.astralsorcery.research.unlock_paper_storage"));
        }
    }

    public static void sendConstellationDiscovery(ServerPlayer sPlayer, BaseConstellation constellation) {
        Component constellationName = constellation.getColoredName();
        Component discoveryCmp = Component.translatable("message.astralsorcery.research.discover_constellation", constellationName)
                .withStyle(ChatFormatting.GRAY);
        sPlayer.sendSystemMessage(discoveryCmp);
    }

    public static void sendConstellationFocalPointDiscovery(ServerPlayer sPlayer, BaseConstellation cst, boolean cstDiscovered) {
        MutableComponent discoveryCmp = Component.translatable("message.astralsorcery.research.memorize_focal_point");
        if (cstDiscovered) {
            discoveryCmp = Component.translatable("message.astralsorcery.research.discover_focal_point",
                    cst.getColoredName());
        }
        discoveryCmp.withStyle(ChatFormatting.GRAY);
        sPlayer.sendSystemMessage(discoveryCmp);
    }

    public static void sendResearchTierDiscovery(ServerPlayer sPlayer, ResearchTier previousTier, ResearchTier targetTier) {
        if (targetTier.isThisLater(previousTier)) {
            sPlayer.sendSystemMessage(Component.translatable("message.astralsorcery.research.tier.any").withStyle(ChatFormatting.GRAY));
        }
        while (previousTier.hasNextTier() && !previousTier.next().isThisLater(targetTier)) {
            previousTier = previousTier.next();
            sPlayer.sendSystemMessage(Component.empty()
                    .append(previousTier.getDiscoveryMessage())
                    .withStyle(ChatFormatting.BLUE));
        }
    }

    public static void sendResearchFlagDiscovery(ServerPlayer sPlayer, ResearchFlag flag) {
        MutableComponent discoveryCmp = Component.translatable(String.format("message.astralsorcery.research.flag.%s", flag.name().toLowerCase(Locale.ROOT)))
                .withStyle(ChatFormatting.GRAY);
        sPlayer.sendSystemMessage(discoveryCmp);
    }

    public static void sendLumenDiscovery(ServerPlayer sPlayer, Collection<Lumen> discovered) {
        String msgKey = "message.astralsorcery.research.discover_lumen.one";
        if (discovered.size() > 1) {
            msgKey = "message.astralsorcery.research.discover_lumen.more";
        }
        MutableComponent discoveredCt = Component.empty();
        int i = 0;
        for (Lumen lumen : discovered) {
            if (i > 0) {
                discoveredCt.append(", ");
            }

            MutableComponent lumenCmp = lumen.getName().copy().withColor(lumen.getColor(sPlayer.serverLevel().getGameTime()).getColor());
            discoveredCt.append(lumenCmp);
            i++;
        }
        sPlayer.sendSystemMessage(Component.translatable(msgKey, discoveredCt).withStyle(ChatFormatting.GRAY));
    }
}
