package com.lec.spring.repository;

import com.lec.spring.domain.Grid;
import com.lec.spring.domain.GridDTO;
import com.lec.spring.domain.Nation;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface GridRepository {

    // 조회용
    List<GridDTO> findAll();

    // 모든 국적 및 도시 조회용
    List<Nation> findAllNation();

    // 필터용
    List<GridDTO> search(
    String user_id,
    String name,
    String gender,
    String nation,
    String city,
    LocalDateTime startDate,
    LocalDateTime endDate
    );


    Grid findById(String user_id);

    // 삭제용
    void delete(String user_id);

    // 국적 및 도시 조회용
    Nation findByNation(String nation, String city);

    // 모든 그리드 아이디 조회
    List<String> findAllId();

    // 생성용
    void create(
            String user_id,
            String name,
            String gender,
            String regDate,
            Long nation_id

    );

    // 수정용
    void upDate(
                String user_id,
                String name,
                String gender,
                Long nation_id);
}



