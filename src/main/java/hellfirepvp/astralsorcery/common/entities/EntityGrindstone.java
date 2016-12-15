package hellfirepvp.astralsorcery.common.entities;

import com.google.common.base.Optional;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
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

    public static final int TICKS_WHEEL_ROTATION = 20;

    public int tickWheelAnimation = 0, prevTickWheelAnimation = 0;
    private boolean repeat = false; //Used for repeat after effect went off..~

    public EntityGrindstone(World worldIn) {
        super(worldIn);
        getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10D);
        setSize(0.76F, 0.8F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(world.isRemote) {
            if(tickWheelAnimation >= 0) {
                prevTickWheelAnimation = tickWheelAnimation;
                tickWheelAnimation--;
                if(tickWheelAnimation <= 0 && repeat) {
                    tickWheelAnimation = TICKS_WHEEL_ROTATION;
                    prevTickWheelAnimation = TICKS_WHEEL_ROTATION + 1;
                    repeat = false;
                }
            } else {
                prevTickWheelAnimation = 0;
            }
        }
    }

    @Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
        if(damageSrc.equals(DamageSource.inWall)) return;
        super.damageEntity(damageSrc, damageAmount);
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(GRINDING, Optional.<ItemStack>absent());
    }

    /*@Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, @Nullable ItemStack stack, EnumHand hand) {
        player.swingArm(hand);
        if(hand == EnumHand.OFF_HAND) {
            return EnumActionResult.SUCCESS;
        }
        ItemStack grind = getGrindItem();
        if(!world.isRemote) {
            if(grind != null) {
                if(player.isSneaking()) {
                    ItemUtils.dropItem(world, posX, posY + 1.3F, posZ, grind);

                    setGrindItem(null);
                } else {
                    Item i = grind.getAttItem();
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
                                world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.AMBIENT, 0.5F, world.rand.nextFloat() * 0.2F + 0.8F);
                                break;
                        }
                    } else if(i instanceof ItemSword) {
                        if(rand.nextInt(40) == 0) {
                            SwordSharpenHelper.setSwordSharpened(grind);
                        }
                    }
                }
            } else {
                if(player.isSneaking()) {
                    setDead();
                    if(!player.isCreative()) {
                        //ItemUtils.dropItemNaturally(world, posX, posY + 0.3, posZ, new ItemStack(ItemsAS.entityPlacer, 1, ItemEntityPlacer.PlacerType.GRINDSTONE.getMeta()));
                    }
                } else {
                    if(stack != null) {
                        Item trySet = stack.getAttItem();
                        if(trySet instanceof IGrindable && ((IGrindable) trySet).canGrind(this, stack)) {
                            ItemStack toSet = stack.copy();
                            toSet.stackSize = 1;
                            setGrindItem(toSet);
                            world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.AMBIENT, 0.5F, world.rand.nextFloat() * 0.2F + 0.8F);

                            if(!player.isCreative()) {
                                stack.stackSize--;
                            }
                        } else if(trySet instanceof ItemSword && !SwordSharpenHelper.isSwordSharpened(stack)) {
                            ItemStack toSet = stack.copy();
                            toSet.stackSize = 1;
                            setGrindItem(toSet);
                            world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.AMBIENT, 0.5F, world.rand.nextFloat() * 0.2F + 0.8F);

                            if(!player.isCreative()) {
                                stack.stackSize--;
                            }
                        }
                    }
                }
            }
        } else {
            if(grind != null && !player.isSneaking()) {
                Item i = grind.getAttItem();
                if(i instanceof IGrindable) {
                    if(((IGrindable) i).canGrind(this, grind)) {
                        for (int j = 0; j < 12; j++) {
                            world.spawnParticle(EnumParticleTypes.CRIT, posX, posY + 0.9, posZ - 0.75,
                                    (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.5,
                                    (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.5,
                                    (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.5);
                        }
                        playWheelEffect();
                    }
                } else if(SwordSharpenHelper.canBeSharpened(grind) && !SwordSharpenHelper.isSwordSharpened(grind)) {
                    for (int j = 0; j < 12; j++) {
                        world.spawnParticle(EnumParticleTypes.CRIT, posX, posY + 0.9, posZ - 0.75,
                                (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.5,
                                (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.5,
                                (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.5);
                    }
                    playWheelEffect();
                }
            }
        }
        return EnumActionResult.PASS;
    }*/

    @Override
    public void onDeath(DamageSource cause) {
        ItemStack grind = getGrindItem();
        if(grind != null) {
            ItemUtils.dropItem(world, posX, posY + 1.3F, posZ, grind);
        }
        super.onDeath(cause);
    }

    public void playWheelEffect() {
        /*PktPlayEffect effect = new PktPlayEffect(PktPlayEffect.EffectType.GRINDSTONE_WHEEL, this);
        if(world.isRemote) {
            playWheelAnimation(effect);
        } else {
            PacketChannel.CHANNEL.sendToAllAround(effect, PacketChannel.pointFromPos(world, getPosition(), 32));
        }*/
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

    @SideOnly(Side.CLIENT)
    public static void playWheelAnimation(PktPlayEffect event) {
        Entity e = null;//event.getClientWorldEntity();
        if(e != null && e instanceof EntityGrindstone) {
            int ticks = ((EntityGrindstone) e).tickWheelAnimation;
            if(ticks > 0) {
                if(ticks * 2 <= TICKS_WHEEL_ROTATION) {
                    ((EntityGrindstone) e).repeat = true;
                }
            } else {
                ((EntityGrindstone) e).tickWheelAnimation = TICKS_WHEEL_ROTATION;
                ((EntityGrindstone) e).repeat = false;
            }
        }
    }

}
