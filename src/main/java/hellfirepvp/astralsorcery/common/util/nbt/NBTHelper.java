/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.nbt;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NBTHelper
 * Created by HellFirePvP
 * Date: 07.05.2016 / 02:15
 */
public class NBTHelper {

    @Nonnull
    public static NBTTagCompound getPersistentData(Entity entity) {
        return getPersistentData(entity.getEntityData());
    }

    @Nonnull
    public static NBTTagCompound getPersistentData(ItemStack item) {
        return getPersistentData(getData(item));
    }

    @Nonnull
    public static NBTTagCompound getPersistentData(NBTTagCompound base) {
        NBTTagCompound compound;
        if (hasPersistentData(base)) {
            compound = base.getCompound(AstralSorcery.MODID);
        } else {
            compound = new NBTTagCompound();
            base.setTag(AstralSorcery.MODID, compound);
        }
        return compound;
    }

    public static boolean hasPersistentData(Entity entity) {
        return hasPersistentData(entity.getEntityData());
    }

    public static boolean hasPersistentData(ItemStack item) {
        return item.hasTag() && hasPersistentData(item.getTag());
    }

    public static boolean hasPersistentData(NBTTagCompound base) {
        return base.hasKey(AstralSorcery.MODID) && base.getTag(AstralSorcery.MODID) instanceof NBTTagCompound;
    }


    public static void removePersistentData(Entity entity) {
        removePersistentData(entity.getEntityData());
    }

    public static void removePersistentData(ItemStack item) {
        if (item.hasTag()) {
            removePersistentData(item.getTag());
        }
    }

    public static void removePersistentData(NBTTagCompound base) {
        base.removeTag(AstralSorcery.MODID);
    }


    public static NBTTagCompound getData(ItemStack stack) {
        NBTTagCompound compound = stack.getTag();
        if (compound == null) {
            compound = new NBTTagCompound();
            stack.setTag(compound);
        }
        return compound;
    }

    public static void setBlockState(NBTTagCompound cmp, String key, IBlockState state) {
        NBTTagCompound serialized = getBlockStateNBTTag(state);
        if (serialized != null) {
            cmp.setTag(key, serialized);
        }
    }

    @Nullable
    public static IBlockState getBlockState(NBTTagCompound cmp, String key) {
        return getBlockStateFromTag(cmp.getCompound(key));
    }

