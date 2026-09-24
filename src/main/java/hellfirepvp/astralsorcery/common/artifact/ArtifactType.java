/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact;

import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactType
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactType {

    private final Supplier<String> unlocalizedName;

    public ArtifactType() {
        this.unlocalizedName = NameUtil.cacheName("artifact", RegistriesAS.REGISTRY_ARTIFACT_TYPES, this);
    }

    public MutableComponent getName() {
        return Component.translatable(this.unlocalizedName.get());
    }
}
