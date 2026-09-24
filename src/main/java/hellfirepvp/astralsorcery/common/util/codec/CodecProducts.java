/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.codec;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CodecProducts
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CodecProducts {

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9> Products.P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9> and(Products.P4<F, T1, T2, T3, T4> p4, Products.P5<F, T5, T6, T7, T8, T9> p5) {
        return new Products.P9<>(p4.t1(), p4.t2(), p4.t3(), p4.t4(), p5.t1(), p5.t2(), p5.t3(), p5.t4(), p5.t5());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9> Products.P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9> and(Products.P3<F, T1, T2, T3> p3, Products.P1<F, T4> p1, Products.P5<F, T5, T6, T7, T8, T9> p5) {
        return new Products.P9<>(p3.t1(), p3.t2(), p3.t3(), p1.t1(), p5.t1(), p5.t2(), p5.t3(), p5.t4(), p5.t5());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Products.P10<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> and(Products.P4<F, T1, T2, T3, T4> p4, Products.P6<F, T5, T6, T7, T8, T9, T10> p6) {
        return new Products.P10<>(p4.t1(), p4.t2(), p4.t3(), p4.t4(), p6.t1(), p6.t2(), p6.t3(), p6.t4(), p6.t5(), p6.t6());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9> Products.P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9> and(Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> p8, App<F, T9> p9) {
        return new Products.P9<>(p8.t1(), p8.t2(), p8.t3(), p8.t4(), p8.t5(), p8.t6(), p8.t7(), p8.t8(), p9);
    }
}
