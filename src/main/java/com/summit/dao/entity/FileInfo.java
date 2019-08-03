package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName(value = "file_info")
public class FileInfo {
    @TableId(value = "file_id", type = IdType.ID_WORKER_STR)
    private String fileId;
    @TableField(value = "file_name")
    private String filenName;
    @TableField(value = "file_path")
    private String filePath;
    @TableField(value = "description")
    private String description;

    public FileInfo(){}

    public FileInfo(String filenName, String filePath, String description) {
        this.filenName = filenName;
        this.filePath = filePath;
        this.description = description;
    }
}