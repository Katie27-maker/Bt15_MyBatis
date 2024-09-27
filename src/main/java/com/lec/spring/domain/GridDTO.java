package com.lec.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GridDTO {

    private String user_id;
    private String name;
    private String gender;
    private LocalDateTime regDate;
    private String nation;
    private String city;

    private Long grid_id;
}
