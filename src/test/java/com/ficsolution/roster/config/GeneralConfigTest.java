package com.ficsolution.roster.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

public class GeneralConfigTest {

    @Test
    void clockMustUseConfiguredTimeZone() {
        GeneralConfig config = new GeneralConfig();
        ReflectionTestUtils.setField(config, "zone", "Europe/Dublin");

        Clock clock = config.clock();

        assertEquals(ZoneId.of("Europe/Dublin"), clock.getZone());
    }
}
