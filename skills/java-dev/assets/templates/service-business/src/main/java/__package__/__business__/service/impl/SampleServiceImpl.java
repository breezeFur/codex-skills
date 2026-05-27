package {{packageRoot}}.{{businessPackage}}.service.impl;

import {{packageRoot}}.framework.exception.BizException;
import {{packageRoot}}.{{businessPackage}}.service.SampleService;
import {{packageRoot}}.{{businessPackage}}.vo.SampleVo;
import org.springframework.stereotype.Service;

@Service
public class SampleServiceImpl implements SampleService {

    @Override
    public SampleVo detail(String id) {
        if (id == null || id.isBlank()) {
            throw new BizException(400, "示例ID不能为空");
        }
        SampleVo vo = new SampleVo();
        vo.setId(id);
        vo.setName("示例名称");
        return vo;
    }
}
