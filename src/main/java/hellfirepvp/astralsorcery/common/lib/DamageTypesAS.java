/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.DamageTypeRegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DamageTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DamageTypesAS {

    public static final DamageTypeRegistryObject STELLAR =
            register("stellar", () -> new DamageType("astralsorcery.stellar", DamageScaling.ALWAYS, 0F));

    private static <T extends DamageType> DamageTypeRegistryObject register(String name, Supplier<DamageType> type) {
        return new DamageTypeRegistryObject(ResourceKey.create(Registries.DAMAGE_TYPE, AstralSorcery.key(name)), type);
    }
}
