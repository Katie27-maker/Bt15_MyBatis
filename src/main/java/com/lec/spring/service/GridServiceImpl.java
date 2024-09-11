package com.lec.spring.service;

import com.lec.spring.domain.FilterDTO;
import com.lec.spring.domain.Nation;
import com.lec.spring.domain.Grid;
import com.lec.spring.domain.GridDTO;
import com.lec.spring.repository.GridRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GridServiceImpl implements GridService {

    private GridRepository gridRepository;

    @Autowired
    public GridServiceImpl(SqlSession sqlSession){
    gridRepository = sqlSession.getMapper(GridRepository.class);
    System.out.println("gridRepository 생성");
    }


    public Object getUsersPointStats(HttpServletResponse response) {
        List<GridDTO> gridDTOList = gridRepository.findAll();
        createExcelDownloadResponse(response, gridDTOList);
        return null;
    }


    public void createExcelDownloadResponse(HttpServletResponse response, List<GridDTO> gridDTOList){

        try{
        Workbook workbook = new XSSFWorkbook(); // 깡통 문서 생성
        Sheet sheet = workbook.createSheet("통계"); // 문서에 시트 새로 생성 ("시트이름")

        //숫자 포맷은 아래 numberCellStyle을 적용시킬 것이다
        CellStyle numberCellStyle = workbook.createCellStyle();
        numberCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

        // 문서 테이블의 헤드 영역 Start
        final String[] header = {"아이디", "이름", "성별", "국가", "도시"};
        Row row = sheet.createRow(0);   // 시트에서 첫번째 행 선언 => createRow("행 숫자)")
        for (int i = 0; i < header.length; i++) {
            Cell cell = row.createCell(i); // 지정된 좌표에 셀 생성 => createCell("열 숫자")
            cell.setCellValue(header[i]);  // 생성된 셀에 데이터 삽입
        }
        // 문서 테이블의 헤드 영역 End
        // 문서 테이블의 바디 영역 Start
        for (int i = 0; i < gridDTOList.size(); i++) {
            row = sheet.createRow(i + 1);  //헤더 이후로 데이터가 출력되어야하니 +1
            GridDTO gridDTO = gridDTOList.get(i);
            Cell cell = null;

            // 유저 아이디 삽입
            cell = row.createCell(0);
            cell.setCellValue(gridDTO.getUser_id());

            // 이름 삽입
            cell = row.createCell(1);
            cell.setCellValue(gridDTO.getName());

            // 성별 삽입
            cell = row.createCell(2);
            cell.setCellValue(gridDTO.getGender());

            // 국가 삽입
            cell = row.createCell(3);
            cell.setCellValue(gridDTO.getNation());

            // 도시 삽입
            cell = row.createCell(4);
            cell.setCellValue(gridDTO.getCity());

        }
        // 문서 테이블의 바디 영역 End
        // 응답하는 파일 형식을 엑셀로 지정
        response.setContentType("application/vnd.ms-excel");
        // 생성되는 엑셀 파일의 파일명 지정과 인코딩 타입 지정 => 한글이 포함되어 있는 문서면 UTF-8로 지정 / 마지막에 ".xlsx"로 파일 확장자명 지정
        response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode("리스트 다운로드", "UTF-8")+".xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public List<GridDTO> list(){
        List<GridDTO> result = gridRepository.findAll();
        return result;
    };

    public List<Nation> findAllNation(){
        List<Nation> result = gridRepository.findAllNation();
        return result;
    }

    public List<GridDTO> fliterList(FilterDTO filterDTO){
        System.out.println("서비스 imp 통과");
        System.out.println(filterDTO);
        String userid = filterDTO.getUser_id();
        String name = filterDTO.getName();
        String gender = filterDTO.getGender();
        String nation = filterDTO.getNation();
        String city = filterDTO.getCity();
        LocalDateTime startDate = filterDTO.getStartRegDate();
        LocalDateTime endDate = filterDTO.getEndRegDate();
        List<GridDTO> result = gridRepository.search(userid,name, gender, nation, city, startDate, endDate);
        System.out.println(result);
        return result;
    };

    @Override
    public void deleteById(List<String> deleteIdList) {
        System.out.println(deleteIdList);
        deleteIdList.forEach(id->{
            System.out.println(id);
            gridRepository.delete(id);
        });
        System.out.println("데이터 삭제 완료");
    }

    @Override
    public void create(List<GridDTO> gridDTO){
        System.out.println("추가하는 row ID 중복 체크 중...");
        List<String> gridAllIdList = gridRepository.findAllId();
        gridDTO.forEach(e->{
            if(gridAllIdList.contains(e.getUser_id())){
                System.out.println("중복되는 아이디 : " + e.getUser_id());
                System.out.println("중복된 아이디 입니다.");
                throw new DuplicateKeyException("이미 존재하는 아이디 입니다.");
            }
        });
        System.out.println("아이디 중복 체크 이상 없음");
        System.out.println("데이터 저장 작업 중...");
        gridDTO.forEach(e->{
            System.out.println("생성 작업");
            String user_id = e.getUser_id();
            String name = e.getName();
            String gender = e.getGender();
            String regDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            // 나라 + 도시로 Nation Id 가져오기
            Long nation_id = findByNation(e.getNation(),e.getCity()).getId();
            System.out.println("국적  id : " + nation_id);
            gridRepository.create(user_id, name, gender,regDate, nation_id );
            System.out.println("생성 완료");
        });
    }
//
//            if(e.getUser_id() != null){
//                System.out.println("수정 작업");
//                // 요기 아래 수정 영역
//                String user_id = e.getUser_id();
//                String name = e.getName();
//                String gender = e.getGender();
//                Long nation_id = gridRepository.findByNation(e.getNation(),e.getCity()).getId();
//                // 요기 아래부터 레포지토리 영역
//                gridRepository.upDate(user_id, name, gender,nation_id);
//                System.out.println("수정 완료");
//
//            } else {

//            }

    public void update(GridDTO gridDTO){
//        Set<String> updategridAllIdList = new HashSet<>(gridRepository.findAllId());
//        gridDTO.forEach(e->{
//            if(updategridAllIdList.contains(e.getUser_id())){
//                System.out.println("중복되는 아이디 : " + e.getUser_id());
//                System.out.println("중복된 아이디 입니다.");
//                throw new DuplicateKeyException("이미 존재하는 아이디 입니다.");
//            }
//        });
        Nation updateNation = gridRepository.findByNation(gridDTO.getNation(), gridDTO.getCity());
        gridRepository.upDate(gridDTO.getUser_id(),gridDTO.getName(), gridDTO.getGender(),updateNation.getId());
    }

    public Nation findByNation(String nation, String city)
    {
        return gridRepository.findByNation(nation, city);

    }
}