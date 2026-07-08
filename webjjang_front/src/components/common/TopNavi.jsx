import { NavLink } from "react-router-dom"

function TopNavi(){
  return(
    <nav className="navbar navbar-expand-sm bg-dark navbar-dark fixed-top">
      <NavLink to={"/"}>Home</NavLink>&nbsp;
      <NavLink to={"/board/list"}>Board</NavLink>&nbsp;
    </nav>
  );
}

export default TopNavi;