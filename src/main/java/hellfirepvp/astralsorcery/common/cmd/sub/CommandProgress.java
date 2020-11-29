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
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.server.command.EnumArgument;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommandProgress
 * Created by HellFirePvP
 * Date: 22.11.2020 / 13:23
 */
public class CommandProgress {

    private CommandProgress() {}

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("progress")
                .requires(cs -> cs.hasPermissionLevel(2))
                .then(Commands.argument("player", EntityArgument.player())
                        /*.then(Commands.literal("next")
                                .executes(ctx -> {
                                    PlayerEntity src = ctx.getSource().asPlayer();
                                    PlayerEntity target = EntityArgument.getPlayer(ctx, "player");
                                    PlayerProgress prog = ResearchHelper.getProgress(target, LogicalSide.SERVER);
                                    ProgressionTier next = prog.getTierReached().next();
                                    return pushPlayerToProgress(src, target, next);
                                }))*/
                        .then(Commands.argument("progress", EnumArgument.enumArgument(ProgressionTier.class))
                                .executes(ctx -> {
                                    PlayerEntity src = ctx.getSource().asPlayer();
                                    PlayerEntity target = EntityArgument.getPlayer(ctx, "player");
                                    ProgressionTier goal = ctx.getArgument("progress", ProgressionTier.class);
                                    return pushPlayerToProgress(src, target, goal);
                                })));
    }

    private static int pushPlayerToProgress(ICommandSource src, PlayerEntity target, ProgressionTier goal) {
        ITextComponent targetName = target.getDisplayName();
        PlayerProgress progress = ResearchHelper.getProgress(target, LogicalSide.SERVER);
        if (!progress.isValid() || progress.getTierReached().isThisLaterOrEqual(goal)) {
            src.sendMessage(new StringTextComponent("Failed! ").append(targetName).appendString("'s progress is higher or equal to ").appendString(goal.name())
                    .mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
            return 0;
        }
        if (ResearchManager.grantProgress(target, goal)) {
            src.sendMessage(new StringTextComponent("Success! ").mergeStyle(TextFormatting.GREEN), Util.DUMMY_UUID);
            return Command.SINGLE_SUCCESS;
        } else {
            src.sendMessage(new StringTextComponent("Failed!").mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
            return 0;
        }
    }
}
