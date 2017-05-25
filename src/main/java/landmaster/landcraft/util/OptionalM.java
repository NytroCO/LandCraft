package landmaster.landcraft.util;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

/**
 * Annotates a class to strip certain interfaces from, or a method to strip.
 * @author Landmaster
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Repeatable(OptionalM.List.class)
public @interface OptionalM {
	/**
	 * If any of these mods are loaded, then the interfaces/method remain(s).
	 * @return The modids such that if any are loaded, then the interfaces/method are not removed.
	 */
	String[] modids();
	/**
	 * If {@link OptionalM} is annotated on a class, then these interfaces are stripped
	 * from the class if none of the mods in {@link #modids()} are loaded. This field is
	 * silently ignored if annotated on a method.
	 * @return The interfaces to strip.
	 */
	String[] stripped() default {}; // unused for method
	
	/**
	 * Containing annotation type for {@link OptionalM}.
	 * @author Landmaster
	 */
	@Retention(RUNTIME)
	@Target({ TYPE, METHOD })
	public static @interface List {
		OptionalM[] value();
	}
}
