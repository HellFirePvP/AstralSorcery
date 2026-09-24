/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.helper;

import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StarlightTransmissionEffectHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StarlightTransmissionEffectHelper {

    private StarlightTransmissionEffectHelper() {}

    public static void attachEventListeners(IEventBus eventBus) {
        eventBus.addListener(StarlightTransmissionEffectHelper::onClientTick);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        if (ClientProxy.getClientTick() % 48 != 0) return;

        Entity view = Minecraft.getInstance().cameraEntity;
        if (view == null) view = Minecraft.getInstance().player;
        if (view == null) return;
        ResourceKey<Level> dimKey = view.level().dimension();

        SyncDataManager.getInstance()
                .getClientData(SyncDataTypesAS.LIGHT_CONNECTION)
                .getConnections(dimKey).forEach((from, tos) -> {
                    Vector3 vFrom = Vector3.atCenter(from);
                    tos.forEach(to -> {
                        Vector3 vTo = Vector3.atCenter(to);

                        EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM_TRANSFER)
                                .spawn(vFrom)
                                .setup(vTo, 0.4F, 0.4F)
                                .setAlpha(0.4F);
                    });
                });
    }

}
