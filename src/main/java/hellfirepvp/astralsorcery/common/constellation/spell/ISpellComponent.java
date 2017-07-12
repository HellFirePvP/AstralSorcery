/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.spell;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ISpellComponent
 * Created by HellFirePvP
 * Date: 07.07.2017 / 10:33
 */
public interface ISpellComponent {

    @Nullable
    public SpellControllerEffect getSpellController();

    public boolean isValid();

    public void onUpdateController();

    default public boolean requiresUpdatesFromController() {
        return true;
    }

    /**
     * Deep copy that'd act like the actual previous component.
     */
    public ISpellComponent copy();

}
