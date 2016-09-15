package hellfirepvp.astralsorcery.client.effect.light;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.data.DataLightConnections;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
            if(rView != null) {
                int dimId = rView.getEntityWorld().provider.getDimension();
                DataLightConnections connections = SyncDataHolder.getDataClient(SyncDataHolder.DATA_LIGHT_CONNECTIONS);
                Map<BlockPos, List<BlockPos>> positions = connections.getClientConnections(dimId);
                if(positions != null) {
                    for (Map.Entry<BlockPos, List<BlockPos>> entry : positions.entrySet()) {
                        BlockPos at = entry.getKey();
                        if(rView.getDistanceSq(at) <= Config.maxEffectRenderDistanceSq) {
                            Vector3 source = new Vector3(at).add(0.5, 0.5, 0.5);
                            for (BlockPos dst : entry.getValue()) {
                                Vector3 to = new Vector3(dst).add(0.5, 0.5, 0.5);
                                EffectHandler.getInstance().lightbeam(to, source, 0.6);
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
