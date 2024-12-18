package seedu.exchangecoursemapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.exchangecoursemapper.command.FilterCoursesCommand;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FilterCoursesCommandTest {

    private FilterCoursesCommand filterCoursesCommand;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        filterCoursesCommand = new FilterCoursesCommand();
    }

    @Test
    public void parseFilterCommand_inputWithOneCourse_expectSeparatedInput() {
        String userInput = "filter cs3241";
        String[] descriptionSubstrings = filterCoursesCommand.parseFilterCommand(userInput);
        assertArrayEquals(new String[]{"filter", "cs3241"}, descriptionSubstrings);
    }

    @Test
    public void parseFilterCommand_inputWithTwoCourses_expectException() {
        String userInput = "filter ee2026 cs3241";
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            filterCoursesCommand.parseFilterCommand(userInput);
        });
        assertEquals("Please note that we can only filter for only one NUS Course!",
                e.getMessage());
    }

    @Test
    public void parseDeleteCommand_inputWithNoIndexes_expectException() {
        String userInput = "filter ";
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            filterCoursesCommand.parseFilterCommand(userInput);
        });
        assertEquals("Please provide the course code you would like to search for.",
                e.getMessage());
    }

    @Test
    public void isValidSocCourseCode_inputWithCsCourseCode_expectTrue() {
        String userInput = "cs3244";
        boolean isValidSocCourseCode = filterCoursesCommand.isValidSocCourseCode(userInput);
        assertTrue(isValidSocCourseCode);
    }

    @Test
    public void isValidSocCourseCode_inputWithEeCourseCode_expectTrue() {
        String userInput = "ee2026";
        boolean isValidSocCourseCode = filterCoursesCommand.isValidSocCourseCode(userInput);
        assertTrue(isValidSocCourseCode);
    }

    @Test
    public void isValidSocCourseCode_inputWithBtCourseCode_expectTrue() {
        String userInput = "bt4014";
        boolean isValidSocCourseCode = filterCoursesCommand.isValidSocCourseCode(userInput);
        assertTrue(isValidSocCourseCode);
    }

    @Test
    public void isValidSocCourseCode_inputWithIsCourseCode_expectTrue() {
        String userInput = "gess1000";
        boolean isValidSocCourseCode = filterCoursesCommand.isValidSocCourseCode(userInput);
        assertFalse(isValidSocCourseCode);
    }

    @Test
    public void getNusCourseCode_inputWithCourseCode_expectNusCourseCode() {
        String userInput = "filter cs3244";
        String[] descriptionSubstrings = filterCoursesCommand.parseFilterCommand(userInput);
        String nusCourseCode = filterCoursesCommand.getNusCourseCode(descriptionSubstrings);
        assertEquals("cs3244", nusCourseCode);
    }

    @Test
    public void displayMappableCourses_mappableNusCourse_expectMappableCoursesList() throws FileNotFoundException {
        JsonObject jsonObject = createDatabaseJsonObject();

        String nusCourseCode = "cs3244";
        filterCoursesCommand.displayMappableCourses(jsonObject, nusCourseCode);
        String expectedOutput = """
                Partner University: The University of Melbourne
                Partner University Course Code: COMP30027
                -----------------------------------------------------
                Partner University: The Australian National University
                Partner University Course Code: COMP3670
                -----------------------------------------------------
                Partner University: The Australian National University
                Partner University Course Code: COMP4620
                -----------------------------------------------------
                Partner University: The Australian National University
                Partner University Course Code: COMP4620
                -----------------------------------------------------
                """;
        String actualOutput = outputStreamCaptor.toString();
        assertEquals(normalizeLineEndings(expectedOutput), normalizeLineEndings(actualOutput));
    }

    @Test
    public void displayMappableCourses_mappableNusCourseInUpperCase_expectMappableCoursesList()
            throws FileNotFoundException {
        JsonObject jsonObject = createDatabaseJsonObject();

        String nusCourseCode = "CS3244";
        filterCoursesCommand.displayMappableCourses(jsonObject, nusCourseCode);
        String expectedOutput = """
                Partner University: The University of Melbourne
                Partner University Course Code: COMP30027
                -----------------------------------------------------
                Partner University: The Australian National University
                Partner University Course Code: COMP3670
                -----------------------------------------------------
                Partner University: The Australian National University
                Partner University Course Code: COMP4620
                -----------------------------------------------------
                Partner University: The Australian National University
                Partner University Course Code: COMP4620
                -----------------------------------------------------
                """;
        String actualOutput = outputStreamCaptor.toString();
        assertEquals(normalizeLineEndings(expectedOutput), normalizeLineEndings(actualOutput));
    }

    @Test
    public void displayMappableCourses_nonMappableNusCourse_expectNoMappableCourses() throws FileNotFoundException {
        JsonObject jsonObject = createDatabaseJsonObject();

        String nusCourseCode = "ee2026";
        filterCoursesCommand.displayMappableCourses(jsonObject, nusCourseCode);
        String expectedOutput = """
                -----------------------------------------------------
                No courses found for the given course code.
                It may not be mappable, or the given course code is not a course offered by NUS!
                -----------------------------------------------------
                """;
        String actualOutput = outputStreamCaptor.toString();
        assertEquals(normalizeLineEndings(expectedOutput), normalizeLineEndings(actualOutput));
    }

    @Test
    public void displayMappableCourses_nonMappableNusCourseInUpperCase_expectNoMappableCourses()
            throws FileNotFoundException {
        JsonObject jsonObject = createDatabaseJsonObject();

        String nusCourseCode = "EE2026";
        filterCoursesCommand.displayMappableCourses(jsonObject, nusCourseCode);
        String expectedOutput = """
                -----------------------------------------------------
                No courses found for the given course code.
                It may not be mappable, or the given course code is not a course offered by NUS!
                -----------------------------------------------------
                """;
        String actualOutput = outputStreamCaptor.toString();
        assertEquals(normalizeLineEndings(expectedOutput), normalizeLineEndings(actualOutput));
    }

    @Test
    public void execute_oneNusCourseCode_expectMappableCoursesList() {
        String input = "filter CS3241";
        filterCoursesCommand.execute(input);
        String expectedOutput = """
                Partner University: The University of Melbourne
                Partner University Course Code: COMP30019
                -----------------------------------------------------
                Partner University: The Australian National University
                Partner University Course Code: COMP4610
                -----------------------------------------------------
                """;
        String actualOutput = outputStreamCaptor.toString();
        assertEquals(normalizeLineEndings(expectedOutput), normalizeLineEndings(actualOutput));
    }

    @Test
    public void execute_twoNusCourseCodes_expectErrorMessage() {
        String userInput = "filter CS3241 Ee2026";
        filterCoursesCommand.execute(userInput);
        String expectedOutput = """
                -----------------------------------------------------
                Please note that we can only filter for only one NUS Course!
                -----------------------------------------------------
                """;
        String actualOutput = outputStreamCaptor.toString();
        assertEquals(normalizeLineEndings(expectedOutput),
                normalizeLineEndings(actualOutput));
    }

    public JsonObject createDatabaseJsonObject() throws FileNotFoundException {
        JsonReader jsonReader = Json.createReader(new FileReader("./data/database.json"));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        return jsonObject;
    }

    String normalizeLineEndings(String input) {
        return input.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").trim();
    }
}
