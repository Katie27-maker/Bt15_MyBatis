package com.lec.spring.controller;

import com.lec.spring.domain.FilterDTO;
import com.lec.spring.domain.GridDTO;
import com.lec.spring.domain.Nation;
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
    public String mainList(Model model){
        List<GridDTO> gridData = gridService.list();
        List<Nation> allNationList = gridService.findAllNation();
        List<String> nationList = allNationList.stream().map(data->data.getNation()).toList();
        Map<String, List<String>> cityList = new HashMap<>();
        nationList.forEach(nation->{
            cityList.put(nation,allNationList.stream()
                    .filter(data->data.getNation().equals(nation))
                    .map(Nation::getCity).toList());
        });
        Set<String> resultNationList = new HashSet<>(nationList);
        System.out.println(gridData);
        System.out.println(resultNationList);
        System.out.println(cityList);
        model.addAttribute("gridData",gridData);
        model.addAttribute("nationData",resultNationList);
        model.addAttribute("cityList", cityList);
        return "list";
    }

    // 필터링 컨트롤러
    @PostMapping("/Grid")
    private @ResponseBody Map<String, Object> filter(@RequestBody FilterDTO fliterDTO){
        System.out.println("필터 작업중...");
        List<GridDTO> resultFilter = gridService.fliterList(fliterDTO);
        List<Nation> allNationList = gridService.findAllNation();
        List<String> nationList = allNationList.stream().map(data->data.getNation()).toList();
        Set<String> resultNationList = new HashSet<>(nationList);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("data", resultFilter);
        response.put("allNationList",allNationList);
        response.put("nationData",resultNationList);
        return response;
    }

    @PostMapping("/searchByGender")
    private @ResponseBody Map<String, Object> searchByGender(@RequestBody FilterDTO fliterDTO){
        System.out.println("젠더 작업 중.");
        List<GridDTO> resultFilter = gridService.fliterList(fliterDTO);
        List<Nation> allNationList = gridService.findAllNation();
        List<String> nationList = allNationList.stream().map(data->data.getNation()).toList();
        Set<String> resultNationList = new HashSet<>(nationList);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("data", resultFilter);
        response.put("allNationList",allNationList);
        response.put("nationData",resultNationList);
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