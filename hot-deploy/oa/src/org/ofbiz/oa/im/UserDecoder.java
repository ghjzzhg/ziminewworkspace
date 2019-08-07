package org.ofbiz.oa.im;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.config.managed.Decoder;

import java.io.IOException;

public class UserDecoder implements Decoder<String, UserMessage> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public UserMessage decode(String s) {
        try {
            return mapper.readValue(s, UserMessage.class);
        } catch (IOException e) {
//            throw new RuntimeException(e);
        }
        return new UserMessage();
    }
}