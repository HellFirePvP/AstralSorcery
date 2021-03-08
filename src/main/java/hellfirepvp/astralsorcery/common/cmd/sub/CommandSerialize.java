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
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockStateHelper;
import hellfirepvp.astralsorcery.common.util.data.JsonHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommandSerialize
 * Created by HellFirePvP
 * Date: 07.03.2021 / 15:38
 */
public class CommandSerialize {

    private CommandSerialize() {}

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("serialize")
                .requires(cs -> cs.hasPermissionLevel(2))
                .then(Commands.literal("hand")
                        .executes(CommandSerialize::serializeHand))
                .then(Commands.literal("look")
                        .executes(CommandSerialize::serializeLook));
    }

    private static int serializeHand(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().asPlayer();
        ItemStack held = player.getHeldItemMainhand();
        String serialized = JsonHelper.serializeItemStack(held).toString();

        IFormattableTextComponent msg = new StringTextComponent(serialized);
        Style s = Style.EMPTY.setFormatting(TextFormatting.GREEN)
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Copy")))
                .setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, serialized));
        msg.setStyle(s);

        context.getSource().sendFeedback(msg, true);
        return Command.SINGLE_SUCCESS;
    }

    private static int serializeLook(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().asPlayer();
        BlockRayTraceResult result = MiscUtils.rayTraceLookBlock(player);
        BlockState state = result == null ? Blocks.AIR.getDefaultState() : player.getEntityWorld().getBlockState(result.getPos());
        String serialized = BlockStateHelper.serialize(state);

        IFormattableTextComponent msg = new StringTextComponent(serialized);
        Style s = Style.EMPTY.setFormatting(TextFormatting.GREEN)
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Copy")))
                .setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, serialized));
        msg.setStyle(s);

        context.getSource().sendFeedback(msg, true);
        return Command.SINGLE_SUCCESS;
    }
}
