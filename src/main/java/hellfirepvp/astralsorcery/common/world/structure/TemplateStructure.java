/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import hellfirepvp.astralsorcery.common.tile.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TemplateStructure
 * Created by HellFirePvP
 * Date: 07.05.2020 / 17:05
 */
public abstract class TemplateStructure extends TemplateStructurePiece {

    public TemplateStructure(IStructurePieceType structureType, TemplateManager mgr, BlockPos templatePosition) {
        super(structureType, 0);
        this.templatePosition = templatePosition;
        this.loadTemplate(mgr);
    }

    public TemplateStructure(IStructurePieceType structureType, TemplateManager mgr, CompoundNBT nbt) {
        super(structureType, nbt);
        this.loadTemplate(mgr);
    }

    private void loadTemplate(TemplateManager mgr) {
        Template tpl = mgr.getTemplateDefaulted(this.getStructureName());
        PlacementSettings settings = new PlacementSettings()
                .setIgnoreEntities(true)
                .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
        this.setup(tpl, this.templatePosition, settings);
    }

    protected void generateShrineCollectorCrystal(IWorld world, BlockPos pos, Random rand) {
        world.setBlockState(pos, BlocksAS.ROCK_COLLECTOR_CRYSTAL.getDefaultState(), Constants.BlockFlags.BLOCK_UPDATE);
        TileCollectorCrystal tcc = MiscUtils.getTileAt(world, pos, TileCollectorCrystal.class, true);
        if (tcc != null) {
            IMajorConstellation cst = MiscUtils.getRandomEntry(ConstellationRegistry.getMajorConstellations(), rand);
            tcc.setAttributes(CrystalPropertiesAS.WORLDGEN_SHRINE_COLLECTOR_ATTRIBUTES);
            tcc.setAttunedConstellation(cst);
        }
    }

    public abstract ResourceLocation getStructureName();
}
