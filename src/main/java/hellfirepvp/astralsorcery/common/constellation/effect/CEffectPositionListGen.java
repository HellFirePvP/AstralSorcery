/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectPositionListGen
 * Created by HellFirePvP
 * Date: 08.11.2016 / 20:11
 */
public abstract class CEffectPositionListGen<T extends CEffectPositionListGen.CEffectGenListEntry> extends ConstellationEffect {

    protected final Function<BlockPos, T> elementProvider;
    protected final int searchRange, maxCount;
    protected final Verifier verifier;
    private List<T> elements = new ArrayList<>();

    public CEffectPositionListGen(@Nullable ILocatable origin, IWeakConstellation constellation, String cfgName, int searchRange, int maxCount, Verifier verifier, Function<BlockPos, T> emptyElementProvider) {
        super(origin, constellation, cfgName);
        this.elementProvider = emptyElementProvider;
        this.searchRange = searchRange;
        this.maxCount = maxCount;
        this.verifier = verifier;
    }

    public int getElementCount() {
        return elements.size();
    }

    @Override
    public void clearCache() {
        this.elements.clear();
    }

    //Returns only null if empty.
    @Nullable
    public T getRandomElement(Random rand) {
        return elements.isEmpty() ? null : elements.get(rand.nextInt(elements.size()));
    }

    @Nullable
    public T getRandomElementByChance(Random rand) {
        if(elements.isEmpty()) return null;
        if(rand.nextInt(Math.max((maxCount - elements.size()) / 4, 0) + 1) == 0) {
            return getRandomElement(rand);
        }
        return null;
    }

    public boolean removeElement(T element) {
        return elements.remove(element);
    }

    public boolean offerNewElement(T element) {
        if(maxCount <= elements.size()) return false;
        if(containsElementAt(element.getPos())) return false;
        return elements.add(element);
    }

    public boolean findNewPosition(World world, BlockPos pos) {
        if(maxCount > elements.size()) {
            int offX = -searchRange + world.rand.nextInt(searchRange * 2 + 1);
            int offY = -searchRange + world.rand.nextInt(searchRange * 2 + 1);
            int offZ = -searchRange + world.rand.nextInt(searchRange * 2 + 1);
            BlockPos at = pos.add(offX, offY, offZ);
            if(MiscUtils.isChunkLoaded(world, at) && verifier.isValid(world, at) && !containsElementAt(at)) {
                T element = newElement(world, at);
                if(element != null) {
                    elements.add(element);
                }
                return true;
            }
        }
        return false;
    }

    public T newElement(World world, BlockPos at) {
        return elementProvider.apply(at);
    }

    private boolean containsElementAt(BlockPos pos) {
        for (T e : elements) {
            if(e.getPos().equals(pos)) return true;
        }
        return false;
    }

    //FIXME SOMEDAY the read version is not world sensitive -> fertilitas reeds?... they need world dependent checking..

    @Override
    public void readFromNBT(NBTTagCompound cmp) {
        elements.clear();
        NBTTagList list = cmp.getTagList("positions", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            BlockPos pos = NBTUtils.readBlockPosFromNBT(tag);
            T element = elementProvider.apply(pos);
            if(element != null) {
                element.readFromNBT(tag);
                elements.add(element);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound cmp) {
        NBTTagList listPositions = new NBTTagList();
        for (T elem : elements) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(elem.getPos(), tag);
            elem.writeToNBT(tag);
            listPositions.appendTag(tag);
        }
        cmp.setTag("positions", listPositions);
    }

    public static interface Verifier {

        public boolean isValid(World world, BlockPos testPos);

    }

    public static interface CEffectGenListEntry {

        public BlockPos getPos();

        public void readFromNBT(NBTTagCompound nbt);

        public void writeToNBT(NBTTagCompound nbt);

    }

}
