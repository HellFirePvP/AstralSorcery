/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPerkTree;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.ASRegistryEvents;
import hellfirepvp.astralsorcery.common.perk.source.ModifierManager;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractPerk
 * Created by HellFirePvP
 * Date: 02.06.2019 / 01:59
 */
public abstract class AbstractPerk extends ForgeRegistryEntry<AbstractPerk> implements ModifierSource {

    protected static final Random rand = new Random();

    public static final PerkCategory CATEGORY_BASE = new PerkCategory("base", TextFormatting.WHITE);
    public static final PerkCategory CATEGORY_ROOT = new PerkCategory("root", TextFormatting.WHITE);
    public static final PerkCategory CATEGORY_MAJOR = new PerkCategory("major", TextFormatting.WHITE);
    public static final PerkCategory CATEGORY_KEY = new PerkCategory("key", TextFormatting.GOLD);
    public static final PerkCategory CATEGORY_EPIPHANY = new PerkCategory("epiphany", TextFormatting.GOLD);
    public static final PerkCategory CATEGORY_FOCUS = new PerkCategory("focus", TextFormatting.GOLD);

    protected final Point.Float offset;
    private String unlocalizedKey;
    private PerkCategory category = CATEGORY_BASE;
    private PerkTreePoint<? extends AbstractPerk> treePoint = null;

    private List<ITextComponent> tooltipCache = null;
    private boolean cacheTooltip = true;
    private float cacheEffectMultiplier = 1.0F;

    public AbstractPerk(ResourceLocation name, int x, int y) {
        this.setRegistryName(name);
        this.offset = new Point.Float(x, y);
        this.attachListeners(MinecraftForge.EVENT_BUS);
        this.unlocalizedKey = String.format("perk.%s.%s", name.getNamespace(), name.getPath());
    }

    protected PerkTreePoint<? extends AbstractPerk> initPerkTreePoint() {
        return new PerkTreePoint<>(this, this.getOffset());
    }

    protected void attachListeners(IEventBus bus) {}

    @Nullable
    protected ConfigEntry addConfig() {
        return null;
    }

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

    public <T> T setCategory(PerkCategory category) {
        this.category = category;
        return (T) this;
    }

    //TODO crafttweaker?
    //@Optional.Method(modid = "crafttweaker")
    //public final void adjustMultipliers() {
    //    double multiplier = hellfirepvp.astralsorcery.common.integration.mods.crafttweaker.tweaks.PerkTree.getMultiplier(this);
    //    applyEffectMultiplier(multiplier);
    //}

    protected void applyEffectMultiplier(float multiplier) {
        this.cacheEffectMultiplier = multiplier;
    }

    protected int applyMultiplierI(double val) {
        return MathHelper.floor(val * this.cacheEffectMultiplier);
    }

    protected double applyMultiplierD(double val) {
        return val * this.cacheEffectMultiplier;
    }

    //Return true to display that the perk's modifiers got disabled by pack's configurations
    public boolean modifiersDisabled(PlayerEntity player, LogicalSide dist) {
        ASRegistryEvents.PerkDisable event = new ASRegistryEvents.PerkDisable(this, player, dist);
        MinecraftForge.EVENT_BUS.post(event);
        return event.isPerkDisabled();
    }

    @Override
    public boolean canApplySource(PlayerEntity player, LogicalSide dist) {
        return !ResearchHelper.getProgress(player, dist).isPerkSealed(this);
    }

    @Override
    public final void onApply(PlayerEntity player, LogicalSide dist) {
        if (modifiersDisabled(player, dist)) {
            return;
        }

        this.applyPerkLogic(player, dist);
    }

    @Override
    public final void onRemove(PlayerEntity player, LogicalSide dist) {
        if (modifiersDisabled(player, dist)) {
            return;
        }

        this.removePerkLogic(player, dist);
    }

    protected void applyPerkLogic(PlayerEntity player, LogicalSide dist) {}

    protected void removePerkLogic(PlayerEntity player, LogicalSide dist) {}

    protected LogicalSide getSide(Entity entity) {
        return entity.getEntityWorld().isRemote() ? LogicalSide.CLIENT : LogicalSide.SERVER;
    }

    @Nullable
    public CompoundNBT getPerkData(PlayerEntity player, LogicalSide dist) {
        return ResearchHelper.getProgress(player, dist).getPerkData(this);
    }

