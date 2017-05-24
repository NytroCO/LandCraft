package landmaster.landcraft.util;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Repeatable(OptionalM.List.class)
public @interface OptionalM {
	String[] modids();
	String[] stripped() default {}; // unused for method
	
	@Retention(RUNTIME)
	@Target({ TYPE, METHOD })
	public static @interface List {
		OptionalM[] value();
	}
}
