/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.common.lib.WorldGenerationAS;
import hellfirepvp.astralsorcery.common.world.TemplateStructure;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.template.TemplateManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AncientShrineStructure
 * Created by HellFirePvP
 * Date: 18.11.2020 / 21:12
 */
public class AncientShrineStructure extends TemplateStructure {

    public AncientShrineStructure(TemplateManager mgr, BlockPos templatePosition) {
        super(WorldGenerationAS.Structures.ANCIENT_SHRINE_PIECE, mgr, templatePosition);
        this.setYOffset(-7);
    }

    public AncientShrineStructure(TemplateManager mgr, CompoundNBT nbt) {
        super(WorldGenerationAS.Structures.ANCIENT_SHRINE_PIECE, mgr, nbt);
        this.setYOffset(-7);
    }

    @Override
    public ResourceLocation getStructureName() {
        return WorldGenerationAS.Structures.KEY_ANCIENT_SHRINE;
    }
}
