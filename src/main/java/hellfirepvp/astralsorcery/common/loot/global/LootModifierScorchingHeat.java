/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot.global;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.util.RecipeHelper;
import hellfirepvp.astralsorcery.common.util.loot.LootUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.fml.hooks.BasicEventHooks;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LootModifierScorchingHeat
 * Created by HellFirePvP
 * Date: 08.05.2020 / 19:17
 */
public class LootModifierScorchingHeat extends LootModifier {

    private LootModifierScorchingHeat(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if (!LootUtil.doesContextFulfillSet(context, LootParameterSets.BLOCK)) {
            return generatedLoot;
        }
        return generatedLoot.stream()
                .filter(stack -> !stack.isEmpty())
                .map(stack -> {
                    Optional<Tuple<ItemStack, Float>> furnaceResult = RecipeHelper.findSmeltingResult(context.getWorld(), stack);
                    if (context.has(LootParameters.THIS_ENTITY)) {
                        Entity e = context.get(LootParameters.THIS_ENTITY);
                        if (e instanceof PlayerEntity) {
                            furnaceResult.ifPresent(result -> BasicEventHooks.firePlayerSmeltedEvent((PlayerEntity) e, result.getA()));
                        }
                    }
                    furnaceResult.ifPresent(result -> {
                        ItemStack resultStack = result.getA();
                        float resultExp = result.getB();

                        ItemStack tool = context.get(LootParameters.TOOL);
                        if (!tool.isEmpty() && !(resultStack.getItem() instanceof BlockItem)) {
                            int silkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, tool);
                            if (silkTouch <= 0) {
                                int addedCount = 0;
                                int fortuneLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool);
                                if (fortuneLevel > 0) {
                                    addedCount = Math.max(context.getRandom().nextInt(fortuneLevel + 2) - 1, 0);
                                    resultStack.setCount(resultStack.getCount() * (addedCount + 1));
                                }

                                resultExp *= (addedCount + 1);
                                if (resultExp > 0) {
                                    int iExp = (int) resultExp;
                                    float partialExp = resultExp - iExp;
                                    if (partialExp > 0 && partialExp > context.getRandom().nextFloat()) {
                                        iExp += 1;
                                    }
                                    if (iExp >= 1) {
                                        Vector3d blockPos = context.get(LootParameters.field_237457_g_);
                                        if (blockPos != null) {
                                            ServerWorld world = context.getWorld();
                                            world.addEntity(new ExperienceOrbEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), iExp));
                                        }
                                    }
                                }
                            }
                        }
                    });
                    return furnaceResult.map(Tuple::getA).orElse(stack);
                })
                .collect(Collectors.toList());
    }

    public static class Serializer extends GlobalLootModifierSerializer<LootModifierScorchingHeat> {

        @Override
        public LootModifierScorchingHeat read(ResourceLocation location, JsonObject object, ILootCondition[] lootConditions) {
            return new LootModifierScorchingHeat(lootConditions);
        }

        @Override
        public JsonObject write(LootModifierScorchingHeat instance) {
            return this.makeConditions(instance.conditions);
        }
    }
}
