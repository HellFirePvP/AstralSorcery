/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network;

import hellfirepvp.astralsorcery.common.network.play.*;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: NetworkRegistry
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class NetworkRegistry {

    public static final String NET_VERSION = "1";

    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(NET_VERSION);

        // Server -> Client
        PktSyncPlayerProgress.HANDLER.register(registrar);
        PktSyncResearchNodes.HANDLER.register(registrar);
        PktSyncData.HANDLER.register(registrar);
        PktUpdateLinkSession.HANDLER.register(registrar);
        PktPlayVisualEffect.HANDLER.register(registrar);
        PktSyncModifierSource.HANDLER.register(registrar);
        PktSyncPerkTree.HANDLER.register(registrar);
        PktSyncPerkLevels.HANDLER.register(registrar);
        PktSyncPerkActivity.HANDLER.register(registrar);
        PktSyncCustomDestroyProgress.HANDLER.register(registrar);
        PktSyncAuxiliaryLightManager.HANDLER.register(registrar);
        PktSyncLumenBindingTypes.HANDLER.register(registrar);
        PktPlayStructurePreview.HANDLER.register(registrar);
        PktOpenClientScreen.HANDLER.register(registrar);

        // Client -> Server
        PktAdjustAstrolabeAngle.HANDLER.register(registrar);
        PktDiscoverConstellation.HANDLER.register(registrar);
        PktRequestSocketPerkItem.HANDLER.register(registrar);
        PktRequestCancelLinkSession.HANDLER.register(registrar);
        PktRequestGatewayTeleport.HANDLER.register(registrar);
        PktRequestLearnedTomeNavigation.HANDLER.register(registrar);

        // Bi-Directional
        PktRequestSeed.HANDLER.register(registrar);
        PktRequestUnlockPerk.HANDLER.register(registrar);
        PktRequestPerkSealAction.HANDLER.register(registrar);
        PktRequestRemovePerk.HANDLER.register(registrar);
    }
}
