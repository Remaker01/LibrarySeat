package com.libraryseat.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryseat.Response;
import com.libraryseat.pojo.Reservation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
//除list和Response外均统一格式
public class JsonUtil {
    private static final JsonFactory FACTORY = new JsonFactory();
    static {
        FACTORY.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    }
    private static final ObjectMapper MAPPER = new ObjectMapper(FACTORY);

    private JsonUtil() {}

    public static void writePojo(Object obj, OutputStream stream) throws IOException{
        if (obj instanceof Response) {
            writeResponse((Response) obj,stream);
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
        writeCollection(reservations,stream);
    }
    public static void writeReservation(Reservation reservation, OutputStream stream) throws IOException {
        writePojo(reservation,stream);
    }
    public static void writeResponse(Response response,OutputStream stream) throws IOException {
        MAPPER.writeValue(stream,response);
    }
}
