/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.world;

import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.observerlib.common.data.WorldCacheDomain;
import hellfirepvp.observerlib.common.data.base.SectionWorldData;
import hellfirepvp.observerlib.common.data.base.WorldSection;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureGenerationBuffer
 * Created by HellFirePvP
 * Date: 30.05.2019 / 15:05
 */
public class StructureGenerationBuffer extends SectionWorldData<StructureGenerationBuffer.StructureRegion> {

    public StructureGenerationBuffer(WorldCacheDomain.SaveKey<?> key) {
        super(key, PRECISION_REGION);
    }

    @Override
    protected StructureRegion createNewSection(int sectionX, int sectionZ) {
        return new StructureRegion(sectionX, sectionZ);
    }

    public void setStructureGeneration(StructureType type, BlockPos pos) {
        getOrCreateSection(pos).setStructure(type, pos);
        markDirty(pos);
    }

    public double getDstToClosest(StructureType type, double idealDistance, BlockPos dstTo) {
        double closest = Double.MAX_VALUE;
        double halfDst = idealDistance / 2.0D;
        int maxDistance = MathHelper.floor(idealDistance * 2);
        Vec3i searchVector = new Vec3i(maxDistance, 0, maxDistance);

        if (type.isAverageDistanceRequired()) {
            for (StructureType tt : RegistriesAS.REGISTRY_STRUCTURE_TYPES.getValues()) {
                if (!tt.isAverageDistanceRequired()) {
                    continue;
                }
                for (StructureRegion region : getSections(dstTo.subtract(searchVector), dstTo.add(searchVector))) {
                    for (BlockPos position : region.getStructures(type)) {
                        double dst = position.distanceSq(dstTo);
                        if (dst <= halfDst) {
                            return dst; //Fast fail on close structures
                        }
                    }
                }
            }
        }

        for (StructureRegion region : getSections(dstTo.subtract(searchVector), dstTo.add(searchVector))) {
            for (BlockPos position : region.getStructures(type)) {
                double dst = position.distanceSq(dstTo);
                if(dst < closest) {
                    closest = dst;
                }
            }
        }
        return Math.sqrt(closest);
    }

    @Nullable
    public BlockPos getClosest(StructureType type, BlockPos dstTo, int searchRadius) {
        double closest = Double.MAX_VALUE;
        BlockPos closestPos = null;
        Vec3i searchVector = new Vec3i(searchRadius, 0, searchRadius);
        for (StructureRegion region : getSections(dstTo.subtract(searchVector), dstTo.add(searchVector))) {
            for (BlockPos position : region.getStructures(type)) {
                double dst = position.distanceSq(dstTo);
                if(dst < closest) {
                    closest = dst;
                    closestPos = position;
                }
            }
        }
        return closestPos;
    }

    @Override
    public void writeToNBT(CompoundNBT nbt) {}

    @Override
    public void readFromNBT(CompoundNBT nbt) {}

    @Override
    public void updateTick(World world) {}

    public static class StructureRegion extends WorldSection {

        private Map<StructureType, Collection<BlockPos>> generatedStructures = new HashMap<>();

        protected StructureRegion(int sX, int sZ) {
            super(sX, sZ);
        }

        public void setStructure(StructureType type, BlockPos pos) {
            generatedStructures.computeIfAbsent(type, t -> new HashSet<>()).add(pos);
        }

        @Nonnull
        public Collection<BlockPos> getStructures(StructureType type) {
            return this.generatedStructures.getOrDefault(type, new HashSet<>());
        }

        @Override
        public void writeToNBT(CompoundNBT compound) {
            for (StructureType type : generatedStructures.keySet()) {
                ListNBT list = new ListNBT();
                for (BlockPos pos : generatedStructures.get(type)) {
                    CompoundNBT tag = new CompoundNBT();
                    NBTHelper.writeBlockPosToNBT(pos, tag);
                    list.add(tag);
                }
                compound.put(type.getRegistryName().toString(), list);
            }
        }

        @Override
        public void readFromNBT(CompoundNBT compound) {
            generatedStructures.clear();

            for (StructureType type : RegistriesAS.REGISTRY_STRUCTURE_TYPES.getValues()) {
                if (compound.contains(type.getRegistryName().toString(), Constants.NBT.TAG_LIST)) {
                    ListNBT list = compound.getList(type.getRegistryName().toString(), Constants.NBT.TAG_COMPOUND);
                    for (int i = 0; i < list.size(); i++) {
                        CompoundNBT cmp = list.getCompound(i);
                        BlockPos pos = NBTHelper.readBlockPosFromNBT(cmp);
                        generatedStructures.computeIfAbsent(type, t -> new HashSet<>()).add(pos);
                    }
                }
            }
        }
    }

}
