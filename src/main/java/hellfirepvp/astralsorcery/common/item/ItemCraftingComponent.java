/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.entities.EntityItemStardust;
import hellfirepvp.astralsorcery.common.item.base.IItemVariants;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCraftingComponent
 * Created by HellFirePvP
 * Date: 17.08.2016 / 13:10
 */
public class ItemCraftingComponent extends Item implements IItemVariants {

    public ItemCraftingComponent() {
        setMaxStackSize(64);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(this.isInCreativeTab(tab)) {
            for (MetaType type : MetaType.values()) {
                items.add(new ItemStack(this, 1, type.getMeta()));
            }
        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        MetaType type = MetaType.fromMeta(stack.getItemDamage());
        switch (type) {
            case STARDUST:
                return true;
        }
        return super.hasCustomEntity(stack);
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        MetaType type = MetaType.fromMeta(itemstack.getItemDamage());
        switch (type) {
            case STARDUST:
                EntityItemStardust stardust = new EntityItemStardust(world, location.posX, location.posY, location.posZ, itemstack);
                stardust.setDefaultPickupDelay();
                stardust.motionX = location.motionX;
                stardust.motionY = location.motionY;
                stardust.motionZ = location.motionZ;
                return stardust;
        }
        return null;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        Item i = stack.getItem();
        if(i instanceof ItemCraftingComponent) {
            MetaType type = MetaType.fromMeta(stack.getItemDamage());
            return super.getUnlocalizedName(stack) + "." + type.getUnlocalizedName();
        }
        return super.getUnlocalizedName(stack);
    }

    @Override
    public String[] getVariants() {
        String[] sub = new String[MetaType.values().length];
        MetaType[] values = MetaType.values();
        for (int i = 0; i < values.length; i++) {
            MetaType mt = values[i];
            sub[i] = mt.getUnlocalizedName();
        }
        return sub;
    }

    @Override
    public int[] getVariantMetadatas() {
        int[] sub = new int[MetaType.values().length];
        MetaType[] values = MetaType.values();
        for (int i = 0; i < values.length; i++) {
            MetaType mt = values[i];
            sub[i] = mt.getMeta();
        }
        return sub;
    }

    public static enum MetaType {

        AQUAMARINE,
        STARMETAL_INGOT,
        STARDUST,
        GLASS_LENS,
        RESO_GEM,
        PARCHMENT;

        public ItemStack asStack() {
            return new ItemStack(ItemsAS.craftingComponent, 1, getMeta());
        }

        public String getUnlocalizedName() {
            return name().toLowerCase();
        }

        public int getMeta() {
            return ordinal();
        }

        public static MetaType fromMeta(int meta) {
            int ord = MathHelper.clamp(meta, 0, values().length -1);
            return values()[ord];
        }

        public boolean isGrindable() {
            return this == STARMETAL_INGOT;
        }
    }

}
