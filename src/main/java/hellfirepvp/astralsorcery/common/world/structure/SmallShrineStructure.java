/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.LootTablesAS;
import hellfirepvp.astralsorcery.common.lib.WorldGenerationAS;
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
public class SmallShrineStructure extends TemplateStructure {

    public SmallShrineStructure(TemplateManager mgr, BlockPos templatePosition) {
        super(WorldGenerationAS.SMALL_SHRINE_PIECE, mgr, templatePosition);
    }

    public SmallShrineStructure(TemplateManager mgr, CompoundNBT nbt) {
        super(WorldGenerationAS.SMALL_SHRINE_PIECE, mgr, nbt);
    }

    @Override
    public ResourceLocation getStructureName() {
        return WorldGenerationAS.KEY_SMALL_SHRINE;
    }

    @Override
    protected void handleDataMarker(String marker, BlockPos blockPos, IWorld world, Random random, MutableBoundingBox structureBox) {
        switch (marker) {
            case "shrine_chest":
                if (random.nextInt(3) == 0) {
                    this.generateChest(world, structureBox, random, blockPos, LootTablesAS.SHRINE_CHEST, null);
                } else {
                    world.setBlockState(blockPos, BlocksAS.MARBLE_BRICKS.getDefaultState(), Constants.BlockFlags.BLOCK_UPDATE);
                }
                break;
        }
    }
}
