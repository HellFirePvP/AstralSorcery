/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the ObserverLib Mod
 * The complete source code for this mod can be found on github.
 * Class: NBTComparator
 * Created by HellFirePvP
 * Date: 28.03.2019 / 19:37
 */
public class NBTComparator {

    public static boolean contains(@Nonnull NBTTagCompound thisCompound, @Nonnull NBTTagCompound otherCompound) {
        for (String key : thisCompound.getKeySet()) {
            if (!otherCompound.hasKey(key, thisCompound.getTagId(key))) {
                return false;
            }

            NBTBase thisNBT = thisCompound.getTag(key);
            NBTBase otherNBT = otherCompound.getTag(key);
            if (!compare(thisNBT, otherNBT)) {
                return false;
            }
        }
        return true;
    }

    private static boolean containList(NBTTagList base, NBTTagList other) {
        if (base.tagCount() > other.tagCount()) {
            return false;
        }

        List<Integer> matched = new ArrayList<>();
        lblMatching:
        for (int index = 0; index < base.tagCount(); index++) {
            NBTBase thisNbt = base.get(index);

            for (int matchIndex = 0; matchIndex < other.tagCount(); matchIndex++) {
                NBTBase matchNBT = other.get(matchIndex);

                if (!matched.contains(matchIndex)){
                    if (compare(thisNbt, matchNBT)) {
                        matched.add(matchIndex);
                        continue lblMatching;
                    }
                }
            }

            return false;
        }

        return true;
    }

    private static boolean compare(NBTBase thisEntry, NBTBase thatEntry) {
        if (thisEntry instanceof NBTTagCompound && thatEntry instanceof NBTTagCompound) {
            return contains((NBTTagCompound) thisEntry, (NBTTagCompound) thatEntry);
        } else if (thisEntry instanceof NBTTagList && thatEntry instanceof NBTTagList) {
            return containList((NBTTagList) thisEntry, (NBTTagList) thatEntry);
        } else {
            return thisEntry.equals(thatEntry);
        }
    }

}
