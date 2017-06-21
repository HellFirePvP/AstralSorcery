/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
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

    public Baked bake() {
        Baked baked = new Baked();
        for (ShapedRecipeSlot srs : ShapedRecipeSlot.values()) {
            ItemHandle handle = get(srs);
            if(handle != null) {
                baked.put(srs, handle.getRecipeIngredient());
            } else {
                baked.put(srs, Ingredient.EMPTY);
            }
        }
        return baked;
    }

    public static class Baked extends HashMap<ShapedRecipeSlot, Ingredient> {

        private int width = -1, height = -1;
        private NonNullList<Ingredient> rawIngredientList = null;

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

        public int getWidth() {
            if(width != -1) {
                return width;
            }
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
            return width;
        }

        public int getHeight() {
            if(height != -1) {
                return height;
            }
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
            return height;
        }

    }

}
