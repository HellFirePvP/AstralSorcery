/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.structure.marker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StructureMarkerReplacement
 * Created by HellFirePvP
 * Date: 06.10.2026 / 18:24
 */
public abstract class StructureMarkerReplacement {

    public static final Codec<StructureMarkerReplacement> CODEC = RegistriesAS.REGISTRY_STRUCTURE_MARKER_REPLACEMENT_TYPES.byNameCodec()
            .dispatch(StructureMarkerReplacement::getType, StructureMarkerReplacement.Type::codec);

    public abstract Type<?> getType();

    @Nullable
    public abstract StructureTemplate.StructureBlockInfo replace(LevelReader level, StructureTemplate.StructureBlockInfo marker, RandomSource rand);

    public record Type<T extends StructureMarkerReplacement>(MapCodec<T> codec) {}
}
