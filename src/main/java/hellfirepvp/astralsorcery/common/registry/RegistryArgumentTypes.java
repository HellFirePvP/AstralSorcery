/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import com.mojang.brigadier.arguments.ArgumentType;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.cmd.argument.ArgumentTypeConstellation;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryArgumentTypes
 * Created by HellFirePvP
 * Date: 04.12.2020 / 16:21
 */
public class RegistryArgumentTypes {

    private RegistryArgumentTypes() {}

    public static void init() {
        register(AstralSorcery.key("constellation"), ArgumentTypeConstellation.class, new ArgumentSerializer<>(ArgumentTypeConstellation::any));
    }

    private static <T extends ArgumentType<?>> void register(ResourceLocation key, Class<T> argumentClazz, IArgumentSerializer<T> serializer) {
        ArgumentTypes.register(key.toString(), argumentClazz, serializer);
    }
}
