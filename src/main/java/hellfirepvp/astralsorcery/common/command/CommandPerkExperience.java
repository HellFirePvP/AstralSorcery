/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
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
 * Class: CommandPerkExperience
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CommandPerkExperience {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("exp")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("exp", LongArgumentType.longArg(0))
                                .executes(CommandPerkExperience::setPerkExperience)));
    }

    private static int setPerkExperience(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = (ServerPlayer) ctx.getArgument("player", EntitySelector.class).findSingleEntity(ctx.getSource());
        long exp = LongArgumentType.getLong(ctx, "exp");

        if (!ResearchHelper.setPerkExp(player, exp)) {
            ctx.getSource().sendFailure(Component.literal("Failed to set player perk experience").withStyle(ChatFormatting.RED));
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Player perk experience set to " + exp).withStyle(ChatFormatting.GREEN), true);
        return Command.SINGLE_SUCCESS;
    }

}
