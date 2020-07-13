/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.common.lib.LootTablesAS;
import hellfirepvp.astralsorcery.common.lib.WorldGenerationAS;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AncientShrineStructure
 * Created by HellFirePvP
 * Date: 07.05.2020 / 09:26
 */
public class AncientShrineStructure extends TemplateStructure {

    public AncientShrineStructure(TemplateManager mgr, BlockPos templatePosition) {
        super(WorldGenerationAS.ANCIENT_SHRINE_PIECE, mgr, templatePosition);
    }

    public AncientShrineStructure(TemplateManager mgr, CompoundNBT nbt) {
        super(WorldGenerationAS.ANCIENT_SHRINE_PIECE, mgr, nbt);
    }

    @Override
    public ResourceLocation getStructureName() {
        return WorldGenerationAS.KEY_ANCIENT_SHRINE;
    }

    @Override
    protected void handleDataMarker(String marker, BlockPos blockPos, IWorld world, Random random, MutableBoundingBox structureBox) {
        switch (marker) {
            case "shrine_chest":
                if (random.nextInt(3) == 0) {
                    this.generateChest(world, structureBox, random, blockPos, LootTablesAS.SHRINE_CHEST, null);
                } else {
                    world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Constants.BlockFlags.BLOCK_UPDATE);
                }
                break;
            case "crystal":
                this.generateShrineCollectorCrystal(world, blockPos, random);
                break;
        }
    }
}
