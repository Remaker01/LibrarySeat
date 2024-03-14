import com.libraryseat.pojo.*;
import com.libraryseat.utils.ExcelUtil;
import com.libraryseat.utils.JsonUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UnitTest {
    @Test
    public void testJson() {
        Room room = new Room();
        room.setRoomid(1000);
        room.setRoomname("综合阅览区1");
        room.setAdmin(10001);
        List<Reservation> reservations = new ArrayList<>(3);
        long current = System.currentTimeMillis();
        Reservation r1 = new Reservation(1,10000,10000,new Timestamp(current));
        Reservation r2 = new Reservation(1,10001,10002,new Timestamp(current+1000));
        Reservation r3 = new Reservation(1,10002,10003,new Timestamp(current+2000));
        reservations.add(r1);reservations.add(r2);reservations.add(r3);
        try(ByteArrayOutputStream stream = new ByteArrayOutputStream(100)) {
            JsonUtil.writePojo(room, stream);
            stream.write('\n');
            JsonUtil.writeReservation(r1, stream);
            stream.write('\n');
            JsonUtil.writeReservations(reservations, stream);
            stream.write('\n');
            JsonUtil.writePojo(null,stream);
            System.out.println(new String(stream.toByteArray(),StandardCharsets.UTF_8));
        }catch (IOException e){
            throw new AssertionError(e);
        }
    }
    @Test
    public void testExcel(){
        File file = new File("test.xlsx");
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            List<User> users = ExcelUtil.getUsersInWorkbook(inputStream, "xlsx");
            Assert.assertEquals("size",users.size(),3);
            users.stream().forEach(user -> {
                System.out.print(user.getUsername());
                System.out.println(':'+user.getTruename());
            });
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
