import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.*;

public class UDPTest {
    MyClient client;
    private final int portNumber = 4445;
 
    @Before
    public void setup(){
        new MyServer(portNumber).start();
        client = new MyClient();
    }
 
    @Test
    public void test() {
    	String[] knockingSequence = {"5", "7000", "4000", "6543"};
    	String[] knockingSequence2 = {"5", "70100", "4000", "6543"};
        String connection = client.sendEcho(knockingSequence, portNumber);
        assertEquals("success", connection);
        String connection2 = client.sendEcho(knockingSequence2, portNumber);
        assertFalse(connection2.equals("success"));
    }
 
    @After
    public void tearDown() {
//        client.sendEcho("end");
//        client.close();
    }
}