/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.types;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.focal.node.BasicFocalPointNode;
import hellfirepvp.astralsorcery.common.focal.node.FocalPointNode;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalNodeTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalNodeTypesAS {

    public static final DeferredRegister<FocalPointNode.Type<?>> FOCAL_NODE_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_FOCAL_NODE_TYPES, AstralSorcery.MODID);

    public static final DeferredHolder<FocalPointNode.Type<?>, FocalPointNode.Type<BasicFocalPointNode>> BASIC =
            FOCAL_NODE_REGISTER.register("basic", () -> new FocalPointNode.Type<>(BasicFocalPointNode.CODEC, BasicFocalPointNode.STREAM_CODEC));

}
