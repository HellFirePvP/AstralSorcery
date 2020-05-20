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
import hellfirepvp.astralsorcery.common.cmd.argument.ArgumentTypeConstellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
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
 * Class: CommandAttune
 * Created by HellFirePvP
 * Date: 21.07.2019 / 20:19
 */
public class CommandAttune implements Command<CommandSource> {

    private static final CommandAttune CMD = new CommandAttune();

    private CommandAttune() {}

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("attune")
                .requires(cs -> cs.hasPermissionLevel(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("constellation", ArgumentTypeConstellation.major())
                                .executes(CMD)));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = (PlayerEntity) context.getArgument("player", EntitySelector.class).selectOne(context.getSource());
        IMajorConstellation cst = (IMajorConstellation) context.getArgument("constellation", IConstellation.class);

        if (ResearchManager.setAttunedConstellation(player, cst)) {
            context.getSource().sendFeedback(
                    new StringTextComponent(TextFormatting.GREEN + "Success! Player has been attuned to " + cst.getConstellationName().getFormattedText()), true);
        } else {
            context.getSource().sendFeedback(
                    new StringTextComponent(TextFormatting.RED + "Failed! Player specified doesn't seem to have the research progress necessary!"), true);
        }
        return 0;
    }
}
