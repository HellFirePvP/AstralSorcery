/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.obj;

/**
 * HellFirePvP@Admin
 * Date: 15.06.2015 / 00:11
 * on WingsExMod
 * ModelFormatException
 */
public class ModelFormatException extends RuntimeException {

    private static final long serialVersionUID = 2023547503969671835L;

    public ModelFormatException() {
        super();
    }

    public ModelFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelFormatException(String message) {
        super(message);
    }

    public ModelFormatException(Throwable cause) {
        super(cause);
    }

}