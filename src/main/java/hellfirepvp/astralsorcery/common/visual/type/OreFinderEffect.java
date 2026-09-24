/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.visual.type;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXScaleFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: OreFinderEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class OreFinderEffect implements VisualEffectTypes.Effect {

    public static final StreamCodec<RegistryFriendlyByteBuf, OreFinderEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(size -> new HashMap<>(), ByteBufCodecs.fromCodecTrusted(BlockState.CODEC), BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list())),
            effect -> effect.effectStates,
            OreFinderEffect::new);
    public static final VisualEffectTypes.EffectType<OreFinderEffect> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("ore_finder"), STREAM_CODEC);

    private final Map<BlockState, List<BlockPos>> effectStates = new HashMap<>();

    private OreFinderEffect(Map<BlockState, List<BlockPos>> effectStates) {
        this.effectStates.putAll(effectStates);
    }

    public static OreFinderEffect create(Level level, List<BlockPos> positions) {
        Map<BlockState, List<BlockPos>> states = new HashMap<>();
        for (BlockPos pos : positions) {
            states.computeIfAbsent(level.getBlockState(pos), s -> new ArrayList<>()).add(pos.immutable());
        }
        return new OreFinderEffect(states);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playEffect(RandomSource rand) {
        this.effectStates.forEach((state, positions) -> {
            ColorWrapper color = this.lookupColorPotential(state);
            positions.forEach(pos -> {
                int count = rand.nextInt(10);
                for (int i = 0; i < count; i++) {
                    Vector3 v = new Vector3(pos).add(Vector3.positiveRandom(rand));
                    float scale = 0.6F + rand.nextFloat() * 0.3F;
                    float gravity = rand.nextFloat() * 0.0004F;
                    int age = 60 + rand.nextInt(40);

                    EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE_DEPTH)
                            .spawn(v)
                            .setScale(scale)
                            .setAlpha(0.4F)
                            .color(FXColorFunction.constant(color))
                            .setGravity(Vector3.y(gravity))
                            .setMaxAge(age);
                    EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE_DEPTH)
                            .spawn(v)
                            .setScale(scale * 0.3F)
                            .setAlpha(0.4F)
                            .color(FXColorFunction.WHITE)
                            .setGravity(Vector3.y(gravity))
                            .setMaxAge(age);
                }
            });
        });
    }

    private ColorWrapper lookupColorPotential(BlockState state) {
        if (state.is(BlocksAS.AQUAMARINE_SHALE)) return ColorsAS.DYE_LIGHT_BLUE;
        if (state.is(Tags.Blocks.ORES_IRON)) return ColorsAS.DYE_BROWN;
        if (state.is(Tags.Blocks.ORES_GOLD)) return ColorsAS.DYE_YELLOW;
        if (state.is(Tags.Blocks.ORES_COPPER)) return ColorsAS.DYE_ORANGE;
        if (state.is(Tags.Blocks.ORES_COAL)) return ColorsAS.DYE_BLACK;
        if (state.is(Tags.Blocks.ORES_DIAMOND)) return ColorsAS.DYE_LIGHT_BLUE;
        if (state.is(Tags.Blocks.ORES_EMERALD)) return ColorsAS.DYE_GREEN;
        if (state.is(Tags.Blocks.ORES_LAPIS)) return ColorsAS.DYE_BLUE;
        if (state.is(Tags.Blocks.ORES_REDSTONE)) return ColorsAS.DYE_RED;
        if (state.is(Tags.Blocks.ORES_QUARTZ)) return ColorsAS.DYE_WHITE;
        if (state.is(Tags.Blocks.ORES_NETHERITE_SCRAP)) return ColorsAS.DYE_BROWN;
        return ColorWrapper.WHITE;
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }
}
