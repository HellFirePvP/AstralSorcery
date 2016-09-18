package hellfirepvp.astralsorcery.common.entities;

import com.google.common.base.Optional;
import hellfirepvp.astralsorcery.common.item.ItemEntityPlacer;
import hellfirepvp.astralsorcery.common.item.base.IGrindable;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.ItemNBTHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityGrindstone
 * Created by HellFirePvP
 * Date: 13.09.2016 / 13:02
 */
public class EntityGrindstone extends EntityLivingBase {

    private static final Random rand = new Random();
    private static final DataParameter<Optional<ItemStack>> GRINDING = EntityDataManager.createKey(EntityGrindstone.class, DataSerializers.OPTIONAL_ITEM_STACK);

    public EntityGrindstone(World worldIn) {
        super(worldIn);
        getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10D);
        setSize(1.1F, 1.2F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(GRINDING, Optional.<ItemStack>absent());
    }

    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, @Nullable ItemStack stack, EnumHand hand) {
        player.swingArm(hand);
        if(hand == EnumHand.OFF_HAND) {
            return EnumActionResult.SUCCESS;
        }
        ItemStack grind = getGrindItem();
        if(!worldObj.isRemote) {
            if(grind != null) {
                if(player.isSneaking()) {
                    ItemUtils.dropItem(worldObj, posX, posY + 1.3F, posZ, grind);

                    setGrindItem(null);
                } else {
                    Item i = grind.getItem();
                    if(i instanceof IGrindable) {
                        IGrindable.GrindResult result = ((IGrindable) i).grind(this, grind, rand);
                        switch (result.getType()) {
                            case SUCCESS:
                                setGrindItem(grind); //Update
                                break;
                            case ITEMCHANGE:
                                setGrindItem(result.getStack());
                                break;
                            case FAIL_BREAK_ITEM:
                                setGrindItem(null);
                                worldObj.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.AMBIENT, 0.5F, worldObj.rand.nextFloat() * 0.2F + 0.8F);
                                break;
                        }
                    }
                }
            } else {
                if(player.isSneaking()) {
                    setDead();
                    if(!player.isCreative()) {
                        ItemUtils.dropItemNaturally(worldObj, posX, posY + 0.3, posZ, new ItemStack(ItemsAS.entityPlacer, 1, ItemEntityPlacer.PlacerType.GRINDSTONE.getMeta()));
                    }
                } else {
                    if(stack != null) {
                        Item trySet = stack.getItem();
                        if(trySet instanceof IGrindable && ((IGrindable) trySet).canGrind(this, stack)) {
                            ItemStack toSet = stack.copy();
                            toSet.stackSize = 1;
                            setGrindItem(toSet);
                            worldObj.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.AMBIENT, 0.5F, worldObj.rand.nextFloat() * 0.2F + 0.8F);

                            if(!player.isCreative()) {
                                stack.stackSize--;
                            }
                        }
                    }
                }
            }
        } else {
            if(grind != null && !player.isSneaking()) {
                Item i = grind.getItem();
                if(i instanceof IGrindable) {
                    if(((IGrindable) i).canGrind(this, grind)) {
                        for (int j = 0; j < 15; j++) {
                            worldObj.spawnParticle(EnumParticleTypes.CRIT, posX, posY + 0.7, posZ,
                                    (rand.nextBoolean() ? 1 : -1) * rand.nextFloat(),
                                    (rand.nextBoolean() ? 1 : -1) * rand.nextFloat(),
                                    (rand.nextBoolean() ? 1 : -1) * rand.nextFloat());
                        }
                    }
                }
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    public void onDeath(DamageSource cause) {
        ItemStack grind = getGrindItem();
        if(grind != null) {
            ItemUtils.dropItem(worldObj, posX, posY + 1.3F, posZ, grind);
        }
        super.onDeath(cause);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        if(compound.hasKey("grindStack")) {
            NBTTagCompound grindStack = compound.getCompoundTag("grindStack");
            ItemStack stack = ItemStack.loadItemStackFromNBT(grindStack);
            setGrindItem(stack);
        } else {
            setGrindItem(null);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        ItemStack grind = getGrindItem();
        if(grind != null) {
            NBTTagCompound out = new NBTTagCompound();
            grind.writeToNBT(out);
            compound.setTag("grindStack", out);
        }
    }

    @Nullable
    public ItemStack getGrindItem() {
        Optional<ItemStack> stack = dataManager.get(GRINDING);
        if(stack.isPresent()) {
            return stack.get();
        }
        return null;
    }

    public void setGrindItem(@Nullable ItemStack stack) {
        if(stack == null) {
            dataManager.set(GRINDING, Optional.<ItemStack>absent());
        } else {
            dataManager.set(GRINDING, Optional.of(stack));
        }
        dataManager.setDirty(GRINDING);
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        return null;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nullable ItemStack stack) {}

    @Override
    public EnumHandSide getPrimaryHand() {
        return EnumHandSide.RIGHT;
    }

}
