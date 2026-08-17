package {{packageRoot}}.db.result;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

/**
 * MyBatis-Plus 分页请求与响应对象，由持久化框架直接填充分页结果。
 *
 * @param <T> 后端确定的分页记录类型
 */
@Schema(description = "分页请求与响应")
public class YPage<T> extends Page<T> {

    @Override
    @Schema(description = "当前页码", example = "1")
    @Min(value = 1, message = "页码必须大于等于 1")
    public long getCurrent() {
        return super.getCurrent();
    }

    @Override
    @Schema(description = "每页数量", example = "20")
    @Min(value = 1, message = "每页数量必须大于等于 1")
    @Max(value = 500, message = "每页数量不能超过 500")
    public long getSize() {
        return super.getSize();
    }

    @Override
    @Schema(description = "总记录数", accessMode = Schema.AccessMode.READ_ONLY)
    public long getTotal() {
        return super.getTotal();
    }

    @Override
    @Schema(description = "总页数", accessMode = Schema.AccessMode.READ_ONLY)
    public long getPages() {
        return super.getPages();
    }

    @Override
    @Schema(description = "当前页记录", accessMode = Schema.AccessMode.READ_ONLY)
    public List<T> getRecords() {
        return super.getRecords();
    }

    public YPage() {
        super();
        setRecords(List.of());
    }

    public YPage(long current, long size) {
        super(current, size);
        setRecords(List.of());
    }

    public YPage(long current, long size, long total) {
        super(current, size, total);
        setRecords(List.of());
    }

    public static <T> YPage<T> of(long current, long size) {
        return new YPage<>(current, size);
    }

    public static <T> YPage<T> of(long current, long size, long total) {
        return new YPage<>(current, size, total);
    }

    public static <T> YPage<T> of(long current, long size, long total, List<T> records) {
        YPage<T> page = new YPage<>(current, size, total);
        page.setRecords(records == null ? List.of() : records);
        return page;
    }

    public static <T> YPage<T> empty(long current, long size) {
        return new YPage<>(current, size, 0);
    }
}
