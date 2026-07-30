import { Routes, Route } from "react-router-dom"; 
import PacsList from "./PacsList";
import NotFoundPage from "../error/NotFoundPage";

function PacsComp(){
  return (
    <div className="mt-5">
      <h2>Pacs System</h2>
      <Routes>
        <Route path="list" element={<PacsList /> } />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </div>
  )
}

export default PacsComp;