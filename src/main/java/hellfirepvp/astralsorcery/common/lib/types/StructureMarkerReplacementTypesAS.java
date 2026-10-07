/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.types;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.worldgen.structure.marker.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StructureMarkerReplacementTypesAS
 * Created by HellFirePvP
 * Date: 06.10.2026 / 20:15
 */
public class StructureMarkerReplacementTypesAS {

    public static final DeferredRegister<StructureMarkerReplacement.Type<?>> MARKER_REPLACEMENT_TYPE_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_STRUCTURE_MARKER_REPLACEMENT_TYPES, AstralSorcery.MODID);

    public static final DeferredHolder<StructureMarkerReplacement.Type<?>, StructureMarkerReplacement.Type<MarkerReplacementNothing>> NOTHING =
            MARKER_REPLACEMENT_TYPE_REGISTER.register("nothing", () -> MarkerReplacementNothing.TYPE);
    public static final DeferredHolder<StructureMarkerReplacement.Type<?>, StructureMarkerReplacement.Type<MarkerReplacementBlock>> BLOCK =
            MARKER_REPLACEMENT_TYPE_REGISTER.register("block", () -> MarkerReplacementBlock.TYPE);
    public static final DeferredHolder<StructureMarkerReplacement.Type<?>, StructureMarkerReplacement.Type<MarkerReplacementLootContainer>> LOOT_CONTAINER =
            MARKER_REPLACEMENT_TYPE_REGISTER.register("loot_container", () -> MarkerReplacementLootContainer.TYPE);
    public static final DeferredHolder<StructureMarkerReplacement.Type<?>, StructureMarkerReplacement.Type<MarkerReplacementRandom>> RANDOM =
            MARKER_REPLACEMENT_TYPE_REGISTER.register("random", () -> MarkerReplacementRandom.TYPE);
    public static final DeferredHolder<StructureMarkerReplacement.Type<?>, StructureMarkerReplacement.Type<MarkerReplacementBiome>> BIOME =
            MARKER_REPLACEMENT_TYPE_REGISTER.register("biome", () -> MarkerReplacementBiome.TYPE);
}
