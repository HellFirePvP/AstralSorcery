/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal.base;

import hellfirepvp.astralsorcery.common.entities.EntityCrystal;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRockCrystalBase
 * Created by HellFirePvP
 * Date: 08.05.2016 / 21:38
 */
public abstract class ItemRockCrystalBase extends Item implements ItemHighlighted {

    public ItemRockCrystalBase() {
        setMaxStackSize(1);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        CrystalProperties prop = CrystalProperties.getCrystalProperties(stack);
        if(prop == null) {
            Item i = stack.getItem();
            if(i instanceof ItemCelestialCrystal || i instanceof ItemTunedCelestialCrystal) {
                CrystalProperties.applyCrystalProperties(stack, CrystalProperties.createRandomCelestial());
            } else {
                CrystalProperties.applyCrystalProperties(stack, CrystalProperties.createRandomRock());
            }
        }
    }

    @Override
    public Color getHightlightColor(ItemStack stack) {
        return Color.WHITE;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        EntityCrystal crystal = new EntityCrystal(world, location.posX, location.posY, location.posZ, itemstack);
        crystal.setDefaultPickupDelay();
        crystal.setNoDespawn();
        crystal.motionX = location.motionX;
        crystal.motionY = location.motionY;
        crystal.motionZ = location.motionZ;
        return crystal;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        addCrystalPropertyToolTip(stack, tooltip);
    }

    @SideOnly(Side.CLIENT)
    protected Optional<Boolean> addCrystalPropertyToolTip(ItemStack stack, List<String> tooltip) {
        boolean isCelestial = this instanceof ItemCelestialCrystal || this instanceof ItemTunedCelestialCrystal;
        return CrystalProperties.addPropertyTooltip(CrystalProperties.getCrystalProperties(stack), tooltip, isCelestial ? CrystalProperties.MAX_SIZE_CELESTIAL : CrystalProperties.MAX_SIZE_ROCK);
    }

    public abstract ItemTunedCrystalBase getTunedItemVariant();

    public static ItemStack createMaxBaseCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.rockCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.getMaxRockProperties());
        return crystal;
    }

    public static ItemStack createMaxCelestialCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.celestialCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.getMaxCelestialProperties());
        return crystal;
    }

    public static ItemStack createRandomBaseCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.rockCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.createRandomRock());
        return crystal;
    }

    public static ItemStack createRandomCelestialCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.celestialCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.createRandomCelestial());
        return crystal;
    }

}
