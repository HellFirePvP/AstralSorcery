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
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import hellfirepvp.astralsorcery.common.cmd.argument.ArgumentTypeConstellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommandConstellation
 * Created by HellFirePvP
 * Date: 22.11.2020 / 11:32
 */
public class CommandConstellation {

    private CommandConstellation() {}

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("constellation")
                .requires(cs -> cs.hasPermissionLevel(2))
                .then(Commands.literal("memorize")
                        .then(Commands.argument("constellation", ArgumentTypeConstellation.any())
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> {
                                            PlayerEntity target = EntityArgument.getPlayer(ctx, "player");
                                            IConstellation cst = ctx.getArgument("constellation", IConstellation.class);
                                            return markConstellationMemorized(ctx.getSource(), target, cst);
                                        }))
                                .executes(ctx -> {
                                    IConstellation cst = ctx.getArgument("constellation", IConstellation.class);
                                    return markConstellationMemorized(ctx.getSource(), null, cst);
                                })))
                .then(Commands.literal("discover")
                        .then(Commands.argument("constellation", ArgumentTypeConstellation.any())
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> {
                                            PlayerEntity target = EntityArgument.getPlayer(ctx, "player");
                                            IConstellation cst = ctx.getArgument("constellation", IConstellation.class);
                                            return discoverConstellation(ctx.getSource(), target, cst);
                                        }))
                                .executes(ctx -> {
                                    IConstellation cst = ctx.getArgument("constellation", IConstellation.class);
                                    return discoverConstellation(ctx.getSource(), null, cst);
                                })));
    }

    private static int markConstellationMemorized(CommandSource src, @Nullable PlayerEntity target, IConstellation cst) throws CommandSyntaxException {
        PlayerEntity source = src.asPlayer();
        target = target != null ? target : source;
        ITextComponent targetName = target.getDisplayName();
        PlayerProgress progress = ResearchHelper.getProgress(target, LogicalSide.SERVER);
        if (!progress.isValid() || progress.hasSeenConstellation(cst)) {
            source.sendMessage(new StringTextComponent("Failed! ").append(targetName).appendString(" has already seen ").append(cst.getConstellationName())
                    .mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
            return 0;
        }
        if (ResearchManager.memorizeConstellation(cst, target)) {
            ResearchHelper.sendConstellationMemorizationMessage(target, progress, cst);
            source.sendMessage(new StringTextComponent("Success! ")
                    .mergeStyle(TextFormatting.GREEN), Util.DUMMY_UUID);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendMessage(new StringTextComponent("Failed!").mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
            return 0;
        }
    }

    private static int discoverConstellation(CommandSource src, @Nullable PlayerEntity target, IConstellation cst) throws CommandSyntaxException {
        PlayerEntity source = src.asPlayer();
        target = target != null ? target : source;
        ITextComponent targetName = target.getDisplayName();
        PlayerProgress progress = ResearchHelper.getProgress(target, LogicalSide.SERVER);
        if (!progress.isValid() || progress.hasConstellationDiscovered(cst)) {
            source.sendMessage(new StringTextComponent("Failed! ").append(targetName).appendString(" has already discovered ").append(cst.getConstellationName())
                    .mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
            return 0;
        }
        if (ResearchManager.discoverConstellation(cst, target)) {
            ResearchHelper.sendConstellationDiscoveryMessage(target, cst);
            source.sendMessage(new StringTextComponent("Success! ").mergeStyle(TextFormatting.GREEN), Util.DUMMY_UUID);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendMessage(new StringTextComponent("Failed!").mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
            return 0;
        }
    }
}
