/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.cmd.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommandExp
 * Created by HellFirePvP
 * Date: 21.07.2019 / 20:19
 */
public class CommandExp implements Command<CommandSource> {

    private static final CommandExp CMD = new CommandExp();

    private CommandExp() {}

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("exp")
                .requires(cs -> cs.hasPermissionLevel(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("exp", LongArgumentType.longArg())
                                .executes(CMD)));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = (PlayerEntity) context.getArgument("player", EntitySelector.class).selectOne(context.getSource());
        long exp = LongArgumentType.getLong(context, "exp");

        if (ResearchManager.setExp(player, exp)) {
            context.getSource().sendFeedback(
                    new StringTextComponent(TextFormatting.GREEN + "Success! Player exp has been set to " + exp), true);
        } else {
            context.getSource().sendFeedback(
                    new StringTextComponent(TextFormatting.RED + "Failed! Player specified doesn't seem to have a research progress!"), true);
        }
        return 0;
    }
}
