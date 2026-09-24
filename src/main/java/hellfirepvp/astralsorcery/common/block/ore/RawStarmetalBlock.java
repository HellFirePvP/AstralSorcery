/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.ore;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RawStarmetalBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RawStarmetalBlock extends Block {

    public static final MapCodec<RawStarmetalBlock> CODEC = simpleCodec(RawStarmetalBlock::new);

    public RawStarmetalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }
}
