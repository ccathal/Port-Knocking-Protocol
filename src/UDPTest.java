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
    public void whenCanSendAndReceivePacket_thenCorrect() {
    	String[] knockingSequence = {"5", "7000", "4000", "6543"};
        client.sendEcho(knockingSequence, portNumber);
//        assertEquals("hello server", echo);
//        echo = client.sendEcho("server is working");
//        assertFalse(echo.equals("hello server"));
    }
 
    @After
    public void tearDown() {
//        client.sendEcho("end");
//        client.close();
    }
}