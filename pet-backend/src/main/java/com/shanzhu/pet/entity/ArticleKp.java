package com.shanzhu.pet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 文章封面
 *
 * @author: ShanZhu
 * @date: 2024-01-08
 */
@Data
@TableName("article_kp")
@ApiModel(value = "ArticleKp对象", description = "")
public class ArticleKp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("标题")
    private String name;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("时间")
    private String time;

    @ApiModelProperty("封面")
    private String img;

    @ApiModelProperty("阅读数")
    private Integer read1;

}
