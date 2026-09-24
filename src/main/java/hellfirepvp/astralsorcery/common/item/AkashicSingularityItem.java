/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.EnumExtensions;
import hellfirepvp.astralsorcery.common.component.StoredItemsComponent;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AkashicSingularityItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AkashicSingularityItem extends ItemCustom {

    public AkashicSingularityItem() {
        super(new Properties()
                .stacksTo(1)
                .rarity(EnumExtensions.RARITY_RELIC.getValue())
                .component(DataComponentsAS.STORED_ITEMS, StoredItemsComponent.EMPTY));
    }

    @Override
    public void fillCreativeTab(Consumer<ItemStack> tabItems) {}

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level instanceof ServerLevel sLevel) {
            this.removeAndDrop(sLevel, player, usedHand, stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel() instanceof ServerLevel sLevel) {
            this.removeAndDrop(sLevel, context.getPlayer(), context.getHand(), context.getItemInHand());
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
    }

    private void removeAndDrop(ServerLevel sLevel, Player player, InteractionHand usedHand, ItemStack stack) {
        StoredItemsComponent storedItems = stack.getOrDefault(DataComponentsAS.STORED_ITEMS, StoredItemsComponent.EMPTY);

        Tuple<StoredItemsComponent, List<ItemStack>> decomposeResult = storedItems.decomposeStackCount(25);
        decomposeResult.getB().forEach(result -> {
            ItemUtil.dropItemNaturally(sLevel, player.position(), result).ifPresent(droppedItem -> {
                droppedItem.setThrower(player);
            });
        });

        storedItems = decomposeResult.getA();
        stack.set(DataComponentsAS.STORED_ITEMS, storedItems);
        if (storedItems.isEmpty()) {
            player.setItemInHand(usedHand, ItemStack.EMPTY);
        }
    }
}
