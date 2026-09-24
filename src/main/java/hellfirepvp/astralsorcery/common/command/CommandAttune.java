/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CommandAttune
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CommandAttune {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("attune")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("constellation", ResourceKeyArgument.key(RegistriesAS.KEY_CONSTELLATIONS))
                                .executes(CommandAttune::attunePlayer)));
    }

    private static int attunePlayer(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = (ServerPlayer) ctx.getArgument("player", EntitySelector.class).findSingleEntity(ctx.getSource());

        ResourceKey<BaseConstellation> cstKey = ctx.getArgument("constellation", ResourceKey.class);
        BaseConstellation cst = RegistriesAS.REGISTRY_CONSTELLATIONS.get(cstKey);
        if (cst == null) {
            ctx.getSource().sendFailure(Component.literal("Unknown constellation" + cstKey.location()).withStyle(ChatFormatting.RED));
            return 0;
        }
        PlayerProgress progress = ResearchManager.getProgress(player, LogicalSide.SERVER);
        if (!progress.hasDiscoveredConstellation(cst)) {
            ResearchHelper.discoverConstellation(player, cst);
        }
        if (!ResearchHelper.attuneConstellation(player, cst)) {
            Component cmp = Component.empty()
                    .append("Failed to attune player to constellation ")
                    .append(cst.getColoredName())
                    .withStyle(ChatFormatting.RED);
            ctx.getSource().sendFailure(cmp);
        }
        Component cmp = Component.empty()
                .append("Attuned player to constellation ")
                .append(cst.getColoredName())
                .withStyle(ChatFormatting.GREEN);
        ctx.getSource().sendSuccess(() -> cmp, true);
        return Command.SINGLE_SUCCESS;
    }
}
