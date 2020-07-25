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
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.data.config.entry.CraftingConfig;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCrystalBase;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MergeCrystalsRecipe
 * Created by HellFirePvP
 * Date: 29.05.2020 / 20:54
 */
public class MergeCrystalsRecipe extends LiquidStarlightRecipe {

    public MergeCrystalsRecipe() {
        super(AstralSorcery.key("merge_crystals"));
    }

    @Override
    public List<Ingredient> getInputForRender() {
        return Arrays.asList(new CrystalIngredient(false, false),
                new CrystalIngredient(false, false));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Ingredient> getOutputForRender() {
        return Collections.singletonList(new CrystalIngredient(false, false));
    }

    @Override
    public boolean doesStartRecipe(ItemStack item) {
        if (!CraftingConfig.CONFIG.liquidStarlightDropInfusedWood.get()) {
            return false;
        }
        return !item.isEmpty() && item.getItem() instanceof ItemCrystalBase;
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
        if (!world.isRemote() && getAndIncrementCraftingTick(trigger) > 100 + r.nextInt(40)) {
            ItemStack crystalFoundOne, crystalFoundTwo;
            if ((crystalFoundOne = consumeItemEntityInBlock(world, at, 1, stack -> stack.getItem() instanceof ItemCrystalBase)) != null &&
                    (crystalFoundTwo = consumeItemEntityInBlock(world, at, 1, stack -> stack.getItem() instanceof ItemCrystalBase)) != null &&
                    world.setBlockState(at, Blocks.AIR.getDefaultState(), Constants.BlockFlags.DEFAULT_AND_RERENDER)) {

                ItemCrystalBase crystalOne = (ItemCrystalBase) crystalFoundOne.getItem();
                CrystalAttributes attrOne = crystalOne.getAttributes(crystalFoundOne);
                attrOne = attrOne != null ? attrOne : CrystalAttributes.Builder.newBuilder(false).build();

                ItemCrystalBase crystalTwo = (ItemCrystalBase) crystalFoundTwo.getItem();
                CrystalAttributes attrTwo = crystalTwo.getAttributes(crystalFoundTwo);
                attrTwo = attrTwo != null ? attrTwo : CrystalAttributes.Builder.newBuilder(false).build();

                CrystalAttributes mergeTo = attrOne.getTotalTierLevel() >= attrTwo.getTotalTierLevel() ? attrOne : attrOne;
                CrystalAttributes mergeFrom = attrOne.getTotalTierLevel() >= attrTwo.getTotalTierLevel() ? attrTwo : attrOne;

                ItemStack resultStack = attrOne.getTotalTierLevel() >= attrTwo.getTotalTierLevel() ? crystalFoundOne.copy() : crystalFoundTwo.copy();
                ItemCrystalBase resultCrystal = (ItemCrystalBase) resultStack.getItem();
                CrystalAttributes.Builder resultBuilder = CrystalAttributes.Builder.newBuilder(false).addAll(mergeTo);

                int freeProperties = resultCrystal.getMaxPropertyTiers() - mergeTo.getTotalTierLevel();
                int copyAmount = Math.min(freeProperties, mergeFrom.getTotalTierLevel());
                int mergeCount = 0;
                for (int i = 0; i < copyAmount; i++) {
                    CrystalAttributes.Attribute attr = MiscUtils.getWeightedRandomEntry(mergeFrom.getCrystalAttributes(), rand, CrystalAttributes.Attribute::getTier);
                    if (attr != null) {
                        mergeFrom = mergeFrom.modifyLevel(attr.getProperty(), -1);
                        if (rand.nextFloat() <= (1F - Math.min(mergeCount, 3) * 0.25F)) {
                            resultBuilder.addProperty(attr.getProperty(), 1);
                        }
                        mergeCount++;
                    }
                }

                resultCrystal.setAttributes(resultStack, resultBuilder.build());
                ItemUtils.dropItemNaturally(world, trigger.getPosX(), trigger.getPosY(), trigger.getPosZ(), resultStack);
            }
        }
    }

    @Override
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
                    .setScaleMultiplier(0.1F + rand.nextFloat() * 0.2F)
                    .color(VFXColorFunction.WHITE)
                    .setMaxAge(35 + rand.nextInt(20));
        }
        for (int i = 0; i < 4; i++) {
            Vector3 target = Vector3.atEntityCenter(trigger);
            Vector3 pos = target.clone().add(Vector3.random().normalize().multiply(3 + rand.nextFloat()));

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .alpha(VFXAlphaFunction.PYRAMID.andThen(VFXAlphaFunction.proximity(target::clone, 2)))
                    .motion(VFXMotionController.target(target::clone, 0.1F))
                    .setScaleMultiplier(0.15F + rand.nextFloat() * 0.1F)
                    .color(VFXColorFunction.WHITE)
                    .setMaxAge(20 + rand.nextInt(20));
        }
    }
}
