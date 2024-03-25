package com.libraryseat.services;

import com.libraryseat.pojo.LibraryMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;
@Service
public class MetadataService {
    private final LibraryMetadata metadata;
    private static final Logger LOGGER = LogManager.getLogger(MetadataService.class.getName());

    public LibraryMetadata getMetadata() {
        return metadata;
    }

    public MetadataService(){
        metadata = new LibraryMetadata();
        Calendar open = Calendar.getInstance(TimeZone.getDefault());
        Calendar close = Calendar.getInstance(TimeZone.getDefault());
        Properties properties = new Properties();
        // 想改就到properties里，代码就不用动了
        try(InputStream stream = ReservationService.class.getResourceAsStream("/library.properties")){
            properties.load(stream);
            String openHour = properties.getProperty("open.hour","8");
            String closeHour = properties.getProperty("close.hour","22");
            String openMinute = properties.getProperty("open.minute","0");
            String closeMinute = properties.getProperty("close.minute","0");
            String province = properties.getProperty("location.province");
            String city = properties.getProperty("location.city");
            setCalendar(open,Integer.parseInt(openHour),Integer.parseInt(openMinute));
            setCalendar(close,Integer.parseInt(closeHour),Integer.parseInt(closeMinute));
            Calendar latest = (Calendar) close.clone();
            latest.add(Calendar.MINUTE,-30);
            metadata.setOpenTime(open);
            metadata.setCloseTime(close);
            metadata.setLatestReservationTime(latest);
            metadata.setProvince(province);
            metadata.setCity(city);
            LOGGER.debug("open={}:{}",openHour,openMinute);
        } catch (Exception e) {
            LOGGER.error("配置文件不存在或格式错误：",e);
            setCalendar(open,8,0);
            setCalendar(close,22,0);
        }
    }
    /**
     * 每天更新年月日。
     */
    public void resetDaily(){
        Calendar today = Calendar.getInstance(TimeZone.getDefault());
        int year = today.get(Calendar.YEAR),month = today.get(Calendar.MONTH),date = today.get(Calendar.DATE);
        metadata.getOpenTime().set(year,month,date); //三个time不用创建新的Calendar，直接设置属性即可。
        metadata.getCloseTime().set(year,month,date);
        metadata.getLatestReservationTime().set(year,month,date);
    }

    private static void setCalendar(Calendar calendar,int hour,int minute) throws IllegalArgumentException{
        if (hour < 0 || hour >= 24)
            throw new IllegalArgumentException("Wrong hour:" + hour);
        if (minute < 0||minute >= 60)
            throw new IllegalArgumentException("Wrong minute:" + minute);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
    }
}
