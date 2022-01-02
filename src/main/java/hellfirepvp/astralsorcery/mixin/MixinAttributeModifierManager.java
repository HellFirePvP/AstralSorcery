/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinAttributeModifierManager
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(AttributeModifierManager.class)
public class MixinAttributeModifierManager implements AttributeEvent.EntityModifierManager {

    @Unique public LivingEntity astralSorceryEntityReference;

    @Nullable
    @Override
    public LivingEntity getLivingEntity() {
        return this.astralSorceryEntityReference;
    }

    @Override
    public void setLivingEntity(LivingEntity entity) {
        this.astralSorceryEntityReference = entity;
    }
}
