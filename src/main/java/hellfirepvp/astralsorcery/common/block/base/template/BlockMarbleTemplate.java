/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base.template;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.lib.MaterialsAS;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMarbleTemplate
 * Created by HellFirePvP
 * Date: 31.05.2019 / 21:30
 */
public class BlockMarbleTemplate extends Block implements CustomItemBlock {

    public BlockMarbleTemplate() {
        super(Properties.create(MaterialsAS.MARBLE)
                .hardnessAndResistance(1F, 3F)
                .sound(SoundType.STONE));
    }

}
