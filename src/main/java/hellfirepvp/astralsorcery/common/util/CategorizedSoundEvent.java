package hellfirepvp.astralsorcery.common.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CategorizedSoundEvent
 * Created by HellFirePvP
 * Date: 06.12.2016 / 13:33
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
