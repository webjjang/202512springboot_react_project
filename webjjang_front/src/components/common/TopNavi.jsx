import { NavLink } from "react-router-dom"

function TopNavi(){
  return(
    <nav className="navbar navbar-expand-sm bg-dark navbar-dark fixed-top">
      <div className="container-fluid">
        <ul className="navbar-nav">
          <li className="nav-item">
            <NavLink to={"/"} className="nav-link">Home</NavLink>&nbsp;
          </li>
          <li className="nav-item">
            <NavLink to={"/image/list"} className="nav-link">Image</NavLink>&nbsp;
          </li>
          <li className="nav-item">
            <NavLink to={"/board/list"} className="nav-link">Board</NavLink>&nbsp;
          </li>
          <li className="nav-item">
            <NavLink to={"/member/write"} className="nav-link">회원가입</NavLink>&nbsp;
          </li>
          <li className="nav-item">
            <NavLink to={"/member/login"} className="nav-link">Login</NavLink>&nbsp;
          </li>
        </ul>
      </div>
    </nav>
  );
}

export default TopNavi;