/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerTileEntity
 * Created by HellFirePvP
 * Date: 03.08.2019 / 16:10
 */
public abstract class ContainerTileEntity<T extends TileEntity> extends Container {

    private final T te;

    protected ContainerTileEntity(T tileEntity, @Nullable ContainerType<?> type, int windowId) {
        super(type, windowId);
        this.te = tileEntity;
    }

    public T getTileEntity() {
        return te;
    }
}
