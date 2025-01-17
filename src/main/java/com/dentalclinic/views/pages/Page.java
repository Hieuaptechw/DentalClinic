package com.dentalclinic.views.pages;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Page {
    String name() default "";
    String fxml() default "";
    String icon() default "";
    boolean allowsDuplicates() default false;
}
