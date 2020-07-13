/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.base;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.lib.StructuresAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualLink;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.block.iterator.BlockPositionGenerator;
import hellfirepvp.astralsorcery.common.util.block.iterator.BlockRandomPositionGenerator;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectAbstractList
 * Created by HellFirePvP
 * Date: 11.06.2019 / 19:59
 */
public abstract class CEffectAbstractList<T extends CEffectAbstractList.ListEntry> extends ConstellationEffect {

    //Used to disable pedestal whitelisting when finding positions to break blocks at.
    protected boolean isLinkedRitual = false;
    private boolean excludesRitual = false; //Excludes exclusive ritual positions
    private boolean excludeRitualColumn = false; //Excludes everything from the pedestal and structure upwards

    protected final BlockPredicate verifier;
    protected final int maxAmount;
    private final BlockPositionGenerator positionStrategy;
    private List<T> elements = new ArrayList<>();

    protected CEffectAbstractList(@Nonnull ILocatable origin, @Nonnull IWeakConstellation cst, int maxAmount, BlockPredicate verifier) {
        super(origin, cst);
        this.maxAmount = maxAmount;
        this.verifier = verifier;

        this.positionStrategy = this.createPositionStrategy();
    }

    protected void excludeRitualPositions() {
        this.setChunkNeedsToBeLoaded();
        if (!this.excludesRitual) {
            this.excludesRitual = true;
            this.positionStrategy.andFilter(this.createExcludeRitualPredicate());
        }
    }

    protected void excludeRitualColumn() {
        this.setChunkNeedsToBeLoaded();
        if (!this.excludeRitualColumn) {
            this.excludeRitualColumn = true;
            this.positionStrategy.andFilter(this.createExcludeRitualColumnPredicate());
        }
    }

    protected void selectSphericalPositions() {
        this.positionStrategy.andFilter((pos, radius) -> {
            double dst = new Vector3(this.getPos().getLocationPos()).add(0.5, 0.5, 0.5)
                    .distanceSquared(new Vector3(pos).add(this.getPos().getLocationPos()).add(0.5, 0.5, 0.5));
            return dst <= radius * radius;
        });
    }

    @Nullable
    public abstract T recreateElement(CompoundNBT tag, BlockPos pos);

    @Nullable
    public abstract T createElement(World world, BlockPos pos);

    @Nonnull
    protected BlockPositionGenerator createPositionStrategy() {
        return new BlockRandomPositionGenerator();
    }

    @Nonnull
    protected BlockPositionGenerator selectPositionStrategy(BlockPositionGenerator defaultGenerator, ConstellationEffectProperties properties) {
        return defaultGenerator;
    }

    private Predicate<BlockPos> createExcludeRitualPredicate() {
        return pos ->
                !pos.equals(BlockPos.ZERO) &&
                        this.isLinkedRitual || (
                                !pos.equals(TileRitualPedestal.RITUAL_ANCHOR_OFFEST) && (
                                        pos.getY() >= 3 || ( //Anything above the ritual is fine aswell
                                                !StructuresAS.STRUCT_RITUAL_PEDESTAL.hasBlockAt(pos) && //actual Ritual layer
                                                        !StructuresAS.STRUCT_RITUAL_PEDESTAL.hasBlockAt(pos.down()) && //Pillars & config
                                                        !StructuresAS.STRUCT_RITUAL_PEDESTAL.hasBlockAt(pos.down(2)) &&  //uh... consistency?
                                                        !StructuresAS.STRUCT_RITUAL_PEDESTAL.hasBlockAt(pos.down(3))))); //Lenses
    }

    private Predicate<BlockPos> createExcludeRitualColumnPredicate() {
        return pos -> (this.isLinkedRitual && pos.getX() == 0 && pos.getZ() == 0) ||
                (!this.isLinkedRitual && StructuresAS.STRUCT_RITUAL_PEDESTAL.hasBlockAt(new BlockPos(pos.getX(), -1, pos.getZ())));
    }

    public int getCount() {
        return this.elements.size();
    }

