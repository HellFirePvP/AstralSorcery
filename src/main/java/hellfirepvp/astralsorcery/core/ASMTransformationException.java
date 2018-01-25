/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.core;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ASMTransformationException
 * Created by HellFirePvP
 * Date: 11.08.2016 / 10:04
 */
public class ASMTransformationException extends RuntimeException {

    public ASMTransformationException() {
    }

    public ASMTransformationException(String message) {
        super(message);
    }

    public ASMTransformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ASMTransformationException(Throwable cause) {
        super(cause);
    }

    public ASMTransformationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
