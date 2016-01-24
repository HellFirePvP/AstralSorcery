package hellfire.astralSorcery.common.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 20:44
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface Sync {}
