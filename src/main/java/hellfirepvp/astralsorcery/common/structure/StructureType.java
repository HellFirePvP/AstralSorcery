/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.HashSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureType
 * Created by HellFirePvP
 * Date: 30.05.2019 / 15:07
 */
public class StructureType {

    private static Collection<StructureType> TYPES = new HashSet<>();

    private final ResourceLocation name;
    private final boolean averageDistanceRequired;

    public StructureType(ResourceLocation name, boolean averageDistanceRequired) {
        this.name = name;
        this.averageDistanceRequired = averageDistanceRequired;
        TYPES.add(this);
    }

    public ResourceLocation getName() {
        return name;
    }

    public boolean isAverageDistanceRequired() {
        return averageDistanceRequired;
    }

    public static Collection<StructureType> getAllTypes() {
        return TYPES;
    }
}
