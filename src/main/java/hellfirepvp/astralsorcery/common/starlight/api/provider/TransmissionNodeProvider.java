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
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionNode;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TransmissionNodeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface TransmissionNodeProvider<T extends TransmissionNode> {

    Codec<TransmissionNodeProvider<?>> NODE_CODEC = RegistriesAS.REGISTRY_TRANSMISSION_NODES.byNameCodec();

    T provideNewNode(BlockPos pos);

    MapCodec<T> nodeCodec();

}
