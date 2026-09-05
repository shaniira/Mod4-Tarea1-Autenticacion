package com.andinaseguros.core.ports.out.clock;

import java.time.Instant;

public interface ClockPort {
    Instant now();
}
