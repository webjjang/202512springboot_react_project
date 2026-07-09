import axios from "axios";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function ImageWrite(){
  // 데이터 처리 - 데이터 표시 전에 처리, 후에 처리 가능--------

  const navigate = useNavigate();

  // 항목 한개를 저장하는 상태 객체 작성 - 5개
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [imageFile, setImageFile] = useState(null);
  const [preview, setPreview] = useState('');

  // 맨 처음에 커서의 위치를 title로 만들어 보자.
  useEffect(()=>{
    document.getElementById('title').focus();
  },[]);

  const handleFileChange = (e) => {

    const file = e.target.files[0];

    if (!file) {
      setImageFile(null);
      setPreview('');
      return;
    }

    setImageFile(file);

    // 미리보기 생성
    const imageUrl = URL.createObjectURL(file);
    setPreview(imageUrl);
  }

  // 등록 버튼의 클릭 처리 -> 실제적으로 등록 시키는 것. 폼을 화면에 표시(랜더링 후) -> 데이터 수집 -> 등록
  const handleSubmit = async (e) => {
    e.preventDefault(); // 기본 동작을 무시시킨다. 페이지를 이동시키면서 데이터 넘기기

    if (!imageFile) {
      alert('이미지 파일을 선택해주세요.');
      return;
    }

    const formData = new FormData();

    // 입력한 데이터를 JSON 데이터로 만든다.
    const vo = {
      title : title,
      content : content
    }

    // 수집한 데이터 출력 확인
    console.log(vo);

    // text 데이터 formData에 추가
    formData.append('vo', new Blob([JSON.stringify(vo)], { type: 'application/json' }));

    // file 데이터 formData에 추가
    formData.append('imageFile', imageFile);

    // Spring Boot로 백엔드 처리 APi 호출해서 데이터 전달
    try {
      const response = await axios.post("http://localhost/image/write.do",
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }
      );
      // alert("일반 게시판에 게시글이 등록되었습니다.");
      alert(response.data); // 서버에서 보낸 데이터를 출력하자.
      navigate("/image/list"); // react 서버
    } catch (error) { // 서버에서 오류가 난 경우 : 500번 오류
      console.log(error);
    }

  }

  // 데이터 표시--------------------------------
  return(
    <>
      <div>/image/write</div>
      <hr />
      <p>이미지 게시판 등록 페이지 입니다.</p>
      <form onSubmit={handleSubmit}>
        <div className="mb-3 mt-3">
          <label htmlFor="title" className="form-label">제목:</label>
          <input type="text" className="form-control" id="title"
           placeholder="제목 입력" name="title" required maxLength={100}
           onChange={(e) => setTitle(e.target.value)}/>
        </div>

        <div className="mb-3 mt-3">
          <label htmlFor="content">내용:</label>
          <textarea className="form-control" rows="5" id="content"
           name="content" required onChange={(e) => setContent(e.target.value)}></textarea>
        </div>

        <div className="mb-3 mt-3">
          <label htmlFor="imageFile" className="form-label">첨부 이미지:</label>
          <input type="file" className="form-control" id="imageFile"
           placeholder="이미지 파일 선택" name="imageFile" required accept="image/*"
           onChange={handleFileChange}/>
        </div>

        <button type="submit" className="btn btn-primary mr-2">등록</button>
        <button type="reset" className="btn btn-success mr-2">새로입력</button>
        <button type="button" className="btn btn-warning"
          onClick={() => location.href = "/image/list"}>취소</button>

        <div className="mb-3 mt-3">
          {preview && <img src={preview} alt="미리보기" style={{ maxWidth: '300px' }} />}
        </div>

      </form>
    </>
  );
}

export default ImageWrite;