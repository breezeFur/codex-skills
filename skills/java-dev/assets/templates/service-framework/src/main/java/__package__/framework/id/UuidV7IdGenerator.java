package {{packageRoot}}.framework.id;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidV7IdGenerator implements IdGenerator {

    @Override
    public String nextId() {
        // Replace this fallback with the project's chosen UUIDv7 library implementation.
        return UUID.randomUUID().toString();
    }
}
