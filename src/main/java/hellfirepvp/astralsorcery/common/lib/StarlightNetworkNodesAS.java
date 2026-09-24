/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.tile.network.provider.FocusCrystalSourceNodeProvider;
import hellfirepvp.astralsorcery.common.tile.network.provider.ForwardingStarlightReceiverNodeProvider;
import hellfirepvp.astralsorcery.common.tile.network.provider.SimpleSingleTransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.tile.network.provider.SimpleTransmissionNodeProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StarlightNetworkNodesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StarlightNetworkNodesAS {

    public static final DeferredRegister<TransmissionNodeProvider<?>> NODE_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_TRANSMISSION_NODES, AstralSorcery.MODID);

    public static final DeferredHolder<TransmissionNodeProvider<?>, SimpleTransmissionNodeProvider> SIMPLE_NODE =
            NODE_REGISTER.register("simple", SimpleTransmissionNodeProvider::new);
    public static final DeferredHolder<TransmissionNodeProvider<?>, SimpleSingleTransmissionNodeProvider> SIMPLE_SINGLE_NODE =
            NODE_REGISTER.register("simple_single", SimpleSingleTransmissionNodeProvider::new);
    public static final DeferredHolder<TransmissionNodeProvider<?>, ForwardingStarlightReceiverNodeProvider> FORWARDING_RECEIVER_NODE =
            NODE_REGISTER.register("forwarding_receiver", ForwardingStarlightReceiverNodeProvider::new);

    public static final DeferredHolder<TransmissionNodeProvider<?>, FocusCrystalSourceNodeProvider> FOCUS_CRYSTAL_SOURCE_NODE =
            NODE_REGISTER.register("source_focus_crystal", FocusCrystalSourceNodeProvider::new);
}
