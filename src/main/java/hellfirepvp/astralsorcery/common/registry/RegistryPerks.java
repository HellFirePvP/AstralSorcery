/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.perk.node.key.*;
import hellfirepvp.astralsorcery.common.perk.node.root.*;
import hellfirepvp.astralsorcery.common.perk.type.AttributeTypeDodge;
import hellfirepvp.astralsorcery.common.perk.type.AttributeTypeMiningSize;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryPerks
 * Created by HellFirePvP
 * Date: 25.07.2020 / 13:31
 */
public class RegistryPerks {

    private RegistryPerks() {}

    public static void initConfig(Consumer<ConfigEntry> registrar) {
        registrar.accept(AttributeTypeMiningSize.CONFIG);
        registrar.accept(AttributeTypeDodge.CONFIG);

        registrar.accept(KeyBleed.CONFIG);
        registrar.accept(KeyCheatDeath.CONFIG);
        registrar.accept(KeyCullingAttack.CONFIG);
        registrar.accept(KeyDamageArmor.CONFIG);
        registrar.accept(KeyDamageEffects.CONFIG);
        registrar.accept(KeyDisarm.CONFIG);
        registrar.accept(KeyGrowables.CONFIG);
        registrar.accept(KeyLastBreath.CONFIG);
        registrar.accept(KeyLightningArc.CONFIG);
        registrar.accept(KeyMending.CONFIG);
        registrar.accept(KeyNoArmor.CONFIG);
        registrar.accept(KeyProjectileDistance.CONFIG);
        registrar.accept(KeyProjectileProximity.CONFIG);
        registrar.accept(KeyRampage.CONFIG);
        registrar.accept(KeySpawnLights.CONFIG);
        registrar.accept(KeyStoneEnrichment.CONFIG);
        registrar.accept(KeyVoidTrash.CONFIG);

        registrar.accept(RootAevitas.CONFIG);
        registrar.accept(RootArmara.CONFIG);
        registrar.accept(RootDiscidia.CONFIG);
        registrar.accept(RootEvorsio.CONFIG);
        registrar.accept(RootVicio.CONFIG);
    }
}
