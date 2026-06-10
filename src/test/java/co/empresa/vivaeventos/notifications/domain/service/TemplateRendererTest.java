package co.empresa.vivaeventos.notifications.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateRendererTest {

    private final TemplateRenderer renderer = new TemplateRenderer();

    @Test
    @DisplayName("should replace simple variable")
    void shouldReplaceSimpleVariable() {
        String result = renderer.render("Hola {{nombre}}", Map.of("nombre", "Juan"));
        assertEquals("Hola Juan", result);
    }

    @Test
    @DisplayName("should replace multiple variables")
    void shouldReplaceMultipleVariables() {
        String result = renderer.render("{{saludo}} {{nombre}}", Map.of("saludo", "Hola", "nombre", "Ana"));
        assertEquals("Hola Ana", result);
    }

    @Test
    @DisplayName("should keep placeholder when variable not found")
    void shouldKeepPlaceholderWhenVariableNotFound() {
        String result = renderer.render("Hola {{nombre}}", Map.of("otro", "valor"));
        assertEquals("Hola {{nombre}}", result);
    }

    @Test
    @DisplayName("should return empty string for null template")
    void shouldReturnEmptyForNullTemplate() {
        String result = renderer.render(null, Map.of("a", "b"));
        assertEquals("", result);
    }

    @Test
    @DisplayName("should return template unchanged when variables null")
    void shouldReturnTemplateUnchangedWhenVariablesNull() {
        String result = renderer.render("Hola {{nombre}}", null);
        assertEquals("Hola {{nombre}}", result);
    }

    @Test
    @DisplayName("should return template unchanged when variables empty")
    void shouldReturnTemplateUnchangedWhenVariablesEmpty() {
        String result = renderer.render("Hola {{nombre}}", Map.of());
        assertEquals("Hola {{nombre}}", result);
    }

    @Test
    @DisplayName("should handle template with no variables")
    void shouldHandleTemplateWithNoVariables() {
        String result = renderer.render("Hola mundo", Map.of("a", "b"));
        assertEquals("Hola mundo", result);
    }

    @Test
    @DisplayName("should handle whitespace inside braces")
    void shouldHandleWhitespaceInsideBraces() {
        String result = renderer.render("Hola {{  nombre  }}", Map.of("nombre", "Luis"));
        assertEquals("Hola Luis", result);
    }

    @Test
    @DisplayName("should handle special regex characters in replacement")
    void shouldHandleSpecialRegexCharacters() {
        String result = renderer.render("{{val}}", Map.of("val", "$1.00 (test)"));
        assertEquals("$1.00 (test)", result);
    }
}
