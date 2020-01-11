/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CategorizedSoundEvent
 * Created by HellFirePvP
 * Date: 30.06.2019 / 22:58
 */
public class CategorizedSoundEvent extends SoundEvent {

    private final SoundCategory category;

    public CategorizedSoundEvent(ResourceLocation soundNameIn, SoundCategory category) {
        super(soundNameIn);
        this.category = category;
    }

    public SoundCategory getCategory() {
        return category;
    }

}