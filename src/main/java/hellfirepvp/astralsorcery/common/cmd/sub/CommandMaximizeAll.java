/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.cmd.sub;

import com.mojang.brigadier.Command;
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
 * Class: CommandMaximizeAll
 * Created by HellFirePvP
 * Date: 21.07.2019 / 16:33
 */
public class CommandMaximizeAll implements Command<CommandSource> {

    private static final CommandMaximizeAll CMD = new CommandMaximizeAll();

    private CommandMaximizeAll() {}

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("maximize")
                .requires(cs -> cs.hasPermissionLevel(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> {
                            PlayerEntity target = (PlayerEntity) ctx.getArgument("player", EntitySelector.class).selectOne(ctx.getSource());
                            ctx.getSource().sendFeedback(new StringTextComponent(TextFormatting.GREEN + "Success!"), true);
                            maximizeAll(target);
                            return 0;
                        }))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        maximizeAll(context.getSource().asPlayer());
        context.getSource().sendFeedback(new StringTextComponent(TextFormatting.GREEN + "Success!"), true);
        return 0;
    }

    private static boolean maximizeAll(PlayerEntity entity) {
        return ResearchManager.forceMaximizeAll(entity);
    }
}
