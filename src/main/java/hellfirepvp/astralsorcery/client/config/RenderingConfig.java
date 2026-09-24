/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.config;

import hellfirepvp.astralsorcery.common.config.ConfigEntry;
import net.neoforged.neoforge.common.ModConfigSpec;

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
    }
}
