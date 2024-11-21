package ro.eventsourcing.pet;

import org.apache.commons.validator.routines.EmailValidator;

import static org.apache.commons.lang3.StringUtils.isBlank;

public record Owner(
        String name,
        String email,
        String address
) {
    public Owner {
        if (isBlank(name)) {
            throw new IllegalArgumentException("Owner name cannot be blank");
        }

        if (isBlank(email)) {
            throw new IllegalArgumentException("Owner email cannot be blank");
        }

        if (!EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException("Owner email is not valid");
        }

        if (isBlank(address)) {
            throw new IllegalArgumentException("Owner address cannot be blank");
        }
    }
}
