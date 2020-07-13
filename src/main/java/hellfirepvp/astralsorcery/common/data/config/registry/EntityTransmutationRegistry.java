/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataAdapter;
import hellfirepvp.astralsorcery.common.data.config.registry.sets.EntityTransmutationEntry;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityTransmutationRegistry
 * Created by HellFirePvP
 * Date: 01.02.2020 / 13:55
 */
public class EntityTransmutationRegistry extends ConfigDataAdapter<EntityTransmutationEntry> {

    public static final EntityTransmutationRegistry INSTANCE = new EntityTransmutationRegistry();

    private EntityTransmutationRegistry() {}

    @Override
    public List<EntityTransmutationEntry> getDefaultValues() {
        return Lists.newArrayList(
                new EntityTransmutationEntry(EntityType.SKELETON, EntityType.WITHER_SKELETON),
                new EntityTransmutationEntry(EntityType.VILLAGER, EntityType.WITCH),
                new EntityTransmutationEntry(EntityType.PIG,      EntityType.ZOMBIE_PIGMAN),
                new EntityTransmutationEntry(EntityType.COW,      EntityType.ZOMBIE),
                new EntityTransmutationEntry(EntityType.PARROT,   EntityType.GHAST),
                new EntityTransmutationEntry(EntityType.CHICKEN,  EntityType.BLAZE),
                new EntityTransmutationEntry(EntityType.SHEEP,    EntityType.STRAY),
                new EntityTransmutationEntry(EntityType.HORSE,    EntityType.SKELETON_HORSE)
        );
    }

    @Nullable
    public EntityType<?> getEntityTransmuteTo(EntityType<?> from) {
        EntityTransmutationEntry transmutation = this.getConfiguredValues()
                .stream()
                .filter(t -> t.getFromEntity().equals(from))
                .findFirst()
                .orElse(null);
        if (transmutation != null) {
            return transmutation.getToEntity();
        }
        return null;
    }

    @Nullable
    public LivingEntity transmuteEntity(ServerWorld world, LivingEntity entity) {
        EntityType<?> transmute = getEntityTransmuteTo(entity.getType());
        if (transmute != null) {
            CompoundNBT tag = new CompoundNBT();
            entity.writeWithoutTypeId(tag);
            world.removeEntity(entity);
            NBTHelper.removeUUID(tag, "UUID");
            try {
                Entity e = transmute.create(world);
                if (!(e instanceof LivingEntity)) {
                    return null;
                }
                e.read(tag);
                return (LivingEntity) e;
            } catch (Exception exc) {
                return null;
            }
        }
        return null;
    }

    @Override
    public String getSectionName() {
        return "entity_transmutation";
    }

    @Override
    public String getCommentDescription() {
        return "Defines the entity types the corrupted pelotrio ritual can transmute from and to. " +
                "Format: <EntityTypeFrom>;<EntityTypeTo>";
    }

    @Override
    public String getTranslationKey() {
        return translationKey("data");
    }

    @Override
    public Predicate<Object> getValidator() {
        return obj -> obj instanceof String;
    }

    @Nullable
    @Override
    public EntityTransmutationEntry deserialize(String string) throws IllegalArgumentException {
        return EntityTransmutationEntry.deserialize(string);
    }
}
