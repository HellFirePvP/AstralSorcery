/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPerkTree;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.ASRegistryEvents;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractPerk
 * Created by HellFirePvP
 * Date: 02.06.2019 / 01:59
 */
public abstract class AbstractPerk extends ForgeRegistryEntry<AbstractPerk> {

    protected static final Random rand = new Random();

    public static final PerkCategory CATEGORY_BASE = new PerkCategory("base", TextFormatting.WHITE.toString());
    public static final PerkCategory CATEGORY_ROOT = new PerkCategory("root", TextFormatting.WHITE.toString());
    public static final PerkCategory CATEGORY_MAJOR = new PerkCategory("major", TextFormatting.WHITE.toString());
    public static final PerkCategory CATEGORY_KEY = new PerkCategory("key", TextFormatting.GOLD.toString());
    public static final PerkCategory CATEGORY_EPIPHANY = new PerkCategory("epiphany", TextFormatting.GOLD.toString());
    public static final PerkCategory CATEGORY_FOCUS = new PerkCategory("focus", TextFormatting.GOLD.toString());

    protected final Point offset;
    private PerkCategory category = CATEGORY_BASE;
    private List<String> tooltipCache = null;
    private boolean cacheTooltip = true;
    protected String ovrUnlocalizedNamePrefix = null;
    private PerkTreePoint<? extends AbstractPerk> treePoint = null;

    public AbstractPerk(ResourceLocation name, int x, int y) {
        this.setRegistryName(name);
        this.offset = new Point(x, y);
    }

    protected PerkTreePoint<? extends AbstractPerk> initPerkTreePoint() {
        return new PerkTreePoint<>(this, this.getOffset());
    }

    public Point getOffset() {
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
    //    double multiplier = hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks.PerkTree.getMultiplier(this);
    //    applyEffectMultiplier(multiplier);
    //}

    protected void applyEffectMultiplier(double multiplier) {}

    //Return true to display that the perk's modifiers got disabled by pack's configurations
    public boolean modifiersDisabled(PlayerEntity player, Dist dist) {
        ASRegistryEvents.PerkDisable event = new ASRegistryEvents.PerkDisable(this, player, dist);
        MinecraftForge.EVENT_BUS.post(event);
        return event.isPerkDisabled();
    }

    //Reserving application/removal methods to delegate for later pre-application logic
    final void applyPerk(PlayerEntity player, Dist dist) {
        if (modifiersDisabled(player, dist)) {
            return;
        }

        this.applyPerkLogic(player, dist);
        // TODO perks
        //if (PerkAttributeHelper.getOrCreateMap(player, dist).markPerkApplied(this)) {
        //    LogCategory.PERKS.info(() -> "Cache: " + this.getRegistryName() + " applied!");
        //}
    }

    final void removePerk(PlayerEntity player, Dist dist) {
        if (modifiersDisabled(player, dist)) {
            return;
        }

        this.removePerkLogic(player, dist);
        // TODO perks
        //if (PerkAttributeHelper.getOrCreateMap(player, dist).markPerkRemoved(this)) {
        //    LogCategory.PERKS.info(() -> "Cache: " + this.getRegistryName() + " removed!");
        //}
    }

    protected abstract void applyPerkLogic(PlayerEntity player, Dist dist);

    protected abstract void removePerkLogic(PlayerEntity player, Dist dist);

    @Nullable
    public CompoundNBT getPerkData(PlayerEntity player, Dist dist) {
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

    public <T> T setNameOverride(AbstractPerk other) {
        return setNameOverride(other.getUnlocalizedName());
    }

    public <T> T setNameOverride(String namePrefix) {
        this.ovrUnlocalizedNamePrefix = namePrefix;
        return (T) this;
    }

    @Nonnull
    public PerkCategory getCategory() {
        return category;
    }

    public AllocationStatus getPerkStatus(@Nullable PlayerEntity player, Dist dist) {
        if (player == null) {
            return AllocationStatus.UNALLOCATED;
        }
        PlayerProgress progress = ResearchHelper.getProgress(player, dist);
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

    public String getUnlocalizedName() {
        if (this.ovrUnlocalizedNamePrefix != null) {
            return this.ovrUnlocalizedNamePrefix;
        }
        return "perk." + getRegistryName().getNamespace() + "." + getRegistryName().getPath();
    }

    protected void disableTooltipCaching() {
        this.cacheTooltip = false;
        this.tooltipCache = null;
    }

    @OnlyIn(Dist.CLIENT)
    public final Collection<String> getLocalizedTooltip() {
        if (cacheTooltip && tooltipCache != null) {
            return tooltipCache;
        }

        tooltipCache = Lists.newArrayList();
        String key = this.ovrUnlocalizedNamePrefix;
        if (modifiersDisabled(Minecraft.getInstance().player, Dist.CLIENT)) {
            tooltipCache.add(TextFormatting.GRAY + I18n.format("perk.info.disabled"));
        } else if (!(this instanceof ProgressGatedPerk) || ((ProgressGatedPerk) this).canSeeClient()) {
            tooltipCache.add(this.getCategory().getTextFormatting() + I18n.format(this.getUnlocalizedName() + ".name"));

            if (key == null) {
                key = "perk." + getRegistryName().getNamespace() + "." + getRegistryName().getPath();
            }
            int prevLength = tooltipCache.size();
            boolean shouldAdd = addLocalizedTooltip(tooltipCache);
            if (shouldAdd && prevLength != tooltipCache.size()) {
                tooltipCache.add(""); //Add empty line..
            }
            if (I18n.hasKey(key + ".desc.1")) { // Might have a indexed list there
                int count = 1;
                while (I18n.hasKey(key + ".desc." + count)) {
                    tooltipCache.add(I18n.format(key + ".desc." + count));
                    count++;
                }
                tooltipCache.add("");
            } else if (I18n.hasKey(key + ".desc")) {
                tooltipCache.add(I18n.format(key + ".desc"));
                tooltipCache.add("");
            }
        } else {
            tooltipCache.add(TextFormatting.RED + I18n.format("perk.info.missing_progress"));
        }
        return tooltipCache;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean addLocalizedTooltip(Collection<String> tooltip) {
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

    public void clearCaches(Dist dist) {}

    //TODO client gui
    @OnlyIn(Dist.CLIENT)
    public void clearClientCaches() {
        this.tooltipCache = null;
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
        private String textFormatting;

        public PerkCategory(@Nonnull String unlocName, @Nonnull String formattingPrefix) {
            this.unlocName = unlocName;
            this.textFormatting = formattingPrefix;
        }

        public String getUnlocalizedName() {
            return unlocName;
        }

        public String getTextFormatting() {
            return textFormatting;
        }

        @Nullable
        @OnlyIn(Dist.CLIENT)
        public String getLocalizedName() {
            String str = "perk.category." + unlocName + ".name";
            if (I18n.hasKey(str)) {
                return I18n.format("perk.category." + unlocName + ".name");
            }
            return null;
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
