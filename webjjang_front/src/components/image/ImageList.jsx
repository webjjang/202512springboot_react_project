import { useState, useEffect } from "react";
import axios from "axios";
import "./Image.css"
import PageNation from "../common/PageNation";
import { Link, useSearchParams } from "react-router-dom";

function ImageList(){
  // -- 데이터 처리 부분 ------------------
  // 데이터 처리 ---------------------------------------------
  // 파라메터로 넘어오는 데이터 수집 - useLocation
  // const location = useLocation();
  const [searchParams] = useSearchParams();
  const page = searchParams.get('page');
  const perPageNum = searchParams.get('perPageNum');
  const key = searchParams.get('key');
  const word = searchParams.get('word');

  console.log("page=",page, ",perPageNum=", perPageNum, ", key=",key,", word=",word);

  const notPageQuery = `perPageNum=${perPageNum==null?"":perPageNum}&key=${key==null?"":key}&word=${word==null?"":word}`;
  const query = `page=${page==null?"":page}&${notPageQuery}`;
  console.log("query=", query);

  // 데이터를 저장하는 부분
  const [myJSON, setMyJSON] = useState({list:[], pageObject:{}});

  // 데이터 가져오기
  useEffect(
    function(){
      console.log("ImageList :: useEffect 실행해서 데이터 가져오기 시작")
      axios.get("http://localhost/image/list.do?" + query)
      .then((response) => {
        console.log("Axios를 이용한 데이터 가져오기");
        console.log("응답 데이터 : " + response);
        console.log("json 데이터 : " + JSON.stringify(response.data));
        setMyJSON(response.data);
      })
      .catch((error) => {
        console.error("에러 발생 :", error);
      })
    }, [query] // 빈 배열을 함수 뒤에 선언하면 컴포넌트 호출되면 한번만 실행한다.
  );

  // myJSON 데이터를 가져와서 tag를 만드는 함수
  let trTag = myJSON.list.map(
    (vo) => {
      return (
        <tr className="dataRow" key={vo.no}
         onClick={() => location.href=`./view?no=${vo.no}&inc=1`}>
          <td className="no" >{vo.no}</td>
          <td>{vo.title}</td>
          <td>{vo.id}</td>
          <td>{vo.name}</td>
          <td>{vo.fileName}</td>
          <td>{vo.writedDate}</td>
          <td>{vo.hit}</td>
        </tr>
      )
    }
  )

  // 데이터 표시 부분
  return(
    <>
      <div>/image/list</div>
      <hr /> <br />
      <table className="table table-hover">
        <thead>
          <tr>
            <th>번호</th>
            <th>제목</th>
            <th>아이디</th>
            <th>이름</th>
            <th>이미지</th>
            <th>작성일</th>
            <th>조회수</th>
          </tr>
        </thead>
        <tbody>
          {trTag}
        </tbody>
      </table>
      <Link to={"/image/write"} className="btn btn-primary">등록</Link>
      <PageNation pageObject={myJSON.pageObject} />
    </>
  );
}

export default ImageList;