/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.spell;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpellCastingManager
 * Created by HellFirePvP
 * Date: 07.07.2017 / 14:10
 */
public class SpellCastingManager implements ITickHandler {

    public static final SpellCastingManager INSTANCE = new SpellCastingManager();
    private List<SpellControllerEffect> activeSpellController = Lists.newArrayList();

    private SpellCastingManager() {}

    void addNewSpell(SpellControllerEffect effect) {
        this.activeSpellController.add(effect);
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        Iterator<SpellControllerEffect> iterator = activeSpellController.iterator();
        while (iterator.hasNext()) {
            SpellControllerEffect spell = iterator.next();
            spell.onUpdate();
            if(spell.isFinished()) {
                iterator.remove();
            }
        }

    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.SERVER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "SpellCast-Controller Manager";
    }
}
