package {{packageRoot}}.{{businessPackage}}.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "示例详情响应")
public class SampleVo {
    @Schema(description = "示例ID")
    private String id;

    @Schema(description = "示例名称")
    private String name;
}
