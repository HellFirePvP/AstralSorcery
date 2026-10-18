/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyConstellation;
import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyLumen;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.*;

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

    private static final Map<BaseConstellation, DeferredHolder<CrystalProperty, CrystalPropertyConstellation>> CONSTELLATION_PROPERTIES = new HashMap<>();
    private static final Map<Lumen, DeferredHolder<CrystalProperty, CrystalPropertyLumen>> LUMEN_PROPERTIES = new HashMap<>();

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
    public static final DeferredHolder<CrystalProperty, CrystalProperty> STARLIGHT_FOCUS =
            CRYSTAL_PROPERTY_REGISTER.register("starlight_focus", () -> new CrystalProperty(ChatFormatting.GRAY, 4));
    public static final DeferredHolder<CrystalProperty, CrystalProperty> LUMEN_CHANNELING =
            CRYSTAL_PROPERTY_REGISTER.register("lumen_channeling", () -> new CrystalProperty(ChatFormatting.GRAY, 4));

    public static Optional<DeferredHolder<CrystalProperty, CrystalPropertyConstellation>> getProperty(BaseConstellation cst) {
        return Optional.ofNullable(CONSTELLATION_PROPERTIES.get(cst));
    }

    public static Optional<DeferredHolder<CrystalProperty, CrystalPropertyLumen>> getProperty(Lumen lumen) {
        return Optional.ofNullable(LUMEN_PROPERTIES.get(lumen));
    }

    static {
        ConstellationsAS.CONSTELLATION_REGISTER.getEntries().forEach(holder -> {
            CONSTELLATION_PROPERTIES.put(holder.get(),
                    CRYSTAL_PROPERTY_REGISTER.register("constellation_" + holder.getKey().location().getPath(),
                            () -> new CrystalPropertyConstellation(holder))
            );
        });

        LumenAS.LUMEN_REGISTER.getEntries().forEach(holder -> {
            LUMEN_PROPERTIES.put(holder.get(),
                    CRYSTAL_PROPERTY_REGISTER.register("lumen_" + holder.getKey().location().getPath(),
                            () -> new CrystalPropertyLumen(holder))
            );
        });
    }
}
