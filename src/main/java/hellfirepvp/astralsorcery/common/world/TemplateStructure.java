package hellfirepvp.astralsorcery.common.world;

import hellfirepvp.astralsorcery.common.world.marker.MarkerManagerAS;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
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

    public abstract ResourceLocation getStructureName();

    @Override
    protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb) {
        MarkerManagerAS.handleMarker(function, pos, worldIn, rand, boundingBox);
    }
}
