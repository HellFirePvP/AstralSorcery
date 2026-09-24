/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.sound.CategorizedSoundEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SoundsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SoundsAS {

    public static final DeferredRegister<SoundEvent> SOUND_REGISTER =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, AstralSorcery.MODID);

    public static final CategorizedSoundEvent ALTAR_CRAFT_START = register("altar_craft_start", SoundSource.BLOCKS);
    public static final CategorizedSoundEvent ALTAR_CRAFT_FINISH = register("altar_craft_finish", SoundSource.BLOCKS);
    public static final CategorizedSoundEvent ALTAR_CRAFT_LOOP_T1 = register("altar_craft_loop_t1", SoundSource.BLOCKS);
    public static final CategorizedSoundEvent ALTAR_CRAFT_LOOP_T2 = register("altar_craft_loop_t2", SoundSource.BLOCKS);
    public static final CategorizedSoundEvent ALTAR_CRAFT_LOOP_T3 = register("altar_craft_loop_t3", SoundSource.BLOCKS);
    public static final CategorizedSoundEvent ALTAR_CRAFT_LOOP_T4 = register("altar_craft_loop_t4", SoundSource.BLOCKS);
    public static final CategorizedSoundEvent ALTAR_CRAFT_LOOP_WAITING = register("altar_craft_loop_waiting", SoundSource.BLOCKS);

    public static final CategorizedSoundEvent ATTUNEMENT_ALTAR_IDLE_LOOP = register("attunement_altar_idle_loop", SoundSource.BLOCKS);
    public static final CategorizedSoundEvent ATTUNEMENT_ALTAR_PLAYER_ATTUNE = register("attunement_altar_player_attune", SoundSource.BLOCKS);
    public static final CategorizedSoundEvent ATTUNEMENT_ALTAR_ITEM_START = register("attunement_altar_item_start", SoundSource.BLOCKS);
    public static final CategorizedSoundEvent ATTUNEMENT_ALTAR_ITEM_LOOP = register("attunement_altar_item_loop", SoundSource.BLOCKS);
    public static final CategorizedSoundEvent ATTUNEMENT_ALTAR_ITEM_FINISH = register("attunement_altar_item_finish", SoundSource.BLOCKS);

    public static final CategorizedSoundEvent INFUSER_CRAFT_START = register("infuser_craft_start", SoundSource.BLOCKS);
    public static final CategorizedSoundEvent INFUSER_CRAFT_LOOP = register("infuser_craft_loop", SoundSource.BLOCKS);
    public static final CategorizedSoundEvent INFUSER_CRAFT_FINISH = register("infuser_craft_finish", SoundSource.BLOCKS);

    public static final CategorizedSoundEvent LUMEN_TRANSFER = register("lumen_transfer", SoundSource.BLOCKS);

    public static final CategorizedSoundEvent GRAPPLING_WAND_CAST = register("grappling_wand_cast", SoundSource.PLAYERS);
    public static final CategorizedSoundEvent GRAPPLING_WAND_ATTACH = register("grappling_wand_attach", SoundSource.PLAYERS);
    public static final CategorizedSoundEvent GRAPPLING_WAND_FAIL = register("grappling_wand_fail", SoundSource.PLAYERS);

    public static final CategorizedSoundEvent ILLUMINATION_WAND_HIGHLIGHT = register("illumination_wand_highlight", SoundSource.PLAYERS);
    public static final CategorizedSoundEvent ILLUMINATION_WAND_UNHIGHLIGHT = register("illumination_wand_unhighlight", SoundSource.PLAYERS);
    public static final CategorizedSoundEvent ILLUMINATION_WAND_LIGHT = register("illumination_wand_light", SoundSource.PLAYERS);

    public static final CategorizedSoundEvent CHISEL_HIT = register("chisel_hit", SoundSource.PLAYERS);

    public static final CategorizedSoundEvent SCREEN_PERK_SEAL = register("screen_perk_seal", SoundSource.MASTER);
    public static final CategorizedSoundEvent SCREEN_PERK_UNSEAL = register("screen_perk_unseal", SoundSource.MASTER);
    public static final CategorizedSoundEvent SCREEN_PERK_UNLOCK = register("screen_perk_unlock", SoundSource.MASTER);
    public static final CategorizedSoundEvent SCREEN_TOME_CLOSE = register("screen_tome_close", SoundSource.MASTER);
    public static final CategorizedSoundEvent SCREEN_TOME_PAGE = register("screen_tome_page", SoundSource.MASTER);

    public static final CategorizedSoundEvent CRYSTAL_BREAK = register("crystal_break", SoundSource.BLOCKS);
    public static final CategorizedSoundEvent CRYSTAL_HIT = register("crystal_hit", SoundSource.BLOCKS);
    public static final CategorizedSoundEvent CRYSTAL_PLACE = register("crystal_place", SoundSource.BLOCKS);
    public static final CategorizedSoundEvent CRYSTAL_STEP = register("crystal_step", SoundSource.BLOCKS);
    public static final DeferredSoundType CRYSTAL_SOUND_TYPE = new DeferredSoundType(1F, 1F,
            CRYSTAL_BREAK.sound(), CRYSTAL_STEP.sound(), CRYSTAL_PLACE.sound(), CRYSTAL_HIT.sound(), CRYSTAL_HIT.sound());

    private static CategorizedSoundEvent register(String name, SoundSource category) {
        DeferredHolder<SoundEvent, SoundEvent> sound = SOUND_REGISTER.register(name,
                () -> SoundEvent.createVariableRangeEvent(AstralSorcery.key(name)));
        return new CategorizedSoundEvent(sound, category);
    }
}
