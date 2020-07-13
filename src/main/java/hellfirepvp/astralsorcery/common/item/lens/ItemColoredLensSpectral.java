/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.lens;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemColoredLensSpectral
 * Created by HellFirePvP
 * Date: 21.09.2019 / 19:56
 */
public class ItemColoredLensSpectral extends ItemColoredLens {

    private static final ColorTypeSpectral COLOR_TYPE_SPECTRAL = new ColorTypeSpectral();

    public ItemColoredLensSpectral() {
        super(COLOR_TYPE_SPECTRAL);
    }

    private static class ColorTypeSpectral extends LensColorType {

        private ColorTypeSpectral() {
            super(AstralSorcery.key("spectral"),
                    TargetType.NONE,
                    () -> new ItemStack(ItemsAS.COLORED_LENS_SPECTRAL),
                    ColorsAS.COLORED_LENS_SPECTRAL,
                    0.2F,
                    true);
        }

        @Override
        public void entityInBeam(Vector3 origin, Vector3 target, Entity entity, float beamStrength) {}

        @Override
        public void blockInBeam(IWorld world, BlockPos pos, BlockState state, float beamStrength) {}
    }
}
