package {{packageRoot}}.framework.constants;

/**
 * 接口返回常量。
 *
 * <p>统一维护接口响应包装器和全局异常处理使用的基础返回码与默认提示文案。</p>
 */
public final class ApiResponseConstants {

    /**
     * 接口调用成功时使用的业务状态码。
     */
    public static final int SUCCESS_CODE = 0;

    /**
     * 请求参数校验失败时使用的业务状态码，对应客户端请求内容不符合接口约束。
     */
    public static final int VALIDATION_ERROR_CODE = 400;

    /**
     * 未捕获系统异常时使用的业务状态码，对应服务端内部处理失败。
     */
    public static final int SYSTEM_ERROR_CODE = 500;

    /**
     * 接口调用成功时返回的默认提示文案。
     */
    public static final String SUCCESS_MESSAGE = "操作成功";

    /**
     * 请求参数校验失败但未解析到字段错误时返回的默认提示文案。
     */
    public static final String VALIDATION_ERROR_MESSAGE = "请求参数校验失败";

    /**
     * 未捕获系统异常时返回给客户端的默认提示文案，避免泄露内部异常细节。
     */
    public static final String SYSTEM_ERROR_MESSAGE = "系统异常";

    private ApiResponseConstants() {
    }
}
