/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

import java.util.HashMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShapeMap
 * Created by HellFirePvP
 * Date: 10.08.2016 / 15:20
 */
public class ShapeMap extends HashMap<ShapedRecipeSlot, ItemHandle> {

    private boolean cut = true;

    public ShapeMap() {
        super();
        put(ShapedRecipeSlot.UPPER_LEFT, null);
        put(ShapedRecipeSlot.UPPER_CENTER, null);
        put(ShapedRecipeSlot.UPPER_RIGHT, null);
        put(ShapedRecipeSlot.LEFT, null);
        put(ShapedRecipeSlot.CENTER, null);
        put(ShapedRecipeSlot.RIGHT, null);
        put(ShapedRecipeSlot.LOWER_LEFT, null);
        put(ShapedRecipeSlot.LOWER_CENTER, null);
        put(ShapedRecipeSlot.LOWER_RIGHT, null);
    }

    void setCut(boolean cut) {
        this.cut = cut;
    }

    public Baked bake() {
        Baked baked = new Baked(cut);
        for (ShapedRecipeSlot srs : ShapedRecipeSlot.values()) {
            ItemHandle handle = get(srs);
            if(handle != null) {
                baked.put(srs, handle.getRecipeIngredient());
            } else {
                baked.put(srs, Ingredient.EMPTY);
            }
        }
        return baked.sanitize();
    }

    public static class Baked extends HashMap<ShapedRecipeSlot, Ingredient> {

        private int width = -1, height = -1;
        private NonNullList<Ingredient> rawIngredientList = null;

        private Baked(boolean doCut) {
            if(!doCut) {
                width = 3;
                height = 3;
            }
        }

        private Baked sanitize()  {
            calculateWidth();
            calculateHeight();
            if(width > 0 && height > 0 && (width < 3 || height < 3)) {
                while (tryShiftIngredients()) {}
            }
            return this;
        }

        private boolean tryShiftIngredients() {
            boolean needsShift = true;
            for (int y = 0; y < 3; y++) {
                Ingredient i = get(ShapedRecipeSlot.getByRowColumnIndex(0, y));
                if(i != Ingredient.EMPTY) {
                    needsShift = false;
                }
            }
            if(needsShift) { //Shift up
                for (int x = 1; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        ShapedRecipeSlot source = ShapedRecipeSlot.getByRowColumnIndex(x, y);
                        ShapedRecipeSlot to = ShapedRecipeSlot.getByRowColumnIndex(x - 1, y);
                        put(to, put(source, Ingredient.EMPTY));
                    }
                }
                return true;
            }
            needsShift = true;
            for (int x = 0; x < 3; x++) {
                Ingredient i = get(ShapedRecipeSlot.getByRowColumnIndex(x, 0));
                if(i != Ingredient.EMPTY) {
                    needsShift = false;
                }
            }
            if(needsShift) { //Shift left
                for (int y = 1; y < 3; y++) {
                    for (int x = 0; x < 3; x++) {
                        ShapedRecipeSlot source = ShapedRecipeSlot.getByRowColumnIndex(x, y);
                        ShapedRecipeSlot to = ShapedRecipeSlot.getByRowColumnIndex(x, y - 1);
                        put(to, put(source, Ingredient.EMPTY));
                    }
                }
                return true;
            }
            return false;
        }

        public NonNullList<Ingredient> getRawIngredientList() {
            if(rawIngredientList != null) {
                return rawIngredientList;
            }
            rawIngredientList = NonNullList.create();
            for (ShapedRecipeSlot srs : ShapedRecipeSlot.values()) {
                Ingredient i = get(srs);
                rawIngredientList.add(i);
            }
            return rawIngredientList;
        }

        private void calculateHeight() {
            if(height != -1) return;
            boolean up = false;
            for (int y = 0; y < 3; y++) {
                ShapedRecipeSlot srs = ShapedRecipeSlot.getByRowColumnIndex(0, y);
                if(get(srs) != Ingredient.EMPTY) {
                    up = true;
                }
            }
            boolean mid = false;
            for (int y = 0; y < 3; y++) {
                ShapedRecipeSlot srs = ShapedRecipeSlot.getByRowColumnIndex(1, y);
                if(get(srs) != Ingredient.EMPTY) {
                    mid = true;
                }
            }
            boolean down = false;
            for (int y = 0; y < 3; y++) {
                ShapedRecipeSlot srs = ShapedRecipeSlot.getByRowColumnIndex(2, y);
                if(get(srs) != Ingredient.EMPTY) {
                    down = true;
                }
            }
            if(up) {
                if(mid) {
                    if(down) {
                        height = 3;
                    } else {
                        height = 2;
                    }
                } else {
                    if(down) {
                        height = 3;
                    } else {
                        height = 1;
                    }
                }
            } else {
                if(mid) {
                    if(down) {
                        height = 2;
                    } else {
                        height = 1;
                    }
                } else {
                    if(down) {
                        height = 1;
                    } else {
                        height = 0;
                    }
                }
            }
        }

        private void calculateWidth() {
            if(width != -1) return;
            boolean left = false;
            for (int y = 0; y < 3; y++) {
                ShapedRecipeSlot srs = ShapedRecipeSlot.getByRowColumnIndex(y, 0);
                if(get(srs) != Ingredient.EMPTY) {
                    left = true;
                }
            }
            boolean mid = false;
            for (int y = 0; y < 3; y++) {
                ShapedRecipeSlot srs = ShapedRecipeSlot.getByRowColumnIndex(y, 1);
                if(get(srs) != Ingredient.EMPTY) {
                    mid = true;
                }
            }
            boolean right = false;
            for (int y = 0; y < 3; y++) {
                ShapedRecipeSlot srs = ShapedRecipeSlot.getByRowColumnIndex(y, 2);
                if(get(srs) != Ingredient.EMPTY) {
                    right = true;
                }
            }
            if(left) {
                if(mid) {
                    if(right) {
                        width = 3;
                    } else {
                        width = 2;
                    }
                } else {
                    if(right) {
                        width = 3;
                    } else {
                        width = 1;
                    }
                }
            } else {
                if(mid) {
                    if(right) {
                        width = 2;
                    } else {
                        width = 1;
                    }
                } else {
                    if(right) {
                        width = 1;
                    } else {
                        width = 0;
                    }
                }
            }
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

    }

}
