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
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.focal.FocalPointManager;
import hellfirepvp.astralsorcery.common.focal.node.BasicFocalPointNode;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.data.ColumnPos;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CommandFocalPoint
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CommandFocalPoint {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("focal_point")
                .then(Commands.literal("create")
                        .then(Commands.argument("constellation", ResourceKeyArgument.key(RegistriesAS.KEY_CONSTELLATIONS))
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(CommandFocalPoint::createFocalPoint))));
    }

    private static int createFocalPoint(CommandContext<CommandSourceStack> ctx) {
        ResourceKey<BaseConstellation> cstKey = ctx.getArgument("constellation", ResourceKey.class);
        BaseConstellation cst = RegistriesAS.REGISTRY_CONSTELLATIONS.get(cstKey);
        if (cst == null) {
            ctx.getSource().sendFailure(Component.literal("Unknown constellation" + cstKey.location()).withStyle(ChatFormatting.RED));
            return 0;
        }
        BlockPos pos = BlockPosArgument.getBlockPos(ctx, "pos");
        ServerLevel lvl = ctx.getSource().getLevel();

        if (!DataAS.DOMAIN_AS.getData(lvl, DataAS.KEY_FOCAL_POINT_DATA).getNodesNear(pos, 16).isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("Too close to another focal point").withStyle(ChatFormatting.RED));
            return 0;
        }

        BasicFocalPointNode newNode = new BasicFocalPointNode(ColumnPos.of(pos), cst);
        FocalPointManager.getInstance().addNewNode(lvl, newNode, true);
        ctx.getSource().sendSuccess(() -> Component.literal("Success").withStyle(ChatFormatting.GREEN), true);
        return Command.SINGLE_SUCCESS;
    }
}
