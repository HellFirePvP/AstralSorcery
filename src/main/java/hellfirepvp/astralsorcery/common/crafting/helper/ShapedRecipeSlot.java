/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShapedRecipeSlot
 * Created by HellFirePvP
 * Date: 10.08.2016 / 15:21
 */
public enum ShapedRecipeSlot {

    UPPER_LEFT  (0, 0, 0),
    UPPER_CENTER(1, 0, 1),
    UPPER_RIGHT (2, 0, 2),
    LEFT        (3, 1, 0),
    CENTER      (4, 1, 1),
    RIGHT       (5, 1, 2),
    LOWER_LEFT  (6, 2, 0),
    LOWER_CENTER(7, 2, 1),
    LOWER_RIGHT (8, 2, 2);

    private int slot;
    public final int rowMultipler, columnMultiplier;

    private ShapedRecipeSlot(int slot, int rowMultipler, int columnMultiplier) {
        this.slot = slot;
        this.rowMultipler = rowMultipler;
        this.columnMultiplier = columnMultiplier;
    }

    public static ShapedRecipeSlot getByRowColumnIndex(int row, int column) {
        for (ShapedRecipeSlot s : ShapedRecipeSlot.values()) {
            if(s.rowMultipler == row && s.columnMultiplier == column) return s;
        }
        return null;
    }

    public int getSlotID() {
        return slot;
    }

}
