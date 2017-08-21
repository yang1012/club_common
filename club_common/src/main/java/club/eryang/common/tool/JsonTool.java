package club.eryang.common.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Mr.yang
 * @version V1.0
 * @ClassName: JsonTool
 * @package club.yang.tools
 * @Description: 标准的对象, json转换。
 * @date 2016年1月17日 下午4:57:43
 */
public class JsonTool {

    /**
     * json转换成泛型对象
     *
     * @param clazz
     * @param json
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T jsonToObject(Class<T> clazz, String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(json, clazz);
    }

    /**
     * 对象转换成json字符串
     *
     * @param o
     * @return
     */
    public static String toJsonStr(Object o) {
        return JSON.toJSONString(o, SerializerFeature.WriteNullListAsEmpty, SerializerFeature
                .WriteNullNumberAsZero, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature
                .WriteNullBooleanAsFalse, SerializerFeature.IgnoreErrorGetter, SerializerFeature.IgnoreNonFieldGetter, SerializerFeature.WriteNonStringKeyAsString, SerializerFeature.WriteNonStringValueAsString, SerializerFeature.WriteMapNullValue);
    }

    public static void main(String[] args) {
        String json = "{\"method\":\"\",\"params\":[{\"skuString\":\"1_31772\"},{\"skuString2\":\"1_3177222222\"}]}";

        // List list = (List) JsonTool.jsonToMap(json).get("params");
        // <String, String> s = (Map<String, String>) list.get(1);
        try {
            HashMap<String, String> t = JsonTool.jsonToObject(new HashMap<String, String>().getClass(), json);
            System.out.println(t);
            System.out.println(t.getClass().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
