package fr.devsylone.fallenkingdom.util;

import org.junit.jupiter.api.Test;

import static fr.devsylone.fallenkingdom.utils.PluginLog.anonymize;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnonymizeTest {

    @Test
    void removeIPv4() {
        final String actual = anonymize("2.90.220.15 120.7.33.1");
        assertEquals("**.**.**.** **.**.**.**", actual);
    }

    @Test
    void removeIPv6() {
        final String actual = anonymize("e01c:9b91:85a5:a8cc:ec3a:f9cb:8814:8ffa");
        assertEquals("****:****:****:****:****:****:****:****", actual);
    }

    @Test
    void keepNonIp() {
        final String expected = "text (260.40.6000.1) text";
        final String actual = anonymize(expected);
        assertEquals(expected, actual);
    }
}
