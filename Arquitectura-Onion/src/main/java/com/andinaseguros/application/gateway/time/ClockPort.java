package com.andinaseguros.application.gateway.time;

import java.time.Instant;

public interface ClockPort {
    Instant now();
}
