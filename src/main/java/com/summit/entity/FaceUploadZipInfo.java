package com.summit.entity;

import lombok.Data;

import java.util.List;

@Data
public class FaceUploadZipInfo {
  /*  private String faceName;*/
    private String upstate;
   /* private String upDescription;*/
    private List<String> errorList;
}
