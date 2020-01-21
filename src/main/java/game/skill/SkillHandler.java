package game.skill;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by jimmy on 10/15/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SkillHandler {
    int skillId() default 0;
    int[] skillIds() default {};
    int skillType() default 0;
    boolean preregister() default false;
}