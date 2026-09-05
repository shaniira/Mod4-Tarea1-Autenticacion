package com.andinaseguros.adapters.outbound.clock;

import com.andinaseguros.core.ports.out.clock.ClockPort;
import java.time.Instant;

public class SystemClockAdapter implements ClockPort {
    public Instant now() {
        return Instant.now();
    }
}
