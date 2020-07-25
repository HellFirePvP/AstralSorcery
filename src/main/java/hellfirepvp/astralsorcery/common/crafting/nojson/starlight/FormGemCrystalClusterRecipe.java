/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson.starlight;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXMotionController;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.CrystalIngredient;
import hellfirepvp.astralsorcery.common.data.config.entry.CraftingConfig;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCrystalBase;
import hellfirepvp.astralsorcery.common.item.dust.ItemIlluminationPowder;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FormGemCrystalClusterRecipe
 * Created by HellFirePvP
 * Date: 16.11.2019 / 13:12
 */
public class FormGemCrystalClusterRecipe extends LiquidStarlightRecipe {

    public FormGemCrystalClusterRecipe() {
        super(AstralSorcery.key("form_gem_crystal_cluster"));
    }

    @Override
    public List<Ingredient> getInputForRender() {
        return Arrays.asList(Ingredient.fromStacks(new ItemStack(ItemsAS.ILLUMINATION_POWDER)),
                new CrystalIngredient(false, false));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Ingredient> getOutputForRender() {
        return Collections.singletonList(Ingredient.fromStacks(new ItemStack(BlocksAS.GEM_CRYSTAL_CLUSTER)));
    }

    @Override
    public boolean doesStartRecipe(ItemStack item) {
        if (!CraftingConfig.CONFIG.liquidStarlightFormGemCrystalCluster.get()) {
            return false;
        }
        return item.getItem() instanceof ItemIlluminationPowder;
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
    public void doServerCraftTick(ItemEntity trigger, World world, BlockPos at) {
        Random r = new Random(MathHelper.getPositionRandom(at));
        if (getAndIncrementCraftingTick(trigger) > 125 + r.nextInt(40)) {
            if (consumeItemEntityInBlock(world, at, ItemsAS.ILLUMINATION_POWDER) != null &&
                    consumeItemEntityInBlock(world, at, 1, stack -> stack.getItem() instanceof ItemCrystalBase) != null) {

                world.setBlockState(at, BlocksAS.GEM_CRYSTAL_CLUSTER.getDefaultState());
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void doClientEffectTick(ItemEntity trigger, World world, BlockPos at) {
        for (int i = 0; i < 4; i++) {
            Vector3 target = Vector3.atEntityCenter(trigger);
            Vector3 pos = target.clone().add(Vector3.random().normalize().multiply(3 + rand.nextFloat()));

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .color(VFXColorFunction.constant(ColorsAS.ILLUMINATION_POWDER_2))
                    .alpha(VFXAlphaFunction.PYRAMID.andThen(VFXAlphaFunction.proximity(target::clone, 2F)))
                    .motion(VFXMotionController.target(target::clone, 0.09F))
                    .setScaleMultiplier(0.25F + rand.nextFloat() * 0.2F)
                    .setMaxAge(20 + rand.nextInt(20));
        }
    }
}
