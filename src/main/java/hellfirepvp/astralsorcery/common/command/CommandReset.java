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
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CommandReset
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CommandReset {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("reset")
                .requires(cs -> cs.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(CommandReset::reset));
    }

    private static int reset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = (ServerPlayer) context.getArgument("player", EntitySelector.class).findSingleEntity(context.getSource());
        ResearchHelper.wipeProgress(player);

        String name = player.getGameProfile().getName();
        context.getSource().sendSuccess(() -> Component.literal(String.format("Wiped progress for '%s'.", name)), true);
        return Command.SINGLE_SUCCESS;
    }

}
