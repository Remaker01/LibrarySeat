import com.libraryseat.pojo.*;
import com.libraryseat.utils.ExcelUtil;
import com.libraryseat.utils.JsonUtil;
import org.junit.Test;

import java.io.*;
import java.util.List;

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
    @Test
    public void testExcel(){
        File file = new File("test.xlsx");
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            List<User> users = ExcelUtil.getUsersInWorkbook(inputStream, "xlsx");
            users.stream().forEach(user -> {
                System.out.print(user.getUsername());
                System.out.println(':'+user.getTruename());
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
