package com.libraryseat.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryseat.Response;
import com.libraryseat.pojo.LibraryMetadata;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
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
        if (obj instanceof LibraryMetadata) {
            writeMetadata((LibraryMetadata) obj,stream);
            return;
        }
        Response r = new Response(obj == null ? "响应数据为空！" : "请求成功！",obj);
        MAPPER.writeValue(stream,r);
    }

    public static void writeCollection(Collection<?> collection, OutputStream stream) throws IOException {
        MAPPER.writeValue(stream,collection);
    }

    public static void writeResponse(Response response,OutputStream stream) throws IOException {
        MAPPER.writeValue(stream,response);
    }

    public static void writeMetadata(LibraryMetadata metadata,OutputStream stream) throws IOException{
        JsonGenerator generator = FACTORY.createGenerator(stream);
        generator.writeStartObject();
        if (metadata == null){
            generator.writeStringField("msg","响应数据为空！");
            generator.writeNullField("data");
        } else {
            generator.writeStringField("msg","请求成功！");
            Calendar open = metadata.getOpenTime(),close=metadata.getCloseTime(),latest=metadata.getLatestReservationTime();
            generator.writeObjectFieldStart("data");
            generator.writeStringField("open",formatCalendar(open));
            generator.writeStringField("close",formatCalendar(close));
            generator.writeStringField("latest",formatCalendar(latest));
            generator.writeStringField("province", metadata.getProvince());
            generator.writeStringField("city", metadata.getCity());
        }
        generator.writeEndObject();
        generator.close();
    }

    private static String formatCalendar(Calendar calendar){
        if (calendar == null)
            return null;
        return String.format("%d:%02d",calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));
    }
}
