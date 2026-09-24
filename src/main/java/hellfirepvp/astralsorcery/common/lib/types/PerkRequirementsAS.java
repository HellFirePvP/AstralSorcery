/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.types;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirementConstellation;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirementProgress;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkRequirementsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkRequirementsAS {

    public static final DeferredRegister<PerkRequirement.Type<?>> PERK_REQUIREMENT_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_PERK_REQUIREMENT_TYPES, AstralSorcery.MODID);

    public static final DeferredHolder<PerkRequirement.Type<?>, PerkRequirement.Type<PerkRequirementConstellation>> CONSTELLATION =
            PERK_REQUIREMENT_REGISTER.register("constellation", () -> PerkRequirementConstellation.TYPE);
    public static final DeferredHolder<PerkRequirement.Type<?>, PerkRequirement.Type<PerkRequirementProgress>> PROGRESS =
            PERK_REQUIREMENT_REGISTER.register("progress", () -> PerkRequirementProgress.TYPE);
}
