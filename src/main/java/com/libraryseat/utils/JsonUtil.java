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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
//除list和Response外均统一格式
public class JsonUtil {
    private static final Logger LOGGER = Logger.getLogger(JsonUtil.class.getName());
    private static final JsonFactory FACTORY = new JsonFactory();
    private static final ObjectMapper MAPPER = new ObjectMapper(FACTORY);
    static {
        LogUtil.initLogger(LOGGER);
        LOGGER.setLevel(Level.FINE);
    }

    private JsonUtil() {}

    public static void writePojo(Object obj, OutputStream stream) throws IOException{
        if (obj == null) {
            writeNullObject(stream);
        } else {
            Map<String,Object> map = new HashMap<>(1);
            map.put("info",obj);
            MAPPER.writeValue(stream,map);
        }
    }

    public static void writeCollection(Collection<?> collection, OutputStream stream) throws IOException {
        MAPPER.writeValue(stream,collection);
    }

    public static void writeReservations(Collection<Reservation> reservations, OutputStream stream) throws IOException {
        JsonGenerator generator = newGenerator(stream);
        generator.writeStartArray();
        if(reservations != null) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            for (Reservation reservation:reservations) {
                generator.writeNumberField("seatid",reservation.getSeatid());
                generator.writeNumberField("roomid",reservation.getRoomid());
                generator.writeNumberField("uid",reservation.getUid());
                generator.writeStringField("resTime", dateFormat.format(reservation.getResTime()));
                generator.writeStringField("signinTime", dateFormat.format(reservation.getSigninTime()));
                generator.writeStringField("signoutTime", dateFormat.format(reservation.getSignoutTime()));
            }
        }
        generator.writeEndArray();
        writeEndAndClose(generator);
    }
//jackson默认把日期时间序列化成时间戳，需特殊处理
    public static void writeReservation(Reservation reservation, OutputStream stream) throws IOException {
        if(reservation == null) {
            writeNullObject(stream);
        } else {
            JsonGenerator generator = newGenerator(stream);
            generator.writeObjectFieldStart("info");
            generator.writeNumberField("seatid",reservation.getSeatid());
            generator.writeNumberField("roomid",reservation.getRoomid());
            generator.writeNumberField("uid",reservation.getUid());
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            generator.writeStringField("resTime", dateFormat.format(reservation.getResTime()));
            generator.writeStringField("signinTime", dateFormat.format(reservation.getSigninTime()));
            generator.writeStringField("signoutTime", dateFormat.format(reservation.getSignoutTime()));
            writeEndAndClose(generator);
        }
    }
    public static void writeResponse(Response response,OutputStream stream) throws IOException {
        MAPPER.writeValue(stream,response);
    }
    private static JsonGenerator newGenerator(OutputStream stream) throws IOException {
        JsonGenerator generator = FACTORY.createGenerator(stream);
        generator.writeStartObject();
        return generator;
    }

    private static void writeEndAndClose(JsonGenerator generator) throws IOException {
        if (generator.isClosed())
            return;
        generator.writeEndObject();
        generator.close();
    }

    private static void writeNullObject(OutputStream stream) throws IOException {
        JsonGenerator generator = newGenerator(stream);
        generator.writeNullField("info");
        writeEndAndClose(generator);
    }
}
