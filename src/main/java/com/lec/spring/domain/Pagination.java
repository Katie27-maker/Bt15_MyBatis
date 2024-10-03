package com.lec.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {

    private int currPageNo; // 현재 페이지
    private int startPage; // 한 쪽에 첫번째 페이지
    private int endPage;  // 한 쪽에 마지막 페이지
    private int pageSize = 5;   // 페이지 당 나오는 row 갯수
    private int totalPage;  // 전체페이지

    public void setDefaultValue(int totalGrid, int range ){

        totalPage = (int) Math.ceil((double) totalGrid / pageSize );
        endPage = Math.min((range * pageSize), totalPage);     //  현재 쪽에 마지막 페이지 번호
        startPage = (((range - 1) * pageSize) + 1);            // 👍 첫 번째 페이지 구하는 공식
    }



}
