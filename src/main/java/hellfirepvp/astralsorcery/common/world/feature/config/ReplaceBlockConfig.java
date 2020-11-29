/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ReplaceBlockConfig
 * Created by HellFirePvP
 * Date: 20.11.2020 / 16:56
 */
public class ReplaceBlockConfig implements IFeatureConfig {

    public static final Codec<ReplaceBlockConfig> CODEC = RecordCodecBuilder.create((codecInstance) -> {
        return codecInstance.group(RuleTest.field_237127_c_.fieldOf("target").forGetter((config) -> {
            return config.target;
        }), BlockState.CODEC.fieldOf("state").forGetter((config) -> {
            return config.state;
        })).apply(codecInstance, ReplaceBlockConfig::new);
    });

    public final RuleTest target;
    public final BlockState state;

    public ReplaceBlockConfig(RuleTest target, BlockState state) {
        this.target = target;
        this.state = state;
    }
}
