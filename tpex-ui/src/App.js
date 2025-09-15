import { useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle";
import "./App.css";
import { Outlet, useLocation } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { Header } from "./common/components/header";
import AlertModal from "./common/components/alert-modal/alert-modal";
import { getCompanyList, hideAlert } from "./store/app/action";
import Home from "./components/home/Home";
import SideNav from "./common/components/sideNav";

const App = props => {

  const {menus} = props;

  const {pathname} = useLocation();

  const showHomePage = pathname === '/';

  const dispatch = useDispatch();
  const {
    showAlert,
    alertStatus,
    alertContent,
    action,
    actionProps
  } = useSelector((state) => state.app);

  const userId = "johsmi";

  useEffect(() => {
    dispatch(getCompanyList({ userId }));
  }, []);

  const okConfirm = () => {
    dispatch(hideAlert());
    dispatch(action.apply(null, [actionProps]));
  };

  const onHideHandler = () => dispatch(hideAlert());

  return (
    <div>

      <SideNav menus={menus} />

      <div className="main-content"> 
        <Header />
        <Outlet />
        {
          showHomePage &&
          <Home />
        }
      </div>

      <AlertModal
        show={showAlert}
        status={alertStatus}
        content={alertContent}
        handleClick={okConfirm}
        onHide={onHideHandler}
      />
    </div>
  );
};

export default App;
