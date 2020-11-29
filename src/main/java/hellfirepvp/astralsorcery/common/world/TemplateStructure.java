/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import hellfirepvp.astralsorcery.common.world.marker.MarkerManagerAS;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TemplateStructure
 * Created by HellFirePvP
 * Date: 18.11.2020 / 20:45
 */
public abstract class TemplateStructure extends TemplateStructurePiece {

    private int yOffset = 0;

    public TemplateStructure(IStructurePieceType structurePieceTypeIn, TemplateManager mgr, BlockPos templatePosition) {
        super(structurePieceTypeIn, 0);
        this.templatePosition = templatePosition;
        this.loadTemplate(mgr);
    }

    public TemplateStructure(IStructurePieceType structurePieceTypeIn, TemplateManager mgr, CompoundNBT nbt) {
        super(structurePieceTypeIn, nbt);
        this.loadTemplate(mgr);
    }

    private void loadTemplate(TemplateManager mgr) {
        Template tpl = mgr.getTemplateDefaulted(this.getStructureName());
        PlacementSettings settings = new PlacementSettings()
                .setIgnoreEntities(true)
                .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
        this.setup(tpl, this.templatePosition, settings);
    }

    public <T extends TemplateStructure> T setYOffset(int yOffset) {
        this.yOffset = yOffset;
        return (T) this;
    }

    public abstract ResourceLocation getStructureName();

    @Override
    public boolean func_230383_a_(ISeedReader world, StructureManager mgr, ChunkGenerator gen, Random rand, MutableBoundingBox box, ChunkPos chunkPos, BlockPos structCenter) {
        MutableBoundingBox genBox = new MutableBoundingBox(box);
        genBox.offset(0, this.yOffset, 0);

        BlockPos original = this.templatePosition;
        this.templatePosition = original.up(this.yOffset);
        try {
            return super.func_230383_a_(world, mgr, gen, rand, genBox, chunkPos, structCenter.up(yOffset));
        } finally {
            this.templatePosition = original;
            this.placeSettings.setBoundingBox(box);
            this.boundingBox = this.template.getMutableBoundingBox(this.placeSettings, this.templatePosition);
        }
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb) {
        if (sbb.isVecInside(pos)) {
            MarkerManagerAS.handleMarker(function, pos, worldIn, rand, boundingBox);
        }
    }
}
