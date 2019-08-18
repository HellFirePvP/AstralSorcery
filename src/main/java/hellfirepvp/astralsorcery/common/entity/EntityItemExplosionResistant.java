/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityItemExplosionResistant
 * Created by HellFirePvP
 * Date: 18.08.2019 / 11:22
 */
public class EntityItemExplosionResistant extends EntityItemHighlighted {

    public EntityItemExplosionResistant(World world) {
        super(EntityTypesAS.ITEM_EXPLOSION_RESISTANT, world);
    }

    public EntityItemExplosionResistant(EntityType<? extends ItemEntity> type, World world) {
        super(type, world);
    }

    public EntityItemExplosionResistant(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityItemExplosionResistant(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    public static EntityType.IFactory<EntityItemExplosionResistant> factoryExplosionResistant() {
        return (spawnEntity, world) -> new EntityItemExplosionResistant(world);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return !source.isExplosion() && super.attackEntityFrom(source, amount);
    }
}
