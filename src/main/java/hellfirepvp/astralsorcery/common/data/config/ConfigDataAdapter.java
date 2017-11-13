/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConfigDataAdapter
 * Created by HellFirePvP
 * Date: 05.11.2017 / 09:44
 */
public interface ConfigDataAdapter<T extends ConfigDataAdapter.DataSet> {

    public Iterable<T> getDefaultDataSets();

    public String getDataFileName();

    public String getDescription();

    @Nullable
    public T appendDataSet(String str);

    @Nonnull
    default public String[] serializeDataSet() {
        List<String> defaultValueStrings = new LinkedList<>();
        for (T data : getDefaultDataSets()) {
            defaultValueStrings.add(data.serialize());
        }
        String[] out = new String[defaultValueStrings.size()];
        return defaultValueStrings.toArray(out);
    }

    public static interface DataSet {

        @Nonnull
        public String serialize();

    }

}
