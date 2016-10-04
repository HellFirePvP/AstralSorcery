package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.entities.EntityGrindstone;
import hellfirepvp.astralsorcery.common.item.base.IGrindable;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.ItemNBTHelper;
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

    public ItemCrystalToolBase() {
        super(0, 0, RegistryItems.crystalToolMaterial, Collections.emptySet());
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
        CrystalProperties.addPropertyTooltip(prop, tooltip);
        super.addInformation(stack, playerIn, tooltip, advanced);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        float str = super.getStrVsBlock(stack, state);
        ToolCrystalProperties properties = getToolProperties(stack);
        return str * properties.getEfficiencyMultiplier();
    }

    public static ToolCrystalProperties getToolProperties(ItemStack stack) {
        NBTTagCompound nbt = ItemNBTHelper.getPersistentData(stack);
        return ToolCrystalProperties.readFromNBT(nbt);
    }

    public static void setToolProperties(ItemStack stack, ToolCrystalProperties properties) {
        NBTTagCompound nbt = ItemNBTHelper.getPersistentData(stack);
        properties.writeToNBT(nbt);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if(getDamage(stack) > damage) {
            return; //We don't want mending. RIP.
        }
        super.setDamage(stack, damage);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        ToolCrystalProperties properties = getToolProperties(stack);
        return properties.getMaxToolDamage();
    }

    @Override
    public boolean canGrind(EntityGrindstone grindstone, ItemStack stack) {
        return true;
    }

    @Nonnull
    @Override
    public GrindResult grind(EntityGrindstone grindstone, ItemStack stack, Random rand) {
        ToolCrystalProperties prop = getToolProperties(stack);
        ToolCrystalProperties result = prop.grindCopy(rand);
        if(result == null) {
            return GrindResult.failBreakItem();
        }
        setToolProperties(stack, result);
        if(stack.getItemDamage() >= stack.getMaxDamage()) {
            return GrindResult.failBreakItem();
        }
        return GrindResult.success();
    }

}
