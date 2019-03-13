/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ParticleEffectWatcher
 * Created by HellFirePvP
 * Date: 16.11.2018 / 17:56
 */
public class ParticleEffectWatcher implements ITickHandler {

    public static final ParticleEffectWatcher INSTANCE = new ParticleEffectWatcher();

    private Map<Integer, List<BlockPos>> worldWatch = Maps.newHashMap();

    private ParticleEffectWatcher() {}

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        worldWatch.getOrDefault(((World) context[0]).provider.getDimension(), Lists.newArrayList()).clear();
    }

    public boolean mayFire(World world, BlockPos pos) {
        int dimId = world.provider.getDimension();
        worldWatch.putIfAbsent(dimId, Lists.newArrayList());
        List<BlockPos> worldPos = worldWatch.get(dimId);
        return !worldPos.contains(pos) && worldPos.add(pos);
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.WORLD);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "ParticleEffect Limiter";
    }

}
