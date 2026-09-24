/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.perk.render;

import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.screen.tome.perk.BatchPerkContext;
import net.minecraft.client.renderer.RenderType;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkRenderType
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record PerkRenderType(RenderType renderType, int priority) implements Comparable<PerkRenderType> {

    @Override
    public int compareTo(PerkRenderType o) {
        int cmp = Integer.compare(this.priority, o.priority);
        if (cmp == 0) {
            return this.renderType.toString().compareTo(o.renderType.toString());
        }
        return cmp;
    }

    public static class Types {

        public static final PerkRenderType PERK_SEARCH =
                new PerkRenderType(RenderTypesAS.SCREEN_EFFECT_PERK_SEARCH, BatchPerkContext.PRIORITY_OVERLAY);
        public static final PerkRenderType PERK_SEAL =
                new PerkRenderType(RenderTypesAS.SCREEN_EFFECT_PERK_SEAL, BatchPerkContext.PRIORITY_FOREGROUND);
        public static final PerkRenderType PERK_NULLIFIER =
                new PerkRenderType(RenderTypesAS.SCREEN_EFFECT_PERK_SEAL, BatchPerkContext.PRIORITY_OVERLAY + 10);

        public static final PerkRenderType PERK_INACTIVE =
                new PerkRenderType(RenderTypesAS.SCREEN_EFFECT_PERK_INACTIVE, BatchPerkContext.PRIORITY_BACKGROUND);
        public static final PerkRenderType PERK_ACTIVE =
                new PerkRenderType(RenderTypesAS.SCREEN_EFFECT_PERK_ACTIVE, BatchPerkContext.PRIORITY_BACKGROUND + 10);
        public static final PerkRenderType PERK_ACTIVATABLE =
                new PerkRenderType(RenderTypesAS.SCREEN_EFFECT_PERK_ACTIVATABLE, BatchPerkContext.PRIORITY_BACKGROUND + 20);

        public static final PerkRenderType PERK_HALO_INACTIVE =
                new PerkRenderType(RenderTypesAS.SCREEN_EFFECT_PERK_HALO_INACTIVE, BatchPerkContext.PRIORITY_BACKGROUND + 50);
        public static final PerkRenderType PERK_HALO_ACTIVE =
                new PerkRenderType(RenderTypesAS.SCREEN_EFFECT_PERK_HALO_ACTIVE, BatchPerkContext.PRIORITY_BACKGROUND + 60);
        public static final PerkRenderType PERK_HALO_ACTIVATABLE =
                new PerkRenderType(RenderTypesAS.SCREEN_EFFECT_PERK_HALO_ACTIVATABLE, BatchPerkContext.PRIORITY_BACKGROUND + 70);

        public static List<PerkRenderType> getDefaultPerkTypes() {
            return List.of(PERK_INACTIVE, PERK_ACTIVE, PERK_ACTIVATABLE);
        }

        public static List<PerkRenderType> getMajorPerkTypes() {
            return List.of(PERK_INACTIVE, PERK_ACTIVE, PERK_ACTIVATABLE, PERK_HALO_INACTIVE, PERK_HALO_ACTIVE, PERK_HALO_ACTIVATABLE);
        }
    }
}
