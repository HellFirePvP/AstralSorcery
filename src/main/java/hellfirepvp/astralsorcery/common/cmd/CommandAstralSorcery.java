/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.cmd.sub.CommandAttune;
import hellfirepvp.astralsorcery.common.cmd.sub.CommandExp;
import hellfirepvp.astralsorcery.common.cmd.sub.CommandMaximizeAll;
import hellfirepvp.astralsorcery.common.cmd.sub.CommandReset;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommandAstralSorcery
 * Created by HellFirePvP
 * Date: 20.04.2019 / 20:49
 */
public class CommandAstralSorcery {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> cmdAstralSorcery = dispatcher.register(
                Commands.literal(AstralSorcery.MODID)
                        .then(CommandAttune.register())
                        .then(CommandExp.register())
                        .then(CommandMaximizeAll.register())
                        .then(CommandReset.register())
        );

        dispatcher.register(Commands.literal("as")
                .redirect(cmdAstralSorcery));
    }

}
