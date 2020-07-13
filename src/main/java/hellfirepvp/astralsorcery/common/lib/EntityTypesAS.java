/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.entity.EntityFlare;
import hellfirepvp.astralsorcery.common.entity.EntityIlluminationSpark;
import hellfirepvp.astralsorcery.common.entity.EntityNocturnalSpark;
import hellfirepvp.astralsorcery.common.entity.EntitySpectralTool;
import hellfirepvp.astralsorcery.common.entity.item.EntityCrystal;
import hellfirepvp.astralsorcery.common.entity.item.EntityItemExplosionResistant;
import hellfirepvp.astralsorcery.common.entity.item.EntityItemHighlighted;
import hellfirepvp.astralsorcery.common.entity.item.EntityStarmetal;
import hellfirepvp.astralsorcery.common.entity.technical.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.entity.technical.EntityObservatoryHelper;
import net.minecraft.entity.EntityType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityTypesAS
 * Created by HellFirePvP
 * Date: 17.08.2019 / 10:24
 */
public class EntityTypesAS {

    private EntityTypesAS() {}

    public static EntityType<EntityNocturnalSpark> NOCTURNAL_SPARK;
    public static EntityType<EntityIlluminationSpark> ILLUMINATION_SPARK;
    public static EntityType<EntityFlare> FLARE;
    public static EntityType<EntitySpectralTool> SPECTRAL_TOOL;

    public static EntityType<EntityItemHighlighted> ITEM_HIGHLIGHT;
    public static EntityType<EntityItemExplosionResistant> ITEM_EXPLOSION_RESISTANT;
    public static EntityType<EntityCrystal> ITEM_CRYSTAL;
    public static EntityType<EntityStarmetal> ITEM_STARMETAL_INGOT;

    public static EntityType<EntityObservatoryHelper> OBSERVATORY_HELPER;
    public static EntityType<EntityGrapplingHook> GRAPPLING_HOOK;

}