    @Nonnull
    public static NBTTagCompound getBlockStateNBTTag(IBlockState state) {
        if(state.getBlock().getRegistryName() == null) {
            state = Blocks.AIR.getDefaultState();
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("registryName", state.getBlock().getRegistryName().toString());
        NBTTagList properties = new NBTTagList();
        for (IProperty property : state.getProperties()) {
            NBTTagCompound propTag = new NBTTagCompound();
            try {
                propTag.setString("value", property.getName(state.get(property)));
            } catch (Exception exc) {
                continue;
            }
            propTag.setString("property", property.getName());
            properties.add(propTag);
        }
        tag.setTag("properties", properties);
        return tag;
    }

    @Nullable
    public static IBlockState getBlockStateFromTag(NBTTagCompound cmp) {
        return getBlockStateFromTag(cmp, null);
    }

    @Nullable
    public static <T extends Comparable<T>> IBlockState getBlockStateFromTag(NBTTagCompound cmp, IBlockState _default) {
        ResourceLocation key = new ResourceLocation(cmp.getString("registryName"));
        Block block = ForgeRegistries.BLOCKS.getValue(key);
        if(block == null || block == Blocks.AIR) return _default;
        IBlockState state = block.getDefaultState();
        Collection<IProperty<?>> properties = state.getProperties();
        NBTTagList list = cmp.getList("properties", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            NBTTagCompound propertyTag = list.getCompound(i);
            String valueStr = propertyTag.getString("value");
            String propertyStr = propertyTag.getString("property");
            IProperty<T> match = (IProperty<T>) MiscUtils.iterativeSearch(properties, prop -> prop.getName().equalsIgnoreCase(propertyStr));
            if(match != null) {
                try {
                    Optional<T> opt = match.parseValue(valueStr);
                    if(opt.isPresent()) {
                        state = state.with(match, opt.get());
                    }
                } catch (Throwable tr) {} // Thanks Exu2
            }
        }
        return state;
    }

    public static void setAsSubTag(NBTTagCompound compound, String tag, Consumer<NBTTagCompound> applyFct) {
        NBTTagCompound newTag = new NBTTagCompound();
        applyFct.accept(newTag);
        compound.setTag(tag, newTag);
    }

    public static void setStack(NBTTagCompound compound, String tag, ItemStack stack) {
        setAsSubTag(compound, tag, stack::write);
    }

    public static void removeUUID(NBTTagCompound compound, String key) {
        compound.removeTag(key + "Most");
        compound.removeTag(key + "Least");
    }

    public static ItemStack getStack(NBTTagCompound compound, String tag) {
        return getStack(compound, tag, ItemStack.EMPTY);
    }

    //Get tags with default value
    public static ItemStack getStack(NBTTagCompound compound, String tag, ItemStack defaultValue) {
        if (compound.hasKey(tag)) {
            return ItemStack.read(compound.getCompound(tag));
        }
        return defaultValue;
    }

    public static boolean getBoolean(NBTTagCompound compound, String tag, boolean defaultValue) {
        return compound.hasKey(tag) ? compound.getBoolean(tag) : defaultValue;
    }

    public static String getString(NBTTagCompound compound, String tag, String defaultValue) {
        return compound.hasKey(tag) ? compound.getString(tag) : defaultValue;
    }

    public static int getInteger(NBTTagCompound compound, String tag, int defaultValue) {
        return compound.hasKey(tag) ? compound.getInt(tag) : defaultValue;
    }

    public static double getDouble(NBTTagCompound compound, String tag, double defaultValue) {
        return compound.hasKey(tag) ? compound.getDouble(tag) : defaultValue;
    }

    public static float getFloat(NBTTagCompound compound, String tag, float defaultValue) {
        return compound.hasKey(tag) ? compound.getFloat(tag) : defaultValue;
    }

    public static byte getByte(NBTTagCompound compound, String tag, byte defaultValue) {
        return compound.hasKey(tag) ? compound.getByte(tag) : defaultValue;
    }

    public static short getShort(NBTTagCompound compound, String tag, short defaultValue) {
        return compound.hasKey(tag) ? compound.getShort(tag) : defaultValue;
    }

    public static long getLong(NBTTagCompound compound, String tag, long defaultValue) {
        return compound.hasKey(tag) ? compound.getLong(tag) : defaultValue;
    }

    public static void writeBlockPosToNBT(BlockPos pos, NBTTagCompound compound) {
        compound.setInt("bposX", pos.getX());
        compound.setInt("bposY", pos.getY());
        compound.setInt("bposZ", pos.getZ());
    }

    public static BlockPos readBlockPosFromNBT(NBTTagCompound compound) {
        int x = compound.getInt("bposX");
        int y = compound.getInt("bposY");
        int z = compound.getInt("bposZ");
        return new BlockPos(x, y, z);
    }

    public static NBTTagCompound writeVector3(Vector3 v) {
        NBTTagCompound cmp = new NBTTagCompound();
        writeVector3(v, cmp);
        return cmp;
    }

    public static void writeVector3(Vector3 v, NBTTagCompound compound) {
        compound.setDouble("vecPosX", v.getX());
        compound.setDouble("vecPosY", v.getY());
        compound.setDouble("vecPosZ", v.getZ());
    }

    public static Vector3 readVector3(NBTTagCompound compound) {
        return new Vector3(
                compound.getDouble("vecPosX"),
                compound.getDouble("vecPosY"),
                compound.getDouble("vecPosZ"));
    }

    public static void writeBoundingBox(AxisAlignedBB box, NBTTagCompound tag) {
        tag.setDouble("boxMinX", box.minX);
        tag.setDouble("boxMinY", box.minY);
        tag.setDouble("boxMinZ", box.minZ);
        tag.setDouble("boxMaxX", box.maxX);
        tag.setDouble("boxMaxY", box.maxY);
        tag.setDouble("boxMaxZ", box.maxZ);
    }

    public static AxisAlignedBB readBoundingBox(NBTTagCompound tag) {
        return new AxisAlignedBB(
                tag.getDouble("boxMinX"),
                tag.getDouble("boxMinY"),
                tag.getDouble("boxMinZ"),
                tag.getDouble("boxMaxX"),
                tag.getDouble("boxMaxY"),
                tag.getDouble("boxMaxZ"));
    }
}
