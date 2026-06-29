package {{packageRoot}}.framework.web;

import {{packageRoot}}.framework.constants.ApiResponseConstants;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "统一接口返回结果")
public record Result<T>(
        @Schema(description = "业务状态码") Integer code,
        @Schema(description = "返回消息") String message,
        @Schema(description = "返回数据") T data,
        @Schema(description = "请求是否成功") Boolean success
) {
    public static <T> Result<T> success(T data) {
        return new Result<>(ApiResponseConstants.SUCCESS_CODE, ApiResponseConstants.SUCCESS_MESSAGE, data, true);
    }

    public static Result<Void> success() {
        return new Result<>(ApiResponseConstants.SUCCESS_CODE, ApiResponseConstants.SUCCESS_MESSAGE, null, true);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null, false);
    }
}
