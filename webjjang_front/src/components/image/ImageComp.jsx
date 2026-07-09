import { Routes, Route } from "react-router-dom"; 
import ImageList from "./ImageList";
import NotFoundPage from "../error/NotFoundPage";
import ImageWrite from "./ImageWrite";
import ImageView from "./ImageView";
import ImageUpdate from "./ImageUpdate";
// import ImageDelete from "./ImageDelete"; - 글보기 컴포넌트 안에 포함.

function ImageComp(){
  return (
    <div class="mt-5">
      <h2>이미지 게시판</h2>
      <Routes>
        <Route path="list" element={<ImageList /> } />
        <Route path="view" element={<ImageView /> } />
        <Route path="write" element={<ImageWrite /> } />
        <Route path="update" element={<ImageUpdate /> } />
        {/* 글보기 안에 삭제 컴포넌트를 포함 시킨다.
          <Route path="delete" element={<ImageDelete /> } /> */}
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </div>
  )
}

export default ImageComp;