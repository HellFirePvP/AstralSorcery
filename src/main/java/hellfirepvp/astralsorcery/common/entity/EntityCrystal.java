/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityCrystal
 * Created by HellFirePvP
 * Date: 21.08.2019 / 21:57
 */
public class EntityCrystal extends EntityItemExplosionResistant {

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
