/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TemplateStructureFeature
 * Created by HellFirePvP
 * Date: 18.11.2020 / 21:22
 */
public abstract class TemplateStructureFeature extends Structure<NoFeatureConfig> {

    public TemplateStructureFeature() {
        super(NoFeatureConfig.field_236558_a_);
    }

    @Override
    public GenerationStage.Decoration getDecorationStage() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    @Override
    public String getStructureName() {
        return this.getRegistryName().toString();
    }
}
