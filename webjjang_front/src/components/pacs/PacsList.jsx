import { useState, useEffect } from "react";
import "./Pacs.css"
import { useNavigate } from "react-router-dom";
import api from "../common/api"; // 토큰을 헤더에 넣으서 요청한다.

function PacsList(){
  // -- 데이터 처리 부분 ------------------
  // 데이터 처리 ---------------------------------------------
  const [studyList, setStudyList] = useState([]);
 
  // 자동으로 페이지 이동을 시킨다. - 컴포넌트로 컨트롤한다.
  const navigate = useNavigate();

  // 데이터 가져오기
  // 페이지 정보나 검색 정보가 바뀌면(이미지 게시판 메뉴나, 페이지 네이션의 페이지 클릭하면) 데이터를 다시 불러온다.
  useEffect(() =>{
    // Spring boot에서 데이터를 가져와서 변수에 담는다.
    const getStudyList = async() => {
     
      try{
        // 데이터 가져오기
        const response = await api.get("http://localhost/pacs/list.do?");
        console.log("PACS Study 데이터 : " + JSON.stringify(response.data));

        // 배열이면 저장한다. 그렇지 않으면 오류 처리한다.
        if(Array.isArray(response.data)) {
          setStudyList(response.data);
        } else {
          // 데이터가 이상이 있는 경우 오류 처리
          console.error("응답 데이터가 배열이 아닙니다.", response.data);
          setStudyList([]);

        }
      } catch (error) {
        // 서버에서 처리 오류
        console.error("PACS 목록 조회 오류 : ", error);
        setStudyList([]);
      } // try ~ catch 의 끝
     } // getStudyList 함수의 끝

     // getStudyList 함수 호출 실행
     getStudyList();
   }, [] // 빈 배열을 함수 뒤에 선언하면 컴포넌트 호출되면 한번만 실행한다.
  );

  /**
   * Study 상세 화면 이동
   */
  const handleDetail = (study) => {
    navigate(`/pacs/view?id=${encodeURIComponent(study.id)}`);
  };

  /**
   * 값이 null, undefined, 빈 문자열일 경우 '-' 바꿔서 반환
   */
  const displayValue = (value) => {
    if (value === null || value === undefined) {
      return "-";
    }

    if (typeof value === "string" && value.trim() === "") {
      return "-";
    }

    return value;
  };

  /**
   * DICOM 날짜 변환
   *
   * 20000101 -> 2000-01-01
   */
  const formatDicomDate = (date) => {
    if (!date || date.length !== 8) {
      return "-";
    }

    return `${date.substring(0, 4)}-${date.substring(4, 6)}-${date.substring(6, 8)}`;
  };

  /**
   * DICOM 시간 변환
   *
   * 143327 -> 14:33:27
   * 1433 -> 14:33
   */
  const formatDicomTime = (time) => {
    if (!time) {
      return "-";
    }

    const value = time.split(".")[0];

    if (value.length < 4) {
      return value;
    }

    const hour = value.substring(0, 2);
    const minute = value.substring(2, 4);
    const second = value.length >= 6 ? value.substring(4, 6) : "";

    return second
      ? `${hour}:${minute}:${second}`
      : `${hour}:${minute}`;
  };


  /**
   * 영상 보기
   *
   * StudyInstanceUID를 React Viewer 페이지로 전달한다.
   */
  const handleViewer = (study) => {
    if (!study.studyInstanceUID) {
      alert("StudyInstanceUID가 없어 영상을 열 수 없습니다.");
      return;
    }

    navigate(
      `/pacs/viewer?studyInstanceUID=${encodeURIComponent(
        study.studyInstanceUID
      )}`
    );
  };



  // 데이터 표시 부분
  return(
    <div className="container-fluid mt-4">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h3 className="mb-1">PACS Study 목록</h3>

          <div className="text-secondary">
            총 {studyList.length}건
          </div>
        </div>

        <button
          type="button"
          className="btn btn-outline-primary"
          onClick={() => window.location.reload()}
        >
          새로고침
        </button>
      </div>

      <div className="table-responsive">
        <table className="table table-hover table-bordered align-middle">
          <thead className="table-dark">
            <tr>
              <th className="text-center">번호</th>
              <th>Patient ID</th>
              <th>Patient Name</th>
              <th className="text-center">성별</th>
              <th className="text-center">생년월일</th>
              <th className="text-center">Study Date</th>
              <th className="text-center">Study Time</th>
              <th>Study Description</th>
              <th className="text-center">Series 수</th>
              <th className="text-center">상태</th>
              <th className="text-center">영상</th>
            </tr>
          </thead>

          <tbody>
            {studyList.length === 0 ? (
              <tr>
                <td colSpan="11" className="text-center py-5 text-secondary">
                  조회된 PACS Study 자료가 없습니다.
                </td>
              </tr>
            ) : (
              // 데이터가 있으면 반복문 처리한다.
              studyList.map((study, index) => (
                <tr
                  key={study.id}
                  className="dataRow"
                  onDoubleClick={() => handleDetail(study)}
                >
                  <td className="text-center">{index + 1}</td>

                  <td>{displayValue(study.patientId)}</td>

                  <td>{displayValue(study.patientName)}</td>

                  <td className="text-center">
                    {displayValue(study.patientSex)}
                  </td>

                  <td className="text-center">
                    {formatDicomDate(study.patientBirthDate)}
                  </td>

                  <td className="text-center">
                    {formatDicomDate(study.studyDate)}
                  </td>

                  <td className="text-center">
                    {formatDicomTime(study.studyTime)}
                  </td>

                  <td>
                    {displayValue(
                      study.studyDescription ||
                        study.requestedProcedureDescription
                    )}
                  </td>

                  <td className="text-center">
                    {/*Array.isArray(study.series)
                      ? study.series.length
                      : 0*/}
                      {study.seriesCount}
                  </td>

                  <td className="text-center">
                    {study.stable ? (
                      <span className="badge text-bg-success">Stable</span>
                    ) : (
                      <span className="badge text-bg-warning">Updating</span>
                    )}
                  </td>

                  <td className="text-center">
                    <button
                      type="button"
                      className="btn btn-sm btn-primary"
                      onClick={() => handleViewer(study)}
                    >
                      영상 보기
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default PacsList;