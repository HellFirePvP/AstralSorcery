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
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.data.config.entry.CraftingConfig;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InfusedWoodRecipe
 * Created by HellFirePvP
 * Date: 01.10.2019 / 20:48
 */
public class InfusedWoodRecipe extends LiquidStarlightRecipe {

    public InfusedWoodRecipe() {
        super(AstralSorcery.key("infused_wood"));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Ingredient> getInputForRender() {
        return Collections.singletonList(Ingredient.fromTag(ItemTags.LOGS));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Ingredient> getOutputForRender() {
        return Collections.singletonList(Ingredient.fromItems(BlocksAS.INFUSED_WOOD));
    }

    @Override
    public boolean doesStartRecipe(ItemStack item) {
        if (!CraftingConfig.CONFIG.liquidStarlightDropInfusedWood.get()) {
            return false;
        }
        return item.getItem().isIn(ItemTags.LOGS);
    }

    @Override
    public boolean matches(ItemEntity trigger, World world, BlockPos at) {
        return true;
    }

    @Override
    public void doServerCraftTick(ItemEntity trigger, World world, BlockPos at) {
        if (getAndIncrementCraftingTick(trigger) > 10) {
            if (consumeItemEntityInBlock(world, at, 1, (ItemStack stack) -> !stack.isEmpty() && stack.getItem().isIn(ItemTags.LOGS)) != null) {
                ItemUtils.dropItemNaturally(world, trigger.getPosX(), trigger.getPosY(), trigger.getPosZ(), new ItemStack(BlocksAS.INFUSED_WOOD));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void doClientEffectTick(ItemEntity trigger, World world, BlockPos at) {
        for (int i = 0; i < 4; i++) {
            Vector3 pos = new Vector3(at).add(0.5, 0.5, 0.5);
            MiscUtils.applyRandomOffset(pos, rand, 0.5F);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .color(VFXColorFunction.constant(ColorsAS.DYE_BROWN))
                    .alpha(VFXAlphaFunction.PYRAMID)
                    .setScaleMultiplier(0.1F + rand.nextFloat() * 0.1F)
                    .setMaxAge(30 + rand.nextInt(20));
        }
    }
}
