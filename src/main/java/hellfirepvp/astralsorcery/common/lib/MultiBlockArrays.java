package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import hellfirepvp.astralsorcery.common.util.struct.StructureBlockArray;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiBlockArrays
 * Created by HellFirePvP
 * Date: 16.05.2016 / 15:45
 */
public class MultiBlockArrays {

    public static StructureBlockArray ancientShrine;

    public static StructureBlockArray desertShrine;

    @PasteBlacklist
    public static PatternBlockArray patternRitualPedestal;

    public static PatternBlockArray patternAltarAttunement;

    public static PatternBlockArray patternAltarConstellation;

    public static PatternBlockArray patternAttunementFrame;

    public static PatternBlockArray patternStarlightInfuser;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface PasteBlacklist {}

}
