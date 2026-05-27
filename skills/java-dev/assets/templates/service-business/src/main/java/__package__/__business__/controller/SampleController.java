package {{packageRoot}}.{{businessPackage}}.controller;

import {{packageRoot}}.framework.web.Result;
import {{packageRoot}}.{{businessPackage}}.service.SampleService;
import {{packageRoot}}.{{businessPackage}}.vo.SampleVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "示例管理", description = "示例数据查询接口")
@RestController
@RequestMapping("/api/samples")
public class SampleController {
    private final SampleService sampleService;

    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @Operation(summary = "查询示例详情", description = "根据示例ID查询示例详情")
    @GetMapping("/{id}")
    public Result<SampleVo> detail(@Parameter(description = "示例ID") @PathVariable String id) {
        return Result.success(sampleService.detail(id));
    }
}
