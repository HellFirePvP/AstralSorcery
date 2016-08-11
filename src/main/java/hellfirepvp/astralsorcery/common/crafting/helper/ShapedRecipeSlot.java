package hellfirepvp.astralsorcery.common.crafting.helper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShapedRecipeSlot
 * Created by HellFirePvP
 * Date: 10.08.2016 / 15:21
 */
public enum ShapedRecipeSlot {

    UPPER_LEFT(0),
    UPPER_CENTER(1),
    UPPER_RIGHT(2),
    LEFT(3),
    CENTER(4),
    RIGHT(5),
    LOWER_LEFT(6),
    LOWER_CENTER(7),
    LOWER_RIGHT(8);

    private int slot;

    private ShapedRecipeSlot(int slot) {
        this.slot = slot;
    }

    public int getSlotID() {
        return slot;
    }

}
