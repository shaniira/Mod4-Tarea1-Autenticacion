package com.andinaseguros.infrastructure.time;

import com.andinaseguros.application.gateway.time.ClockPort;
import java.time.Instant;

public class SystemClockAdapter implements ClockPort {
    public Instant now() {
        return Instant.now();
    }
}
