package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.ItemEntityPlacer;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryAchievements;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TelescopeRecipe
 * Created by HellFirePvP
 * Date: 26.09.2016 / 13:54
 */
public class TelescopeRecipe extends DiscoveryRecipe {

    public TelescopeRecipe() {
        super(new ShapedRecipe(ItemEntityPlacer.PlacerType.TELESCOPE.asStack())
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(Blocks.PLANKS,
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(Items.GOLD_INGOT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(Items.STICK,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT));
    }

    @Override
    public void onCraftServerFinish(TileAltar altar, Random rand) {
        EntityPlayer crafter = altar.getActiveCraftingTask().tryGetCraftingPlayerServer();
        if(crafter != null) {
            crafter.addStat(RegistryAchievements.achvBuildTelescope);
        }
        super.onCraftServerFinish(altar, rand);
    }

    @Override
    public boolean allowsForChaining() {
        return false;
    }

}
