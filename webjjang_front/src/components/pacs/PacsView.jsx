import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import api from "../common/api"; // 토큰을 헤더에 넣으서 요청한다.

const PacsView = () => {

  // /pacs/view?id=idData
  // 넘어오는 id 받기
  const [searchParams] = useSearchParams();
  const id = searchParams.get("id");

  // 페이지 이동 객체
  const navigate = useNavigate();

  // Spring boot 서버에 데이터를 요청해서 저장하는 변수.
  const [study, setStudy] = useState(null);
  // id가 넘어왔고 서버에 데이터 요청하면 불러 오는 중으로 표시하기 위한 변수
  const [loading, setLoading] = useState(Boolean(id));
  // 예외가 발생되면 저장하는 변수.
  const [error, setError] = useState("");

  console.log("PACS 상세 조회 ID : ", id);

  useEffect(() => {
    // id가 없으면 API 요청을 실행하지 않음
    if (!id) {
      return;
    }

    // 요청 취소를 제어하는 객체
    const controller = new AbortController();

    // Spring Boot Server에 데이터를 요청하는 함수 선언 - 반드시 호출을 해야 실행됨.
    const getStudyDetail = async () => {
      try {
        // 데이터 요청해서 spring boot 서버에서 응답이 올때 까지 기다린다.
        const response = await api.get(`http://localhost/pacs/view.do?id=${id}`, {
          // 요청 취소를 위한 signal 전달 -> controller.abort()가 호출
          signal: controller.signal,
        });

        // 가져온 데이터를 세팅한다.
        setStudy(response.data);
      } catch (err) {
        // 요청이 취소된 경우에는 에러 처리하지 않음
        if (
          err.name === "CanceledError" ||
          err.code === "ERR_CANCELED"
        ) {
          return;
        }

        console.error("PACS 상세 데이터 조회 실패:", err);

        if (err.response?.status === 401) {
          setError("로그인이 만료되었습니다. 다시 로그인해 주세요.");
        } else if (err.response?.status === 403) {
          setError("PACS 상세정보를 조회할 권한이 없습니다.");
        } else if (err.response?.status === 404) {
          setError("해당 Study 정보를 찾을 수 없습니다.");
        } else {
          setError(
            err.response?.data?.message ||
              "PACS 상세정보를 불러오는 중 오류가 발생했습니다."
          );
        }
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      }
    };

    // 데이터를 가져오기 위함 함수 호출
    getStudyDetail();

    return () => {
      controller.abort();
    };
  }, [id]);

  // id가 없는 상태는 Effect가 아니라 렌더링에서 직접 처리
  if (!id) {
    return (
      <div className="container py-4">
        <div className="alert alert-danger" role="alert">
          Study ID가 전달되지 않았습니다.
        </div>

        <button
          type="button"
          className="btn btn-outline-secondary"
          onClick={() => navigate(-1)}
        >
          목록으로 돌아가기
        </button>
      </div>
    );
  }

  // 불러오는 중 처리 - 통신 속도가 느린 경우 화면에 보여주는 내용 ----------------
  if (loading) {
    return (
      <div className="container py-5">
        <div className="d-flex flex-column align-items-center">
          <div
            className="spinner-border text-primary"
            role="status"
            aria-hidden="true"
          />

          <div className="mt-3 text-secondary">
            PACS 상세정보를 불러오는 중입니다.
          </div>
        </div>
      </div>
    );
  }

  // 오류가 난 경우 화면에 표시하는 부분 --------------------------------------
  if (error) {
    return (
      <div className="container py-4">
        <div className="alert alert-danger" role="alert">
          <div className="fw-bold mb-1">
            PACS 상세정보 조회 실패
          </div>

          <div>{error}</div>
        </div>

        <button
          type="button"
          className="btn btn-outline-secondary"
          onClick={() => navigate(-1)}
        >
          목록으로 돌아가기
        </button>
      </div>
    );
  }

  // 정상처리가 됐는데 데이터가 없어서 못 불러온 경우 처리 -------------------------------
  if (!study) {
    return (
      <div className="container py-4">
        <div className="alert alert-warning">
          조회된 Study 정보가 없습니다.
        </div>

        <button
          type="button"
          className="btn btn-outline-secondary"
          onClick={() => navigate(-1)}
        >
          목록으로 돌아가기
        </button>
      </div>
    );
  }


  /**
   * null, undefined, 빈 문자열을 화면 표시용 값으로 변환
   */
  const displayValue = (value) => {
    if (value === null || value === undefined || value === "") {
      return "-";
    }

    return value;
  };

  /**
   * YYYYMMDD 형식의 DICOM 날짜를 YYYY-MM-DD로 변환
   */
  const formatDicomDate = (value) => {
    if (!value) {
      return "-";
    }

    const date = String(value).replaceAll("-", "");

    if (date.length !== 8) {
      return value;
    }

    return `${date.substring(0, 4)}-${date.substring(4, 6)}-${date.substring(
      6,
      8
    )}`;
  };

  /**
   * HHMMSS 또는 HHMMSS.SSS 형식의 DICOM 시간을 HH:mm:ss로 변환
   */
  const formatDicomTime = (value) => {
    if (!value) {
      return "-";
    }

    const time = String(value).split(".")[0].replaceAll(":", "");

    if (time.length < 4) {
      return value;
    }

    const hour = time.substring(0, 2);
    const minute = time.substring(2, 4);
    const second = time.length >= 6 ? time.substring(4, 6) : "00";

    return `${hour}:${minute}:${second}`;
  };

  /**
   * 환자 성별 표시
   */
  const formatPatientSex = (value) => {
    switch (value) {
      case "M":
        return "남성";
      case "F":
        return "여성";
      case "O":
        return "기타";
      default:
        return displayValue(value);
    }
  };


    /**
   * SeriesVO 내부 필드명이 프로젝트마다 다를 수 있으므로
   * 여러 후보 필드를 확인하도록 작성
   */
  const getSeriesId = (series) => {
    // 옵셔널 체이닝 연산자 사용.
    // series?.id || series?.seriesId -> series 안에 id 값이 있으면 id 리턴 아니면 오른쪽 seriesId 리턴
    return (
      series?.id ||
      series?.seriesId ||
      series?.orthancId ||
      series?.seriesInstanceUID ||
      ""
    );
  };

  const getSeriesDescription = (series) => {
    return (
      series?.seriesDescription ||
      series?.description ||
      series?.protocolName ||
      "-"
    );
  };

  const getSeriesNumber = (series) => {
    return series?.seriesNumber ?? series?.number ?? "-";
  };

  const getSeriesModality = (series) => {
    return series?.modality || series?.modalityType || "-";
  };

  const getSeriesInstanceCount = (series) => {
    if (series?.instanceCount !== null && series?.instanceCount !== undefined) {
      return series.instanceCount;
    }

    if (Array.isArray(series?.instances)) {
      return series.instances.length;
    }

    if (Array.isArray(series?.instanceList)) {
      return series.instanceList.length;
    }

    return 0;
  };

  const seriesList = Array.isArray(study.seriesList)
    ? study.seriesList
    : [];

  // 데이터가 존재하는 경우 화면에 표시하는 부분-------------------------------------------
  return (
    <div className="container-fluid py-4 px-lg-4">
      {/* 제목 영역 */}
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
        <div>
          <h3 className="mb-1">PACS Study 상세정보</h3>
          <div className="text-secondary">
            환자 및 의료영상 검사 정보를 확인합니다.
          </div>
        </div>

        <div className="d-flex gap-2">
          <button
            type="button"
            className="btn btn-outline-secondary"
            onClick={() => navigate(-1)}
          >
            목록
          </button>
        </div>
      </div>

      {/* 요약 정보 */}
      <div className="row g-3 mb-4">
        <div className="col-sm-6 col-xl-3">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <div className="small text-secondary mb-1">환자 ID</div>
              <div className="fs-5 fw-semibold text-break">
                {displayValue(study.patientId)}
              </div>
            </div>
          </div>
        </div>

        <div className="col-sm-6 col-xl-3">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <div className="small text-secondary mb-1">환자명</div>
              <div className="fs-5 fw-semibold text-break">
                {displayValue(study.patientName)}
              </div>
            </div>
          </div>
        </div>

        <div className="col-sm-6 col-xl-3">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <div className="small text-secondary mb-1">Series 수</div>
              <div className="fs-5 fw-semibold">
                {study.seriesCount ?? seriesList.length}
              </div>
            </div>
          </div>
        </div>

        <div className="col-sm-6 col-xl-3">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <div className="small text-secondary mb-1">Instance 수</div>
              <div className="fs-5 fw-semibold">
                {study.instanceCount ?? 0}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* 환자 정보 */}
      <div className="card border-0 shadow-sm mb-4">
        <div className="card-header bg-primary text-white py-3">
          <h5 className="mb-0">환자 정보</h5>
        </div>

        <div className="card-body p-0">
          <div className="table-responsive">
            <table className="table table-bordered align-middle mb-0">
              <tbody>
                <tr>
                  <th className="table-light" style={{ width: "15%" }}>
                    환자 ID
                  </th>
                  <td style={{ width: "35%" }}>
                    {displayValue(study.patientId)}
                  </td>

                  <th className="table-light" style={{ width: "15%" }}>
                    환자명
                  </th>
                  <td style={{ width: "35%" }}>
                    {displayValue(study.patientName)}
                  </td>
                </tr>

                <tr>
                  <th className="table-light">성별</th>
                  <td>{formatPatientSex(study.patientSex)}</td>

                  <th className="table-light">생년월일</th>
                  <td>{formatDicomDate(study.patientBirthDate)}</td>
                </tr>

                <tr>
                  <th className="table-light">Parent Patient</th>
                  <td colSpan={3}>
                    <span className="text-break">
                      {displayValue(study.parentPatient)}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* 검사 정보 */}
      <div className="card border-0 shadow-sm mb-4">
        <div className="card-header bg-dark text-white py-3">
          <h5 className="mb-0">검사 정보</h5>
        </div>

        <div className="card-body p-0">
          <div className="table-responsive">
            <table className="table table-bordered align-middle mb-0">
              <tbody>
                <tr>
                  <th className="table-light" style={{ width: "18%" }}>
                    Orthanc Study ID
                  </th>
                  <td colSpan={3}>
                    <span className="text-break">
                      {displayValue(study.id)}
                    </span>
                  </td>
                </tr>

                <tr>
                  <th className="table-light">Study Instance UID</th>
                  <td colSpan={3}>
                    <span className="text-break">
                      {displayValue(study.studyInstanceUID)}
                    </span>
                  </td>
                </tr>

                <tr>
                  <th className="table-light">검사일</th>
                  <td>{formatDicomDate(study.studyDate)}</td>

                  <th className="table-light" style={{ width: "18%" }}>
                    검사시간
                  </th>
                  <td>{formatDicomTime(study.studyTime)}</td>
                </tr>

                <tr>
                  <th className="table-light">검사 설명</th>
                  <td>{displayValue(study.studyDescription)}</td>

                  <th className="table-light">Study ID</th>
                  <td>{displayValue(study.studyID)}</td>
                </tr>

                <tr>
                  <th className="table-light">Accession Number</th>
                  <td>{displayValue(study.accessionNumber)}</td>

                  <th className="table-light">담당 의사</th>
                  <td>{displayValue(study.referringPhysicianName)}</td>
                </tr>

                <tr>
                  <th className="table-light">요청 검사 설명</th>
                  <td colSpan={3}>
                    {displayValue(study.requestedProcedureDescription)}
                  </td>
                </tr>

                <tr>
                  <th className="table-light">안정 상태</th>
                  <td>
                    {study.stable ? (
                      <span className="badge text-bg-success">Stable</span>
                    ) : (
                      <span className="badge text-bg-warning">Processing</span>
                    )}
                  </td>

                  <th className="table-light">Series / Instance</th>
                  <td>
                    <span className="badge text-bg-primary me-2">
                      Series {study.seriesCount ?? seriesList.length}
                    </span>

                    <span className="badge text-bg-secondary">
                      Instance {study.instanceCount ?? 0}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Series 목록 */}
      <div className="card border-0 shadow-sm">
        <div className="card-header bg-info-subtle py-3 d-flex justify-content-between align-items-center">
          <h5 className="mb-0">Series 목록</h5>

          <span className="badge text-bg-info">
            {seriesList.length}개
          </span>
        </div>

        <div className="card-body p-0">
          {seriesList.length === 0 ? (
            <div className="alert alert-light text-center mb-0 rounded-0 py-5">
              조회된 Series 정보가 없습니다.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover table-bordered align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th className="text-center" style={{ width: "70px" }}>
                      번호
                    </th>
                    <th style={{ minWidth: "130px" }}>Series 번호</th>
                    <th style={{ minWidth: "120px" }}>Modality</th>
                    <th style={{ minWidth: "250px" }}>Series 설명</th>
                    <th
                      className="text-center"
                      style={{ minWidth: "120px" }}
                    >
                      Instance 수
                    </th>
                    <th style={{ minWidth: "300px" }}>Series ID</th>
                  </tr>
                </thead>

                <tbody>
                  {seriesList.map((series, index) => {
                    const seriesId = getSeriesId(series);

                    return (
                      <tr key={seriesId || `${index}-${getSeriesNumber(series)}`}>
                        <td className="text-center">{index + 1}</td>

                        <td>{getSeriesNumber(series)}</td>

                        <td>
                          <span className="badge text-bg-secondary">
                            {getSeriesModality(series)}
                          </span>
                        </td>

                        <td>{getSeriesDescription(series)}</td>

                        <td className="text-center">
                          <span className="badge text-bg-primary">
                            {getSeriesInstanceCount(series)}
                          </span>
                        </td>

                        <td>
                          <span className="small text-break">
                            {displayValue(seriesId)}
                          </span>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default PacsView;