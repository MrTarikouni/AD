/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author alumne
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TableColumn {
    String dataType();
    boolean primaryKey() default false;
    boolean autoPK() default false;
    boolean unique() default false;
    boolean nullable() default true;
    String foreignKeyColumn() default "";
    String foreignKeyTable()  default "";
    String defaultValue() default "";
    String valueOnNull() default "";
}

