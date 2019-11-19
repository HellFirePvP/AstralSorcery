/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.gui.GuiJournalPerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.APIRegistryEvent;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
 * Date: 30.06.2018 / 11:40
 */
public abstract class AbstractPerk {

    protected static final Random rand = new Random();

    public static final PerkCategory CATEGORY_BASE = new PerkCategory("base", TextFormatting.WHITE.toString());
    public static final PerkCategory CATEGORY_ROOT = new PerkCategory("root", TextFormatting.WHITE.toString());
    public static final PerkCategory CATEGORY_MAJOR = new PerkCategory("major", TextFormatting.WHITE.toString());
    public static final PerkCategory CATEGORY_KEY = new PerkCategory("key", TextFormatting.GOLD.toString());
    public static final PerkCategory CATEGORY_EPIPHANY = new PerkCategory("epiphany", TextFormatting.GOLD.toString());
    public static final PerkCategory CATEGORY_FOCUS = new PerkCategory("focus", TextFormatting.GOLD.toString());

    private final ResourceLocation registryName;
    protected final Point offset;
    private PerkCategory category = CATEGORY_BASE;
    private List<String> tooltipCache = null;
    private boolean cacheTooltip = true;
    protected String ovrUnlocalizedNamePrefix = null;
    private PerkTreePoint<? extends AbstractPerk> treePoint = null;

    public AbstractPerk(String name, int x, int y) {
        this.registryName = new ResourceLocation(AstralSorcery.MODID, name.toLowerCase());
        this.offset = new Point(x, y);
    }

    public AbstractPerk(ResourceLocation name, int x, int y) {
        this.registryName = name;
        this.offset = new Point(x, y);
    }

    protected PerkTreePoint<? extends AbstractPerk> initPerkTreePoint() {
        return new PerkTreePoint<>(this, this.getOffset());
    }

    public ResourceLocation getRegistryName() {
        return registryName;
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

    @Optional.Method(modid = "crafttweaker")
    public final void adjustMultipliers() {
        double multiplier = hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks.PerkTree.getMultiplier(this);
        applyEffectMultiplier(multiplier);
    }

    protected void applyEffectMultiplier(double multiplier) {}

    //Return true to display that the perk's modifiers got disabled by pack's configurations
    public boolean modifiersDisabled(EntityPlayer player, Side side) {
        APIRegistryEvent.PerkDisable event = new APIRegistryEvent.PerkDisable(this, player, side);
        MinecraftForge.EVENT_BUS.post(event);
        return event.isPerkDisabled();
    }

    //Reserving application/removal methods to delegate for later pre-application logic
    final void applyPerk(EntityPlayer player, Side side) {
        if (modifiersDisabled(player, side)) {
            return;
        }

        this.applyPerkLogic(player, side);
        if (PerkAttributeHelper.getOrCreateMap(player, side).markPerkApplied(this)) {
            LogCategory.PERKS.info(() -> "Cache: " + this.getRegistryName() + " applied!");
        }
    }

    final void removePerk(EntityPlayer player, Side side) {
        if (modifiersDisabled(player, side)) {
            return;
        }

        this.removePerkLogic(player, side);
        if (PerkAttributeHelper.getOrCreateMap(player, side).markPerkRemoved(this)) {
            LogCategory.PERKS.info(() -> "Cache: " + this.getRegistryName() + " removed!");
        }
    }

    protected abstract void applyPerkLogic(EntityPlayer player, Side side);

    protected abstract void removePerkLogic(EntityPlayer player, Side side);

    @Nullable
    public NBTTagCompound getPerkData(EntityPlayer player, Side side) {
        return ResearchManager.getProgress(player, side).getPerkData(this);
    }

    /**
     * Called when the perk is in any way modified in regards to its 'contents' for a specific player e.g. gems
     * Called AFTER the perk has been re-applied with the new data.
     */
    public void modifyPerkServer(EntityPlayer player, PlayerProgress progress, NBTTagCompound dataStorage) {}

    /**
     * Called ONCE when the perk is unlocked
     * You may use the NBTTagCompound to save data to remove it again later
     * The player might be null for root perks on occasion.
     */
    public void onUnlockPerkServer(@Nullable EntityPlayer player, PlayerProgress progress, NBTTagCompound dataStorage) {}

    /**
     * Clean up and remove the perk from that single player.
     * Data in the dataStorage is filled with the data set in onUnlockPerkServer
     * Called after the perk is already removed from the player
     */
    public void onRemovePerkServer(EntityPlayer player, PlayerProgress progress, NBTTagCompound dataStorage) {}

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

    public PerkTreePoint.AllocationStatus getPerkStatus(@Nullable EntityPlayer player, Side side) {
        if (player == null) {
            return PerkTreePoint.AllocationStatus.UNALLOCATED;
        }
        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (!progress.isValid()) {
            return PerkTreePoint.AllocationStatus.UNALLOCATED;
        }
        if (progress.hasPerkUnlocked(this)) {
            return PerkTreePoint.AllocationStatus.ALLOCATED;
        }

        return mayUnlockPerk(progress, player) ? PerkTreePoint.AllocationStatus.UNLOCKABLE : PerkTreePoint.AllocationStatus.UNALLOCATED;
    }

    public boolean mayUnlockPerk(PlayerProgress progress, EntityPlayer player) {
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
        return "perk." + getRegistryName().getResourceDomain() + "." + getRegistryName().getResourcePath();
    }

    protected void disableTooltipCaching() {
        this.cacheTooltip = false;
        this.tooltipCache = null;
    }

    @SideOnly(Side.CLIENT)
    public final Collection<String> getLocalizedTooltip() {
        if (cacheTooltip && tooltipCache != null) {
            return tooltipCache;
        }

        tooltipCache = Lists.newArrayList();
        String key = this.ovrUnlocalizedNamePrefix;
        if (modifiersDisabled(Minecraft.getMinecraft().player, Side.CLIENT)) {
            tooltipCache.add(TextFormatting.GRAY + I18n.format("perk.info.disabled"));
        } else if (!(this instanceof ProgressGatedPerk) || ((ProgressGatedPerk) this).canSeeClient()) {
            tooltipCache.add(this.getCategory().getTextFormatting() + I18n.format(this.getUnlocalizedName() + ".name"));

            if (key == null) {
                key = "perk." + getRegistryName().getResourceDomain() + "." + getRegistryName().getResourcePath();
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

    @SideOnly(Side.CLIENT)
    public boolean addLocalizedTooltip(Collection<String> tooltip) {
        return false;
    }

    //Should return a localized string of the mod (or part of a mod) that added this perk
    //Default: modname of added mod
    @Nullable
    @SideOnly(Side.CLIENT)
    public Collection<String> getSource() {
        String modid = getRegistryName().getResourceDomain();
        ModContainer mod = Loader.instance().getIndexedModList().get(modid);
        if (mod != null) {
            return Lists.newArrayList(mod.getName());
        }
        return null;
    }

    public void clearCaches(Side side) {}

    @SideOnly(Side.CLIENT)
    public void clearClientCaches() {
        this.tooltipCache = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof AbstractPerk)) return false;
        AbstractPerk that = (AbstractPerk) o;
        return Objects.equals(getRegistryName(), that.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRegistryName());
    }

    // Return true to prevent further, other interactions when left-clicking this perk
    @SideOnly(Side.CLIENT)
    public boolean handleMouseClick(GuiJournalPerkTree gui, int mouseX, int mouseY) {
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
        @SideOnly(Side.CLIENT)
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
