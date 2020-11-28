package hellfirepvp.astralsorcery.common.entity;

import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityShootingStar
 * Created by HellFirePvP
 * Date: 26.11.2020 / 19:30
 */
public class EntityShootingStar extends ThrowableEntity {

    protected EntityShootingStar(World worldIn) {
        super(EntityTypesAS.SHOOTING_STAR, worldIn);
    }

    protected EntityShootingStar(double x, double y, double z, World worldIn) {
        super(EntityTypesAS.SHOOTING_STAR, x, y, z, worldIn);
    }

    public static EntityType.IFactory<EntityShootingStar> factory() {
        return (type, world) -> new EntityShootingStar(world);
    }

    @Override
    protected void registerData() {

    }
}
