/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.item.base.CauldronInteractableItem;
import hellfirepvp.astralsorcery.common.item.base.CreativeTabItem;
import hellfirepvp.astralsorcery.common.item.base.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StardewItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StardewItem extends PotionItem implements ItemDynamicColor, CreativeTabItem, CauldronInteractableItem {

    public StardewItem() {
        super(new Properties()
                .setNoRepair()
                .durability(4)
                .component(DataComponents.UNBREAKABLE, new Unbreakable(false)));
    }

    @Override
    public void fillCreativeTab(Consumer<ItemStack> tabItems) {
        tabItems.accept(new ItemStack(this));

        tabItems.accept(this.getDefaultInstance());
    }

    public static ItemStack create(List<MobEffectInstance> effects) {
        ItemStack stack = ItemsAS.STARDEW.toStack();
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), effects));
        return stack;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        Player player = entity instanceof Player ? (Player) entity : null;
        if (player instanceof ServerPlayer sPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(sPlayer, stack);
        }

        if (!level.isClientSide()) {
            PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            contents.forEachEffect(effect -> {
                if (effect.getEffect().value().isInstantenous()) {
                    effect.getEffect().value().applyInstantenousEffect(player, player, entity, effect.getAmplifier(), 1);
                } else {
                    entity.addEffect(effect);
                }
            });
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        if (!entity.hasInfiniteMaterials()) {
            stack.setDamageValue(Math.min(stack.getDamageValue() + 1, stack.getMaxDamage()));
        }

        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            stack.remove(DataComponents.POTION_CONTENTS);
            stack.set(DataComponents.RARITY, ItemsAS.STARDEW.asItem().getDefaultInstance().getRarity());
        }

        entity.gameEvent(GameEvent.DRINK);
        return stack;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getCount() > 1) return InteractionResultHolder.pass(stack);
        if (!isWater(stack) && isEmpty(stack)) {
            return InteractionResultHolder.pass(stack);
        }
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        if (isWater(stack)) return this.getDescriptionId() + ".water";
        if (isEmpty(stack)) return this.getDescriptionId() + ".empty";
        return this.getDescriptionId();
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return isWater(stack) || isEmpty(stack) ? 16 : super.getMaxStackSize(stack);
    }

    public static boolean isWater(ItemStack stack) {
        return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER);
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack.getDamageValue() >= stack.getMaxDamage() ||
                !stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).hasEffects();
    }

    @Override
    public int getColor(ItemStack stack, long tick, int tintIndex) {
        if (tintIndex != 0) {
            return 0xFFFFFFFF;
        }
        int color = PotionContents.getColor(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getAllEffects());
        return color | 0xFF000000;
    }

    @Override
    public CauldronInteraction.InteractionMap getInteractionMap() {
        return CauldronInteraction.WATER;
    }

    @Override
    public CauldronInteraction getInteraction() {
        return (state, level, pos, player, hand, stack) -> {
            if (isWater(stack)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (!isEmpty(stack)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (!level.isClientSide()) {
                player.setItemInHand(hand, this.getDefaultInstance());
                player.awardStat(Stats.USE_CAULDRON);
                LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        };
    }
}
