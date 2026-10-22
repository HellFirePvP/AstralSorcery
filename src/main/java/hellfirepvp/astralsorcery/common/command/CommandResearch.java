/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.research.ResearchFlag;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.server.command.CommandUtils;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CommandResearch
 * Created by HellFirePvP
 * Date: 22.10.2026 / 18:27
 */
public class CommandResearch {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_TIERS = (ctx, builder) ->
            SharedSuggestionProvider.suggest(Arrays.stream(ResearchTier.values()).map(ResearchTier::getSerializedName), builder);
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_FLAGS = (ctx, builder) ->
            SharedSuggestionProvider.suggest(Arrays.stream(ResearchFlag.values()).map(ResearchFlag::getSerializedName), builder);

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("research")
                .then(Commands.literal("memorize_constellation")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("constellation", ResourceKeyArgument.key(RegistriesAS.KEY_CONSTELLATIONS))
                                        .executes(CommandResearch::memorizeConstellation))))
                .then(Commands.literal("discover_constellation")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("constellation", ResourceKeyArgument.key(RegistriesAS.KEY_CONSTELLATIONS))
                                        .executes(CommandResearch::discoverConstellation))))
                .then(Commands.literal("discover_focal_point")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("constellation", ResourceKeyArgument.key(RegistriesAS.KEY_CONSTELLATIONS))
                                        .executes(CommandResearch::discoverFocalPoint))))
                .then(Commands.literal("set_tier")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("tier", StringArgumentType.word())
                                        .suggests(SUGGEST_TIERS)
                                        .executes(CommandResearch::setResearchTier))))
                .then(Commands.literal("add_flag")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("flag", StringArgumentType.word())
                                        .suggests(SUGGEST_FLAGS)
                                        .executes(CommandResearch::addResearchFlag))));
    }

    private static int memorizeConstellation(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        BaseConstellation cst = getConstellation(ctx);
        if (cst == null) {
            return 0;
        }

        if (!ResearchHelper.memorizeConstellation(player, cst)) {
            Component cmp = Component.empty()
                    .append(player.getDisplayName())
                    .append(" has already memorized constellation ")
                    .append(cst.getColoredName())
                    .withStyle(ChatFormatting.RED);
            ctx.getSource().sendFailure(cmp);
            return 0;
        }
        Component cmp = Component.empty()
                .append(player.getDisplayName())
                .append(" memorized constellation ")
                .append(cst.getColoredName())
                .withStyle(ChatFormatting.GREEN);
        ctx.getSource().sendSuccess(() -> cmp, true);
        return Command.SINGLE_SUCCESS;
    }

    private static int discoverConstellation(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        BaseConstellation cst = getConstellation(ctx);
        if (cst == null) {
            return 0;
        }

        if (!ResearchHelper.discoverConstellation(player, cst)) {
            Component cmp = Component.empty()
                    .append(player.getDisplayName())
                    .append(" has already discovered constellation ")
                    .append(cst.getColoredName())
                    .withStyle(ChatFormatting.RED);
            ctx.getSource().sendFailure(cmp);
            return 0;
        }
        Component cmp = Component.empty()
                .append(player.getDisplayName())
                .append(" discovered constellation ")
                .append(cst.getColoredName())
                .withStyle(ChatFormatting.GREEN);
        ctx.getSource().sendSuccess(() -> cmp, true);
        return Command.SINGLE_SUCCESS;
    }

    private static int discoverFocalPoint(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        BaseConstellation cst = getConstellation(ctx);
        if (cst == null) {
            return 0;
        }

        if (!cst.is(TagsAS.Constellations.MAY_BE_FOCAL_POINT)) {
            Component cmp = Component.empty()
                    .append("Constellation ")
                    .append(cst.getColoredName())
                    .append(" cannot be a focal point")
                    .withStyle(ChatFormatting.RED);
            ctx.getSource().sendFailure(cmp);
            return 0;
        }
        if (!ResearchHelper.memorizeFocalPoint(player, cst)) {
            Component cmp = Component.empty()
                    .append(player.getDisplayName())
                    .append(" has already discovered the focal point of ")
                    .append(cst.getColoredName())
                    .withStyle(ChatFormatting.RED);
            ctx.getSource().sendFailure(cmp);
            return 0;
        }
        Component cmp = Component.empty()
                .append(player.getDisplayName())
                .append(" discovered the focal point of ")
                .append(cst.getColoredName())
                .withStyle(ChatFormatting.GREEN);
        ctx.getSource().sendSuccess(() -> cmp, true);
        return Command.SINGLE_SUCCESS;
    }

    private static int setResearchTier(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        String tierName = StringArgumentType.getString(ctx, "tier");
        ResearchTier tier = getByName(ResearchTier.values(), tierName);
        if (tier == null) {
            ctx.getSource().sendFailure(Component.literal("Unknown research tier " + tierName).withStyle(ChatFormatting.RED));
            return 0;
        }

        if (!ResearchHelper.setResearchProgress(player, tier)) {
            ctx.getSource().sendFailure(Component.literal("Failed to set player research tier").withStyle(ChatFormatting.RED));
            return 0;
        }
        Component cmp = Component.empty()
                .append("Set research tier of ").append(player.getDisplayName()).append(" to ").append(tier.getName())
                .withStyle(ChatFormatting.GREEN);
        ctx.getSource().sendSuccess(() -> cmp, true);
        return Command.SINGLE_SUCCESS;
    }

    private static int addResearchFlag(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        String flagName = StringArgumentType.getString(ctx, "flag");
        ResearchFlag flag = getByName(ResearchFlag.values(), flagName);
        if (flag == null) {
            ctx.getSource().sendFailure(Component.literal("Unknown research flag " + flagName).withStyle(ChatFormatting.RED));
            return 0;
        }

        if (!ResearchHelper.setKnowledgeFlag(player, flag)) {
            Component cmp = Component.empty()
                    .append(player.getDisplayName())
                    .append(" already has research flag " + flag.getSerializedName())
                    .withStyle(ChatFormatting.RED);
            ctx.getSource().sendFailure(cmp);
            return 0;
        }
        Component cmp = Component.empty()
                .append("Added research flag " + flag.getSerializedName() + " to ")
                .append(player.getDisplayName())
                .withStyle(ChatFormatting.GREEN);
        ctx.getSource().sendSuccess(() -> cmp, true);
        return Command.SINGLE_SUCCESS;
    }

    @Nullable
    private static BaseConstellation getConstellation(CommandContext<CommandSourceStack> ctx) {
        ResourceKey<BaseConstellation> cstKey = CommandUtils.getResourceKey(ctx, "constellation", RegistriesAS.KEY_CONSTELLATIONS).orElseThrow();
        BaseConstellation cst = RegistriesAS.REGISTRY_CONSTELLATIONS.get(cstKey);
        if (cst == null) {
            ctx.getSource().sendFailure(Component.literal("Unknown constellation " + cstKey.location())
                    .withStyle(ChatFormatting.RED));
        }
        return cst;
    }

    @Nullable
    private static <T extends StringRepresentable> T getByName(T[] values, String name) {
        return Arrays.stream(values)
                .filter(value -> value.getSerializedName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
