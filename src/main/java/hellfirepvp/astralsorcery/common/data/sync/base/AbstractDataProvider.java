/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.base;

import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractDataProvider
 * Created by HellFirePvP
 * Date: 27.08.2019 / 06:27
 */
public abstract class AbstractDataProvider<T extends AbstractData, C extends ClientData<C>> {

    private ResourceLocation key;

    public AbstractDataProvider(ResourceLocation key) {
        this.key = key;
    }

    public abstract T provideServerData();

    public abstract C provideClientData();

    public abstract ClientDataReader<C> createReader();

    public final ResourceLocation getKey() {
        return key;
    }

}
