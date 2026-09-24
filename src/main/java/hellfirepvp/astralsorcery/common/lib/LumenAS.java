/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.LumenLike;
import hellfirepvp.astralsorcery.common.lumen.LumenPrismatic;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenAS {

    public static final DeferredRegister<Lumen> LUMEN_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_LUMEN, AstralSorcery.MODID);

    public static final DeferredLumen<Lumen> NONE = registerSimple("none", ColorWrapper.WHITE);

    public static final DeferredLumen<Lumen> AEVITAS =  registerElementary("aevitas", ColorsAS.LUMEN_AEVITAS);
    public static final DeferredLumen<Lumen> ARMARA =   registerElementary("armara", ColorsAS.LUMEN_ARMARA);
    public static final DeferredLumen<Lumen> DISCIDIA = registerElementary("discidia", ColorsAS.LUMEN_DISCIDIA);
    public static final DeferredLumen<Lumen> EVORSIO =  registerElementary("evorsio", ColorsAS.LUMEN_EVORSIO);
    public static final DeferredLumen<Lumen> VICIO =    registerElementary("vicio", ColorsAS.LUMEN_VICIO);

    public static final DeferredLumen<Lumen> VIREL =    registerSimple("virel", ColorsAS.LUMEN_VIREL); //life
    public static final DeferredLumen<Lumen> SOLYN =    registerSimple("solyn", ColorsAS.LUMEN_SOLYN); //light
    public static final DeferredLumen<Lumen> NULLAE =   registerSimple("nullae", ColorsAS.LUMEN_NULLAE); //emptiness
    public static final DeferredLumen<Lumen> CALDOR =   registerSimple("caldor", ColorsAS.LUMEN_CALDOR); //order

    public static final DeferredLumen<Lumen> HYLE =     registerSimple("hyle", ColorsAS.LUMEN_HYLE); //matter
    public static final DeferredLumen<Lumen> DYNAMIS =  registerSimple("dynamis", ColorsAS.LUMEN_DYNAMIS); //energy
    public static final DeferredLumen<Lumen> AION =     registerSimple("aion", ColorsAS.LUMEN_AION); //time
    public static final DeferredLumen<Lumen> AKASHA =   registerSimple("akasha", ColorsAS.LUMEN_AKASHA); //space

    public static final DeferredLumen<LumenPrismatic> PRISMATIC = register("prismatic", LumenPrismatic::new);

    private static DeferredLumen<Lumen> registerElementary(String name, ColorWrapper color) {
        return register(name, () -> new Lumen(color, true));
    }

    private static DeferredLumen<Lumen> registerSimple(String name, ColorWrapper color) {
        return register(name, () -> new Lumen(color));
    }

    private static <T extends Lumen> DeferredLumen<T> register(String name, Supplier<T> lumen) {
        return new DeferredLumen<>(LUMEN_REGISTER.register(name, lumen).getKey());
    }

    public static class DeferredLumen<T extends Lumen> extends DeferredHolder<Lumen, T> implements LumenLike {

        protected DeferredLumen(ResourceKey<Lumen> key) {
            super(key);
        }

        @Override
        public Lumen asLumen() {
            return this.get();
        }

        public LumenStack stack() {
            return LumenStack.of(this.get(), LumenStack.FLASK_VALUE);
        }

        public LumenStack stack(int amount) {
            return LumenStack.of(this.get(), amount);
        }
    }
}
