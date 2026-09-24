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
 * Class: CommandMaximize
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CommandMaximize {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("maximize")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(CommandMaximize::maximizePlayer))
                .executes(CommandMaximize::maximizeSelf);
    }

    private static int maximizeSelf(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return maximize(context.getSource(), context.getSource().getPlayerOrException());
    }

    private static int maximizePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return maximize(context.getSource(), (ServerPlayer) context.getArgument("player", EntitySelector.class).findSingleEntity(context.getSource()));
    }

    private static int maximize(CommandSourceStack sender, ServerPlayer target) {
        ResearchHelper.maximizeAll(target);
        sender.sendSuccess(() -> Component.literal("Success").withStyle(ChatFormatting.GREEN), true);
        return Command.SINGLE_SUCCESS;
    }
}
