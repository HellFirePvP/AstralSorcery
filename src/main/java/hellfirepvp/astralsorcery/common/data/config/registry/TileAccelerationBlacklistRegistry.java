/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataAdapter;
import hellfirepvp.astralsorcery.common.data.config.registry.sets.TileAccelerationBlacklistEntry;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.tile.base.network.TileSourceBase;
import hellfirepvp.astralsorcery.common.tile.base.network.TileTransmissionBase;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAccelerationBlacklistRegistry
 * Created by HellFirePvP
 * Date: 24.01.2020 / 20:13
 */
public class TileAccelerationBlacklistRegistry extends ConfigDataAdapter<TileAccelerationBlacklistEntry> {

    public static final TileAccelerationBlacklistRegistry INSTANCE = new TileAccelerationBlacklistRegistry();

    private List<Class<?>> erroredTiles = Lists.newArrayList();

    private TileAccelerationBlacklistRegistry() {}

    public boolean canBeAccelerated(TileEntity tile) {
        if (!(tile instanceof ITickable)) {
            return false;
        }

        Class<?> tileClass = tile.getClass();
        if (erroredTiles.contains(tileClass)) {
            return false;
        }

        for (TileAccelerationBlacklistEntry entry : this.getConfiguredValues()) {
            if (entry.test(tile)) {
                return false;
            }
        }
        return true;
    }

    public void addErrored(TileEntity tile) {
        if (tile != null && !this.erroredTiles.contains(tile.getClass())) {
            this.erroredTiles.add(tile.getClass());
        }
    }

    @Override
    public List<TileAccelerationBlacklistEntry> getDefaultValues() {
        return Lists.newArrayList(
                //Accelerating storage system components looks like a bad idea
                new TileAccelerationBlacklistEntry("appeng"),
                new TileAccelerationBlacklistEntry("raoulvdberge.refinedstorage"),

                //Filter all transmission & source tiles
                new TileAccelerationBlacklistEntry(TileTransmissionBase.class.getName()),
                new TileAccelerationBlacklistEntry(TileSourceBase.class.getName()),

                //Bad news when accelerating in terms of performance and matching of processes... x)
                new TileAccelerationBlacklistEntry(TileAltar.class.getName()),
                new TileAccelerationBlacklistEntry(TileAttunementAltar.class.getName()),
                new TileAccelerationBlacklistEntry(TileRitualPedestal.class.getName())
        );
    }

    @Override
    public String getSectionName() {
        return "tile_acceleration_blacklist";
    }

    @Override
    public String getCommentDescription() {
        return "Accepts & matches against strings: " +
                "1) what a tileentity-type's registry name starts with, " +
                "2) what a tileentity's fully qualified class name starts with, " +
                "3) (special case) a fully qualified class name (Instances & sub-class instance of that class will be blacklisted)";
    }

    @Override
    public String getTranslationKey() {
        return translationKey("data");
    }

    @Override
    public Predicate<Object> getValidator() {
        return (obj) -> obj instanceof String;
    }

    @Nullable
    @Override
    public TileAccelerationBlacklistEntry deserialize(String string) throws IllegalArgumentException {
        if (string.isEmpty()) {
            throw new IllegalArgumentException("TileAccelerationBlacklist entry filter must not be empty!");
        }
        return new TileAccelerationBlacklistEntry(string);
    }
}
