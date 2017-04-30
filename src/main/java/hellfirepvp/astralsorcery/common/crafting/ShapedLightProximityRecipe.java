/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting;

import hellfirepvp.astralsorcery.common.data.DataLightBlockEndpoints;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShapedLightProximityRecipe
 * Created by HellFirePvP
 * Date: 02.08.2016 / 22:57
 */
public class ShapedLightProximityRecipe implements IRecipe {

    public static BlockPos clientWorkbenchPosition = null;

    public static final int MAX_CRAFT_GRID_WIDTH = 3;
    public static final int MAX_CRAFT_GRID_HEIGHT = 3;
    protected ItemStack output = null;
    protected Object[] input = null;
    protected int width = 0;
    protected int height = 0;
    protected boolean mirrored = true;

    public ShapedLightProximityRecipe(Block result, Object... recipe) {
        this(new ItemStack(result), recipe);
    }

    public ShapedLightProximityRecipe(Item result, Object... recipe) {
        this(new ItemStack(result), recipe);
    }

    public ShapedLightProximityRecipe(ItemStack result, Object... recipe) {
        output = result.copy();

        String shape = "";
        int idx = 0;

        if (recipe[idx] instanceof Boolean) {
            mirrored = (Boolean) recipe[idx];
            if (recipe[idx + 1] instanceof Object[]) {
                recipe = (Object[]) recipe[idx + 1];
            } else {
                idx = 1;
            }
        }

        if (recipe[idx] instanceof String[]) {
            String[] parts = ((String[]) recipe[idx++]);

            for (String s : parts) {
                width = s.length();
                shape += s;
            }

            height = parts.length;
        } else {
            while (recipe[idx] instanceof String) {
                String s = (String) recipe[idx++];
                shape += s;
                width = s.length();
                height++;
            }
        }

        if (width * height != shape.length()) {
            String ret = "Invalid shaped ore recipe: ";
            for (Object tmp : recipe) {
                ret += tmp + ", ";
            }
            ret += output;
            throw new RuntimeException(ret);
        }

        HashMap<Character, Object> itemMap = new HashMap<>();

        for (; idx < recipe.length; idx += 2) {
            Character chr = (Character) recipe[idx];
            Object in = recipe[idx + 1];

            if (in instanceof ItemStack) {
                itemMap.put(chr, ((ItemStack) in).copy());
            } else if (in instanceof Item) {
                itemMap.put(chr, new ItemStack((Item) in));
            } else if (in instanceof Block) {
                itemMap.put(chr, new ItemStack((Block) in, 1, OreDictionary.WILDCARD_VALUE));
            } else if (in instanceof String) {
                itemMap.put(chr, OreDictionary.getOres((String) in));
            /*
             * ADDED CLAUSE TO ALLOW FOR MULTIPLE ITEMSTACK DEFINITIONS
             */
            } else if (in instanceof List) {
                itemMap.put(chr, in);
            } else {
                String ret = "Invalid shaped ore recipe: ";
                for (Object tmp : recipe) {
                    ret += tmp + ", ";
                }
                ret += output;
                throw new RuntimeException(ret);
            }
        }

        input = new Object[width * height];
        int x = 0;
        for (char chr : shape.toCharArray()) {
            input[x++] = itemMap.get(chr);
        }
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1) {
        return output.copy();
    }

    @Override
    public int getRecipeSize() {
        return input.length;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        if(!vanillaMatch(inv, world)) return false;

        Container c = inv.eventHandler;
        if (!(c instanceof ContainerWorkbench)) return false;
        ContainerWorkbench workbench = (ContainerWorkbench) c;
        BlockPos pos = workbench.pos;
        if (pos == null) return false;
        if (world.isRemote) {
            GuiScreen sc = Minecraft.getMinecraft().currentScreen;
            if (sc == null || !(sc instanceof GuiCrafting) || clientWorkbenchPosition == null) return false;
            if (!((DataLightBlockEndpoints) SyncDataHolder.getDataClient(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS))
                    .doesPositionReceiveStarlightClient(world, clientWorkbenchPosition))
                return false;
        } else {
            if (!((DataLightBlockEndpoints) SyncDataHolder.getDataServer(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS))
                    .doesPositionReceiveStarlightServer(world, pos))
                return false;
        }
        return true;
    }

    private boolean vanillaMatch(InventoryCrafting inv, World world) {
        for (int x = 0; x <= MAX_CRAFT_GRID_WIDTH - width; x++) {
            for (int y = 0; y <= MAX_CRAFT_GRID_HEIGHT - height; ++y) {
                if (checkMatch(inv, x, y, false)) {
                    return true;
                }

                if (mirrored && checkMatch(inv, x, y, true)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
        for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++) {
            for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++) {
                int subX = x - startX;
                int subY = y - startY;
                Object target = null;

                if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
                    if (mirror) {
                        target = input[width - subX - 1 + subY * width];
                    } else {
                        target = input[subX + subY * width];
                    }
                }

                ItemStack slot = inv.getStackInRowAndColumn(x, y);

                if (target instanceof ItemStack) {
                    if (!OreDictionary.itemMatches((ItemStack) target, slot, false)) {
                        return false;
                    }
                } else if (target instanceof List) {
                    boolean matched = false;

                    Iterator<ItemStack> itr = ((List<ItemStack>) target).iterator();
                    while (itr.hasNext() && !matched) {
                        matched = OreDictionary.itemMatches(itr.next(), slot, false);
                    }

                    if (!matched) {
                        return false;
                    }
                } else if (target == null && slot != null) {
                    return false;
                }
            }
        }

        return true;
    }

    public Object[] getInput() {
        return this.input;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }

}
