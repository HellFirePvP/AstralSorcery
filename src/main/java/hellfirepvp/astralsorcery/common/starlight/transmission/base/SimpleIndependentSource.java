package hellfirepvp.astralsorcery.common.starlight.transmission.base;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
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

    private Constellation starlightType;

    public SimpleIndependentSource(@Nonnull Constellation constellation) {
        this.starlightType = constellation;
    }

    @Override
    public Constellation getStarlightType() {
        return starlightType;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.starlightType = Constellation.readFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        starlightType.writeToNBT(compound);
    }

}
