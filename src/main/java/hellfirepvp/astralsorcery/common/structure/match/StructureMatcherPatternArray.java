/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure.match;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.structure.*;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.structure.change.BlockStateChangeSet;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureMatcherPatternArray
 * Created by HellFirePvP
 * Date: 02.12.2018 / 13:24
 */
public class StructureMatcherPatternArray extends StructureMatcher {

    private PatternBlockArray structure;
    private ObservableArea structureArea;

    private List<BlockPos> mismatches = Lists.newArrayList();

    public StructureMatcherPatternArray(@Nonnull ResourceLocation registryName) {
        super(registryName);
        setStructure(registryName);
    }

    private void setStructure(ResourceLocation structName) {
        MatchableStructure struct = StructureRegistry.INSTANCE.getStructure(structName);
        if (struct instanceof PatternBlockArray) {
            this.structure = (PatternBlockArray) struct;
            this.structureArea = new ObservableAreaBoundingBox(structure.getMin(), structure.getMax());
        } else {
            throw new IllegalArgumentException("Passed structure matcher key does not have a registered underlying structure pattern: " + structName);
        }
    }

    public void initialize(IBlockAccess world, BlockPos center) {
        for (BlockPos offset : this.structure.getPattern().keySet()) {
            if (!this.structure.matchSingleBlock(world, center, offset)) {
                this.mismatches.add(offset);
            }
        }
    }

    @Override
    public ObservableArea getObservableArea() {
        return this.structureArea;
    }

    @Override
    public boolean notifyChange(IBlockAccess world, BlockPos centre, BlockStateChangeSet changeSet) {
        for (BlockStateChangeSet.StateChange change : changeSet.getChanges()) {
            if (this.structure.hasBlockAt(change.pos) &&
                    !this.structure.matchSingleBlockState(change.pos, change.newState)) {
                if (!this.mismatches.contains(change.pos)) {
                    this.mismatches.add(change.pos);
                }
            } else {
                this.mismatches.remove(change.pos);
            }
        }
        return this.mismatches.size() <= 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.mismatches.clear();
        NBTTagList tagMismatches = tag.getTagList("mismatchList", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tagMismatches.tagCount(); i++) {
            NBTTagCompound tagPos = tagMismatches.getCompoundTagAt(i);
            this.mismatches.add(NBTHelper.readBlockPosFromNBT(tagPos));
        }

        setStructure(new ResourceLocation(tag.getString("structureToMatch")));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList tagMismatches = new NBTTagList();

        for (BlockPos pos : this.mismatches) {
            NBTTagCompound tagPos = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(pos, tagPos);
            tagMismatches.appendTag(tagPos);
        }

        tag.setTag("mismatchList", tagMismatches);

        tag.setString("structureToMatch", this.structure.getRegistryName().toString());
    }

}
