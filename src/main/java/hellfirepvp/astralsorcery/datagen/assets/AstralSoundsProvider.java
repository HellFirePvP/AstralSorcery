/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.assets;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.sound.CategorizedSoundEvent;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralSoundsProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralSoundsProvider extends SoundDefinitionsProvider {

    public AstralSoundsProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, AstralSorcery.MODID, helper);
    }

    @Override
    public void registerSounds() {
        this.addSound(SoundsAS.ALTAR_CRAFT_START, "altar",
                NameUtil.suffixPath(SoundsAS.ALTAR_CRAFT_START.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.ALTAR_CRAFT_START.getId(), "_2"));
        this.addSound(SoundsAS.ALTAR_CRAFT_FINISH, "altar",
                NameUtil.suffixPath(SoundsAS.ALTAR_CRAFT_FINISH.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.ALTAR_CRAFT_FINISH.getId(), "_2"));
        this.addSound(SoundsAS.ALTAR_CRAFT_LOOP_T1, "altar");
        this.addSound(SoundsAS.ALTAR_CRAFT_LOOP_T2, "altar");
        this.addSound(SoundsAS.ALTAR_CRAFT_LOOP_T3, "altar");
        this.addSound(SoundsAS.ALTAR_CRAFT_LOOP_T4, "altar");
        this.addSound(SoundsAS.ALTAR_CRAFT_LOOP_WAITING, "altar");

        this.addSound(SoundsAS.ATTUNEMENT_ALTAR_IDLE_LOOP, "attunement");
        this.addSound(SoundsAS.ATTUNEMENT_ALTAR_PLAYER_ATTUNE, "attunement");
        this.addSound(SoundsAS.ATTUNEMENT_ALTAR_ITEM_START, "attunement",
                NameUtil.suffixPath(SoundsAS.ATTUNEMENT_ALTAR_ITEM_START.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.ATTUNEMENT_ALTAR_ITEM_START.getId(), "_2"));
        this.addSound(SoundsAS.ATTUNEMENT_ALTAR_ITEM_LOOP, "attunement");
        this.addSound(SoundsAS.ATTUNEMENT_ALTAR_ITEM_FINISH, "attunement",
                NameUtil.suffixPath(SoundsAS.ATTUNEMENT_ALTAR_ITEM_FINISH.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.ATTUNEMENT_ALTAR_ITEM_FINISH.getId(), "_2"));

        this.addSound(SoundsAS.INFUSER_CRAFT_START, "infuser",
                NameUtil.suffixPath(SoundsAS.INFUSER_CRAFT_START.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.INFUSER_CRAFT_START.getId(), "_2"));
        this.addSound(SoundsAS.INFUSER_CRAFT_LOOP, "infuser",
                NameUtil.suffixPath(SoundsAS.INFUSER_CRAFT_LOOP.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.INFUSER_CRAFT_LOOP.getId(), "_2"));
        this.addSound(SoundsAS.INFUSER_CRAFT_FINISH, "infuser",
                NameUtil.suffixPath(SoundsAS.INFUSER_CRAFT_FINISH.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.INFUSER_CRAFT_FINISH.getId(), "_2"));

        this.addSound(SoundsAS.LUMEN_TRANSFER, "lumen",
                NameUtil.suffixPath(SoundsAS.LUMEN_TRANSFER.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.LUMEN_TRANSFER.getId(), "_2"),
                NameUtil.suffixPath(SoundsAS.LUMEN_TRANSFER.getId(), "_3"),
                NameUtil.suffixPath(SoundsAS.LUMEN_TRANSFER.getId(), "_4"),
                NameUtil.suffixPath(SoundsAS.LUMEN_TRANSFER.getId(), "_5"),
                NameUtil.suffixPath(SoundsAS.LUMEN_TRANSFER.getId(), "_6"));

        this.addSound(SoundsAS.GRAPPLING_WAND_CAST, "grappling_wand",
                NameUtil.suffixPath(SoundsAS.GRAPPLING_WAND_CAST.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.GRAPPLING_WAND_CAST.getId(), "_2"),
                NameUtil.suffixPath(SoundsAS.GRAPPLING_WAND_CAST.getId(), "_3"),
                NameUtil.suffixPath(SoundsAS.GRAPPLING_WAND_CAST.getId(), "_4"));
        this.addSound(SoundsAS.GRAPPLING_WAND_ATTACH, "grappling_wand",
                NameUtil.suffixPath(SoundsAS.GRAPPLING_WAND_ATTACH.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.GRAPPLING_WAND_ATTACH.getId(), "_2"),
                NameUtil.suffixPath(SoundsAS.GRAPPLING_WAND_ATTACH.getId(), "_3"),
                NameUtil.suffixPath(SoundsAS.GRAPPLING_WAND_ATTACH.getId(), "_4"));
        this.addSound(SoundsAS.GRAPPLING_WAND_FAIL, "grappling_wand",
                NameUtil.suffixPath(SoundsAS.GRAPPLING_WAND_FAIL.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.GRAPPLING_WAND_FAIL.getId(), "_2"),
                NameUtil.suffixPath(SoundsAS.GRAPPLING_WAND_FAIL.getId(), "_3"),
                NameUtil.suffixPath(SoundsAS.GRAPPLING_WAND_FAIL.getId(), "_4"));

        this.addSound(SoundsAS.ILLUMINATION_WAND_HIGHLIGHT, "illumination_wand",
                NameUtil.suffixPath(SoundsAS.ILLUMINATION_WAND_HIGHLIGHT.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.ILLUMINATION_WAND_HIGHLIGHT.getId(), "_2"));
        this.addSound(SoundsAS.ILLUMINATION_WAND_UNHIGHLIGHT, "illumination_wand",
                NameUtil.suffixPath(SoundsAS.ILLUMINATION_WAND_UNHIGHLIGHT.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.ILLUMINATION_WAND_UNHIGHLIGHT.getId(), "_2"));
        this.addSound(SoundsAS.ILLUMINATION_WAND_LIGHT, "illumination_wand",
                NameUtil.suffixPath(SoundsAS.ILLUMINATION_WAND_LIGHT.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.ILLUMINATION_WAND_LIGHT.getId(), "_2"));

        this.addSound(SoundsAS.CHISEL_HIT, "chisel",
                NameUtil.suffixPath(SoundsAS.CHISEL_HIT.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.CHISEL_HIT.getId(), "_2"),
                NameUtil.suffixPath(SoundsAS.CHISEL_HIT.getId(), "_3"),
                NameUtil.suffixPath(SoundsAS.CHISEL_HIT.getId(), "_4"));

        this.addSound(SoundsAS.SCREEN_PERK_SEAL,
                NameUtil.suffixPath(SoundsAS.SCREEN_PERK_SEAL.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.SCREEN_PERK_SEAL.getId(), "_2"));
        this.addSound(SoundsAS.SCREEN_PERK_UNSEAL,
                NameUtil.suffixPath(SoundsAS.SCREEN_PERK_UNSEAL.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.SCREEN_PERK_UNSEAL.getId(), "_2"));
        this.addSound(SoundsAS.SCREEN_PERK_UNLOCK,
                NameUtil.suffixPath(SoundsAS.SCREEN_PERK_UNLOCK.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.SCREEN_PERK_UNLOCK.getId(), "_2"));
        this.addSound(SoundsAS.SCREEN_TOME_CLOSE);
        this.addSound(SoundsAS.SCREEN_TOME_PAGE,
                NameUtil.suffixPath(SoundsAS.SCREEN_TOME_PAGE.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.SCREEN_TOME_PAGE.getId(), "_2"));

        this.addSound(SoundsAS.CRYSTAL_BREAK, "crystal",
                NameUtil.suffixPath(SoundsAS.CRYSTAL_BREAK.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_BREAK.getId(), "_2"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_BREAK.getId(), "_3"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_BREAK.getId(), "_4"));
        this.addSound(SoundsAS.CRYSTAL_HIT, "crystal",
                NameUtil.suffixPath(SoundsAS.CRYSTAL_HIT.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_HIT.getId(), "_2"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_HIT.getId(), "_3"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_HIT.getId(), "_4"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_HIT.getId(), "_5"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_HIT.getId(), "_6"));
        this.addSound(SoundsAS.CRYSTAL_PLACE, "crystal",
                NameUtil.suffixPath(SoundsAS.CRYSTAL_PLACE.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_PLACE.getId(), "_2"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_PLACE.getId(), "_3"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_PLACE.getId(), "_4"));
        this.addSound(SoundsAS.CRYSTAL_STEP, "crystal",
                NameUtil.suffixPath(SoundsAS.CRYSTAL_STEP.getId(), "_1"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_STEP.getId(), "_2"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_STEP.getId(), "_3"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_STEP.getId(), "_4"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_STEP.getId(), "_5"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_STEP.getId(), "_6"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_STEP.getId(), "_7"),
                NameUtil.suffixPath(SoundsAS.CRYSTAL_STEP.getId(), "_8"));
    }

    private <T extends SoundEvent> void addSound(CategorizedSoundEvent sound) {
        this.add(sound.sound(),
                definition()
                        .with(sound(sound.getId())));
    }

    private void addSound(CategorizedSoundEvent sound, ResourceLocation... soundIds) {
        SoundDefinition def = definition();
        for (ResourceLocation id : soundIds) {
            def.with(sound(id));
        }
        this.add(sound.sound(), def);
    }

    private void addSound(CategorizedSoundEvent sound, String folder, ResourceLocation... soundIds) {
        if (!folder.endsWith("/")) folder = folder + "/";

        SoundDefinition def = definition();
        if (soundIds.length > 0) {
            for (ResourceLocation id : soundIds) {
                def.with(sound(NameUtil.prefixPath(id, folder)));
            }
        } else {
            def.with(sound(NameUtil.prefixPath(sound.getId(), folder)));
        }
        this.add(sound.sound(), def);
    }
}
