package org.ohm.gastro.service;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by ezhulkov on 10.04.15.
 */
@Target(METHOD)
@Documented
@Retention(RUNTIME)
public @interface RatingModifier {

}
