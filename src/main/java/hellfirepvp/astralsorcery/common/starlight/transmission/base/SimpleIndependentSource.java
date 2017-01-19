/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission.base;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleIndependentSource
 * Created by HellFirePvP
 * Date: 05.08.2016 / 00:27
 */
public abstract class SimpleIndependentSource implements IIndependentStarlightSource {

    private IWeakConstellation starlightType;

    public SimpleIndependentSource(IWeakConstellation constellation) {
        this.starlightType = constellation;
    }

    @Override
    public IWeakConstellation getStarlightType() {
        return starlightType;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.starlightType = (IWeakConstellation) IConstellation.readFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        starlightType.writeToNBT(compound);
    }

}
