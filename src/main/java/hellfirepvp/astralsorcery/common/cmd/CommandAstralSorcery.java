/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.cmd.sub.CommandHelp;
import net.minecraft.command.CommandSource;

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
                LiteralArgumentBuilder.<CommandSource>literal(AstralSorcery.MODID)
                        .then(CommandHelp.register())
        );

        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("as")
                .requires(cs -> cs.hasPermissionLevel(2))
                .redirect(cmdAstralSorcery));
    }

}
