/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base.template;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockSlabTemplate
 * Created by HellFirePvP
 * Date: 21.07.2019 / 10:36
 */
public class BlockSlabTemplate extends SlabBlock implements CustomItemBlock {

    private final BlockState baseState;

    public BlockSlabTemplate(BlockState baseState, Properties properties) {
        super(properties);
        this.baseState = baseState;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState tool) {
        return this.baseState.getHarvestTool();
    }

    @Override
    public int getHarvestLevel(BlockState state) {
        return this.baseState.getHarvestLevel();
    }
}
