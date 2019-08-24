/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.lens;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LensColorType
 * Created by HellFirePvP
 * Date: 24.08.2019 / 21:23
 */
public abstract class LensColorType {

    //No. not making a registry for this.
    private static final Map<ResourceLocation, LensColorType> BY_NAME = new HashMap<>();

    private final ResourceLocation name;
    private final TargetType type;
    private final Color color;
    private final float flowReduction;

    public LensColorType(ResourceLocation name, TargetType type, Color color, float flowReduction) {
        this.name = name;
        this.type = type;
        this.color = color;
        this.flowReduction = flowReduction;
        BY_NAME.put(name, this);
    }

    public ResourceLocation getName() {
        return name;
    }

    public TargetType getType() {
        return type;
    }

    public float getFlowReduction() {
        return flowReduction;
    }

    public Color getColor() {
        return color;
    }

    @Nullable
    public static LensColorType byName(ResourceLocation name) {
        return BY_NAME.get(name);
    }

    public abstract void entityInBeam(Vector3 origin, Vector3 target, Entity entity, float beamStrength);

    public abstract void blockInBeam(IWorld world, BlockPos pos, BlockState state, float beamStrength);

    public static enum TargetType {

        ANY,
        ENTITY,
        BLOCK,
        NONE

    }

}
