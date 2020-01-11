/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry.sets;

import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataSet;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityTechnicalEntry
 * Created by HellFirePvP
 * Date: 28.07.2019 / 09:40
 */
public class EntityTechnicalEntry implements ConfigDataSet {

    private final ResourceLocation name;

    public EntityTechnicalEntry(@Nullable Mods mod, String name) {
        this(new ResourceLocation(mod == null ? "minecraft" : mod.getModId(), name));
    }

    public EntityTechnicalEntry(ResourceLocation name) {
        this.name = name;
    }

    public EntityType<?> getEntityType() {
        return ForgeRegistries.ENTITIES.getValue(this.name);
    }

    @Nonnull
    @Override
    public String serialize() {
        return this.name.toString();
    }

    public static EntityTechnicalEntry deserialize(String string) throws IllegalArgumentException {
        ResourceLocation name = new ResourceLocation(string);
        Mods mod = Mods.byModId(name.getNamespace());
        if (mod != null && !mod.isPresent()) {
            throw new IllegalArgumentException("Mod not present: " + mod.getModId());
        }
        if (ForgeRegistries.ENTITIES.getValue(name) == null) {
            throw new IllegalArgumentException("Unknown Entity Type: " + name);
        }
        return new EntityTechnicalEntry(name);
    }
}
