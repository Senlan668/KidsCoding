package com.kidscoding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建用户请求对象（DTO — Data Transfer Object）
 *
 * 类比 Python Pydantic:
 *   class CreateUserRequest(BaseModel):
 *       username: str = Field(min_length=2, max_length=20)
 *       nickname: str = Field(min_length=1, max_length=50)
 *       role: str = Field(pattern="^(PARENT|CHILD|TEACHER)$")
 *
 * 为什么不直接用 User Entity？
 *   1. Entity 的 id 是自动生成的，不应该由前端传
 *   2. 校验规则可能不同：创建时必填，更新时可选
 *   3. 职责分离：DTO 负责和前端交互，Entity 负责和数据库交互
 */
@Data
public class CreateUserRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度2-20个字符")
    private String username;

    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 50, message = "昵称长度1-50个字符")
    private String nickname;

    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "PARENT|CHILD|TEACHER", message = "角色必须是 PARENT/CHILD/TEACHER")
    private String role;
}
