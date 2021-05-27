/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2021
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityCustomItemReplacement extends ItemEntity {

    @Nullable
    private ItemEntity replacedEntity;

    public EntityCustomItemReplacement(EntityType<? extends ItemEntity> type, World world) {
        super(type, world);
    }

    public EntityCustomItemReplacement(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityCustomItemReplacement(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    public void setReplacedEntity(@Nullable ItemEntity replacedEntity) {
        this.replacedEntity = replacedEntity;
    }

    @Override
    public void tick() {
        super.tick();

        if (getEntityWorld().isRemote()) {
            return;
        }

        //If the replaced item seems to be a fake-item, remove this one as well.
        //See ItemEntity#makeFakeItem
        if (this.replacedEntity != null &&
                this.ticksExisted < 5 &&
                !this.replacedEntity.isAlive() &&
                this.replacedEntity.pickupDelay == Short.MAX_VALUE &&
                replacedEntity.age == getItem().getEntityLifespan(getEntityWorld()) - 1) {
            this.remove();
        }
    }
}
