package com.waicool20.kaga.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) public @interface IniConfig {
    String key();

    boolean read() default true;
}
