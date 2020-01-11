/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraftforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataSerializersAS
 * Created by HellFirePvP
 * Date: 06.07.2019 / 19:12
 */
public class DataSerializersAS {

    private DataSerializersAS() {}

    public static IDataSerializer<Long> LONG;
    public static IDataSerializer<Vector3> VECTOR;
    public static IDataSerializer<FluidStack> FLUID;

}
