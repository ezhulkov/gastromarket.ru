package org.ohm.gastro.service;

import org.ohm.gastro.service.ImageService.FileType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by ezhulkov on 10.04.15.
 */
@Target(TYPE)
@Documented
@Retention(RUNTIME)
public @interface ImageUploader {

    FileType value();

}
