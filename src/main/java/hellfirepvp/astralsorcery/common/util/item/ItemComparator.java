/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.item;

import com.google.common.collect.Sets;
import hellfirepvp.astralsorcery.common.util.nbt.NBTComparator;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemComparator
 * Created by HellFirePvP
 * Date: 28.03.2019 / 19:16
 */
// Yes, i know, this isn't really a java.lang.Comparator
public class ItemComparator {

    public static boolean compare(@Nonnull ItemStack thisStack, @Nonnull ItemStack sampleCompare, Clause... clauses) {
        Set<Clause> lClauses = Sets.newHashSet(clauses);

        if (lClauses.contains(Clause.ITEM)) {
            if (thisStack.isEmpty() && !sampleCompare.isEmpty()) {
                return false;
            }
            if (!thisStack.isEmpty() && !thisStack.getItem().equals(sampleCompare.getItem())) { //Includes inverse case of the above.
                return false;
            }
        }

        if (lClauses.contains(Clause.AMOUNT_EXACT)) {
            if (thisStack.getCount() != sampleCompare.getCount()) {
                return false;
            }
        } else if (lClauses.contains(Clause.AMOUNT_LEAST)) {
            if (thisStack.getCount() > sampleCompare.getCount()) {
                return false;
            }
        }

        boolean thisHasTag = thisStack.hasTag() && !thisStack.getTag().isEmpty();
        boolean sampleHasTag = sampleCompare.hasTag() && !sampleCompare.getTag().isEmpty();

        if (lClauses.contains(Clause.NBT_STRICT)) {
            if (!thisHasTag && sampleHasTag) {
                return false;
            } else if (thisHasTag && (!sampleHasTag || !thisStack.getTag().equals(sampleCompare.getTag()))) {
                return false;
            }
        } else if (lClauses.contains(Clause.NBT_LEAST)) {
            if (thisHasTag) {
                if (!sampleHasTag) {
                    return false;
                }

                if (!NBTComparator.contains(thisStack.getTag(), sampleCompare.getTag())) {
                    return false;
                }
            }
        }

        if (lClauses.contains(Clause.CAPABILITIES_COMPATIBLE)) {
            if (!thisStack.areCapsCompatible(sampleCompare)) {
                return false;
            }
        }

        return true;
    }

    public static enum Clause {

        ITEM,

        AMOUNT_EXACT,
        AMOUNT_LEAST,

        NBT_STRICT,
        NBT_LEAST,

        CAPABILITIES_COMPATIBLE;

        public static class Sets {

            public static final Clause[] ITEMSTACK_STRICT = { ITEM, AMOUNT_EXACT, NBT_STRICT, CAPABILITIES_COMPATIBLE };

            public static final Clause[] ITEMSTACK_STRICT_NOAMOUNT = { ITEM, NBT_STRICT, CAPABILITIES_COMPATIBLE };

            public static final Clause[] ITEMSTACK_CRAFTING = { ITEM, NBT_LEAST };

        }

    }

}
