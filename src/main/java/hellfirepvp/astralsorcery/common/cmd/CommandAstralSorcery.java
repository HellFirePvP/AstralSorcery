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
import hellfirepvp.astralsorcery.common.cmd.sub.*;
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
                Commands.literal("as")
                        .then(CommandAttune.register())
                        .then(CommandConstellation.register())
                        .then(CommandExp.register())
                        .then(CommandMaximizeAll.register())
                        .then(CommandReset.register())
                        .then(CommandProgress.register())
                        .then(CommandSerialize.register())
        );

        dispatcher.register(Commands.literal(AstralSorcery.MODID)
                .redirect(cmdAstralSorcery));
    }

}
