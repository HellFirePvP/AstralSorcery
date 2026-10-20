/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.config;

import hellfirepvp.astralsorcery.common.config.ConfigEntry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderingConfig
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderingConfig extends ConfigEntry {

    public static final RenderingConfig CONFIG = new RenderingConfig();

    public ModConfigSpec.BooleanValue patreonEffects;
    public ModConfigSpec.BooleanValue animateAdditionalInputs;
    public ModConfigSpec.ConfigValue<List<? extends String>> skyRenderDimensions;
    public ModConfigSpec.ConfigValue<List<? extends String>> constellationOverlaySkyDimensions;

    private RenderingConfig() {
        super("rendering");
    }

    @Override
    public void createEntries(ModConfigSpec.Builder cfgBuilder) {
        this.patreonEffects = cfgBuilder
                .comment("Enable/Disable patreon effects")
                .translation(translationKey("patreonEffects"))
                .define("patreonEffects", true);
        this.animateAdditionalInputs = cfgBuilder
                .comment("Animate additional inputs on altar recipe pages in the tome")
                .translation(translationKey("animateAdditionalInputs"))
                .define("animateAdditionalInputs", true);
        this.skyRenderDimensions = cfgBuilder
                .comment("Dimensions in which to do Astral Sorcery's sky render. Only applies to dimensions with a normal sky type.")
                .translation(translationKey("skyRenderDimensions"))
                .defineListAllowEmpty("skyRenderDimensions", List.of("minecraft:overworld"), () -> "minecraft:overworld", s -> s instanceof String str && ResourceLocation.tryParse(str) != null);
        this.constellationOverlaySkyDimensions = cfgBuilder
                .comment("Dimensions in which to only render constellations on top of its sky render. Ignored for dimensions listed in skyRenderDimensions. Be aware that this also prevents the Solar and Lunar Eclipses from being shown/rendered in the sky")
                .translation(translationKey("constellationOverlaySkyDimensions"))
                .defineListAllowEmpty("constellationOverlaySkyDimensions", List.of(), () -> "minecraft:overworld", s -> s instanceof String str && ResourceLocation.tryParse(str) != null);
    }
}
