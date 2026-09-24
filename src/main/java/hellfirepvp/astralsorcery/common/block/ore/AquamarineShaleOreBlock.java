/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.ore;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AquamarineShaleOreBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AquamarineShaleOreBlock extends ColoredFallingBlock {

    public static final MapCodec<AquamarineShaleOreBlock> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            IntProvider.codec(0, 100).fieldOf("experience").forGetter(p_304879_ -> p_304879_.xpRange),
            ColorRGBA.CODEC.fieldOf("falling_dust_color").forGetter(p_304722_ -> p_304722_.dustColor),
            propertiesCodec())
    .apply(inst, AquamarineShaleOreBlock::new));

    private final IntProvider xpRange;
    private final ColorRGBA dustColor;

    public AquamarineShaleOreBlock(IntProvider xpRange, ColorRGBA dustColor, Properties properties) {
        super(dustColor, properties);
        this.xpRange = xpRange;
        this.dustColor = dustColor;
    }

    @Override
    public MapCodec<ColoredFallingBlock> codec() {
        return MiscUtil.cast(CODEC);
    }

    @Override
    public int getExpDrop(BlockState state, LevelAccessor level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker, ItemStack tool) {
        return this.xpRange.sample(level.getRandom());
    }
}
