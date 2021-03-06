package org.mycore.frontend.editor.validation.value;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;
import org.mycore.frontend.editor.validation.MCRValidatorTest;
import org.mycore.frontend.editor.validation.value.MCRMaxDateTimeValidator;

public class MCRMaxDateTimeValidatorTest extends MCRValidatorTest {

    @Before
    public void setup() {
        validator = new MCRMaxDateTimeValidator();
        validator.setProperty("type", "datetime");
        validator.setProperty("format", "dd.MM.yyyy");
    }

    @Test
    public void testHasRequiredProperties() {
        validator.setProperty("max", "22.04.1971");
        assertTrue(validator.hasRequiredProperties());
    }

    @Test
    public void testMaxValid() {
        validator.setProperty("max", "22.04.1971");
        assertTrue(validator.isValid("22.04.1971"));
    }

    @Test
    public void testMaxInvalid() {
        validator.setProperty("max", "22.04.1971");
        assertFalse(validator.isValid("23.04.1971"));
    }
}
