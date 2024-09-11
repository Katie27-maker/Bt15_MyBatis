package com.lec.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterDTO {

    private String user_id;
    private String name;
    private String gender;
    private LocalDateTime startRegDate;
    private LocalDateTime endRegDate;
    private String nation;
    private String city;
}
