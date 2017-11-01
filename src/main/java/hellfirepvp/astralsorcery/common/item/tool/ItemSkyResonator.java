/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.render.INBTModel;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemSkyResonator
 * Created by HellFirePvP
 * Date: 17.01.2017 / 00:53
 */
public class ItemSkyResonator extends Item implements INBTModel {

    public ItemSkyResonator() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(isInCreativeTab(tab)) {
            items.add(new ItemStack(this));

            ItemStack enhanced = new ItemStack(this);
            setEnhanced(enhanced);
            items.add(enhanced);

            enhanced = new ItemStack(this);
            setEnhanced(enhanced);
            for (ResonatorUpgrade upgrade : ResonatorUpgrade.values()) {
                setUpgradeUnlocked(enhanced, upgrade);
            }
            items.add(enhanced);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if(!isEnhanced(stack)) return;

        ResonatorUpgrade current = getCurrentUpgrade(null, stack);
        for (ResonatorUpgrade upgrade : getUpgrades(stack)) {
            tooltip.add((upgrade == current ? TextFormatting.AQUA : TextFormatting.BLUE) + I18n.format(upgrade.getUnlocalizedUpgradeName()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if(!isEnhanced(stack)) {
            return super.getUnlocalizedName(stack);
        }
        return getCurrentUpgrade(null, stack).getUnlocalizedName();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote && player.isSneaking()) {
            cycleUpgrade(player, player.getHeldItem(hand));
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
        if(!worldIn.isRemote && player.isSneaking()) {
            cycleUpgrade(player, player.getHeldItem(hand));
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack, ModelResourceLocation defaultModelPath) {
        if(!isEnhanced(stack)) {
            return defaultModelPath;
        }
        String path = defaultModelPath.getResourcePath() + "_upgraded";
        ResonatorUpgrade upgrade = getCurrentUpgrade(getCurrentClientPlayer(), stack);
        path += "_" + upgrade.appendixUpgrade;
        return new ModelResourceLocation(new ResourceLocation(defaultModelPath.getResourceDomain(), path), defaultModelPath.getVariant());
    }

    @Override
    public List<ResourceLocation> getAllPossibleLocations(ModelResourceLocation defaultLocation) {
        List<ResourceLocation> out = new LinkedList<>();
        out.add(defaultLocation);
        for (ResonatorUpgrade upgrade : ResonatorUpgrade.values()) {
            out.add(new ResourceLocation(defaultLocation.getResourceDomain(),
                    defaultLocation.getResourcePath() + "_upgraded_" + upgrade.appendixUpgrade));
        }
        return out;
    }

    public static boolean isEnhanced(ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator)) return false;
        return NBTHelper.getPersistentData(stack).getBoolean("enhanced");
    }

    public static void setEnhanced(ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator)) return;
        NBTHelper.getPersistentData(stack).setBoolean("enhanced", true);
        setUpgradeUnlocked(stack, ResonatorUpgrade.STARLIGHT);
    }

    public static boolean cycleUpgrade(@Nonnull EntityPlayer player, ItemStack stack) {
        if(!isEnhanced(stack)) return false;
        ResonatorUpgrade current = getCurrentUpgrade(player, stack);
        ResonatorUpgrade next = getNextSelectableUpgrade(player, stack);
        return next != null && next != current && setCurrentUpgrade(player, stack, next);
    }

