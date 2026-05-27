package {{packageRoot}}.db.config;

import {{packageRoot}}.framework.context.RequestContext;
import {{packageRoot}}.framework.context.RequestContextHolder;
import {{packageRoot}}.framework.id.IdGenerator;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Component
public class AuditMetaObjectHandler implements MetaObjectHandler {
    private static final String SYSTEM_USER_ID = "system";

    private final IdGenerator idGenerator;

    public AuditMetaObjectHandler(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        long now = System.currentTimeMillis();
        String userId = currentUserId();
        if (getFieldValByName("id", metaObject) == null) {
            strictInsertFill(metaObject, "id", String.class, idGenerator.nextId());
        }
        strictInsertFill(metaObject, "createdAt", Long.class, now);
        strictInsertFill(metaObject, "updatedAt", Long.class, now);
        strictInsertFill(metaObject, "createdBy", String.class, userId);
        strictInsertFill(metaObject, "updatedBy", String.class, userId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName("updatedAt", System.currentTimeMillis(), metaObject);
        setFieldValByName("updatedBy", currentUserId(), metaObject);
    }

    private String currentUserId() {
        RequestContext context = RequestContextHolder.get();
        if (context == null || context.userId() == null || context.userId().isBlank()) {
            return SYSTEM_USER_ID;
        }
        return context.userId();
    }
}
