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
import hellfirepvp.astralsorcery.common.research.tome.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomePageTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomePageTypesAS {

    public static final DeferredRegister<TomePage.TomePageType<?>> PAGE_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_TOME_PAGE_TYPES, AstralSorcery.MODID);

    public static final DeferredHolder<TomePage.TomePageType<?>, TomePage.TomePageType<TomePageEmpty>> EMPTY_PAGE =
            PAGE_REGISTER.register("empty", () -> new TomePage.TomePageType<>(TomePageEmpty.CODEC, TomePageEmpty.STREAM_CODEC));
    public static final DeferredHolder<TomePage.TomePageType<?>, TomePage.TomePageType<TomePageText>> TEXT_PAGE =
            PAGE_REGISTER.register("text", () -> new TomePage.TomePageType<>(TomePageText.CODEC, TomePageText.STREAM_CODEC));
    public static final DeferredHolder<TomePage.TomePageType<?>, TomePage.TomePageType<?>> STRUCTURE_PAGE =
            PAGE_REGISTER.register("structure", () -> new TomePage.TomePageType<>(TomePageStructure.CODEC, TomePageStructure.STREAM_CODEC));
    public static final DeferredHolder<TomePage.TomePageType<?>, TomePage.TomePageType<?>> RECIPE_PAGE =
            PAGE_REGISTER.register("recipe", () -> new TomePage.TomePageType<>(TomePageRecipe.CODEC, TomePageRecipe.STREAM_CODEC));
    public static final DeferredHolder<TomePage.TomePageType<?>, TomePage.TomePageType<?>> CONSTELLATION_PAGE =
            PAGE_REGISTER.register("constellation", () -> new TomePage.TomePageType<>(TomePageConstellation.CODEC, TomePageConstellation.STREAM_CODEC));
    public static final DeferredHolder<TomePage.TomePageType<?>, TomePage.TomePageType<?>> LUMEN_DESCRIPTION_PAGE =
            PAGE_REGISTER.register("lumen_description", () -> new TomePage.TomePageType<>(TomePageLumenDescription.CODEC, TomePageLumenDescription.STREAM_CODEC));

}
