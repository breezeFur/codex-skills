package {{packageRoot}}.db.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("sample")
@Schema(description = "示例实体")
public class SampleEntity {
    @TableId("id")
    @Schema(description = "主键ID")
    private String id;

    @TableField("name")
    @Schema(description = "名称")
    private String name;

    @TableLogic
    @TableField("deleted")
    @Schema(description = "是否删除")
    private Integer deleted;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间，Unix 毫秒时间戳")
    private Long createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间，Unix 毫秒时间戳")
    private Long updatedAt;

    @TableField(value = "created_by", fill = FieldFill.INSERT)
    @Schema(description = "创建用户ID")
    private String createdBy;

    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新用户ID")
    private String updatedBy;
}