    public void clear() {
        this.elements.clear();
    }

    @Nullable
    public T getRandomElement() {
        return MiscUtils.getRandomEntry(this.elements, rand);
    }

    @Nullable
    public T getRandomElementChanced() {
        if (this.elements.isEmpty()) {
            return null;
        }
        if (rand.nextInt(Math.max(0, (this.maxAmount - this.getCount()) / 4) + 1) == 0) {
            return getRandomElement();
        }
        return null;
    }

    @Nullable
    public T peekNewPosition(World world, BlockPos pos, ConstellationEffectProperties prop) {
        if (this.excludesRitual || this.excludeRitualColumn) {
            MiscUtils.executeWithChunk(world, pos, () -> {
                this.isLinkedRitual = MiscUtils.getTileAt(world, pos, TileRitualLink.class, true) != null;
            });
        }
        BlockPositionGenerator gen = this.selectPositionStrategy(this.positionStrategy, prop);
        if (gen != this.positionStrategy) {
            gen.copyFilterFrom(this.positionStrategy);
        }
        BlockPos at = gen.generateNextPosition(BlockPos.ZERO, prop.getSize());
        BlockPos actual = at.add(pos);
        return MiscUtils.executeWithChunk(world, actual, () -> {
            if (this.verifier.test(world, actual, world.getBlockState(actual))) {
                return this.createElement(world, actual);
            }
            return null;
        });
    }

    @Nullable
    public T findNewPosition(World world, BlockPos pos, ConstellationEffectProperties prop) {
        if (this.getCount() >= this.maxAmount) {
            return null;
        }
        T newElement = this.peekNewPosition(world, pos, prop);
        if (newElement != null && !this.hasElement(newElement.getPos())) {
            this.elements.add(newElement);
            return newElement;
        }
        return null;
    }

    public boolean removeElement(T entry) {
        return removeElement(entry.getPos());
    }

    public boolean removeElement(BlockPos pos) {
        return this.elements.removeIf(e -> e.getPos().equals(pos));
    }

    public boolean hasElement(BlockPos pos) {
        return MiscUtils.contains(this.elements, e -> e.getPos().equals(pos));
    }

    @Override
    public void readFromNBT(CompoundNBT cmp) {
        super.readFromNBT(cmp);

        this.elements.clear();

        ListNBT list = cmp.getList("elements", Constants.NBT.TAG_COMPOUND);
        for (INBT nbt : list) {
            CompoundNBT tag = (CompoundNBT) nbt;
            BlockPos pos = NBTHelper.readBlockPosFromNBT(tag);
            CompoundNBT tagData = tag.getCompound("data");
            T element = this.recreateElement(tagData, pos);
            if (element != null) {
                element.readFromNBT(tagData);
                this.elements.add(element);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundNBT cmp) {
        super.writeToNBT(cmp);

        ListNBT list = new ListNBT();
        for (T element : this.elements) {
            CompoundNBT tag = new CompoundNBT();
            NBTHelper.writeBlockPosToNBT(element.getPos(), tag);

            CompoundNBT dataTag = new CompoundNBT();
            element.writeToNBT(dataTag);
            tag.put("data", dataTag);

            list.add(tag);
        }
        cmp.put("elements", list);
    }

    public static interface ListEntry {

        public BlockPos getPos();

        public void writeToNBT(CompoundNBT nbt);

        public void readFromNBT(CompoundNBT nbt);

    }

    public static class CountConfig extends Config {

        private final int defaultMaxAmount;

        public ForgeConfigSpec.IntValue maxAmount;

        public CountConfig(String constellationName, double defaultRange, double defaultRangePerLens, int defaultMaxAmount) {
            super(constellationName, defaultRange, defaultRangePerLens);
            this.defaultMaxAmount = defaultMaxAmount;
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.maxAmount = cfgBuilder
                    .comment("Defines the amount of blocks this ritual will try to capture at most.")
                    .translation(translationKey("maxAmount"))
                    .defineInRange("maxAmount", this.defaultMaxAmount, 1, 2048);
        }
    }

}
