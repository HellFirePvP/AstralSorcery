/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting;

import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.FluidIngredient;
import hellfirepvp.astralsorcery.common.item.render.ItemGatedVisibility;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemComparator;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemHandle
 * Created by HellFirePvP
 * Date: 30.05.2019 / 16:44
 */
public final class ItemHandle {

    private static final Constructor<CompoundIngredient> COMPOUND_CTOR;

    public static ItemHandle EMPTY = new ItemHandle();
    public static boolean ignoreGatingRequirement = true;

    public final Type handleType;

    private List<ItemStack> applicableItems = new LinkedList<>();
    private Tag<Item> tag = null;
    private FluidStack fluidTypeAndAmount = null;

    /*
     * PLEASE do not use this without populating the respective fields...
     */
    private ItemHandle(Type type) {
        this.handleType = type;
    }

    private ItemHandle() {
        this(Type.STACK);
        applicableItems.add(ItemStack.EMPTY);
    }

    public ItemHandle(Tag<Item> tag) {
        this.tag = tag;
        this.handleType = Type.TAG;
    }

    public ItemHandle(Fluid fluid) {
        this.fluidTypeAndAmount = new FluidStack(fluid, 1000);
        this.handleType = Type.FLUID;
    }

    public ItemHandle(Fluid fluid, int mbAmount) {
        this.fluidTypeAndAmount = new FluidStack(fluid, mbAmount);
        this.handleType = Type.FLUID;
    }

    public ItemHandle(FluidStack compareStack) {
        this.fluidTypeAndAmount = compareStack.copy();
        this.handleType = Type.FLUID;
    }

    public ItemHandle(@Nonnull ItemStack matchStack) {
        this.applicableItems.add(ItemUtils.copyStackWithSize(matchStack, matchStack.getCount()));
        this.handleType = Type.STACK;
    }

    public ItemHandle(@Nonnull ItemStack... matchStacks) {
        for (ItemStack stack : matchStacks) {
            this.applicableItems.add(ItemUtils.copyStackWithSize(stack, stack.getCount()));
        }
        this.handleType = Type.STACK;
    }

    public ItemHandle(NonNullList<ItemStack> matchStacks) {
        for (ItemStack stack : matchStacks) {
            this.applicableItems.add(ItemUtils.copyStackWithSize(stack, stack.getCount()));
        }
        this.handleType = Type.STACK;
    }

    public static ItemHandle of(Ingredient ingredient) {
        return new ItemHandle(ingredient.getMatchingStacks());
    }

    // TODO re-add after adding crystals
    /*
    public static ItemHandle getCrystalVariant(boolean hasToBeTuned, boolean hasToBeCelestial) {
        if(hasToBeTuned) {
            if(hasToBeCelestial) {
                return new ItemHandle(new ItemStack(ItemsAS.tunedCelestialCrystal));
            }

            ItemHandle handle = new ItemHandle(new ItemStack(ItemsAS.tunedRockCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.tunedCelestialCrystal));
            return handle;
        } else {
            if(hasToBeCelestial) {
                ItemHandle handle = new ItemHandle(new ItemStack(ItemsAS.celestialCrystal));
                handle.applicableItems.add(new ItemStack(ItemsAS.tunedCelestialCrystal));
                return handle;
            }

            ItemHandle handle = new ItemHandle(new ItemStack(ItemsAS.rockCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.celestialCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.tunedRockCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.tunedCelestialCrystal));
            return handle;
        }
    }
    */

    public NonNullList<ItemStack> getApplicableItems() {
        NonNullList<ItemStack> list = NonNullList.create();
        switch (this.handleType) {
            case TAG:
                list.addAll(ItemUtils.getItemsOfTag(this.tag));
            case STACK:
                list.addAll(applicableItems);
                break;
            case FLUID:
                list.add(FluidUtil.getFilledBucket(this.fluidTypeAndAmount));
                break;
        }
        return list;
    }

