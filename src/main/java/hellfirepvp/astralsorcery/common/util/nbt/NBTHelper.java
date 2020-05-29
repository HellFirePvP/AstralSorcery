/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
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
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        return getPersistentData(entity.getPersistentData());
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
        return hasPersistentData(entity.getPersistentData());
    }

    public static boolean hasPersistentData(ItemStack item) {
        return item.hasTag() && hasPersistentData(item.getTag());
    }

    public static boolean hasPersistentData(CompoundNBT base) {
        return base.contains(AstralSorcery.MODID) && base.get(AstralSorcery.MODID) instanceof CompoundNBT;
    }


    public static void removePersistentData(Entity entity) {
        removePersistentData(entity.getPersistentData());
    }

    public static void removePersistentData(ItemStack item) {
        if (item.hasTag()) {
            removePersistentData(item.getTag());
        }
    }

    public static void removePersistentData(CompoundNBT base) {
        base.remove(AstralSorcery.MODID);
    }

    public static void deepMerge(CompoundNBT dst, CompoundNBT src) {
        for (String s : src.keySet()) {
            INBT nbtElement = src.get(s);
            if (nbtElement.getId() == Constants.NBT.TAG_COMPOUND) {
                if (dst.contains(s, Constants.NBT.TAG_COMPOUND)) {
                    deepMerge(dst.getCompound(s), (CompoundNBT) nbtElement);
                } else {
                    dst.put(s, nbtElement.copy());
                }
            } else if (nbtElement.getId() == Constants.NBT.TAG_LIST) {
                if (dst.contains(s, Constants.NBT.TAG_LIST)) {
                    ListNBT dstList = (ListNBT) dst.get(s);
                    ListNBT srcList = (ListNBT) nbtElement;
                    if (dstList.getTagType() == srcList.getTagType()) {
                        deepMergeList(dstList, srcList);
                    } else {
                        dst.put(s, srcList.copy());
                    }
                } else {
                    dst.put(s, nbtElement.copy());
                }
            } else {
                dst.put(s, nbtElement.copy());
            }
        }
    }

    //Stupid NBT stuff ahead. the iterator and the actual .get returns from 2 different lists.
    //Don't use the iterator on ListNBT...
    private static void deepMergeList(ListNBT dst, ListNBT src) {
        for (int j = 0; j < src.size(); j++) {
            INBT toAdd = src.get(j);

            boolean found = false;
            for (int i = 0; i < dst.size(); i++) {
                INBT existing = dst.get(i);
                if (existing.equals(toAdd)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                dst.add(toAdd.copy());
            }
        }
    }

    @Nonnull
    public static <E, N extends INBT> List<E> readList(CompoundNBT nbt, String key, int type, Function<N, E> deserializer) {
        if (!nbt.contains(key, Constants.NBT.TAG_LIST)) {
            return new ArrayList<>();
        }
        return readList(nbt.getList(key, type), deserializer);
    }

    @Nonnull
    public static <E, N extends INBT> List<E> readList(ListNBT nbt, Function<N, E> deserializer) {
        return nbt.stream()
                .map(n -> deserializer.apply((N) n))
                .collect(Collectors.toList());
    }

    public static <E> void writeList(CompoundNBT tag, String key, Collection<E> collection, Function<E, INBT> serializer) {
        tag.put(key, writeList(collection, serializer));
    }

    public static <E> ListNBT writeList(Collection<E> collection, Function<E, INBT> serializer) {
        ListNBT nbt = new ListNBT();
        nbt.addAll(collection.stream()
                .map(serializer)
                .collect(Collectors.toList()));
        return nbt;
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
        return readOptional(nbt, key, reader, null);
    }

    @Nullable
    public static <T> T readOptional(CompoundNBT nbt, String key, Function<CompoundNBT, T> reader, T _default) {
        if (nbt.getBoolean(key + "_present")) {
            CompoundNBT read = nbt.getCompound(key);
            return reader.apply(read);
        }
        return _default;
    }

    public static <T extends Enum<T>> void writeEnum(CompoundNBT nbt, String key, T enumValue) {
        nbt.putInt(key, enumValue.ordinal());
    }

    public static <T extends Enum<T>> T readEnum(CompoundNBT nbt, String key, Class<T> enumClazz) {
        if (!enumClazz.isEnum()) {
            throw new IllegalArgumentException("Passed class is not an enum!");
        }
        return enumClazz.getEnumConstants()[nbt.getInt(key)];
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
        if (state.getBlock().getRegistryName() == null) {
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
        if (block == null || block == Blocks.AIR) return _default;
        BlockState state = block.getDefaultState();
        Collection<IProperty<?>> properties = state.getProperties();
        ListNBT list = cmp.getList("properties", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT propertyTag = list.getCompound(i);
            String valueStr = propertyTag.getString("value");
            String propertyStr = propertyTag.getString("property");
            IProperty<T> match = (IProperty<T>) MiscUtils.iterativeSearch(properties, prop -> prop.getName().equalsIgnoreCase(propertyStr));
            if (match != null) {
                try {
                    Optional<T> opt = match.parseValue(valueStr);
                    if (opt.isPresent()) {
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

    @Nullable
    public static <T> T readFromSubTag(CompoundNBT compound, String tag, Function<CompoundNBT, T> readFct) {
        if (compound.contains(tag, Constants.NBT.TAG_COMPOUND)) {
            return readFct.apply(compound.getCompound(tag));
        }
        return null;
    }

    public static <T extends IForgeRegistryEntry<T>> void setRegistryEntry(CompoundNBT compoundNBT, String tag, T entry) {
        setResourceLocation(compoundNBT, tag + "_registry", RegistryManager.ACTIVE.getRegistry(entry.getRegistryType()).getRegistryName());
        setResourceLocation(compoundNBT, tag, entry.getRegistryName());
    }

    @Nullable
    public static <T extends IForgeRegistryEntry<T>> T getRegistryEntry(CompoundNBT compoundNBT, String tag) {
        ResourceLocation registryName = getResourceLocation(compoundNBT, tag + "_registry");
        if (registryName != null) {
            ForgeRegistry<T> registry = RegistryManager.ACTIVE.getRegistry(registryName);
            if (registry != null) {
                ResourceLocation key = getResourceLocation(compoundNBT, tag);
                if (key != null) {
                    return registry.getValue(key);
                }
            }
        }
        return null;
    }

    public static void setResourceLocation(CompoundNBT compoundNBT, String tag, ResourceLocation key) {
        compoundNBT.putString(tag, key.toString());
    }

    @Nullable
    public static ResourceLocation getResourceLocation(CompoundNBT compoundNBT, String tag) {
        if (compoundNBT.contains(tag)) {
            return new ResourceLocation(compoundNBT.getString(tag));
        }
        return null;
    }

    public static void setStack(CompoundNBT compound, String tag, ItemStack stack) {
        setAsSubTag(compound, tag, stack::write);
    }

    public static ItemStack getStack(CompoundNBT compound, String tag) {
        return readFromSubTag(compound, tag, ItemStack::read);
    }

    public static void setFluid(CompoundNBT compound, String tag, FluidStack stack) {
        setAsSubTag(compound, tag, stack::writeToNBT);
    }

    public static FluidStack getFluid(CompoundNBT compound, String tag) {
        return readFromSubTag(compound, tag, FluidStack::loadFluidStackFromNBT);
    }

    public static void removeUUID(CompoundNBT compound, String key) {
        compound.remove(key + "Most");
        compound.remove(key + "Least");
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
