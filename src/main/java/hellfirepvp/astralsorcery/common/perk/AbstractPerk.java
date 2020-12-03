/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPerkTree;
import hellfirepvp.astralsorcery.common.data.research.PerkAllocationType;
import hellfirepvp.astralsorcery.common.data.research.PlayerPerkData;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.perk.source.ModifierManager;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.util.CacheEventBus;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractPerk
 * Created by HellFirePvP
 * Date: 02.06.2019 / 01:59
 */
public class AbstractPerk implements ModifierSource {

    protected static final Random rand = new Random();

    public static final PerkCategory CATEGORY_BASE = new PerkCategory("base", TextFormatting.WHITE);
    public static final PerkCategory CATEGORY_ROOT = new PerkCategory("root", TextFormatting.WHITE);
    public static final PerkCategory CATEGORY_MAJOR = new PerkCategory("major", TextFormatting.WHITE);
    public static final PerkCategory CATEGORY_KEY = new PerkCategory("key", TextFormatting.GOLD);
    public static final PerkCategory CATEGORY_EPIPHANY = new PerkCategory("epiphany", TextFormatting.GOLD);
    public static final PerkCategory CATEGORY_FOCUS = new PerkCategory("focus", TextFormatting.GOLD);

    private final ResourceLocation registryName;
    private final CacheEventBus busWrapper;
    protected final Point.Float offset;
    private String unlocalizedKey;
    private PerkCategory category = CATEGORY_BASE;
    private boolean hiddenUnlessAllocated = false;
    private PerkTreePoint<? extends AbstractPerk> treePoint = null;

    private ResourceLocation customPerkType = null;

    private List<IFormattableTextComponent> tooltipCache = null;
    private boolean cacheTooltip = true;

    public AbstractPerk(ResourceLocation name, float x, float y) {
        this.registryName = name;
        this.busWrapper = CacheEventBus.of(MinecraftForge.EVENT_BUS);
        this.offset = new Point.Float(x, y);
        this.unlocalizedKey = String.format("perk.%s.%s", name.getNamespace(), name.getPath());
    }

    protected PerkTreePoint<? extends AbstractPerk> initPerkTreePoint() {
        return new PerkTreePoint<>(this, this.getOffset());
    }

    protected void invalidate(LogicalSide side) {
        this.busWrapper.unregisterAll();
        PerkCooldownHelper.removePerkCooldowns(side, this);
    }

    protected void validate(LogicalSide side) {
        this.attachListeners(side, busWrapper);
    }

    protected void attachListeners(LogicalSide side, IEventBus bus) {}

    @Nonnull
    public Point.Float getOffset() {
        return offset;
    }

    public final PerkTreePoint<? extends AbstractPerk> getPoint() {
        if (treePoint == null) {
            treePoint = initPerkTreePoint();
        }
        return treePoint;
    }

    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

    public <T> T setCategory(PerkCategory category) {
        this.category = category;
        return (T) this;
    }

    public <T> T setHiddenUnlessAllocated(boolean hiddenUnlessAllocated) {
        this.hiddenUnlessAllocated = hiddenUnlessAllocated;
        return (T) this;
    }

    @Override
    public boolean canApplySource(PlayerEntity player, LogicalSide dist) {
        return !ResearchHelper.getProgress(player, dist).getPerkData().isPerkSealed(this);
    }

    @Override
    public final void onApply(PlayerEntity player, LogicalSide dist) {
        this.applyPerkLogic(player, dist);
    }

    @Override
    public final void onRemove(PlayerEntity player, LogicalSide dist) {
        this.removePerkLogic(player, dist);
    }

    protected void applyPerkLogic(PlayerEntity player, LogicalSide dist) {}

    protected void removePerkLogic(PlayerEntity player, LogicalSide dist) {}

    protected LogicalSide getSide(Entity entity) {
        return entity.getEntityWorld().isRemote() ? LogicalSide.CLIENT : LogicalSide.SERVER;
    }

    @Nullable
    public CompoundNBT getPerkData(PlayerEntity player, LogicalSide dist) {
        return ResearchHelper.getProgress(player, dist).getPerkData().getData(this);
    }

    /**
     * Called ONCE when the perk is unlocked
     * You may use the CompoundNBT to save data to remove it again later
     * The player might be null for root perks on occasion.
     */
    public void onUnlockPerkServer(@Nullable PlayerEntity player, PerkAllocationType allocationType, PlayerProgress progress, CompoundNBT dataStorage) {}

    /**
     * Clean up and remove the perk from that single player.
     * Data in the dataStorage is filled with the data set in onUnlockPerkServer
     * Called after the perk is already removed from the player, but still in the player's perkData
     */
    public void onRemovePerkServer(PlayerEntity player, PerkAllocationType allocationType, PlayerProgress progress, CompoundNBT dataStorage) {}

    public <T extends AbstractPerk> T setName(String name) {
        this.unlocalizedKey = name;
        return (T) this;
    }

    @Nonnull
    public PerkCategory getCategory() {
        return category;
    }

    public AllocationStatus getPerkStatus(@Nullable PlayerEntity player, LogicalSide side) {
        if (player == null) {
            return AllocationStatus.UNALLOCATED;
        }
        PlayerProgress progress = ResearchHelper.getProgress(player, side);
        if (!progress.isValid()) {
            return AllocationStatus.UNALLOCATED;
        }
        PlayerPerkData perkData = progress.getPerkData();
        if (perkData.hasPerkAllocation(this, PerkAllocationType.UNLOCKED)) {
            return AllocationStatus.ALLOCATED;
        }
        if (perkData.hasPerkAllocation(this)) {
            return AllocationStatus.GRANTED;
        }

        return mayUnlockPerk(progress, player) ? AllocationStatus.UNLOCKABLE : AllocationStatus.UNALLOCATED;
    }

