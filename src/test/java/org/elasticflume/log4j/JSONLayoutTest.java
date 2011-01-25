package org.elasticflume.log4j;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;

public class JSONLayoutTest {
    private static final Logger DEFAULT_LOGGER = Logger.getLogger("org.elasticsearch");
    private JSONLayout jsonLayout;

    @Before
    public void setup() {
        jsonLayout = new JSONLayout();
        jsonLayout.activateOptions();
    }

    @Test
    public void validateBasicLogStructure() {
        LoggingEvent event = createDefaultLoggingEvent();
        String logOutput = jsonLayout.format(event);
        matchBasicLogOutput(logOutput, event);
    }

    @Test
    public void MDCValueIsLoggedCorrectly() {

        Map<String, String> mdcMap = createMapAndPopulateMDC();
        Set<String> mdcKeySet = mdcMap.keySet();

        LoggingEvent event = createDefaultLoggingEvent();
        jsonLayout.setMdcKeys(mdcKeySet.toArray(new String[0]));
        String logOutput = jsonLayout.format(event);

        matchBasicLogOutput(logOutput, event);
        assertThat(jsonLayout.getMdcKeys().length, is(mdcKeySet.size()));
        for (String key : mdcKeySet) {
            assertThat(jsonLayout.getMdcKeys(), hasItemInArray(key));
//        System.out.println(logOutput);
        }
        validateMDCValues(logOutput);
    }

    private void matchBasicLogOutput(String logOutput, LoggingEvent event) {
        validateLevel(logOutput, event);
        validateLogger(logOutput, event);
        validateThreadName(logOutput, event);
        validateMessage(logOutput, event);
    }

    private LoggingEvent createDefaultLoggingEvent() {
        return new LoggingEvent("", DEFAULT_LOGGER, Level.INFO, "Hello World", null);
    }

    private Map<String, String> createMapAndPopulateMDC() {
        Map<String,String> mdcMap = new LinkedHashMap<String,String>();
        mdcMap.put("UserId", "U1");
        mdcMap.put("ProjectId", "P1");

        for(Map.Entry<String,String> entry: mdcMap.entrySet()){
            MDC.put(entry.getKey(),entry.getValue());
        }
        return mdcMap;
    }

    private void validateLevel(String logOutput, LoggingEvent event) {
        if (event.getLevel() != null) {
            String partialOutput = "\"level\":\"" + event.getLevel().toString() + "\"";
            assertThat(logOutput, containsString(partialOutput));
        } else {
            fail("Expected the level value to be set in the logging event");
        }
    }

    private void validateLogger(String logOutput, LoggingEvent event) {
        if (event.getLogger() != null) {
            String partialOutput = "\"logger\":\"" + event.getLoggerName() + "\"";
            assertThat(logOutput, containsString(partialOutput));
        } else {
            fail("Expected the logger to be set in the logging event");
        }
    }

    private void validateThreadName(String logOutput, LoggingEvent event) {
        if (event.getThreadName() != null) {
            String partialOutput = "\"threadName\":\"" + event.getThreadName() + "\"";
            assertThat(logOutput, containsString(partialOutput));
        } else {
            fail("Expected the threadname to be set in the logging event");
        }
    }

    private void validateMessage(String logOutput, LoggingEvent event) {
        if (event.getMessage() != null) {
            String partialOutput = "\"message\":\"" + event.getMessage() + "\"";
            assertThat(logOutput, containsString(partialOutput));
        } else {
            fail("Expected the message to be set in the logging event");
        }
    }

    private void validateMDCValues(String logOutput) {
        String partialOutput = "\"MDC\":{";
        partialOutput += "\"UserId\":\"" + "U1" + "\",";
        partialOutput += "\"ProjectId\":\"" + "P1" + "\"";
        partialOutput += "}";
        assertThat(logOutput, containsString(partialOutput));
    }

}
