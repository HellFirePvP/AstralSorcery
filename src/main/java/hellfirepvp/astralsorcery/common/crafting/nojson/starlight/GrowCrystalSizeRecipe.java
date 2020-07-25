/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson.starlight;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.CrystalIngredient;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.data.config.entry.CraftingConfig;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedRockCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCrystalBase;
import hellfirepvp.astralsorcery.common.item.crystal.ItemRockCrystal;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
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

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GrowCrystalSizeRecipe
 * Created by HellFirePvP
 * Date: 05.05.2020 / 17:15
 */
public class GrowCrystalSizeRecipe extends LiquidStarlightRecipe {

    public GrowCrystalSizeRecipe() {
        super(AstralSorcery.key("crystal_grow"));
    }

    @Override
    public List<Ingredient> getInputForRender() {
        return Collections.singletonList(new CrystalIngredient(false, false));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Ingredient> getOutputForRender() {
        return Collections.singletonList(new CrystalIngredient(false, false));
    }

    @Override
    public boolean doesStartRecipe(ItemStack item) {
        if (!CraftingConfig.CONFIG.liquidStarlightCrystalGrowth.get()) {
            return false;
        }
        return !item.isEmpty() && item.getItem() instanceof ItemCrystalBase;
    }

    @Override
    public boolean matches(ItemEntity trigger, World world, BlockPos at) {
        List<Entity> otherEntities = getEntitiesInBlock(world, at);
        otherEntities.remove(trigger);
        return otherEntities.isEmpty();
    }

    @Override
    public void doServerCraftTick(ItemEntity trigger, World world, BlockPos at) {
        Random r = new Random(MathHelper.getPositionRandom(at));
        if (!world.isRemote() && getAndIncrementCraftingTick(trigger) > 200 + r.nextInt(40)) {
            ItemStack stack = trigger.getItem();
            CrystalAttributes attr = ((ItemCrystalBase) stack.getItem()).getAttributes(stack);
            if (attr != null && world.setBlockState(at, Blocks.AIR.getDefaultState())) {
                attr = attr.modifyLevel(CrystalPropertiesAS.Properties.PROPERTY_SIZE, 1);
                ((ItemCrystalBase) stack.getItem()).setAttributes(stack, attr);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void doClientEffectTick(ItemEntity trigger, World world, BlockPos at) {
        Color c = ColorsAS.DEFAULT_GENERIC_PARTICLE;
        if (trigger.getItem().getItem() instanceof ItemRockCrystal ||
                trigger.getItem().getItem() instanceof ItemAttunedRockCrystal) {
            c = ColorsAS.ROCK_CRYSTAL;
        }
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
                    .color(VFXColorFunction.constant(c))
                    .setMaxAge(30 + rand.nextInt(20));
        }
    }
}
