/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemEntityReplacement
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ItemEntityReplacement extends ItemEntity {

    @Nullable
    private ItemEntity replacedEntity;

    public ItemEntityReplacement(EntityType<? extends ItemEntityReplacement> entityType, Level level) {
        super(entityType, level);
    }

    public ItemEntityReplacement(Level level, double posX, double posY, double posZ, ItemStack itemStack) {
        super(level, posX, posY, posZ, itemStack);
    }

    public ItemEntityReplacement(Level level, double posX, double posY, double posZ, ItemStack itemStack, double deltaX, double deltaY, double deltaZ) {
        super(level, posX, posY, posZ, itemStack, deltaX, deltaY, deltaZ);
    }

    public static <T extends ItemEntityReplacement> T replace(EntityType<T> type, ItemEntity toReplace) {
        Level level = toReplace.level();
        T newEntity = type.create(level);
        if (newEntity == null) {
            throw new IllegalStateException("Failed to create entity of type " + type);
        }
        newEntity.setPos(toReplace.getX(), toReplace.getY(), toReplace.getZ());
        newEntity.setDeltaMovement(level.random.nextDouble() * 0.2 - 0.1, 0.2, level.random.nextDouble() * 0.2 - 0.1);
        newEntity.setItem(toReplace.getItem());
        newEntity.lifespan = toReplace.getItem().getEntityLifespan(level);
        newEntity.bobOffs = toReplace.bobOffs;
        newEntity.age = toReplace.age;
        newEntity.load(toReplace.saveWithoutId(new CompoundTag()));
        newEntity.setReplacedEntity(toReplace);
        return newEntity;
    }

    public void setReplacedEntity(@Nullable ItemEntity replacedEntity) {
        this.replacedEntity = replacedEntity;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) return;

        // If the replaced item seems to be a fake-item, remove this one as well.
        // See ItemEntity#makeFakeItem
        if (this.replacedEntity != null &&
                this.tickCount < 5 &&
                !this.replacedEntity.isAlive() &&
                this.replacedEntity.pickupDelay == Short.MAX_VALUE &&
                this.replacedEntity.age == this.getItem().getEntityLifespan(this.level()) - 1) {
            this.remove(RemovalReason.DISCARDED);
        }
    }
}
