/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.focal.place;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalTransmutationCraftingInput
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalTransmutationCraftingInput extends CustomRecipeInput {

    private final BaseConstellation constellation;
    private final Level level;
    private final BlockPos pos;
    private final BlockState state;
    private final boolean isFocusedStarlight;

    public FocalTransmutationCraftingInput(BaseConstellation constellation, Level level, BlockPos pos, boolean isFocusedStarlight) {
        this.constellation = constellation;
        this.level = level;
        this.pos = pos;
        this.isFocusedStarlight = isFocusedStarlight;
        this.state = level.getBlockState(pos);
    }

    public BaseConstellation getConstellation() {
        return this.constellation;
    }

    public Level getLevel() {
        return this.level;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getState() {
        return this.state;
    }

    public boolean isFocusedStarlight() {
        return this.isFocusedStarlight;
    }

    @Override
    public boolean isEmpty() {
        return this.state.is(BlockTags.AIR);
    }
}
