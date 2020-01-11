/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.animation.TileEntityRendererFast;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomFastTileEntityRenderer
 * Created by HellFirePvP
 * Date: 28.11.2019 / 19:58
 */
public abstract class CustomFastTileEntityRenderer<T extends TileEntity> extends TileEntityRendererFast<T> {

    @Override
    public abstract void renderTileEntityFast(T tile, double x, double y, double z, float pTicks, int destroyStage, BufferBuilder buffer);
}
