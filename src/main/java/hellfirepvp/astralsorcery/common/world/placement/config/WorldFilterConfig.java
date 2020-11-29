/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.placement.IPlacementConfig;

import java.util.List;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldFilterConfig
 * Created by HellFirePvP
 * Date: 20.11.2020 / 15:52
 */
public class WorldFilterConfig implements IPlacementConfig {

    public static final Codec<WorldFilterConfig> CODEC = RecordCodecBuilder.create(codecInstance -> {
        return codecInstance.group(Codec.BOOL.fieldOf("ignoreFilter").forGetter(config -> {
            return config.ignoreFilter.get();
        }), World.CODEC.listOf().fieldOf("worldFilter").forGetter(config -> {
            return config.worldFilter.get();
        })).apply(codecInstance, WorldFilterConfig::new);
    });

    private final Supplier<Boolean> ignoreFilter;
    private final Supplier<List<RegistryKey<World>>> worldFilter;

    public WorldFilterConfig(boolean ignoreFilter, List<RegistryKey<World>> worldFilter) {
        this(() -> ignoreFilter, () -> worldFilter);
    }

    public WorldFilterConfig(Supplier<Boolean> ignoreFilter, Supplier<List<RegistryKey<World>>> worldFilter) {
        this.ignoreFilter = ignoreFilter;
        this.worldFilter = worldFilter;
    }

    public boolean generatesIn(IServerWorld world) {
         return this.ignoreFilter.get() || this.worldFilter.get().contains(world.getWorld().getDimensionKey());
    }
}
