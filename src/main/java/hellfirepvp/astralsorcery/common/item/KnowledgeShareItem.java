/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.component.StoredPlayerProgressComponent;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KnowledgeShareItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KnowledgeShareItem extends ItemCustom {

    public KnowledgeShareItem() {
        super(new Properties()
                .component(DataComponentsAS.STORED_PLAYER_PROGRESS, StoredPlayerProgressComponent.EMPTY)
                .stacksTo(1));
    }

    @Override
    public void fillCreativeTab(Consumer<ItemStack> tabItems) {
        tabItems.accept(new ItemStack(this));

        ItemStack creativeStack = new ItemStack(this);
        creativeStack.set(DataComponentsAS.STORED_PLAYER_PROGRESS, StoredPlayerProgressComponent.CREATIVE);
        tabItems.accept(creativeStack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (stack.isEmpty() || level.isClientSide() || !(player instanceof ServerPlayer sPlayer)) {
            return InteractionResultHolder.success(stack);
        }
        this.handleInteract(sPlayer, stack);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (stack.isEmpty() || context.getLevel().isClientSide() || !(context.getPlayer() instanceof ServerPlayer sPlayer)) {
            return InteractionResult.SUCCESS;
        }
        this.handleInteract(sPlayer, stack);
        return InteractionResult.SUCCESS;
    }

    protected void handleInteract(ServerPlayer sPlayer, ItemStack stack) {
        StoredPlayerProgressComponent stored = stack.getOrDefault(DataComponentsAS.STORED_PLAYER_PROGRESS, StoredPlayerProgressComponent.EMPTY);
        if ((stored.creative() || stored.progress().isPresent()) &&
                (stored.owningPlayer().isEmpty() || !stored.owningPlayer().get().getId().equals(sPlayer.getUUID()))) {
            giveKnowledge(sPlayer, stored);
        } else if (sPlayer.isShiftKeyDown() && stored.isEmpty() &&
                (stored.owningPlayer().isEmpty() || stored.owningPlayer().get().getId().equals(sPlayer.getUUID()))) {
            storeKnowledge(sPlayer, stack);
        }
    }

    private void giveKnowledge(ServerPlayer sPlayer, StoredPlayerProgressComponent stored) {
        if (stored.creative()) {
            ResearchHelper.maximizeAll(sPlayer);
            return;
        }

        stored.progress().ifPresent(progress -> {
            ResearchHelper.acceptKnowledgeShare(sPlayer, progress);
        });
    }

    private void storeKnowledge(ServerPlayer sPlayer, ItemStack stack) {
        PlayerProgress progress = ResearchManager.getProgress(sPlayer, LogicalSide.SERVER);

        //Copy shareable progress into a new blank progress
        PlayerProgress storedProgress = PlayerProgress.blankProgress();
        storedProgress.mergeKnowledgeShare(progress);

        StoredPlayerProgressComponent cmp = new StoredPlayerProgressComponent(
                Optional.of(storedProgress),
                Optional.of(sPlayer.getGameProfile()),
                false);
        stack.set(DataComponentsAS.STORED_PLAYER_PROGRESS, cmp);
    }
}
