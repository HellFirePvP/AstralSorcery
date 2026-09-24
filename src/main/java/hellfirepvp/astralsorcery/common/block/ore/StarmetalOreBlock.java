/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.ore;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.DropExperienceBlock;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StarmetalOreBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StarmetalOreBlock extends DropExperienceBlock {

    public static final MapCodec<StarmetalOreBlock> CODEC = simpleCodec(StarmetalOreBlock::new);

    public StarmetalOreBlock(Properties properties) {
        super(ConstantInt.of(3), properties);
    }

    @Override
    public MapCodec<StarmetalOreBlock> codec() {
        return CODEC;
    }
}