    /**
     * Called when the perk is in any way modified in regards to its 'contents' for a specific player e.g. gems
     * Called AFTER the perk has been re-applied with the new data.
     */
    public void modifyPerkServer(PlayerEntity player, PlayerProgress progress, CompoundNBT dataStorage) {}

    /**
     * Called ONCE when the perk is unlocked
     * You may use the CompoundNBT to save data to remove it again later
     * The player might be null for root perks on occasion.
     */
    public void onUnlockPerkServer(@Nullable PlayerEntity player, PlayerProgress progress, CompoundNBT dataStorage) {}

    /**
     * Clean up and remove the perk from that single player.
     * Data in the dataStorage is filled with the data set in onUnlockPerkServer
     * Called after the perk is already removed from the player
     */
    public void onRemovePerkServer(PlayerEntity player, PlayerProgress progress, CompoundNBT dataStorage) {}

    public <T extends AbstractPerk> T setName(AbstractPerk other) {
        return setName(other.unlocalizedKey);
    }

    public <T extends AbstractPerk> T setName(String namePrefix) {
        this.unlocalizedKey = namePrefix;
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
        if (progress.hasPerkUnlocked(this)) {
            return AllocationStatus.ALLOCATED;
        }

        return mayUnlockPerk(progress, player) ? AllocationStatus.UNLOCKABLE : AllocationStatus.UNALLOCATED;
    }

    public boolean mayUnlockPerk(PlayerProgress progress, PlayerEntity player) {
        if (!progress.hasFreeAllocationPoint(player)) return false;

        for (AbstractPerk otherPerks : PerkTree.PERK_TREE.getConnectedPerks(this)) {
            if (progress.hasPerkUnlocked(otherPerks)) {
                return true;
            }
        }
        return false;
    }

    public ITextComponent getName() {
        return new TranslationTextComponent(this.unlocalizedKey + ".name")
                .applyTextStyle(this.getCategory().getTextFormatting());
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public Collection<ITextComponent> getDescription() {
        List<ITextComponent> toolTip = new ArrayList<>();
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
    public final Collection<ITextComponent> getLocalizedTooltip() {
        if (cacheTooltip && tooltipCache != null) {
            return tooltipCache;
        }

        tooltipCache = Lists.newArrayList();
        if (modifiersDisabled(Minecraft.getInstance().player, LogicalSide.CLIENT)) {
            tooltipCache.add(new TranslationTextComponent("perk.info.astralsorcery.disabled")
                    .setStyle(new Style().setColor(TextFormatting.GRAY)));
        } else if (!(this instanceof ProgressGatedPerk) || ((ProgressGatedPerk) this).canSeeClient()) {
            tooltipCache.add(this.getName());

            int prevLength = tooltipCache.size();
            boolean shouldAdd = addLocalizedTooltip(tooltipCache);
            if (shouldAdd && prevLength != tooltipCache.size()) {
                tooltipCache.add(new StringTextComponent(""));
            }
            tooltipCache.addAll(this.getDescription());
        } else {
            tooltipCache.add(new TranslationTextComponent("perk.info.astralsorcery.missing_progress")
                    .setStyle(new Style().setColor(TextFormatting.RED)));
        }
        return tooltipCache;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean addLocalizedTooltip(Collection<ITextComponent> tooltip) {
        return false;
    }

    //Should return a localized string of the mod (or part of a mod) that added this perk
    //Default: modname of added mod
    @Nullable
    @OnlyIn(Dist.CLIENT)
    public Collection<ITextComponent> getSource() {
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

    public static class PerkCategory {

        private final String unlocName;
        private TextFormatting color;

        public PerkCategory(@Nonnull String unlocName, @Nonnull TextFormatting color) {
            this.unlocName = unlocName;
            this.color = color;
        }

        public TextFormatting getTextFormatting() {
            return color;
        }

        public String getUnlocalizedName() {
            return "perk.category.astralsorcery." + unlocName + ".name";
        }

        @OnlyIn(Dist.CLIENT)
        public String getLocalizedName() {
            return I18n.format(getUnlocalizedName());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PerkCategory that = (PerkCategory) o;
            return Objects.equals(unlocName, that.unlocName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(unlocName);
        }

    }

}
