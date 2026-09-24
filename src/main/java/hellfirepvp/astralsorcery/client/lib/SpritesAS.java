/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.lib;

import hellfirepvp.astralsorcery.client.resource.SpriteSheet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SpritesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SpritesAS {

    //Effects
    public static SpriteSheet SPRITE_LIGHT_BEAM;
    public static SpriteSheet SPRITE_LIGHT_BEAM_TRANSFER;
    public static SpriteSheet SPRITE_ATTUNEMENT_RELAY_FLARE;
    public static SpriteSheet SPRITE_ATTUNEMENT_ITEM_FLARE;
    public static SpriteSheet SPRITE_ENTITY_FLARE;
    public static SpriteSheet SPRITE_GRAPPLING_HOOK;

    public static SpriteSheet SPRITE_PERK_ACTIVATABLE;
    public static SpriteSheet SPRITE_PERK_ACTIVE;
    public static SpriteSheet SPRITE_PERK_INACTIVE;
    public static SpriteSheet SPRITE_PERK_HALO_ACTIVATABLE;
    public static SpriteSheet SPRITE_PERK_HALO_ACTIVE;
    public static SpriteSheet SPRITE_PERK_HALO_INACTIVE;
    public static SpriteSheet SPRITE_PERK_SEARCH;
    public static SpriteSheet SPRITE_PERK_SEAL;
    public static SpriteSheet SPRITE_PERK_SEAL_BREAK;
    public static SpriteSheet SPRITE_PERK_UNLOCK;

    public static void init() {
        SPRITE_LIGHT_BEAM             = TexturesAS.LIGHT_BEAM.asSpriteSheet(4, 16);
        SPRITE_LIGHT_BEAM_TRANSFER    = TexturesAS.LIGHT_BEAM_TRANSFER.asSpriteSheet(4, 16);
        SPRITE_ATTUNEMENT_RELAY_FLARE = TexturesAS.ATTUNEMENT_RELAY_FLARE.asSpriteSheet(6, 8);
        SPRITE_ATTUNEMENT_ITEM_FLARE  = TexturesAS.ATTUNEMENT_ITEM_FLARE.asSpriteSheet(6, 8);
        SPRITE_ENTITY_FLARE           = TexturesAS.ENTITY_FLARE.asSpriteSheet(6, 8);
        SPRITE_GRAPPLING_HOOK         = TexturesAS.GRAPPLING_HOOK.asSpriteSheet(4, 8);

        SPRITE_PERK_ACTIVATABLE      = TexturesAS.SCREEN_EFFECT_PERK_ACTIVATABLE.asSpriteSheet(5, 8);
        SPRITE_PERK_ACTIVE           = TexturesAS.SCREEN_EFFECT_PERK_ACTIVE.asSpriteSheet(5, 8);
        SPRITE_PERK_INACTIVE         = TexturesAS.SCREEN_EFFECT_PERK_INACTIVE.asSpriteSheet(5, 8);
        SPRITE_PERK_HALO_ACTIVATABLE = TexturesAS.SCREEN_EFFECT_PERK_HALO_ACTIVATABLE.asSpriteSheet(4, 8);
        SPRITE_PERK_HALO_ACTIVE      = TexturesAS.SCREEN_EFFECT_PERK_HALO_ACTIVE.asSpriteSheet(4, 8);
        SPRITE_PERK_HALO_INACTIVE    = TexturesAS.SCREEN_EFFECT_PERK_HALO_INACTIVE.asSpriteSheet(4, 8);
        SPRITE_PERK_SEARCH           = TexturesAS.SCREEN_EFFECT_PERK_SEARCH.asSpriteSheet(4, 8);
        SPRITE_PERK_SEAL             = TexturesAS.SCREEN_EFFECT_PERK_SEAL.asSpriteSheet(4, 8);
        SPRITE_PERK_SEAL_BREAK       = TexturesAS.SCREEN_EFFECT_PERK_SEAL_BREAK.asSpriteSheet(7, 8);
        SPRITE_PERK_UNLOCK           = TexturesAS.SCREEN_EFFECT_PERK_UNLOCK.asSpriteSheet(5, 16);
    }
}
