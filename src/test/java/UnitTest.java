import com.libraryseat.pojo.*;
import com.libraryseat.utils.JsonUtil;
import org.junit.Test;

import java.io.IOException;

public class UnitTest {
    @Test
    public void testJson() {
        Room room = new Room();
        room.setRoomid(1000);
        room.setRoomname("综合阅览区1");
        room.setAdmin(10001);
        try {
            JsonUtil.writePojo(room,System.err);
        }catch (IOException e) {
            throw new AssertionError();
        }
    }
}
