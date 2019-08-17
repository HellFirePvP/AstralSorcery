/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.render.entity.EmptyRenderEntity;
import hellfirepvp.astralsorcery.common.entity.EntityIlluminationSpark;
import hellfirepvp.astralsorcery.common.entity.EntityNocturnalSpark;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.network.FMLPlayMessages;

import java.util.function.BiFunction;

import static hellfirepvp.astralsorcery.common.lib.EntityTypesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryEntities
 * Created by HellFirePvP
 * Date: 17.08.2019 / 08:47
 */
public class RegistryEntities {

    private RegistryEntities() {}

    public static void init() {
        NOCTURNAL_SPARK = register("nocturnal_spark",
                EntityType.Builder.create(EntityNocturnalSpark.factory(), EntityClassification.MISC)
                        .disableSummoning()
                        .immuneToFire()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(32)
                        .setCustomClientFactory((spawnEntity, world) -> new EntityNocturnalSpark(world))
                        .size(0.1F, 0.1F));
        ILLUMINATION_SPARK = register("illumination_spark",
                EntityType.Builder.create(EntityIlluminationSpark.factory(), EntityClassification.MISC)
                        .disableSummoning()
                        .immuneToFire()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(32)
                        .setCustomClientFactory((spawnEntity, world) -> new EntityIlluminationSpark(world))
                        .size(0.1F, 0.1F));
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        RenderingRegistry.registerEntityRenderingHandler(EntityNocturnalSpark.class, new EmptyRenderEntity.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityIlluminationSpark.class, new EmptyRenderEntity.Factory());
    }

    private static <E extends Entity> EntityType<E> register(String name, EntityType.Builder<E> typeBuilder) {
        EntityType<E> type = typeBuilder.build(name);
        type.setRegistryName(new ResourceLocation(AstralSorcery.MODID, name));
        AstralSorcery.getProxy().getRegistryPrimer().register(type);
        return type;
    }

}
