/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.item;

import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.entity.InteractableEntity;
import hellfirepvp.astralsorcery.common.item.ItemChisel;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCrystalBase;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
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
 * Class: EntityCrystal
 * Created by HellFirePvP
 * Date: 21.08.2019 / 21:57
 */
public class EntityCrystal extends EntityItemExplosionResistant implements InteractableEntity {

    public EntityCrystal(EntityType<? extends ItemEntity> type, World world) {
        super(type, world);
    }

    public EntityCrystal(EntityType<? extends ItemEntity> type, World world, double x, double y, double z) {
        super(type, world, x, y, z);
    }

    public EntityCrystal(EntityType<? extends ItemEntity> type, World world, double x, double y, double z, ItemStack stack) {
        super(type, world, x, y, z, stack);
    }

    public static EntityType.IFactory<EntityCrystal> factoryCrystal() {
        return (spawnEntity, world) -> new EntityCrystal(EntityTypesAS.ITEM_CRYSTAL, world);
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
                if (!thisStack.isEmpty() && thisStack.getItem() instanceof ItemCrystalBase) {

                    CrystalAttributes thisAttributes = ((ItemCrystalBase) thisStack.getItem()).getAttributes(thisStack);
                    if (thisAttributes != null) {

                        //TODO chipping sound ?
                        boolean doDamage = false;
                        if (rand.nextFloat() < 0.35F) {
                            int fortuneLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, held);
                            doDamage = this.splitCrystal(thisAttributes, fortuneLevel);
                        }
                        if (doDamage || rand.nextFloat() < 0.35F) {
                            held.damageItem(1, (PlayerEntity) entity, (player) -> player.sendBreakAnimation(Hand.MAIN_HAND));
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean splitCrystal(CrystalAttributes thisAttributes, int fortuneLevel) {
        ItemCrystalBase newBase = ((ItemCrystalBase) this.getItem().getItem()).getInertDuplicateItem();
        if (newBase == null) {
            return false;
        }
        ItemStack created = new ItemStack(newBase);
        if (created.isEmpty()) {
            return false;
        }
        int maxSplit = MathHelper.ceil(thisAttributes.getTotalTierLevel() / 2F);
        if (maxSplit >= thisAttributes.getTotalTierLevel()) {
            return false;
        }

        int lostModifiers = 0;
        if (maxSplit > 1 && rand.nextFloat() < (0.6F / (fortuneLevel + 1))) {
            lostModifiers++;
            if (maxSplit > 2 && rand.nextFloat() < (0.2F / (fortuneLevel + 1))) {
                lostModifiers++;
            }
        }

        CrystalAttributes resultThisAttributes = thisAttributes;
        CrystalAttributes.Builder resultSplitAttributes = CrystalAttributes.Builder.newBuilder(false);
        for (int i = 0; i < maxSplit; i++) {
            CrystalProperty prop = MiscUtils.getRandomEntry(resultThisAttributes.getProperties(), rand);
            if (prop == null) {
                break;
            }
            resultThisAttributes = resultThisAttributes.modifyLevel(prop, -1);
            if (lostModifiers > 0) {
                lostModifiers--;
            } else {
                resultSplitAttributes.addProperty(prop, 1);
            }
        }

        ((ItemCrystalBase) this.getItem().getItem()).setAttributes(this.getItem(), resultThisAttributes);
        newBase.setAttributes(created, resultSplitAttributes.build());
        ItemUtils.dropItemNaturally(getEntityWorld(), this.getPosX(), this.getPosY() + 0.25F, this.getPosZ(), created);
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (!world.isRemote() && this.age + 10 >= this.lifespan) {
            this.age = 0;
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
