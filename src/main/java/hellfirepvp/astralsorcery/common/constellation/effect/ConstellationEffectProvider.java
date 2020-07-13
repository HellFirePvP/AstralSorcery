/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationEffectProvider
 * Created by HellFirePvP
 * Date: 11.06.2019 / 19:34
 */
public abstract class ConstellationEffectProvider extends ForgeRegistryEntry<ConstellationEffectProvider> implements IForgeRegistryEntry<ConstellationEffectProvider> {

    private final IWeakConstellation cst;

    protected ConstellationEffectProvider(IWeakConstellation cst) {
        this.cst = cst;
        this.setRegistryName(cst.getRegistryName());
    }

    @Nonnull
    public IWeakConstellation getConstellation() {
        return cst;
    }

    public abstract ConstellationEffect createEffect(@Nullable ILocatable origin);

}
