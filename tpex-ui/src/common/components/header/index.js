import React from 'react';
import displayPic from '../../../assets/images/displayPic.svg';
import TpexSimpleButton from '../button';
import './style.css';
import { useDispatch, useSelector } from 'react-redux';
import { changeCompany, showAlert } from '../../../store/app/action';
import { LABEL_CONST } from '../../../constants/label.constant.en';
import TpexAppSelect from '../app-select';

const Header = () => {
  const dispatch = useDispatch();

  const { companyList, currentCompanyCode } = useSelector(state => state.app );

  const onCompanyChange = evt => {
    dispatch(showAlert({
      alertStatus: LABEL_CONST.WARNING,
      alertTitle: "Save Changes",
      alertContent: "Please make sure to save your data before proceeding!",
      action: changeCompany,
      actionProps: evt
    }));
  };
  
  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col g-0 main-header">
          <nav className="navbar navbar-expand-lg">
            
            <button
              className="navbar-toggler"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#navbarNavDarkDropdown"
              aria-controls="navbarNavDarkDropdown"
              aria-expanded="false"
              aria-label="Toggle navigation"
            ><span className="navbar-toggler-icon"></span></button>

            <div className="collapse navbar-collapse justify-content-end">

              <ul className="navbar-nav">
                <li className="nav-item dropdown recentlyViewed">
                  <a
                    className="nav-link dropdown-toggle"
                    href="/"
                    id="navbarDarkDropdownMenuLink"
                    role="button"
                    data-bs-toggle="dropdown"
                    aria-expanded="false"
                  >Recently Viewed</a>

                  <ul className="dropdown-menu">
                    <li className="recentlyViewed-text">Recently Viewed</li>
                    <li>
                      <ul className="recentlyViewed-list">
                        <li className="blue">
                          <i className="blueIcon"></i>
                          <div className="list-text">Monthly Operations</div>
                        </li>
                        <li className="purple">
                          <i className="purpleIcon"></i>
                          <div className="list-text">Invoice Operations</div>
                        </li>
                        <li className="green">
                          <i className="greenIcon"></i>
                          <div className="list-text">PVMS</div>
                        </li>
                        <li className="yellow">
                          <i className="yellowIcon"></i>
                          <div className="list-text">Import Part Operations</div>
                        </li>
                      </ul>
                    </li>
                  </ul>
                </li>
                
                <li className="nav-item dropdown adminDropdown">
                  <TpexAppSelect
                    id="companyCode"
                    items={companyList}
                    currentSelectedItemValue={currentCompanyCode}
                    onChangeHandler={onCompanyChange}
                    value="code"
                    label="name"
                  />
                </li>
                
                <li className="nav-item dropdown userProfile">
                  <a className="nav-link dropdown-toggle" href="/" id="navbarDarkDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    <span className="greetings">Hi,</span>
                    <span>Admin </span>
                    <span className="userName">A</span>
                  </a>
                  <ul className="dropdown-menu">
                    <li>
                    <div className="userProfileCard">
                      <div><img src={displayPic} alt="" width={100} height={100} className="profileImage"/></div>
                      <div className="userProfileCard-body">
                        <h5 className="userProfileCard-title">John Doe</h5>
                        <p className="userProfileCard-text">Application Developer</p>
                        <p className="userProfileCard-email">jdoe@gmail.com</p>
                        <TpexSimpleButton color="signout" text="Sign Out" />
                      </div>
                    </div>
                    </li>
                    <li><a className="dropdown-item" href="/"><i className="favourite-icon"></i>My Favourite</a></li>
                    <li><a className="dropdown-item" href="/"><i className="favourite-icon"></i>My Favourite</a></li>
                  </ul>

                </li>
              </ul>
            </div>
          </nav>
        </div>  
      </div>    
    </div>
  );
}

export { Header };