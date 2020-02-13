/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry.sets;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataSet;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityTransmutationEntry
 * Created by HellFirePvP
 * Date: 01.02.2020 / 13:55
 */
public class EntityTransmutationEntry implements ConfigDataSet {

    private EntityType<?> fromEntity;
    private EntityType<?> toEntity;

    public EntityTransmutationEntry(EntityType<?> fromEntity, EntityType<?> toEntity) {
        this.fromEntity = fromEntity;
        this.toEntity = toEntity;
    }

    public EntityType<?> getFromEntity() {
        return fromEntity;
    }

    public EntityType<?> getToEntity() {
        return toEntity;
    }

    @Nonnull
    @Override
    public String serialize() {
        return String.format("%s;%s", fromEntity.getRegistryName().toString(), toEntity.getRegistryName().toString());
    }

    @Nullable
    public static EntityTransmutationEntry deserialize(String str) throws IllegalArgumentException {
        String[] split = str.split(";");
        if (split.length != 2) {
            return null;
        }
        ResourceLocation fromKey = new ResourceLocation(split[0]);
        EntityType<?> fromType = ForgeRegistries.ENTITIES.getValue(fromKey);
        if (fromType == null) {
            throw new IllegalArgumentException(split[0] + " is not a known EntityType.");
        }
        ResourceLocation toKey = new ResourceLocation(split[1]);
        EntityType<?> toType = ForgeRegistries.ENTITIES.getValue(toKey);
        if (toType == null) {
            throw new IllegalArgumentException(split[0] + " is not a known EntityType.");
        }
        if (!toType.isSummonable() || toType.getClassification() == EntityClassification.MISC) {
            throw new IllegalArgumentException("EntityType " + split[1] + " seems to be not summonable or isn't classified as creature.");
        }
        return new EntityTransmutationEntry(fromType, toType);
    }
}