    public boolean mayUnlockPerk(PlayerProgress progress, PlayerEntity player) {
        PlayerPerkData perkData = progress.getPerkData();
        if (!perkData.hasFreeAllocationPoint(player, getSide(player))) return false;

        for (AbstractPerk otherPerks : PerkTree.PERK_TREE.getConnectedPerks(getSide(player), this)) {
            if (perkData.hasPerkAllocation(otherPerks, PerkAllocationType.UNLOCKED)) {
                return true;
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isVisible(PlayerProgress progress, PlayerEntity player) {
        return !this.hiddenUnlessAllocated || progress.getPerkData().hasPerkAllocation(this);
    }

    public IFormattableTextComponent getName() {
        return new TranslationTextComponent(this.unlocalizedKey + ".name")
                .mergeStyle(this.getCategory().getTextFormatting());
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public Collection<IFormattableTextComponent> getDescription() {
        List<IFormattableTextComponent> toolTip = new ArrayList<>();
        if (I18n.hasKey(this.unlocalizedKey + ".desc.1")) { // Might have a indexed list there
            int count = 1;
            while (I18n.hasKey(this.unlocalizedKey + ".desc." + count)) {
                toolTip.add(new TranslationTextComponent(this.unlocalizedKey + ".desc." + count));
                count++;
            }
            toolTip.add(new StringTextComponent(""));
        } else if (I18n.hasKey(this.unlocalizedKey + ".desc")) {
            toolTip.add(new TranslationTextComponent(this.unlocalizedKey + ".desc"));
            toolTip.add(new StringTextComponent(""));
        }
        return toolTip;
    }

    protected void disableTooltipCaching() {
        this.cacheTooltip = false;
        this.tooltipCache = null;
    }

    @OnlyIn(Dist.CLIENT)
    public final Collection<IFormattableTextComponent> getLocalizedTooltip() {
        if (cacheTooltip && tooltipCache != null) {
            return tooltipCache;
        }
        tooltipCache = Lists.newArrayList();

        if (!(this instanceof ProgressGatedPerk) || ((ProgressGatedPerk) this).canSeeClient()) {
            tooltipCache.add(this.getName());

            int prevLength = tooltipCache.size();
            boolean shouldAdd = addLocalizedTooltip(tooltipCache);
            if (shouldAdd && prevLength != tooltipCache.size()) {
                tooltipCache.add(new StringTextComponent(""));
            }
            tooltipCache.addAll(this.getDescription());
        } else {
            tooltipCache.add(new TranslationTextComponent("perk.info.astralsorcery.missing_progress")
                    .mergeStyle(TextFormatting.RED));
        }
        return tooltipCache;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean addLocalizedTooltip(Collection<IFormattableTextComponent> tooltip) {
        return false;
    }

    //Should return a localized string of the mod (or part of a mod) that added this perk
    //Default: modname of added mod
    @Nullable
    @OnlyIn(Dist.CLIENT)
    public Collection<IFormattableTextComponent> getSource() {
        String modid = getRegistryName().getNamespace();
        ModContainer mod = ModList.get().getModContainerById(modid).orElse(null);
        if (mod != null) {
            return Lists.newArrayList(new StringTextComponent(mod.getModInfo().getDisplayName()));
        }
        return null;
    }

    public void clearCaches(LogicalSide side) {}

    @OnlyIn(Dist.CLIENT)
    public void clearClientTextCaches() {
        this.tooltipCache = null;
    }

    @Override
    public ResourceLocation getProviderName() {
        return ModifierManager.PERK_PROVIDER_KEY;
    }

    @Override
    public boolean isEqual(ModifierSource other) {
        return this.equals(other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractPerk)) return false;
        AbstractPerk that = (AbstractPerk) o;
        return Objects.equals(getRegistryName(), that.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRegistryName());
    }

    //Return true to prevent further, other interactions when left-clicking this perk
    @OnlyIn(Dist.CLIENT)
    public boolean handleMouseClick(ScreenJournalPerkTree gui, double mouseX, double mouseY) {
        return false;
    }

    /**
     * Deserialize data from Json to the perk.
     */
    public void deserializeData(JsonObject perkData) {}

    /**
     * Push the perk's custom additional data into the jsonObject given.
     */
    public void serializeData(JsonObject perkData) {}

    @Nullable
    public final ResourceLocation getCustomPerkType() {
        return customPerkType;
    }

    public final void setCustomPerkType(ResourceLocation customPerkType) {
        this.customPerkType = customPerkType;
    }

    public final JsonObject serializePerk() {
        JsonObject data = new JsonObject();

        data.addProperty("registry_name", this.getRegistryName().toString());
        if (this.getCustomPerkType() != null) {
            data.addProperty("perk_class", this.getCustomPerkType().toString());
        }
        data.addProperty("x", this.getOffset().x);
        data.addProperty("y", this.getOffset().y);
        data.addProperty("name", this.unlocalizedKey);
        data.addProperty("hiddenUnlessAllocated", this.hiddenUnlessAllocated);

        JsonObject perkData = new JsonObject();
        this.serializeData(perkData);
        data.add("data", perkData);
        return data;
    }

    public static class PerkCategory {

        private final IFormattableTextComponent name;
        private final TextFormatting color;

        public PerkCategory(@Nonnull String unlocName, @Nonnull TextFormatting color) {
            this.name = new TranslationTextComponent("perk.category.astralsorcery." + unlocName + ".name");
            this.color = color;
        }

        public TextFormatting getTextFormatting() {
            return color;
        }

        public IFormattableTextComponent getName() {
            return this.name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PerkCategory that = (PerkCategory) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
