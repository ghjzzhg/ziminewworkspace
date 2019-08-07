package org.ofbiz.oa.im;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.config.managed.Decoder;

import java.io.IOException;

public class ProtocolDecoder implements Decoder<String, IMProtocal> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public IMProtocal decode(String s) {
        try {
            return mapper.readValue(s, IMProtocal.class);
        } catch (IOException e) {
//            throw new RuntimeException(e);
        }
        return new IMProtocal();
    }
}