package com.lec.spring.controller;

import com.lec.spring.domain.FilterDTO;
import com.lec.spring.domain.GridDTO;
import com.lec.spring.domain.Nation;
import com.lec.spring.domain.Pagination;
import com.lec.spring.service.GridService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

// Controller layer
//  request 처리 ~ response
@Controller
@RequestMapping("/")
public class GridController {

    @Autowired
    private GridService gridService;


    // 사이트 내에 출력된 그리드들 엑셀로 다운로드
    @GetMapping("/ExcelLoad")
    public ResponseEntity getUsersPointStats(HttpServletResponse response) {
        return ResponseEntity.ok(gridService.getUsersPointStats(response));
    }

    // 사이트 접속시 모든 그리드 리스트 호출
    @GetMapping("/Grid")
    public String mainList(Model model,
                           @RequestParam(required=false, defaultValue="1") Integer index,           //  쪽 인덱스 번호
                           @RequestParam(required = false, defaultValue = "1") Integer range ) {    //  쪽 번호

        System.out.println("index : " + index);
        System.out.println("range : " + range);
        Pagination pagination = new Pagination();
        pagination.setCurrPageNo(((range - 1) * 5)  + index);    // 👍 현재 페이지 구하는 공식

        List<GridDTO> gridData = gridService.list();
        pagination.setDefaultValue(gridData.size(), range);      // 페이지네이션 초기화
        List<GridDTO> resultGridData = gridData.subList(((pagination.getCurrPageNo()-1)* pagination.getPageSize()),
                                                          Math.min(pagination.getCurrPageNo() * pagination.getPageSize(),gridData.size()));

        List<Nation> allNationList = gridService.findAllNation();
        List<String> nationList = allNationList.stream().map(data->data.getNation()).toList();
        Map<String, List<String>> cityList = new HashMap<>();
        nationList.forEach(nation->{
            cityList.put(nation,allNationList.stream()
                    .filter(data->data.getNation().equals(nation))
                    .map(Nation::getCity).toList());
        });
        Set<String> resultNationList = new HashSet<>(nationList);
        System.out.println(resultGridData);
        System.out.println(resultNationList);
        System.out.println(cityList);
        model.addAttribute("pageData", pagination);
        model.addAttribute("gridData",resultGridData);
        model.addAttribute("nationData",resultNationList);
        model.addAttribute("cityList", cityList);
        return "list";
    }

    @GetMapping("/GridLink")
    public @ResponseBody Map<String, Object> link(@RequestParam Long grid_id){
        System.out.println(grid_id);
        GridDTO gridData = gridService.linkSearch(grid_id);
        List<Nation> allNationList = gridService.findAllNation();
        List<String> nationList = allNationList.stream().map(data->data.getNation()).toList();
        Set<String> resultNationList = new HashSet <>(nationList);
        System.out.println(gridData);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("data", gridData);
        response.put("allNationList",allNationList);
        response.put("nationData",resultNationList);
        return response;

    }

    // 필터링 컨트롤러
    @PostMapping("/Grid")
    private @ResponseBody Map<String, Object> filter(@RequestBody FilterDTO fliterDTO,
                                                     @RequestParam(required=false, defaultValue="1") Integer index,           //  쪽 인덱스 번호
                                                     @RequestParam(required = false, defaultValue = "1") Integer range){
        System.out.println("필터 작업중...");
        List<GridDTO> resultFilter = gridService.fliterList(fliterDTO);
        List<Nation> allNationList = gridService.findAllNation();

        Pagination pagination = new Pagination();
        pagination.setDefaultValue(resultFilter.size(), range);      // 페이지네이션 초기화
        pagination.setCurrPageNo(((range - 1) * 5)  + index);    // 👍 현재 페이지 구하는 공식
        List<GridDTO> resultGridData = resultFilter.subList(((pagination.getCurrPageNo()-1)* pagination.getPageSize()),
                Math.min(pagination.getCurrPageNo() * pagination.getPageSize(),resultFilter.size()));

        List<String> nationList = allNationList.stream().map(data->data.getNation()).toList();
        Set<String> resultNationList = new HashSet<>(nationList);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("data", resultGridData);
        response.put("allNationList",allNationList);
        response.put("nationData",resultNationList);
        response.put("pageData", pagination);

        return response;
    }

    @PostMapping("/searchByGender")
    private @ResponseBody Map<String, Object> searchByGender(@RequestBody FilterDTO fliterDTO,
                                                             @RequestParam(required=false, defaultValue="1") Integer index,           //  쪽 인덱스 번호
                                                             @RequestParam(required = false, defaultValue = "1") Integer range){
        System.out.println("젠더 작업 중.");
        List<GridDTO> resultFilter = gridService.fliterList(fliterDTO);
        List<Nation> allNationList = gridService.findAllNation();
        List<String> nationList = allNationList.stream().map(data->data.getNation()).toList();
        Set<String> resultNationList = new HashSet<>(nationList);
        Map<String, Object> response = new HashMap<>();

        Pagination pagination = new Pagination();
        pagination.setDefaultValue(resultFilter.size(), range);

        pagination.setCurrPageNo(((range - 1) * 5)  + index);    // 👍 현재 페이지 구하는 공식
        List<GridDTO> genderGridData = resultFilter.subList(((pagination.getCurrPageNo()-1)* pagination.getPageSize()),
                Math.min(pagination.getCurrPageNo() * pagination.getPageSize(),resultFilter.size()));

        response.put("status", "OK");
        response.put("data", genderGridData);
        response.put("allNationList",allNationList);
        response.put("nationData",resultNationList);
        response.put("pageData", pagination);
        System.out.println("넘어오기 완료");
        return response;
    }

//    삭제 컨트롤러
    @DeleteMapping("/Grid")
    public @ResponseBody Map<String, Object> delete(@RequestBody List<String> deleteIdList){
        System.out.println("삭제 중");
        System.out.println(deleteIdList);
        gridService.deleteById(deleteIdList);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
           return response;
    }

//    저장 컨트롤러

     @PutMapping("/Grid")
     public @ResponseBody Map<String, Object> create(@RequestBody List<GridDTO> gridDTO){
         System.out.println("데이터 저장 중...");
         System.out.print("저장 하는 데이터 : ");
         System.out.println(gridDTO);
         Map<String, Object> response = new HashMap<>();
         try {
             gridService.create(gridDTO);
             response.put("status", "OK");
         } catch (DuplicateKeyException e){
             response.put("status","Duplicate");
         }
         return response;
     }

     @PatchMapping("/Grid")
     public @ResponseBody Map<String, Object> update(@RequestBody GridDTO gridDTO){
         System.out.println("데이터 수정 중임");
         System.out.println("수정하는 데이터" + gridDTO);
         Map<String, Object> response = new HashMap<>();
         try {
             gridService.update(gridDTO);
             response.put("status", "OK");
         } catch (DuplicateKeyException e){
             response.put("status","Duplicate");
         }
         return response;
     };


} // end Controller