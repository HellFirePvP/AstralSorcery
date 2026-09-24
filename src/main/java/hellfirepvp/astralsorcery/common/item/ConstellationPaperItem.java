/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.client.screen.ScreenConstellationPaper;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.common.component.ConstellationPaperComponent;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.entity.ItemEntityReplacement;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.item.base.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.ResearchMessageHelper;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ConstellationPaperItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ConstellationPaperItem extends ItemCustom implements ItemDynamicColor {

    public ConstellationPaperItem() {
        super(new Properties()
                .stacksTo(1)
                .component(DataComponentsAS.CONSTELLATION_PAPER, ConstellationPaperComponent.EMPTY));
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return CreativeTabsAS.CREATIVE_TAB_AS_PAPERS.get();
    }

    @Override
    public void fillCreativeTab(Consumer<ItemStack> tabItems) {
        tabItems.accept(new ItemStack(this));

        RegistriesAS.REGISTRY_CONSTELLATIONS.forEach(cst -> {
            ItemStack stack = new ItemStack(this);
            stack.set(DataComponentsAS.CONSTELLATION_PAPER, new ConstellationPaperComponent(cst));
            tabItems.accept(stack);
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        ConstellationPaperComponent cmp = stack.getOrDefault(DataComponentsAS.CONSTELLATION_PAPER, ConstellationPaperComponent.EMPTY);
        if (cmp.getConstellation().isEmpty()) {
            tooltipComponents.add(Component.translatable("item.astralsorcery.constellation_paper.empty")
                    .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (entity instanceof ServerPlayer sPlayer) {
            PlayerProgress progress = ResearchManager.getProgress(sPlayer, LogicalSide.SERVER);
            if (progress.isValid()) {
                ConstellationPaperComponent cmp = stack.getOrDefault(DataComponentsAS.CONSTELLATION_PAPER, ConstellationPaperComponent.EMPTY);
                if (cmp.getConstellation().isEmpty()) {
                    List<BaseConstellation> constellations = new ArrayList<>();
                    RegistriesAS.REGISTRY_CONSTELLATIONS.forEach(cst -> {
                        if (!progress.hasSeenConstellation(cst) && cst.canDiscover(sPlayer, progress)) {
                            constellations.add(cst);
                        }
                    });

                    MiscUtil.getRandomEntry(constellations, rand).ifPresent(cst -> {
                        stack.set(DataComponentsAS.CONSTELLATION_PAPER, new ConstellationPaperComponent(cst));
                    });
                }

                cmp.getConstellation().ifPresent(cst -> {
                    if (!progress.hasSeenConstellation(cst) && cst.canDiscover(sPlayer, progress)) {
                        if (ResearchHelper.memorizeConstellation(sPlayer, cst)) {
                            ResearchMessageHelper.sendConstellationMemorization(sPlayer, progress, cst);
                        }
                    }
                });
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack held = player.getItemInHand(usedHand);

        return held.getOrDefault(DataComponentsAS.CONSTELLATION_PAPER, ConstellationPaperComponent.EMPTY).getConstellation().map(cst -> {
            if (level.isClientSide()) {
                this.openConstellationScreen(cst);
                return InteractionResultHolder.success(held);
            }
            return InteractionResultHolder.pass(held);
        }).orElse(InteractionResultHolder.pass(held));
    }

    @OnlyIn(Dist.CLIENT)
    private void openConstellationScreen(BaseConstellation cst) {
        PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_PAGE).forUI().play();
        Minecraft.getInstance().setScreen(new ScreenConstellationPaper(cst));
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    @Nullable
    public Entity createEntity(Level level, Entity location, ItemStack stack) {
        if (location instanceof ItemEntity itemEntity) {
            ColorWrapper cstColor = stack.getOrDefault(DataComponentsAS.CONSTELLATION_PAPER, ConstellationPaperComponent.EMPTY)
                    .getConstellation()
                    .map(BaseConstellation::getConstellationColor)
                    .orElse(null);
            return ItemEntityReplacement.replace(EntitiesAS.ITEM_HIGHLIGHTED.get(), itemEntity)
                    .setColor(cstColor);
        }
        return super.createEntity(level, location, stack);
    }

    @Override
    public int getColor(ItemStack stack, long tick, int tintIndex) {
        if (tintIndex != 1) return 0xFFFFFFFF;

        ConstellationPaperComponent cmp = stack.getOrDefault(DataComponentsAS.CONSTELLATION_PAPER, ConstellationPaperComponent.EMPTY);
        return cmp.getConstellation()
                .map(BaseConstellation::getConstellationColor)
                .map(ColorWrapper::getColor)
                .map(color -> 0xFF000000 | color)
                .orElse(0xFF595959);
    }
}
