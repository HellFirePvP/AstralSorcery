/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.tile;

import net.minecraft.client.renderer.entity.ItemRenderer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileLumenAlchemyArrayRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileLumenAlchemyArrayRenderer extends TileLumenArrayRenderer {

    public TileLumenAlchemyArrayRenderer(ItemRenderer itemRenderer) {
        super(itemRenderer);
    }

    @Override
    protected float getTankMaxHeight() {
        return 15.5F;
    }

    @Override
    protected float getTankSize() {
        return 8F;
    }
}
