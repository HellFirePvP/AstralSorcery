/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.api.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionSourceNode;
import hellfirepvp.astralsorcery.common.util.MiscUtil;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TransmissionSourceNodeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface TransmissionSourceNodeProvider<T extends TransmissionSourceNode> extends TransmissionNodeProvider<T> {

    Codec<TransmissionSourceNodeProvider<?>> SOURCE_NODE_CODEC = MiscUtil.cast(RegistriesAS.REGISTRY_TRANSMISSION_NODES.byNameCodec());

}
