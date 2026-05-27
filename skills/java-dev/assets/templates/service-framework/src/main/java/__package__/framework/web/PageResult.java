package {{packageRoot}}.framework.web;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "分页返回结果")
public record PageResult<T>(
        @Schema(description = "数据列表") List<T> records,
        @Schema(description = "总条数") Long total,
        @Schema(description = "当前页码") Long pageNum,
        @Schema(description = "每页条数") Long pageSize
) {
}
