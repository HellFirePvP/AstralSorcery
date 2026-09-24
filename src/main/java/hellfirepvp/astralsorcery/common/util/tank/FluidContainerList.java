/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.tank;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FluidContainerList
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FluidContainerList {

    public static final Codec<FluidContainerList> CODEC = Codec.unboundedMap(CodecUtil.stringInteger(), FluidContainer.CODEC)
            .xmap(FluidContainerList::new, view -> view.tankMap);

    private final Map<Integer, FluidContainer> tankMap = new HashMap<>();

    private FluidContainerList(Map<Integer, FluidContainer> tankMap) {
        this.tankMap.putAll(tankMap);
    }

    public static FluidContainerList create() {
        return new FluidContainerList(new HashMap<>());
    }

    public FluidContainer getTank(int tank) {
        return this.tankMap.computeIfAbsent(tank, id -> new FluidContainer(FluidStack.EMPTY));
    }
}
