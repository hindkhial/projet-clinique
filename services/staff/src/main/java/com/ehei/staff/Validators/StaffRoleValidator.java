package com.ehei.staff.Validators;

import com.ehei.staff.enums.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StaffRoleValidator implements ConstraintValidator<ValidStaffRole, Role> {

    @Override
    public boolean isValid(Role role, ConstraintValidatorContext context) {
        if (role == null) {
            return true; // @NotNull gère la validation null
        }

        // Interdire MEDECIN
        if (role == Role.MEDECIN) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Le rôle MEDECIN n'est pas autorisé. Utilisez /medecin/create ."
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
