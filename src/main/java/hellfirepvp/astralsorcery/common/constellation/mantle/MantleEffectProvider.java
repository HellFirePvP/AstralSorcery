/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectProvider
 * Created by HellFirePvP
 * Date: 17.02.2020 / 20:13
 */
public abstract class MantleEffectProvider extends ForgeRegistryEntry<MantleEffectProvider> implements IForgeRegistryEntry<MantleEffectProvider> {

    private final IWeakConstellation cst;

    protected MantleEffectProvider(IWeakConstellation cst) {
        this.cst = cst;
        this.setRegistryName(cst.getRegistryName());
    }

    @Nonnull
    public IWeakConstellation getConstellation() {
        return cst;
    }

    public abstract MantleEffect createEffect(@Nullable CompoundNBT data);
}
