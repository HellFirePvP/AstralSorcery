/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.network.play.PktSyncAuxiliaryLightManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.lighting.LightEngine;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SyncedAuxiliaryLightManager
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SyncedAuxiliaryLightManager implements AuxiliaryLightManager {

    private final ServerLevel sLevel;

    private SyncedAuxiliaryLightManager(ServerLevel sLevel) {
        this.sLevel = sLevel;
    }

    public static SyncedAuxiliaryLightManager get(ServerLevel level) {
        return new SyncedAuxiliaryLightManager(level);
    }

    @Override
    public void setLightAt(BlockPos pos, int value) {
        AuxiliaryLightManager lightMgr = this.sLevel.getAuxLightManager(pos);
        if (lightMgr == null) return;
        value = Mth.clamp(value, 0, LightEngine.MAX_LEVEL);

        int oldValue = lightMgr.getLightAt(pos);
        lightMgr.setLightAt(pos, value);
        if (oldValue != value) {
            PacketDistributor.sendToPlayersTrackingChunk(this.sLevel, new ChunkPos(pos),
                    PktSyncAuxiliaryLightManager.updateLight(pos, value));
        }
    }

    @Override
    public int getLightAt(BlockPos pos) {
        AuxiliaryLightManager lightMgr = this.sLevel.getAuxLightManager(pos);
        if (lightMgr == null) return 0;
        return lightMgr.getLightAt(pos);
    }
}
