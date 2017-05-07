/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.item.base.IGrindable;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileGrindstone;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalTool
 * Created by HellFirePvP
 * Date: 18.09.2016 / 12:25
 */
public abstract class ItemCrystalToolBase extends ItemTool implements IGrindable {

    private static final Random rand = new Random();
    private final int crystalCount;

    public ItemCrystalToolBase(int crystalCount) {
        super(0, 0, RegistryItems.crystalToolMaterial, Collections.emptySet());
        setMaxDamage(0);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        this.crystalCount = crystalCount;
    }

    public void setDamageVsEntity(float damageVsEntity) {
        this.damageVsEntity = damageVsEntity;
    }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        ToolCrystalProperties prop = getToolProperties(stack);
        CrystalProperties.addPropertyTooltip(prop, tooltip, CrystalProperties.MAX_SIZE_CELESTIAL * crystalCount);
        super.addInformation(stack, playerIn, tooltip, advanced);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        float str = super.getStrVsBlock(stack, state);
        ToolCrystalProperties properties = getToolProperties(stack);
        return str * properties.getEfficiencyMultiplier();
    }

    public static ToolCrystalProperties getToolProperties(ItemStack stack) {
        NBTTagCompound nbt = NBTHelper.getPersistentData(stack);
        return ToolCrystalProperties.readFromNBT(nbt);
    }

    public static void setToolProperties(ItemStack stack, ToolCrystalProperties properties) {
        NBTTagCompound nbt = NBTHelper.getPersistentData(stack);
        properties.writeToNBT(nbt);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
        damageProperties(stack, damage);
    }

    @Override
    public boolean isItemTool(ItemStack stack) {
        return true;
    }

    private void damageProperties(ItemStack stack, int damage) {
        ToolCrystalProperties prop = getToolProperties(stack);
        if(prop == null) {
            stack.setItemDamage(stack.getMaxDamage());
            return;
        }
        if(prop.getSize() <= 0) {
            super.setDamage(stack, 11);
            return;
        }
        if(damage < 0) {
            return;
        }
        for (int i = 0; i < damage; i++) {
            double chance = Math.pow(((double) prop.getCollectiveCapability()) / 100D, 2);
            if(chance >= rand.nextFloat()) {
                if(rand.nextInt(40) == 0) prop.damageCutting();
                double purity = ((double) prop.getPurity()) / 100D;
                for (int j = 0; j < 3; j++) {
                    if(purity <= rand.nextFloat()) {
                        if(rand.nextInt(40) == 0) prop.damageCutting();
                    }
                }
            }
        }
        setToolProperties(stack, prop);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 10;
    }

    @Override
    public boolean canGrind(TileGrindstone grindstone, ItemStack stack) {
        return true;
    }

    @Nonnull
    @Override
    public GrindResult grind(TileGrindstone grindstone, ItemStack stack, Random rand) {
        ToolCrystalProperties prop = getToolProperties(stack);
        ToolCrystalProperties result = prop.grindCopy(rand);
        if(result == null) {
            return GrindResult.failBreakItem();
        }
        setToolProperties(stack, result);
        if(result.getSize() <= 0) {
            return GrindResult.failBreakItem();
        }
        return GrindResult.success();
    }

}
