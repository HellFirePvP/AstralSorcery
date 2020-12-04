/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry.sets;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataSet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAccelerationBlacklistEntry
 * Created by HellFirePvP
 * Date: 24.01.2020 / 20:13
 */
public class TileAccelerationBlacklistEntry implements ConfigDataSet, Predicate<TileEntity> {

    private final String filterString;
    private Class<?> filteredSuperClass;

    public TileAccelerationBlacklistEntry(String filterString) {
        this.filterString = filterString;
        try {
            this.filteredSuperClass = Class.forName(filterString);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            this.filteredSuperClass = null; //Then we match by string..
        }
    }

    @Override
    public boolean test(TileEntity tile) {
        String testStr = this.filterString.toLowerCase(Locale.ROOT);
        if (testStr.isEmpty()) {
            return false;
        }

        if (this.filteredSuperClass != null) {
            return this.filteredSuperClass.isAssignableFrom(tile.getClass());
        }

        ResourceLocation key = tile.getType().getRegistryName();
        if (key != null && key.toString().toLowerCase(Locale.ROOT).startsWith(testStr)) {
            return true;
        }

        String className = tile.getClass().getName().toLowerCase(Locale.ROOT);
        return className.startsWith(testStr);
    }

    @Nonnull
    @Override
    public String serialize() {
        return this.filterString;
    }
}
