// 설별 색상
function changeGenderColor(element) {
    const row = element.closest('tr'); // 현재 성별 라디오 버튼이 있는 <tr> 요소를 찾음
    const isMale = element.value === '남'; // 선택된 성별이 '남'인지 여부 확인

    // <tr> 내부의 모든 <td> 태그를 가져옴
    row.querySelectorAll('td').forEach(function (td) {
        if (isMale) {
            td.style.backgroundColor = 'lightblue'; // 남자가 선택되면 파란색 배경
        } else {
            td.style.backgroundColor = 'lightpink'; // 여자가 선택되면 분홍색 배경
        }
    });
}



// 초기 로드 시 선택된 성별에 따라 배경색을 설정
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.userGender:checked').forEach(function (radio) {
        changeGenderColor(radio); // 선택된 라디오 버튼에 맞게 색상 적용
    });
});


const cityList = {
    한국 : ["서울특별시", "인천광역시", "대구광역시"],
    프랑스 : ["파리", "루베", "스트라스부르"],
    독일 : ["라이프치히", "뤼벡", "브레멘"],
}

function nationBtnChange(nationSelect) {
    let citySelect = $(`#${nationSelect.name}City`);
    // console.log(citySelect)

    if (nationSelect.value && cityList[nationSelect.value]) {
        // 해당 국가의 도시 옵션 추가
        cityList[nationSelect.value].forEach(function(city) {
            var option = `<label><input type="checkbox" value="${city}">${city}</input></label>`
            citySelect.append(option);
        });
    }
}

