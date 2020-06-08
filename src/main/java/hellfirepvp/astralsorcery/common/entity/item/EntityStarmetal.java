/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.item;

import hellfirepvp.astralsorcery.common.entity.InteractableEntity;
import hellfirepvp.astralsorcery.common.item.ItemChisel;
import hellfirepvp.astralsorcery.common.item.ItemStarmetalIngot;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityStarmetal
 * Created by HellFirePvP
 * Date: 17.05.2020 / 10:08
 */
public class EntityStarmetal extends ItemEntity implements InteractableEntity {

    public EntityStarmetal(EntityType<? extends ItemEntity> type, World world) {
        super(type, world);
    }

    public EntityStarmetal(EntityType<? extends ItemEntity> type, World world, double x, double y, double z) {
        this(type, world);
        this.setPosition(x, y, z);
        this.rotationYaw = this.rand.nextFloat() * 360.0F;
        this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
    }

    public EntityStarmetal(EntityType<? extends ItemEntity> type, World world, double x, double y, double z, ItemStack stack) {
        this(type, world, x, y, z);
        this.setItem(stack);
        this.lifespan = stack.isEmpty() ? 6000 : stack.getEntityLifespan(world);
    }

    public static EntityType.IFactory<EntityStarmetal> factoryStarmetalIngot() {
        return (spawnEntity, world) -> new EntityStarmetal(EntityTypesAS.ITEM_STARMETAL_INGOT, world);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return true;
    }

    @Override
    public boolean hitByEntity(Entity entity) {
        if (!this.getEntityWorld().isRemote() && entity instanceof ServerPlayerEntity) {
            ItemStack held = ((ServerPlayerEntity) entity).getHeldItem(Hand.MAIN_HAND);
            if (!held.isEmpty() && held.getItem() instanceof ItemChisel) {

                ItemStack thisStack = this.getItem();
                if (!thisStack.isEmpty() && thisStack.getItem() instanceof ItemStarmetalIngot) {

                    boolean doDamage = false;
                    if (rand.nextFloat() < 0.4F) {
                        int fortuneLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, held);
                        doDamage = this.createStardust(fortuneLevel);
                    }
                    if (doDamage || rand.nextFloat() < 0.35F) {
                        held.damageItem(1, (PlayerEntity) entity, (player) -> player.sendBreakAnimation(Hand.MAIN_HAND));
                    }
                }
            }
        }
        return true;
    }

    private boolean createStardust(int fortuneLevel) {
        ItemStack created = new ItemStack(ItemsAS.STARDUST);
        ItemUtils.dropItemNaturally(getEntityWorld(), this.getPosX(), this.getPosY() + 0.25F, this.getPosZ(), created);

        float breakIngot = 0.90F;
        breakIngot -= MathHelper.clamp(fortuneLevel, 0, 10) * 0.06F;
        if (rand.nextFloat() < breakIngot) {
            ItemStack thisStack = this.getItem();
            thisStack.shrink(1);
            this.setItem(thisStack);
        }
        return true;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
