/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.data.config.entry;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingConfig
 * Created by HellFirePvP
 * Date: 20.04.2019 / 14:45
 */
public class RenderingConfig extends ConfigEntry {

    public static final RenderingConfig CONFIG = new RenderingConfig();

    public ForgeConfigSpec.DoubleValue maxEffectRenderDistance;
    public ForgeConfigSpec.EnumValue<ParticleAmount> particleAmount;
    public ForgeConfigSpec.BooleanValue patreonEffects;
    public ForgeConfigSpec.IntValue minYFosicDisplay;

    public ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionsWithSkyRendering;
    public ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionsWithOnlyConstellationRendering;

    private RenderingConfig() {
        super("rendering");
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        maxEffectRenderDistance = cfgBuilder
                .comment("Defines how close to the position of a particle/floating texture you have to be in order for it to render.")
                .translation(translationKey("maxEffectRenderDistance"))
                .defineInRange("maxEffectRenderDistance", 64.0, 1, 512);
        particleAmount = cfgBuilder
                .comment("Sets the amount of particles/effects")
                .translation(translationKey("particleAmount"))
                .defineEnum("particleAmount", ParticleAmount.ALL);
        patreonEffects = cfgBuilder
                .comment("Enables/Disables all patreon effects.")
                .translation(translationKey("patreonEffects"))
                .define("patreonEffects", true);
        minYFosicDisplay = cfgBuilder
                .comment("Defines the minimum y-level the fosic resonator will display the fosic field on.")
                .translation(translationKey("minYFosicDisplay"))
                .defineInRange("minYFosicDisplay", 0, 0, 256);

        dimensionsWithSkyRendering = cfgBuilder
                .comment("Whitelist of dimension ID's that will have special astral sorcery sky rendering")
                .translation(translationKey("skyRenderingEnabled"))
                .defineList("skyRenderingEnabled", Lists.newArrayList(World.OVERWORLD.getLocation().toString()), Predicates.alwaysTrue());

        dimensionsWithOnlyConstellationRendering = cfgBuilder
                .comment("If a dimension is listed here, the skyrender will only render constellations on top of the existing skybox.")
                .translation(translationKey("skyRenderingConstellations"))
                .defineList("skyRenderingConstellations", Lists.newArrayList(), Predicates.alwaysTrue());
    }

    public double getMaxEffectRenderDistanceSq() {
        double val = this.maxEffectRenderDistance.get();
        return val * val;
    }

    public static enum ParticleAmount {

        NONE(455) {
            @Override
            public boolean shouldSpawn(Random r) {
                return false;
            }
        },
        MINIMAL(10),
        LOWERED(4),
        ALL(1);

        private final int rChance;

        ParticleAmount(int rChance) {
            this.rChance = rChance;
        }

        public boolean shouldSpawn(Random r) {
            return r.nextInt(this.rChance) == 0;
        }

        public ParticleAmount less() {
            return values()[Math.max(this.ordinal() - 1, 0)];
        }

    }

}
