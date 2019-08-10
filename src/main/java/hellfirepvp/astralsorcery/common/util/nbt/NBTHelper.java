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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NBTHelper
 * Created by HellFirePvP
 * Date: 07.05.2016 / 02:15
 */
public class NBTHelper {

    @Nonnull
    public static CompoundNBT getPersistentData(Entity entity) {
        return getPersistentData(entity.getEntityData());
    }

    @Nonnull
    public static CompoundNBT getPersistentData(ItemStack item) {
        return getPersistentData(getData(item));
    }

    @Nonnull
    public static CompoundNBT getPersistentData(CompoundNBT base) {
        CompoundNBT compound;
        if (hasPersistentData(base)) {
            compound = base.getCompound(AstralSorcery.MODID);
        } else {
            compound = new CompoundNBT();
            base.put(AstralSorcery.MODID, compound);
        }
        return compound;
    }

    public static boolean hasPersistentData(Entity entity) {
        return hasPersistentData(entity.getEntityData());
    }

    public static boolean hasPersistentData(ItemStack item) {
        return item.hasTag() && hasPersistentData(item.getTag());
    }

    public static boolean hasPersistentData(CompoundNBT base) {
        return base.contains(AstralSorcery.MODID) && base.get(AstralSorcery.MODID) instanceof CompoundNBT;
    }


    public static void removePersistentData(Entity entity) {
        removePersistentData(entity.getEntityData());
    }

    public static void removePersistentData(ItemStack item) {
        if (item.hasTag()) {
            removePersistentData(item.getTag());
        }
    }

    public static void removePersistentData(CompoundNBT base) {
        base.remove(AstralSorcery.MODID);
    }


    public static CompoundNBT getData(ItemStack stack) {
        CompoundNBT compound = stack.getTag();
        if (compound == null) {
            compound = new CompoundNBT();
            stack.setTag(compound);
        }
        return compound;
    }

    public static <T> void writeOptional(CompoundNBT nbt, String key, @Nullable T object, BiConsumer<CompoundNBT, T> writer) {
        nbt.putBoolean(key + "_present", object != null);
        if (object != null) {
            CompoundNBT write = new CompoundNBT();
            writer.accept(write, object);
            nbt.put(key, write);
        }
    }

    @Nullable
    public static <T> T readOptional(CompoundNBT nbt, String key, Function<CompoundNBT, T> reader) {
        if (nbt.getBoolean(key + "_present")) {
            CompoundNBT read = nbt.getCompound(key);
            return reader.apply(read);
        }
        return null;
    }

    public static void setBlockState(CompoundNBT cmp, String key, BlockState state) {
        CompoundNBT serialized = getBlockStateNBTTag(state);
        cmp.put(key, serialized);
    }

    @Nullable
    public static BlockState getBlockState(CompoundNBT cmp, String key) {
        return getBlockStateFromTag(cmp.getCompound(key));
    }

    @Nonnull
    public static CompoundNBT getBlockStateNBTTag(BlockState state) {
        if(state.getBlock().getRegistryName() == null) {
            state = Blocks.AIR.getDefaultState();
        }
        CompoundNBT tag = new CompoundNBT();
        tag.putString("registryName", state.getBlock().getRegistryName().toString());
        ListNBT properties = new ListNBT();
        for (IProperty property : state.getProperties()) {
            CompoundNBT propTag = new CompoundNBT();
            try {
                propTag.putString("value", property.getName(state.get(property)));
            } catch (Exception exc) {
                continue;
            }
            propTag.putString("property", property.getName());
            properties.add(propTag);
        }
        tag.put("properties", properties);
        return tag;
    }

    @Nullable
    public static BlockState getBlockStateFromTag(CompoundNBT cmp) {
        return getBlockStateFromTag(cmp, null);
    }

    @Nullable
    public static <T extends Comparable<T>> BlockState getBlockStateFromTag(CompoundNBT cmp, BlockState _default) {
        ResourceLocation key = new ResourceLocation(cmp.getString("registryName"));
        Block block = ForgeRegistries.BLOCKS.getValue(key);
        if(block == null || block == Blocks.AIR) return _default;
        BlockState state = block.getDefaultState();
        Collection<IProperty<?>> properties = state.getProperties();
        ListNBT list = cmp.getList("properties", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT propertyTag = list.getCompound(i);
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

    public static void setAsSubTag(CompoundNBT compound, String tag, Consumer<CompoundNBT> applyFct) {
        CompoundNBT newTag = new CompoundNBT();
        applyFct.accept(newTag);
        compound.put(tag, newTag);
    }

    public static void setStack(CompoundNBT compound, String tag, ItemStack stack) {
        setAsSubTag(compound, tag, stack::write);
    }

    public static void removeUUID(CompoundNBT compound, String key) {
        compound.remove(key + "Most");
        compound.remove(key + "Least");
    }

    public static ItemStack getStack(CompoundNBT compound, String tag) {
        return getStack(compound, tag, ItemStack.EMPTY);
    }

    //Get tags with default value
    public static ItemStack getStack(CompoundNBT compound, String tag, ItemStack defaultValue) {
        if (compound.contains(tag)) {
            return ItemStack.read(compound.getCompound(tag));
        }
        return defaultValue;
    }

    public static CompoundNBT writeBlockPosToNBT(BlockPos pos, CompoundNBT compound) {
        compound.putInt("bposX", pos.getX());
        compound.putInt("bposY", pos.getY());
        compound.putInt("bposZ", pos.getZ());
        return compound;
    }

    public static BlockPos readBlockPosFromNBT(CompoundNBT compound) {
        int x = compound.getInt("bposX");
        int y = compound.getInt("bposY");
        int z = compound.getInt("bposZ");
        return new BlockPos(x, y, z);
    }

    public static CompoundNBT writeVector3(Vector3 v) {
        CompoundNBT cmp = new CompoundNBT();
        writeVector3(v, cmp);
        return cmp;
    }

    public static CompoundNBT writeVector3(Vector3 v, CompoundNBT compound) {
        compound.putDouble("vecPosX", v.getX());
        compound.putDouble("vecPosY", v.getY());
        compound.putDouble("vecPosZ", v.getZ());
        return compound;
    }

    public static Vector3 readVector3(CompoundNBT compound) {
        return new Vector3(
                compound.getDouble("vecPosX"),
                compound.getDouble("vecPosY"),
                compound.getDouble("vecPosZ"));
    }

    public static CompoundNBT writeBoundingBox(AxisAlignedBB box, CompoundNBT tag) {
        tag.putDouble("boxMinX", box.minX);
        tag.putDouble("boxMinY", box.minY);
        tag.putDouble("boxMinZ", box.minZ);
        tag.putDouble("boxMaxX", box.maxX);
        tag.putDouble("boxMaxY", box.maxY);
        tag.putDouble("boxMaxZ", box.maxZ);
        return tag;
    }

    public static AxisAlignedBB readBoundingBox(CompoundNBT tag) {
        return new AxisAlignedBB(
                tag.getDouble("boxMinX"),
                tag.getDouble("boxMinY"),
                tag.getDouble("boxMinZ"),
                tag.getDouble("boxMaxX"),
                tag.getDouble("boxMaxY"),
                tag.getDouble("boxMaxZ"));
    }
}
