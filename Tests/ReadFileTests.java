import org.junit.jupiter.api.Test;

public class ReadFileTests {
    @Test
    public void ReadFirstFileTest(){
        String path = "C:\\Users\\erant\\Desktop\\project\\corpus";
        ReadFile readFile = new ReadFile(path);
        String docName = readFile.getNextDoc();

        //assertEquals("FB396001",docName, "Doc Name Except - FB396001");

    }

}
