/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectEntityCollect
 * Created by HellFirePvP
 * Date: 09.11.2016 / 18:42
 */
public abstract class CEffectEntityCollect<T extends Entity> extends ConstellationEffect {

    protected final Class<T> classToSearch;
    protected final Predicate<T> searchFilter;
    protected double range;
    public static boolean enabled = true;

    private static Map<Class, AxisAlignedBB> baseBoundingBox = new HashMap<>();

    public CEffectEntityCollect(IWeakConstellation constellation, String cfgName, double defaultRange, Class<T> entityClass, Predicate<T> filter) {
        super(constellation, cfgName);
        this.classToSearch = entityClass;
        this.searchFilter = filter;
        this.range = defaultRange;
    }

    public List<T> collectEntities(World world, BlockPos pos) {
        if(!enabled) return Lists.newArrayList();
        return world.getEntitiesWithinAABB(classToSearch, baseBoundingBox.get(getClass()).offset(pos), searchFilter);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        range = cfg.getFloat(getKey() + "Range", getConfigurationSection(), (float) range, 2, 64, "Defines the range in which the ritual will try to find entities");
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");

        baseBoundingBox.put(getClass(), new AxisAlignedBB(0, 0, 0, 1, 1, 1).grow(range));
    }

}
