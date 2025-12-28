package com.ehei.staff.Validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StaffRoleValidator.class)
@Documented
public @interface ValidStaffRole {
    String message() default "Le rôle MEDECIN n'est pas autorisé pour un staff";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
