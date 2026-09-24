/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.perk.socket;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.component.DynamicModifiersComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.DynamicModifierHelper;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.*;
import hellfirepvp.astralsorcery.common.perk.tree.perk.root.RootPerkAevitas;
import hellfirepvp.astralsorcery.common.perk.tree.point.GemSocketPerkTreePoint;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerPerkData;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocationType;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GemSocketPerk
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GemSocketPerk extends AttributeModifierPerk<GemSocketPerk.Data> {

    public static final MapCodec<GemSocketPerk> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, GemSocketPerk::new));
    public static final PerkType<GemSocketPerk> TYPE =
            PerkType.of(GemSocketPerk.CODEC, PerkDataTypesAS.GEM_SOCKET_DATA, GemSocketPerk::new);

    private GemSocketPerk(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.DEFAULT, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected GemSocketPerk(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
        this.disableTooltipCaching();
    }

    @Override
    protected PerkTreePoint<?> initPerkTreePoint() {
        return new GemSocketPerkTreePoint<>(this.getOffset(), this);
    }

    @Override
    public void onRemovePerkServer(ServerPlayer player, PerkAllocationType allocation, PlayerProgress progress, Data data) {
        super.onRemovePerkServer(player, allocation, progress, data);

        if (progress.getPerkData().getAllocationTypes(this).size() <= 1) {
            this.dropGemStack(player, data);
        }
    }

    @Override
    public Collection<PerkAttributeModifier> getModifiers(Player player, LogicalSide side, boolean ignoreRequirements) {
        List<PerkAttributeModifier> modifiers = new ArrayList<>(super.getModifiers(player, side, ignoreRequirements));
        if (!ignoreRequirements && ResearchManager.getProgress(player, side).getPerkData().isPerkSealed(this)) {
            return modifiers;
        }

        PlayerProgress progress = ResearchManager.getProgress(player, side);
        ItemStack contained = this.getGemStack(progress);
        if (!contained.isEmpty()) {
            if (contained.getItem() instanceof GemSocketItem gemSocketItem) {
                modifiers.addAll(gemSocketItem.getModifiers(contained, this, player, side));
            } else {
                modifiers.addAll(contained.getOrDefault(DataComponentsAS.DYNAMIC_MODIFIERS, DynamicModifiersComponent.EMPTY).modifiers());
            }
        }
        return modifiers;
    }

    @Override
    protected boolean addTooltip(Collection<MutableComponent> tooltip, PlayerProgress progress, @Nullable Player player, LogicalSide side) {
        boolean addLine = super.addTooltip(tooltip, progress, player, side);
        if (!this.canSee(progress)) {
            return addLine;
        }
        PlayerPerkData perkData = progress.getPerkData();
        ItemStack contained = this.getGemStack(progress);
        if (!contained.isEmpty()) {
            tooltip.add(Component.translatable("perk.info.astralsorcery.gem.content.item", contained.getHoverName())
                    .withStyle(ChatFormatting.GRAY));

            if (contained.getItem() instanceof GemSocketItem gemSocketItem) {
                List<MutableComponent> additionalToolTip = new ArrayList<>();
                gemSocketItem.addTooltip(contained, this, additionalToolTip);
                if (!additionalToolTip.isEmpty()) {
                    tooltip.addAll(additionalToolTip);
                    tooltip.add(Component.empty());
                }
            }

            if (perkData.hasPerkEffect(this)) {
                tooltip.add(Component.translatable("perk.info.astralsorcery.gem.remove").withStyle(ChatFormatting.GRAY));
            }
        } else {
            tooltip.add(Component.translatable("perk.info.astralsorcery.gem.empty").withStyle(ChatFormatting.GRAY));
            if (perkData.hasPerkEffect(this) && player != null) {
                tooltip.add(Component.translatable("perk.info.astralsorcery.gem.content.empty").withStyle(ChatFormatting.GRAY));

                boolean hasSocketableItems = !ItemUtil.findItemsInInventory(player, stack -> {
                    return this.canSocketItem(stack, player, progress, side);
                }).isEmpty();
                if (!hasSocketableItems) {
                    tooltip.add(Component.translatable("perk.info.astralsorcery.gem.content.empty.none")
                            .withStyle(ChatFormatting.RED));
                }
            }
        }

        return true;
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }

    public boolean hasGemStack(PlayerProgress progress) {
        return progress.getPerkData().getPerkData(this)
                .map(data -> !data.getGemStack().isEmpty())
                .orElse(false);
    }

    public ItemStack getGemStack(PlayerProgress progress) {
        return progress.getPerkData().getPerkData(this)
                .map(Data::getGemStack)
                .orElse(ItemStack.EMPTY);
    }

    public void dropGemStack(ServerPlayer sPlayer) {
        ResearchManager.getProgress(sPlayer, LogicalSide.SERVER).getPerkData().getPerkData(this).ifPresent(data -> {
            this.dropGemStack(sPlayer, data);
        });
    }

    public void dropGemStack(ServerPlayer sPlayer, Data gemSocketData) {
        ItemStack stack = gemSocketData.getGemStack();
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof GemSocketItem gemSocketItem) {
                gemSocketItem.onExtract(stack, this, sPlayer, ResearchManager.getProgress(sPlayer, LogicalSide.SERVER));
            }
            if (!sPlayer.addItem(stack)) {
                ItemUtil.dropItem(sPlayer.serverLevel(), sPlayer.getX(), sPlayer.getY(), sPlayer.getZ(), stack);
            }
        }
        Data newGemData = gemSocketData.copy();
        newGemData.setGemStack(ItemStack.EMPTY);

        ResearchHelper.updatePerkData(sPlayer, this, gemSocketData, newGemData);
    }

    public boolean setGemStack(ServerPlayer sPlayer, ItemStack stack) {
        return ResearchManager.getProgress(sPlayer, LogicalSide.SERVER).getPerkData().getPerkData(this).map(data -> {
            return this.setGemStack(sPlayer, stack, data);
        }).orElse(false);
    }

    public boolean setGemStack(ServerPlayer sPlayer, ItemStack stack, Data gemSocketData) {
        PlayerProgress progress = ResearchManager.getProgress(sPlayer, LogicalSide.SERVER);
        if (!progress.getPerkData().hasPerkEffect(this)) return false;

        boolean updateData = false;
        Data newGemData = gemSocketData.copy();

        ItemStack socketedItem = newGemData.getGemStack();
        if (!socketedItem.isEmpty()) {
            if (socketedItem.getItem() instanceof GemSocketItem gemSocketItem) {
                gemSocketItem.onExtract(socketedItem, this, sPlayer, progress);
            }
            if (!sPlayer.addItem(socketedItem)) {
                ItemUtil.dropItem(sPlayer.serverLevel(), sPlayer.getX(), sPlayer.getY(), sPlayer.getZ(), socketedItem);
            }
            newGemData.setGemStack(ItemStack.EMPTY);
            updateData = true;
        }
        if (!stack.isEmpty()) {
            if (this.canSocketItem(stack, sPlayer, progress, LogicalSide.SERVER)) {
                if (stack.getItem() instanceof GemSocketItem gemSocketItem) {
                    gemSocketItem.onInsert(stack, this, sPlayer, progress);
                }
                newGemData.setGemStack(stack);
                updateData = true;
            }
        }

        if (updateData) {
            ResearchHelper.updatePerkData(sPlayer, this, gemSocketData, newGemData);
        }
        return true;
    }

    public boolean canSocketItem(ItemStack stack, Player player, PlayerProgress progress, LogicalSide side) {
        if (stack.isEmpty()) return false;
        if (!stack.is(TagsAS.Items.FUNCTIONAL_PERKTREE_SOCKETABLE_ITEM)) return false;
        if (stack.getItem() instanceof GemSocketItem gemSocketItem) {
            return gemSocketItem.canInsert(stack, this, player, progress, side);
        }
        return true;
    }

    public static class Data extends AbstractPerk.Data {

        protected static <T extends Data> Products.P2<RecordCodecBuilder.Mu<T>, Boolean, ItemStack> gemDataFields(RecordCodecBuilder.Instance<T> instance) {
            return dataFields(instance).and(
                    CodecUtil.defaulted(ItemStack.OPTIONAL_CODEC, "gemStack", () -> ItemStack.EMPTY, Data::getGemStack)
            );
        }

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(inst -> gemDataFields(inst).apply(inst, Data::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> SYNC_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                Data::isSealed,
                ItemStack.OPTIONAL_STREAM_CODEC,
                Data::getGemStack,
                Data::new
        );

        protected ItemStack gemStack;

        protected Data(boolean sealed, ItemStack gemStack) {
            super(sealed);
            this.gemStack = gemStack;
        }

        public <T extends AbstractPerk.Data> T copy() {
            return MiscUtil.cast(new Data(this.isSealed(), this.gemStack.copy()));
        }

        public static Data create() {
            return new Data(false, ItemStack.EMPTY);
        }

        @Override
        public PerkDataType<?> getType() {
            return PerkDataTypesAS.GEM_SOCKET_DATA.get();
        }

        protected void setGemStack(ItemStack gemStack) {
            this.gemStack = gemStack;
        }

        protected ItemStack getGemStack() {
            return this.gemStack;
        }
    }
}
