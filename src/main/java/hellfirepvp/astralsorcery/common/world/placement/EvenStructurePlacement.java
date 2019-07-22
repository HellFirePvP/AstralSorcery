/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import net.minecraft.world.gen.placement.IPlacementConfig;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EvenStructurePlacement
 * Created by HellFirePvP
 * Date: 22.07.2019 / 09:03
 */
public class EvenStructurePlacement implements IPlacementConfig {

    private final StructureType type;

    public EvenStructurePlacement(StructureType type) {
        this.type = type;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
    }

}
