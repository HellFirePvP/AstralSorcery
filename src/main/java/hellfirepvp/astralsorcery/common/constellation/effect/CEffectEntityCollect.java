package hellfirepvp.astralsorcery.common.constellation.effect;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import java.util.List;

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

    private static AxisAlignedBB baseBoundingBox = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    public CEffectEntityCollect(IMajorConstellation constellation, String cfgName, double defaultRange, Class<T> entityClass, Predicate<T> filter) {
        super(constellation, cfgName);
        this.classToSearch = entityClass;
        this.searchFilter = filter;
        this.range = defaultRange;
    }

    public List<T> collectEntities(World world, BlockPos pos) {
        if(!enabled) return Lists.newArrayList();
        return world.getEntitiesWithinAABB(classToSearch, baseBoundingBox.offset(pos), searchFilter);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        range = cfg.getFloat(getKey() + "Range", getConfigurationSection(), (float) range, 2, 64, "Defines the range in which the ritual will try to find entities");
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");

        baseBoundingBox = new AxisAlignedBB(0, 0, 0, 1, 1, 1).expand(range, range, range);
    }

}