//#추가 영역
$(function () {
    var newGridIndex = 0;
    // 추가 버튼 클릭 시 반응하는 함수
    $("#addBtn").click(function () {
        if ($(".newGridData").length == 3) {
            alert("최대 3줄까지만 동시에 추가됩니다.");
        } else {
            newGridIndex++;
            var newRow =
                `<tr class="newGridData">
                    <td><input class="selectBox" type="checkbox"></td>
                    <td><input class="userId" type="text"></td>
                    <td><input class="userName" type="text"></td>
                    <td>
                        <input class="userGender" id="male" type="radio" name="newGender${newGridIndex}" value="남">
                        <p>남</p>
                        <input class="userGender" id="female" type="radio" name="newGender${newGridIndex}" value="여">
                        <p>여</p>
                    </td>
                    <td>
                        <select class="newUserNation" name="new${newGridIndex}" id="newNation" onchange="nationBtnChange(this)">
                            <option value="">-- 국가 선택 --</option>
                            <option value="한국">한국</option>
                            <option value="프랑스">프랑스</option>
                            <option value="독일">독일</option>
                            <option value="스위스">스위스</option>
                        </select>
                    </td>
                    <td>
                        <select class="newUserCity" name="new${newGridIndex}" id="new${newGridIndex}City">
                            <option value="">도시를 선택해 주세요.</option>
                        </select>
                    </td>
                </tr>`;
            if (($('#addList tr:first')[0]) !== undefined) {
                $('#addList tr:first').before(newRow);
            } else {
                $('#addList').after(newRow);
            }
        }
    });


    //#저장 영역
    $("#saveBtn").click(function () {
        let gridDataRow = $(".newGridData"); // 모든 그리드 태그 가져옴
        // 서버로 보낼 객체 생성 과정.
        let requestData = gridDataRow.map(function () {
            // GridDTO
            let nationData = "";
            let cityData = "";
            // 신규 그리드 국가면 newUserNation 태그 데이터 가져오고 아니면 userNation 데이터 가져옴
            if ($(this).find('.userNation').length === 0) {
                nationData = $(this).find('.newUserNation').val();
            } else nationData = $(this).find('.userNation').val();

            // 위 와 동일
            if ($(this).find('.userCity').length === 0) {
                cityData = $(this).find('.newUserCity').val();
            } else cityData = $(this).find('.userCity').val();

            return {
                'user_id': $(this).find('.userId').val(),
                'name': $(this).find('.userName').val(),
                // 아래 성별은 단순히 클래스로 불러오면 두개를 불러오는데 그중에 체크된 녀석 불러옴
                'gender': $(this).find('.userGender:checked').val(),
                'nation': nationData,
                'city': cityData
            }
        });

        console.log("새로 저장하는 그리들 데이터");
        console.log(gridDataRow);
        console.log("서버로 요청 보내는 데이터")
        console.log(requestData);

        let checkPass = true;
        // 검증 1. 추가 데이터들 중 빈값 체크
        requestData.each(function () {
            $.each(this, (key, data) => {
                if (data === undefined || data === null || data === "") {
                    alert("값을 다 기입하세요.");
                    checkPass = false;
                    return false;
                }
            });
        });

        // 검증 2. 추가 데이터 중 ID 값 중복 확인
        let newGridIdList = [...requestData].map(e => e.user_id);
        if (newGridIdList.length !== new Set(newGridIdList).size) {
            alert("중복되는 아이디가 있어요.")
            checkPass = false;
        }
        // 서버 전달 영역
        if (checkPass) {
            console.log("서버로 데이터 전송 중...");
            $.ajax({
                url: "/Grid",
                type: "PUT",
                cache: false,
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(requestData.get()),
                dataType: 'json',
                success: function (data, status) {
                    if (status === "success") {
                        if (data.status === "Duplicate") alert("중복된 아이디 입니다.\n아이디를 변경해주세요");
                        else if (data.status !== "OK") alert("요청 실패");
                        else {
                            alert("요청 완료")
                            // 요기 부분에 도착하면 추가랑 수정이 완료된거니깐
                            // 이 아래에 ajax로 get 타입에 /"Grid"를 호출하는거지!
                            // 자동으로 /Grid 새로고침
                            window.location.href = '/Grid'
                        }
                    }
                }
            })
        }
    });

    // 변경되 데이터
    $('#changbtn').click(function () {
        let requestData = {
            'user_id': $('#modalUserId').val(),
            'name': $('#modalName').val(),
            // 아래 성별은 단순히 클래스로 불러오면 두개를 불러오는데 그중에 체크된 녀석 불러옴
            'gender': $('.modalGender:checked').val(),
            'nation': $('#modalNation').val(),
            'city': $('#modalCity').val()
        }
        console.log(requestData);
        $.ajax({
            url: "/Grid",
            type: "Patch",
            cache: false,
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(requestData),
            dataType: 'json',
            success: function (data, status) {
                if (status === "success") {
                    if (data.status === "Duplicate") alert("중복된 아이디 입니다.\n아이디를 변경해주세요");
                    else if (data.status !== "OK") alert("요청 실패");
                    else {
                        alert("요청 완료")
                        // 요기 부분에 도착하면 추가랑 수정이 완료된거니깐
                        // 이 아래에 ajax로 get 타입에 /"Grid"를 호출하는거지!
                        // 자동으로 /Grid 새로고침
                        window.location.href = '/Grid'
                    }
                }
            }
        })
    })


        // 모달 관련 부분
        $('.gridData').dblclick(function () {
            // 여기에 제이쿼리로 모 달창 disply를 block이든 flex로 바꾸는 코드 생성해보봠.

            // $('#userModal').style.display = 'block';
            $('#userModal').css('display', 'block');
            // 위 주석 사이에
            // 여기 아래부터 제이쿼리로 데이터 넣어야하는 태그들 제이쿼리로 불러와서 value값에 아래 selectedData에 있는 것처럼 넣어봠

            $('#modalUserId').val($(this).find(".userId").val());   // 1. 모달태그( ), 2.
            $('#modalName').val($(this).find(".userName").val());
            if ($(this).find(".userGender:checked").val() === "남") {
                // 모달 창에서 남자 태그 체크하는 코드 작성
                $("#modalMale").prop("checked", true);
            } else {
                // 모달 창에서 여자 태그 체크하는 코드 작성
                $("#modalFemale").prop("checked", true);
            }


            // 나라 모달창 가져오기
            $('#modalNation').val($(this).find(".userNation").val());
            nationBtnChange($('#modalNation')[0]);
            $('#modalCity').val($(this).find(".userCity").val());


            // 모달 창 닫기 함수
            $("#closebtn").click(function () {
                $('#userModal').fadeOut();
            })

            // let selectedData  = {
            //     'let userId': $(this).find(".userId").val(),
            //     'let name' : $(this).find(".userName").val(),
            //     'let gender' : $(this).find(".userGender:checked").val(),
            //     'let nation' : $(this).find(".userNation").val(),
            //     'let city' : $(this).find(".userCity").val(),
            // }
        });


        //#조회 영역
        // $('#searchBtnm').click(function () {
        //     let filterData = {
        //         'gender': $('[name="fliterGender"]:checked').val(),
        //     }
        // })

    
        $('#gMale, #gFmale').click(function () {
            console.log("성별 눌러짐")
                let gender = $('#gMale').is(':checked') ? '남' : ($('#gFmale').is(':checked') ? '여' : null);
                    let filterData = {
                        'user_id': checkData($(`#idBox`).val()),
                        'name': checkData($(`#nameBox`).val()),
                        'gender': gender,
                        'startRegDate': checkDateData($(`#startDate`).val()),
                        'endRegDate': checkDateData($(`#endDate`).val()),
                        'nation': checkData($(`#filterNation`).val()),
                        'city': checkData($(`#filterCity`).val()),
                    };
                function checkData(data) {
                    if (data === "") return null;
                    else return data;
                }
                function checkDateData(data) {
                    if (data === "") return null;
                    else return new Date(data);
                }
                $.ajax({
                    url: "/searchByGender",
                    type: "POST",
                    cache: false,
                    contentType: 'application/json; charset=utf-8',
                    data: JSON.stringify(filterData),
                    dataType: 'json',
                    success: function (data, status) {
                    if (status === 'success') {
                        if (data.status !== "OK")
                            alert("필터 데이터 요청 실패")
                        else {
                            console.log("저장된 데이터");
                            let gridList = data.data;
                            let nationList = data.nationData;
                            let allNationList = data.allNationList;
                            reloading(gridList, nationList, allNationList)
                        }
                    }
                }
            });
        })

        $('#searchBtn').click(function () {
            alert("필터 조회 시작");
            let filterData = {
                'user_id': checkData($(`#idBox`).val()),
                'name': checkData($(`#nameBox`).val()),
                'gender': $('[name="fliterGender"]:checked').val(),
                'startRegDate': checkDateData($(`#startDate`).val()),
                'endRegDate': checkDateData($(`#endDate`).val()),
                'nation': checkData($(`#filterNation`).val()),
                'city': checkData($(`#filterCity`).val()),
            }
            function checkData(data) {
                if (data === "") return null;
                else return data;
            }
            function checkDateData(data) {
                if (data === "") return null;
                else return new Date(data);
            }
            $.ajax({
                url: "/Grid",
                type: "POST",
                cache: false,
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(filterData),
                dataType: 'json',
                success: function (data, status) {
                    if (status === 'success') {
                        if (data.status !== "OK")
                            alert("필터 데이터 요청 실패")
                        else {
                            console.log("저장된 데이터");
                            console.log(data)
                            let gridList = data.data;
                            let nationList = data.nationData;
                            let allNationList = data.allNationList;
                            reloading(gridList, nationList, allNationList);
                        }
                    }
                }
            });
        })

    //#페이지 로딩
    function reloading(gridList, nationList, allNationList) {
        $('#addList').empty();
        gridList.forEach(grid => {
            let gridTag =
                `<tr class="gridData">
                            <td><input class="gridId" type="checkbox"></td>
                            <td><input class="userId" type="text" value="${grid.user_id}" disabled></td>
                            <td><input class="userName" type="text" value="${grid.name}"></td>
                            <td>
                                <input class="userGender" id="male" type="radio" name="${grid.user_id}" value="남">
                                <p>남</p>
                                <input class="userGender" id="female" type="radio" name="${grid.user_id}" value="여">
                                <p>여</p>
                            </td>
                            <td>
                                <select class="userNation" name="${grid.user_id}" value=${grid.nation} onchange="nationBtnChange(this)>
                                    <option value="">-- 국가 선택 --</option>
                                    `
            nationList.forEach(nation => {
                gridTag += `<option id="${grid.user_id + 'Nation'}" value="${nation}" ${nation === grid.nation ? 'selected' : ''}>${nation}</option>`;
            });
            gridTag += `</select>
                                        </td>
                                        <td>
                                            <select class="userCity" value="${grid.city}">
                                                <option value="">-- 도시 선택 --</option>`
            //cityList의 forEach 구간
            allNationList.forEach(data => {
                if (data.nation === grid.nation) {
                    gridTag += `<option id="${grid.user_id + 'City'}" value="${data.city}" ${data.city === grid.city ? 'selected' : ''}>${data.city}</option>`;
                }
            });
            //city 하단구간
            gridTag += `</select>
                                        </td>
                                    </tr>`
            $('#addList').append(gridTag);

            if (grid.gender === "남") {
                $(`.userGender#male[name="${grid.user_id}"]`).prop('checked', true);
            } else {
                $(`.userGender#female[name="${grid.user_id}"]`).prop('checked', true);
            }
            changeGenderColor(document.querySelector(`.userGender[name="${grid.user_id}"]:checked`));
        })
    }

        //#삭제 영역
        $(`#deleteBtn`).click(function () {
            console.log("삭제 버튼 클릭");
            // 삭제하려는 모든 그리드 리스트
            let deleteGridList = [...$(`.selectBox:checked`).parents(".newGridData, .gridData")];   // 태그

            // 삭제하려는 저장된 그리드 ID 리스트 추출
            let deleteIdList = [...$(`.selectBox:checked`).parents(".gridData").find(".userId")].map(e => e.value);

            // 삭제 작업 진행
            if (deleteIdList.length > 0) {  // 저장되어 있는 데아터 중 삭제하려는 데이터 서버 요청
                $.ajax({
                    url: "/Grid",
                    type: "DELETE",
                    cache: false,
                    contentType: 'application/json; charset=utf-8',
                    data: JSON.stringify(deleteIdList),
                    dataType: 'json',
                    success: function (data, status) {
                        if (status === 'success') {
                            if (data.status !== "OK")
                                alert("데이터 삭제 요청 실패")
                            else {
                                // 태그 삭제 함수 호출
                                deleteGridTag();
                            }
                        }
                    }
                })
            } else if (deleteGridList.length > 0) deleteGridTag(); // 추가하는 그리드 삭제
            else alert("삭제하려는 데이터가 없습니다.");

            // 그리드 태그 삭제 함수
            function deleteGridTag() {
                deleteGridList.map(e => e.remove());
                alert("삭제가 완료되었습니다.")
            }
        })

        Object.keys(cityList).forEach(city=>{
            let optionNation = document.createElement('option');
            optionNation.value = city;
            optionNation.text = city;
            $('#modalNation').append(optionNation);
        });
    })