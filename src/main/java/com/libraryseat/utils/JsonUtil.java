package com.libraryseat.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryseat.Response;
import com.libraryseat.pojo.*;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
//除list和Response外均统一格式
public class JsonUtil {
    private static final JsonFactory FACTORY = new JsonFactory();
    private static final ObjectMapper MAPPER = new ObjectMapper(FACTORY);

    private JsonUtil() {}

    public static void writePojo(Object obj, OutputStream stream) throws IOException{
        if (obj instanceof Reservation) {
            writeReservation((Reservation) obj, stream);
            return;
        }
        Map<String,Object> map = new HashMap<>(1);
        map.put("info",obj);
        MAPPER.writeValue(stream,map);
    }

    public static void writeCollection(Collection<?> collection, OutputStream stream) throws IOException {
        MAPPER.writeValue(stream,collection);
    }

    public static void writeReservations(Collection<Reservation> reservations, OutputStream stream) throws IOException {
        JsonGenerator generator = FACTORY.createGenerator(stream);
        generator.writeStartArray();
        if(reservations != null) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            for (Reservation reservation:reservations) {
                generator.writeStartObject();
                generator.writeNumberField("seatid",reservation.getSeatid());
                generator.writeNumberField("roomid",reservation.getRoomid());
                generator.writeNumberField("uid",reservation.getUid());
                generator.writeStringField("resTime", formatTime(dateFormat,reservation.getResTime()));
                generator.writeStringField("signinTime", formatTime(dateFormat,reservation.getSigninTime()));
                generator.writeStringField("signoutTime", formatTime(dateFormat,reservation.getSignoutTime()));
                generator.writeEndObject();
            }
        }
        generator.writeEndArray();
        generator.close();
    }
//jackson默认把日期时间序列化成时间戳，需特殊处理
    public static void writeReservation(Reservation reservation, OutputStream stream) throws IOException {
        JsonGenerator generator = newGenerator(stream);
        generator.writeObjectFieldStart("info");
        generator.writeNumberField("seatid",reservation.getSeatid());
        generator.writeNumberField("roomid",reservation.getRoomid());
        generator.writeNumberField("uid",reservation.getUid());
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        generator.writeStringField("resTime", formatTime(dateFormat,reservation.getResTime()));
        generator.writeStringField("signinTime", formatTime(dateFormat,reservation.getSigninTime()));
        generator.writeStringField("signoutTime", formatTime(dateFormat,reservation.getSignoutTime()));
        generator.writeEndObject();
        generator.close();
    }
    public static void writeResponse(Response response,OutputStream stream) throws IOException {
        MAPPER.writeValue(stream,response);
    }
    private static JsonGenerator newGenerator(OutputStream stream) throws IOException {
        JsonGenerator generator = FACTORY.createGenerator(stream);
        generator.writeStartObject();
        return generator;
    }

    private static String formatTime(SimpleDateFormat formatter,Date date){
        if (formatter == null)
            return null;
        if(date == null)
            return "";
        return formatter.format(date);
    }
}
