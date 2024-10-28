package seedu.exchangecoursemapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.exchangecoursemapper.command.ListSchoolCommand;
import seedu.exchangecoursemapper.command.ObtainContactsCommand;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListSchoolCommandTest {

    private ListSchoolCommand listSchoolCommand;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        outputStreamCaptor.reset();
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(outputStreamCaptor));
        listSchoolCommand = new ListSchoolCommand();
    }

    @Test
    public void execute_commandSuccessful() {
        listSchoolCommand.execute("list schools");

        String result = outputStreamCaptor.toString().trim();

        assertTrue(result.contains("The University of Melbourne"));
        assertTrue(result.contains("The Australian National University"));
        assertTrue(result.contains("Victoria University of Wellington"));
        assertTrue(result.contains("The University of Western Australia"));
    }

    @Test
    public void execute_validJsonFile_printsCorrectUniversityNames() throws Exception {
        JsonReader jsonReader = Json.createReader(new FileReader("./data/database.json"));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        ListSchoolCommand.displaySchoolList(jsonObject);

        String result = outputStreamCaptor.toString().trim();

        assertTrue(result.contains("The University of Melbourne"));
        assertTrue(result.contains("The Australian National University"));
        assertTrue(result.contains("Victoria University of Wellington"));
        assertTrue(result.contains("The University of Western Australia"));
    }

    @Test
    public void execute_validJsonFile_printsWrongUniversityNames() throws Exception {
        JsonReader jsonReader = Json.createReader(new FileReader("./data/database.json"));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        ListSchoolCommand.displaySchoolList(jsonObject);

        String result = outputStreamCaptor.toString().trim();

        assertFalse(result.contains("Chulalongkorn University"));
    }
}
