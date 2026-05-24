package com.example.stemlinkapp.event;

import com.example.stemlinkapp.domain.Booking;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MentorshipSessionCreatedEvent extends ApplicationEvent {
    private final Booking booking;

    public MentorshipSessionCreatedEvent(Object source, Booking booking) {
        super(source);
        this.booking = booking;
    }
}
