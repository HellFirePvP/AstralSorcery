package hellfirepvp.astralsorcery.common.crafting.nojson.starlight;

import hellfirepvp.astralsorcery.common.block.tile.BlockCelestialCrystalCluster;
import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.CrystalIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeItem;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.CrystalGenerator;
import hellfirepvp.astralsorcery.common.item.ItemStardust;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCrystalBase;
import hellfirepvp.astralsorcery.common.item.crystal.ItemRockCrystal;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileCelestialCrystals;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FormCelestialCrystalClusterRecipe
 * Created by HellFirePvP
 * Date: 01.10.2019 / 21:32
 */
public class FormCelestialCrystalClusterRecipe extends LiquidStarlightRecipe {

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Ingredient> getInputForRender() {
        return Arrays.asList(Ingredient.fromStacks(new ItemStack(ItemsAS.STARDUST)),
                new CrystalIngredient(false, false));
    }

    @Override
    public boolean doesStartRecipe(ItemStack item) {
        return item.getItem() instanceof ItemStardust;
    }

    @Override
    public boolean matches(ItemEntity trigger, World world, BlockPos at) {
        List<Entity> otherEntities = getEntitiesInBlock(world, at);
        otherEntities.remove(trigger);
        Optional<Entity> crystalEntity = otherEntities.stream()
                .filter(e -> e instanceof ItemEntity)
                .filter(e -> ((ItemEntity) e).getItem().getItem() instanceof ItemCrystalBase)
                .findFirst();
        return crystalEntity.isPresent() && otherEntities.size() == 1;
    }

    @Override
    public void doCraftTick(ItemEntity trigger, World world, BlockPos at) {
        Random r = new Random(MathHelper.getPositionRandom(at));
        if (!world.isRemote() && getAndIncrementCraftingTick(trigger) > 125 + r.nextInt(40)) {
            ItemStack crystalFound;
            if (consumeItemEntityInBlock(world, at, ItemsAS.STARDUST) != null &&
                    (crystalFound = consumeItemEntityInBlock(world, at, 1, stack -> stack.getItem() instanceof ItemCrystalBase)) != null) {

                if (world.setBlockState(at, BlocksAS.CELESTIAL_CRYSTAL_CLUSTER.getDefaultState())) {
                    TileCelestialCrystals cluster = MiscUtils.getTileAt(world, at, TileCelestialCrystals.class, true);
                    if (cluster != null) {
                        CrystalAttributes attr = ((CrystalAttributeItem) crystalFound.getItem()).getAttributes(crystalFound);
                        ItemStack targetCrystal = new ItemStack(ItemsAS.CELESTIAL_CRYSTAL);
                        ((CrystalAttributeItem) crystalFound.getItem()).setAttributes(targetCrystal, attr);
                        cluster.setAttributes(CrystalGenerator.upgradeProperties(targetCrystal));
                    }
                }
            }
        }
    }
}
