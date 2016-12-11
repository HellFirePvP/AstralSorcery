package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.entities.EntityAquamarine;
import hellfirepvp.astralsorcery.common.entities.EntityCrystal;
import hellfirepvp.astralsorcery.common.entities.EntityGrindstone;
import hellfirepvp.astralsorcery.common.entities.EntityItemHighlighted;
import hellfirepvp.astralsorcery.common.entities.EntityItemStardust;
import hellfirepvp.astralsorcery.common.entities.EntityTelescope;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryEntities
 * Created by HellFirePvP
 * Date: 08.05.2016 / 23:19
 */
public class RegistryEntities {

    public static void init() {
        registerEntities();
    }

    private static void registerEntities() {
        int modEid = 0;

        //registerEntity(EntityTelescope.class, "EntityTelescope", modEid++, 64, 10, true);
        //registerEntity(EntityGrindstone.class, "EntityGrindstone", modEid++, 64, 10, true);
        registerEntity(EntityItemHighlighted.class, "EntityHighlighted", modEid++, 64, 20, true);
        registerEntity(EntityItemStardust.class, "EntityStardust", modEid++, 64, 20, true);
        registerEntity(EntityCrystal.class, "EntityCrystal", modEid++, 64, 20, true);
        registerEntity(EntityAquamarine.class, "EntityAquamarine", modEid++, 64, 20, true);
    }

    private static void registerEntity(Class<? extends Entity> entityClass, String name, int id, int trackingRange, int updateFreq, boolean sendVelUpdates) {
        EntityRegistry.registerModEntity(entityClass, name, id, AstralSorcery.instance, trackingRange, updateFreq, sendVelUpdates);
    }

}
