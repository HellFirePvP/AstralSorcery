/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.types;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.artifact.ArtifactType;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactTypesAS {

    public static final DeferredRegister<ArtifactType> ARTIFACT_TYPES_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_ARTIFACT_TYPES, AstralSorcery.MODID);

    public static final DeferredHolder<ArtifactType, ArtifactType> SIDEREAL = simpleType("sidereal");
    public static final DeferredHolder<ArtifactType, ArtifactType> LUMINOUS = simpleType("luminous");
    public static final DeferredHolder<ArtifactType, ArtifactType> CHRONAL = simpleType("chronal");
    public static final DeferredHolder<ArtifactType, ArtifactType> ASTRIFORM = simpleType("astriform");
    public static final DeferredHolder<ArtifactType, ArtifactType> AXIOMATIC = simpleType("axiomatic");
    public static final DeferredHolder<ArtifactType, ArtifactType> SPECTRAL = simpleType("spectral");

    private static DeferredHolder<ArtifactType, ArtifactType> simpleType(String name) {
        return ARTIFACT_TYPES_REGISTER.register(name, ArtifactType::new);
    }
}
