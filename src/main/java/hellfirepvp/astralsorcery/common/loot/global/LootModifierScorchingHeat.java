/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
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
                    Optional<ItemStack> furnaceResult = RecipeHelper.findSmeltingResult(context.getWorld(), stack);
                    if (context.has(LootParameters.THIS_ENTITY)) {
                        Entity e = context.get(LootParameters.THIS_ENTITY);
                        if (e instanceof PlayerEntity) {
                            furnaceResult.ifPresent(result -> BasicEventHooks.firePlayerSmeltedEvent((PlayerEntity) e, result));
                        }
                    }
                    furnaceResult.ifPresent(result -> {
                        ItemStack tool = context.get(LootParameters.TOOL);
                        if (!tool.isEmpty() && !(result.getItem() instanceof BlockItem)) {
                            int fortuneLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool);
                            if (fortuneLevel > 0) {
                                int addedCount = context.getRandom().nextInt(fortuneLevel + 2) - 1;
                                if (addedCount < 0) {
                                    addedCount = 0;
                                }
                                result.setCount(result.getCount() * (addedCount + 1));
                            }
                        }
                    });
                    return furnaceResult.orElse(stack);
                })
                .collect(Collectors.toList());
    }

    public static class Serializer extends GlobalLootModifierSerializer<LootModifierScorchingHeat> {

        @Override
        public LootModifierScorchingHeat read(ResourceLocation location, JsonObject object, ILootCondition[] lootConditions) {
            return new LootModifierScorchingHeat(lootConditions);
        }
    }
}
