package pax.TrainsSchedule.API;


import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import pax.TrainsSchedule.Model.Station;
import pax.TrainsSchedule.Model.Station.List;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedInput;

public class SuggestGsonConverter extends GsonConverter {

    public SuggestGsonConverter(Gson gson) {
        super(gson);
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        List res = null;

        String dirty = toString(body);
        if (dirty.isEmpty()) return null;

        String clean = dirty.replaceAll("]]]", "");
        clean = clean.replaceAll("\\[null,\\[\\[", "");
        String[] arr = clean.split("],\\[");

        if (arr.length == 0) return null;

        res = new List(arr.length);
        for (String substr : arr) {
            substr = substr.substring(1, substr.length() - 1);
            String[] arr2 = substr.split("\",\"");
            if (arr2.length != 3) continue;
            Station newSugg = new Station(arr2[0], arr2[1], arr2[2]);
            res.add(newSugg);
        }

        return res;
    }

    private String toString(TypedInput body) {
        String charset = "UTF-8";
        if (body.mimeType() != null) {
            charset = MimeUtil.parseCharset(body.mimeType());
        }
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(body.in(), charset));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
}
