import Modal from "react-bootstrap/Modal";
import TpexSimpleButton from "../button";
import "../../components/alert-modal/alert-modal.css";
import { LABEL_CONST } from "../../../constants/label.constant.en";


export default function AlertModal({
  show,
  onHide,
  status,
  content,
  handleClick,
  parentBtnName
}) {
  return (
    <Modal
      show={show}
      onHide={onHide}
      aria-labelledby="contained-modal-title-vcenter"
      centered
      className={`alert-modal alert-${status}`}
    >
      <i className={`alert-icon alert-${status}-icon`}></i>
      <Modal.Header>
        <Modal.Title>
          {status === LABEL_CONST.ERROR ? `${status}!` : status}
        </Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <span className="alert-modal-content">
          {content}
        </span><br />
        {status === LABEL_CONST.WARNING && (
          <div className="d-flex align-items-end flex-row modal-button">
            <TpexSimpleButton
              color={status}
              text="Yes"
              handleClick={handleClick}
              title={parentBtnName && parentBtnName !=='' ? `${parentBtnName}-${status}`:''}
            />
            <TpexSimpleButton
              color={`${status}-secondary`}
              leftmargin="3"
              text="No"
              handleClick={onHide}
            />
          </div>
        )}
        {status === LABEL_CONST.ERROR && (
          <div className="d-flex justify-content-end flex-column modal-button">
            <TpexSimpleButton color="Error" text="OK" handleClick={onHide} />
          </div>
        )}
        {status === LABEL_CONST.INFORMATION && (
          <div className="d-flex justify-content-end flex-column modal-button">
            <TpexSimpleButton
              color="Information"
              text="OK"
              handleClick={onHide}
            />
          </div>
        )}
      </Modal.Body>
    </Modal>
  );
}
