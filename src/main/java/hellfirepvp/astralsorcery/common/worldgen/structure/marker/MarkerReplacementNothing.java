/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.structure.marker;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.types.StructureMarkerReplacementTypesAS;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MarkerReplacementNothing
 * Created by HellFirePvP
 * Date: 06.10.2026 / 18:41
 */
public class MarkerReplacementNothing extends StructureMarkerReplacement {

    public static final MarkerReplacementNothing INSTANCE = new MarkerReplacementNothing();

    public static final Type<MarkerReplacementNothing> TYPE = new Type<>(MapCodec.unit(INSTANCE));

    private MarkerReplacementNothing() {}

    @Override
    public Type<?> getType() {
        return StructureMarkerReplacementTypesAS.NOTHING.get();
    }

    @Override
    public StructureTemplate.StructureBlockInfo replace(LevelReader level, StructureTemplate.StructureBlockInfo marker, RandomSource rand) {
        return new StructureTemplate.StructureBlockInfo(marker.pos(), Blocks.AIR.defaultBlockState(), null);
    }
}
