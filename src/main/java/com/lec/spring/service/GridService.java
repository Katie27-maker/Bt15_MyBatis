package com.lec.spring.service;

import com.lec.spring.domain.FilterDTO;
import com.lec.spring.domain.GridDTO;
import com.lec.spring.domain.Nation;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface GridService {

    List<GridDTO> list();

    List<GridDTO> fliterList(FilterDTO filterDTO);

    default void deleteById(List<String> deleteIdList) {
    }

    void create(List<GridDTO> gridDTO);

    void update(GridDTO gridDTO);

    List<Nation> findAllNation();

    void createExcelDownloadResponse(HttpServletResponse response, List<GridDTO> gridDTOList);

    Object getUsersPointStats(HttpServletResponse response);
}
