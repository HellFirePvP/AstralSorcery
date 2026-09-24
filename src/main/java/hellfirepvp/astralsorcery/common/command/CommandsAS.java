/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CommandsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CommandsAS {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> cmd = dispatcher.register(
                Commands.literal("as")
                        .requires(src -> src.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(CommandReset.register())
                        .then(CommandDebug.register())
                        .then(CommandMaximize.register())
                        .then(CommandFocalPoint.register())
                        .then(CommandAttune.register())
                        .then(CommandPerkExperience.register())
        );

        dispatcher.register(Commands.literal(AstralSorcery.MODID).redirect(cmd));
    }

}
