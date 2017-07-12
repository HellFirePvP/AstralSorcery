/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.spell;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpellEffectRegistry
 * Created by HellFirePvP
 * Date: 07.07.2017 / 12:55
 */
public class SpellEffectRegistry {

    private static Map<IConstellation, ISpellEffect> spellEffectRegistry = new HashMap<>();
    private static Map<IMajorConstellation, SpellControllerProvider> controllerEffectMap = new HashMap<>();

    @Nullable
    public static ISpellEffect getSpellEffect(IConstellation constellation) {
        return spellEffectRegistry.get(constellation);
    }

    @Nullable
    public static SpellControllerEffect createEmptySpell(IMajorConstellation constellation, EntityLivingBase caster) {
        SpellControllerProvider provider = controllerEffectMap.get(constellation);
        if(provider != null) {
            return provider.createEmptySpellFor(caster);
        }
        return null;
    }

    public static void registerControllerEffect(IMajorConstellation cst, SpellControllerProvider effect) {
        controllerEffectMap.put(cst, effect);
    }

    public static void registerSpellEffect(IConstellation cst, ISpellEffect effect) {
        spellEffectRegistry.put(cst, effect);
    }

    public static interface SpellControllerProvider {

        public SpellControllerEffect createEmptySpellFor(EntityLivingBase caster);

    }

}
