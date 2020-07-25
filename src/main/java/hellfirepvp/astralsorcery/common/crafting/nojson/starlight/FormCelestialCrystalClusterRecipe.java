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
import hellfirepvp.astralsorcery.client.effect.function.VFXMotionController;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.CrystalIngredient;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeItem;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.CrystalGenerator;
import hellfirepvp.astralsorcery.common.data.config.entry.CraftingConfig;
import hellfirepvp.astralsorcery.common.item.ItemStardust;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCrystalBase;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileCelestialCrystals;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
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
 * Class: FormCelestialCrystalClusterRecipe
 * Created by HellFirePvP
 * Date: 01.10.2019 / 21:32
 */
public class FormCelestialCrystalClusterRecipe extends LiquidStarlightRecipe {

    public FormCelestialCrystalClusterRecipe() {
        super(AstralSorcery.key("form_celestial_crystal_cluster"));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Ingredient> getInputForRender() {
        return Arrays.asList(Ingredient.fromStacks(new ItemStack(ItemsAS.STARDUST)),
                new CrystalIngredient(false, false));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Ingredient> getOutputForRender() {
        return Collections.singletonList(Ingredient.fromStacks(new ItemStack(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER)));
    }

    @Override
    public boolean doesStartRecipe(ItemStack item) {
        if (!CraftingConfig.CONFIG.liquidStarlightFormCelestialCrystalCluster.get()) {
            return false;
        }
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
    public void doServerCraftTick(ItemEntity trigger, World world, BlockPos at) {
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void doClientEffectTick(ItemEntity trigger, World world, BlockPos at) {
        for (int i = 0; i < 3; i++) {
            Vector3 pos = Vector3.atEntityCenter(trigger);
            MiscUtils.applyRandomOffset(pos, rand, 0.15F);

            Vector3 motion = Vector3.RotAxis.Y_AXIS.clone();
            motion.rotate(Math.toRadians(10 + rand.nextInt(20)), Vector3.RotAxis.X_AXIS)
                .rotate(rand.nextFloat() * Math.PI * 2, Vector3.RotAxis.Y_AXIS)
                .normalize().multiply(0.07F + rand.nextFloat() * 0.04F);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .setMotion(motion)
                    .setScaleMultiplier(0.05F + rand.nextFloat() * 0.2F)
                    .setMaxAge(30 + rand.nextInt(20));
        }
        for (int i = 0; i < 4; i++) {
            Vector3 target = Vector3.atEntityCenter(trigger);
            Vector3 pos = target.clone().add(Vector3.random().normalize().multiply(3 + rand.nextFloat()));

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .alpha(VFXAlphaFunction.PYRAMID.andThen(VFXAlphaFunction.proximity(target::clone, 2)))
                    .motion(VFXMotionController.target(target::clone, 0.1F))
                    .setScaleMultiplier(0.15F + rand.nextFloat() * 0.1F)
                    .setMaxAge(20 + rand.nextInt(20));
        }
    }
}
