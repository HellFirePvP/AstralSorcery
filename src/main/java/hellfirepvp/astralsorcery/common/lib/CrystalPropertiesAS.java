/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CrystalPropertiesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CrystalPropertiesAS {

    public static final DeferredRegister<CrystalProperty> CRYSTAL_PROPERTY_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_CRYSTAL_PROPERTIES, AstralSorcery.MODID);

    public static final DeferredHolder<CrystalProperty, CrystalProperty> SIZE =
            CRYSTAL_PROPERTY_REGISTER.register("size", () -> new CrystalProperty(ChatFormatting.GRAY, 8));
    public static final DeferredHolder<CrystalProperty, CrystalProperty> PURITY =
            CRYSTAL_PROPERTY_REGISTER.register("purity", () -> new CrystalProperty(ChatFormatting.GRAY, 4));
    public static final DeferredHolder<CrystalProperty, CrystalProperty> CUT =
            CRYSTAL_PROPERTY_REGISTER.register("cut", () -> new CrystalProperty(ChatFormatting.GRAY, 4));

    public static final DeferredHolder<CrystalProperty, CrystalProperty> TOOL_EFFICIENCY =
            CRYSTAL_PROPERTY_REGISTER.register("tool_efficiency", () -> new CrystalProperty(ChatFormatting.GRAY, 3));
    public static final DeferredHolder<CrystalProperty, CrystalProperty> TOOL_DURABILITY =
            CRYSTAL_PROPERTY_REGISTER.register("tool_durability", () -> new CrystalProperty(ChatFormatting.GRAY, 3));
}
