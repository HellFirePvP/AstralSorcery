/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.data.ASDataSerializers;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraftforge.registries.DataSerializerEntry;

import static hellfirepvp.astralsorcery.common.lib.DataSerializersAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryDataSerializers
 * Created by HellFirePvP
 * Date: 06.07.2019 / 19:13
 */
public class RegistryDataSerializers {

    private RegistryDataSerializers() {}

    public static void registerSerializers() {
        LONG = register(ASDataSerializers.LONG, "long");
        VECTOR = register(ASDataSerializers.VECTOR, "vector");
        FLUID = register(ASDataSerializers.FLUID, "fluid");
    }

    private static <V, T extends IDataSerializer<V>> T register(T dataSerializer, String name) {
        DataSerializerEntry entry = new DataSerializerEntry(dataSerializer);
        entry.setRegistryName(AstralSorcery.key(name.toLowerCase()));
        AstralSorcery.getProxy().getRegistryPrimer().register(entry);
        return dataSerializer;
    }

}
