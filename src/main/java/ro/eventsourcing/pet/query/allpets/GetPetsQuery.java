package ro.eventsourcing.pet.query.allpets;

import java.util.Optional;

public record GetPetsQuery(
        int offset,
        int size,
        Optional<String> name
) {

    public static GetPetsQuery noFilter() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int offset = 0;
        private int size = 10;
        private String name = null;

        public Builder offset(int offset) {
            this.offset = offset;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public GetPetsQuery build() {
            return new GetPetsQuery(offset, size, Optional.ofNullable(name));
        }
    }
}
