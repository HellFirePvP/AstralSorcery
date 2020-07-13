/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.base;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationEffectEntityCollect
 * Created by HellFirePvP
 * Date: 11.06.2019 / 19:52
 */
public abstract class ConstellationEffectEntityCollect<T extends Entity> extends ConstellationEffect {

    private final Class<T> entityClazz;
    private final Predicate<T> filter;

    protected ConstellationEffectEntityCollect(@Nonnull ILocatable origin, @Nonnull IWeakConstellation cst, Class<T> entityClazz, Predicate<T> filter) {
        super(origin, cst);
        this.filter = filter;
        this.entityClazz = entityClazz;
    }

    @Nonnull
    protected List<T> collectEntities(World world, BlockPos center, ConstellationEffectProperties properties) {
        return world.getEntitiesWithinAABB(this.entityClazz, BOX.grow(properties.getSize()).offset(center), this.filter);
    }

}
