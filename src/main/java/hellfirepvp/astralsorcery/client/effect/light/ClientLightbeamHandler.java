/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.light;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.data.DataLightConnections;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalLens;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientLightbeamHandler
 * Created by HellFirePvP
 * Date: 06.08.2016 / 17:17
 */
public class ClientLightbeamHandler implements ITickHandler {

    private static final Random rand = new Random();
    private int ticksExisted = 0;

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        ticksExisted++;
        if(ticksExisted % 40 == 0) {
            ticksExisted = 0;
            Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
            if(rView == null) rView = Minecraft.getMinecraft().player;
            if(rView != null) {
                int dimId = rView.getEntityWorld().provider.getDimension();
                DataLightConnections connections = SyncDataHolder.getDataClient(SyncDataHolder.DATA_LIGHT_CONNECTIONS);
                if(connections.clientReceivingData) return;

                Map<BlockPos, List<BlockPos>> positions = connections.getClientConnections(dimId);
                if(positions != null) {
                    for (Map.Entry<BlockPos, List<BlockPos>> entry : positions.entrySet()) {
                        if (entry == null) continue;
                        BlockPos at = entry.getKey();
                        if (rView.getDistanceSq(at) <= Config.maxEffectRenderDistanceSq) {
                            Vector3 source = new Vector3(at).add(0.5, 0.5, 0.5);
                            Color overlay = null;
                            TileCrystalLens lens = MiscUtils.getTileAt(rView.getEntityWorld(), at, TileCrystalLens.class, true);
                            if (lens != null) {
                                if (lens.getLensColor() != null) {
                                    overlay = lens.getLensColor().wrappedColor;
                                }
                            }
                            for (BlockPos dst : entry.getValue()) {
                                Vector3 to = new Vector3(dst).add(0.5, 0.5, 0.5);
                                EffectLightbeam beam = EffectHandler.getInstance().lightbeam(to, source, 0.6);
                                if (overlay != null) {
                                    beam.setColorOverlay(overlay.getRed() / 255F, overlay.getGreen() / 255F, overlay.getBlue() / 255F, 1F);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.CLIENT);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Client Lightbeam Display";
    }
}
