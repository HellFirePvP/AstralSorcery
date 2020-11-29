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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.placement.IPlacementConfig;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChanceConfig
 * Created by HellFirePvP
 * Date: 19.11.2020 / 22:45
 */
public class ChanceConfig implements IPlacementConfig {

    public static final Codec<ChanceConfig> CODEC = RecordCodecBuilder.create(codecInstance -> {
        return codecInstance.group(Codec.FLOAT.fieldOf("chance").forGetter(config -> {
            return config.chance;
        })).apply(codecInstance, ChanceConfig::new);
    });

    private final float chance;

    public ChanceConfig(float chance) {
        this.chance = MathHelper.clamp(chance, 0F, 1F);
    }

    public boolean test(Random rand) {
        return rand.nextFloat() < this.chance;
    }
}
