/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot.global;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.data.config.registry.OreItemRarityRegistry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.perk.node.key.KeyVoidTrash;
import hellfirepvp.astralsorcery.common.util.loot.LootUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LootModifierPerkVoidTrash
 * Created by HellFirePvP
 * Date: 08.05.2020 / 19:55
 */
public class LootModifierPerkVoidTrash extends LootModifier {

    private LootModifierPerkVoidTrash(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if (!LootUtil.doesContextFulfillSet(context, LootParameterSets.BLOCK)) {
            return generatedLoot;
        }
        Entity e = context.get(LootParameters.THIS_ENTITY);
        if (!(e instanceof PlayerEntity)) {
            return generatedLoot;
        }
        PlayerEntity player = (PlayerEntity) e;
        PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!prog.isValid() || !prog.hasPerkEffect(perk -> perk instanceof KeyVoidTrash)) {
            return generatedLoot;
        }
        AbstractPerk registeredPerk = PerkTree.PERK_TREE.getPerk(perk -> perk instanceof KeyVoidTrash);
        if (registeredPerk == null) {
            return generatedLoot;
        }
        KeyVoidTrash voidTrashPerk = (KeyVoidTrash) registeredPerk;

        double chance = voidTrashPerk.getConfig().getOreChance() *
                PerkAttributeHelper.getOrCreateMap(player, LogicalSide.SERVER).getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT);

        return generatedLoot.stream()
                .filter(stack -> !stack.isEmpty())
                .map(result -> {
                    if (voidTrashPerk.getConfig().isTrash(result)) {
                        result = ItemStack.EMPTY;

                        if (context.getRandom().nextFloat() < chance) {
                            Item drop = OreItemRarityRegistry.VOID_TRASH_REWARD.getRandomItem(context.getRandom());
                            if (drop != null) {
                                result = new ItemStack(drop);
                            }
                        }
                    }
                    return result;
                })
                .filter(stack -> !stack.isEmpty())
                .collect(Collectors.toList());
    }

    public static class Serializer extends GlobalLootModifierSerializer<LootModifierPerkVoidTrash> {

        @Override
        public LootModifierPerkVoidTrash read(ResourceLocation location, JsonObject object, ILootCondition[] lootConditions) {
            return new LootModifierPerkVoidTrash(lootConditions);
        }
    }
}
