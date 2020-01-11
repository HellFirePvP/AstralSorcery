/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NBTComparator
 * Created by HellFirePvP
 * Date: 28.03.2019 / 19:37
 */
public class NBTComparator {

    public static boolean contains(@Nonnull CompoundNBT thisCompound, @Nonnull CompoundNBT otherCompound) {
        for (String key : thisCompound.keySet()) {
            if (!otherCompound.contains(key)) {
                return false;
            }

            INBT thisNBT = thisCompound.get(key);
            INBT otherNBT = otherCompound.get(key);
            if (!compare(thisNBT, otherNBT)) {
                return false;
            }
        }
        return true;
    }

    private static boolean containList(ListNBT base, ListNBT other) {
        if (base.size() > other.size()) {
            return false;
        }

        List<Integer> matched = new ArrayList<>();
        lblMatching:
        for (INBT thisNbt : base) {
            for (int matchIndex = 0; matchIndex < other.size(); matchIndex++) {
                INBT matchNBT = other.get(matchIndex);

                if (!matched.contains(matchIndex)) {
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

    private static boolean compare(INBT thisEntry, INBT thatEntry) {
        if (thisEntry instanceof CompoundNBT && thatEntry instanceof CompoundNBT) {
            return contains((CompoundNBT) thisEntry, (CompoundNBT) thatEntry);
        } else if (thisEntry instanceof ListNBT && thatEntry instanceof ListNBT) {
            return containList((ListNBT) thisEntry, (ListNBT) thatEntry);
        } else {
            return thisEntry.equals(thatEntry);
        }
    }

}