    @Nullable
    public static ResonatorUpgrade getNextSelectableUpgrade(@Nonnull EntityPlayer viewing, ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator)) return null;
        if(!isEnhanced(stack)) return null;
        ResonatorUpgrade current = getCurrentUpgrade(viewing, stack);
        int currentOrd = current.ordinal();
        int test = currentOrd;
        do {
            test++;
            test %= ResonatorUpgrade.values().length;
            ResonatorUpgrade testUpgrade = ResonatorUpgrade.values()[test];
            if(testUpgrade.canSwitchTo(viewing, stack) && testUpgrade != current) {
                return testUpgrade;
            }
        } while (test != currentOrd);
        return null;
    }

    public static boolean setCurrentUpgrade(EntityPlayer setting, ItemStack stack, ResonatorUpgrade upgrade) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator)) return false;
        if(upgrade.canSwitchTo(setting, stack)) {
            NBTHelper.getPersistentData(stack).setInteger("selected_upgrade", upgrade.ordinal());
            return true;
        }
        return false;
    }

    @Nonnull
    public static ResonatorUpgrade getCurrentUpgrade(@Nullable EntityPlayer viewing, ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator)) return ResonatorUpgrade.STARLIGHT; //Fallback
        if(!isEnhanced(stack)) return ResonatorUpgrade.STARLIGHT;
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        int current = cmp.getInteger("selected_upgrade");
        ResonatorUpgrade upgrade = ResonatorUpgrade.values()[MathHelper.clamp(current, 0, ResonatorUpgrade.values().length - 1)];
        if(viewing != null) {
            if(!upgrade.canSwitchTo(viewing, stack)) {
                return ResonatorUpgrade.STARLIGHT;
            }
        }
        return upgrade;
    }

    public static void setUpgradeUnlocked(ItemStack stack, ResonatorUpgrade upgrade) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator)) return;
        if(!isEnhanced(stack)) return;
        upgrade.applyUpgrade(stack);
    }

    public static boolean hasUpgrade(ItemStack stack, ResonatorUpgrade upgrade) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator)) return false;
        if(!isEnhanced(stack)) return false;
        return upgrade.hasUpgrade(stack);
    }

    public static List<ResonatorUpgrade> getUpgrades(ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemSkyResonator)) return Lists.newArrayList();
        if(!isEnhanced(stack)) return Lists.newArrayList(ResonatorUpgrade.STARLIGHT);
        List<ResonatorUpgrade> upgrades = Lists.newLinkedList();
        for (ResonatorUpgrade ru : ResonatorUpgrade.values()) {
            if(ru.hasUpgrade(stack)) {
                upgrades.add(ru);
            }
        }
        return upgrades;
    }

    public static enum ResonatorUpgrade {

        STARLIGHT("starlight", (p, s) -> true),
        FLUID_FIELDS("liquid", (p, s) -> ResearchManager.getProgressTestAccess(p).getTierReached().isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT)),
        STRUCTURE_CHECK("structure", (p, s) -> ResearchManager.getProgressTestAccess(p).getTierReached().isThisLaterOrEqual(ProgressionTier.ATTUNEMENT));

        private final ResonatorUpgradeCheck check;
        private final String appendixUpgrade;

        private ResonatorUpgrade(String appendixUpgrade, ResonatorUpgradeCheck check) {
            this.check = check;
            this.appendixUpgrade = appendixUpgrade;
        }

        public String getUnlocalizedName() {
            return "item.itemskyresonator." + appendixUpgrade;
        }

        public String getUnlocalizedUpgradeName() {
            return "item.itemskyresonator.upgrade." + appendixUpgrade + ".name";
        }

        public boolean hasUpgrade(ItemStack stack) {
            int id = ordinal();
            NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
            if(cmp.hasKey("upgrades", Constants.NBT.TAG_LIST)) {
                NBTTagList list = cmp.getTagList("upgrades", Constants.NBT.TAG_INT);
                for (int i = 0; i < list.tagCount(); i++) {
                    if(list.getIntAt(i) == id) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean canSwitchTo(@Nonnull EntityPlayer player, ItemStack stack) {
            return hasUpgrade(stack) && check.hasAccessToUpgrade(player, stack);
        }

        public void applyUpgrade(ItemStack stack) {
            if(hasUpgrade(stack)) return;

            NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
            if(!cmp.hasKey("upgrades", Constants.NBT.TAG_LIST)) {
                cmp.setTag("upgrades", new NBTTagList());
            }
            NBTTagList list = cmp.getTagList("upgrades", Constants.NBT.TAG_INT);
            list.appendTag(new NBTTagInt(ordinal()));
        }

    }

    public static interface ResonatorUpgradeCheck {

        public boolean hasAccessToUpgrade(@Nonnull EntityPlayer player, ItemStack stack);

    }

}