    @OnlyIn(Dist.CLIENT)
    public NonNullList<ItemStack> getApplicableItemsForRender() {
        NonNullList<ItemStack> applicable = getApplicableItems();
        Iterator<ItemStack> iterator = applicable.iterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            if(stack.isEmpty()) continue;
            Item i = stack.getItem();
            if(!ignoreGatingRequirement && i instanceof ItemGatedVisibility) {
                if(!((ItemGatedVisibility) i).isSupposedToSeeInRender(stack)) {
                    iterator.remove();
                }
            }
        }
        return applicable;
    }

    //CACHE THIS !!!111ELEVEN11!
    public Ingredient getRecipeIngredient() {
        switch (handleType) {
            case TAG:
                return Ingredient.fromTag(this.tag);
            case FLUID:
                return new FluidIngredient(new FluidStack(fluidTypeAndAmount.getFluid(), Fluid.BUCKET_VOLUME));
            case STACK:
            default:
                List<Ingredient> ingredients = new ArrayList<>();
                for (ItemStack stack : this.applicableItems) {
                    if (!stack.isEmpty()) {
                        Ingredient i = new HandleIngredient(stack);
                        if(i != Ingredient.EMPTY) {
                            ingredients.add(i);
                        }
                    }
                }
                try {
                    return COMPOUND_CTOR.newInstance(ingredients);
                } catch (Exception e) {}
        }
        return Ingredient.EMPTY;
    }

    @Nullable
    public Tag<Item> getTag() {
        return tag;
    }

    @Nullable
    public FluidStack getFluidTypeAndAmount() {
        return fluidTypeAndAmount;
    }

    public boolean matchCrafting(ItemStack stack) {
        if(stack.isEmpty()) return false;

        switch (handleType) {
            case TAG:
                return this.tag.contains(stack.getItem());
            case STACK:
                for (ItemStack applicable : applicableItems) {
                    if (ItemComparator.compare(applicable, stack, ItemComparator.Clause.Sets.ITEMSTACK_CRAFTING)) {
                        return true;
                    }
                }
                return false;
            case FLUID:
                FluidStack contained = FluidUtil.getFluidContained(stack).orElse(null);
                if (contained == null || contained.getFluid() == null || !contained.getFluid().equals(fluidTypeAndAmount.getFluid())) {
                    return false;
                }
                return ItemUtils.drainFluidFromItem(stack, fluidTypeAndAmount, false).isSuccess();
            default:
                break;
        }
        return false;
    }

    public void serialize(PacketBuffer packetBuffer) {
        packetBuffer.writeInt(handleType.ordinal());
        switch (handleType) {
            case TAG:
                ByteBufUtils.writeString(packetBuffer, this.tag.getId().toString());
                break;
            case STACK:
                packetBuffer.writeInt(this.applicableItems.size());
                for (ItemStack applicableItem : this.applicableItems) {
                    ByteBufUtils.writeItemStack(packetBuffer, applicableItem);
                }
                break;
            case FLUID:
                ByteBufUtils.writeFluidStack(packetBuffer, this.fluidTypeAndAmount);
                break;
            default:
                break;
        }
    }

    public static ItemHandle deserialize(PacketBuffer packetBuffer) {
        Type type = Type.values()[packetBuffer.readInt()];
        ItemHandle handle = new ItemHandle(type);
        switch (type) {
            case TAG:
                handle.tag = ItemTags.getCollection().get(new ResourceLocation(ByteBufUtils.readString(packetBuffer)));
                break;
            case STACK:
                int amt = packetBuffer.readInt();
                for (int i = 0; i < amt; i++) {
                    handle.applicableItems.add(ByteBufUtils.readItemStack(packetBuffer));
                }
                break;
            case FLUID:
                handle.fluidTypeAndAmount = ByteBufUtils.readFluidStack(packetBuffer);
                break;
            default:
                break;
        }
        return handle;
    }

    static {
        Constructor<CompoundIngredient> ctor;
        try {
            ctor = CompoundIngredient.class.getDeclaredConstructor(Collection.class);
        } catch (Exception exc) {
            throw new IllegalStateException("Could not find CompoundIngredient Constructor! Recipes can't be created; Exiting execution! Try with AS and forge alone first! Please report this along with exact forge version and other mods.");
        }
        ctor.setAccessible(true);
        COMPOUND_CTOR = ctor;
    }

    public static class HandleIngredient extends Ingredient {

        private HandleIngredient(ItemStack stack) {
            super(Stream.of(new SingleItemList(stack)));
        }

        @Override
        public boolean test(@Nullable ItemStack other) {
            if (other == null) {
                return false;
            }

            for (ItemStack thisStack : this.getMatchingStacks()) {
                if (ItemComparator.compare(thisStack, other, ItemComparator.Clause.Sets.ITEMSTACK_CRAFTING)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static enum Type {

        TAG,
        STACK,
        FLUID

    }

}
