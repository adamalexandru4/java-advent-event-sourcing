package ro.eventsourcing.notification;

import ro.eventsourcing.core.repository.View;

import java.util.UUID;

class Notification extends View<UUID> {
    private String email;
    private String title;
    private String description;

    public Notification(String email, String title, String description) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.title = title;
        this.description = description;
    }
}
