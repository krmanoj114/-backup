package com.tpex.auth.dto;

import lombok.Data;

@Data
public class MessageErrorSC2 {

	private String msgID;
	private String msgDesc;

	public MessageErrorSC2(String errCd) {
		this.msgID = errCd;
		
		if (errCd == null) {
			this.msgDesc = "Unexpected error.";
		} else {
			switch (errCd) {
			case "MSC29123AERR":
				this.msgDesc = "SC2 Error: Invalid User ID or Password given. Please re-check or contact Helpdesk  to reset your password.";
				break;
			case "MSC29001AERR":
				this.msgDesc = "SC2 Error: Invalid User ID or Password given. Please re-check or use 'Forgot Password' Function.";
				break;
			case "MSC29130AERR":
				this.msgDesc = "SC2 Error: User is not authorized to access the system.";
				break;
			case "MSC29124AERR":
				this.msgDesc = "Unable to access system by this user during this period, please retry next time or Contact Helpdesk.";
				break;
			case "MSC29127AERR":
				this.msgDesc = "Unable to access system by this user during this period, please retry again next time or contact Help Desk.";
				break;
			case "MSC29125AERR":
				this.msgDesc = "This user  is unable to access system from this workstation. Please contact AD Administrator.";
				break;
			case "MSC29128AERR":
				this.msgDesc = "This user  is unable to access system from this workstation. Please contact Help Desk.";
				break;
			case "MSC29121AERR":
				this.msgDesc = "Your password has expired and cannot be changed using this facility. To reset your password please contact Helpdesk.";
				break;
			case "MSC29002AERR":
				this.msgDesc = "Your password has expired.";
				break;
			case "MSCD4022AERR":
				this.msgDesc = "Unable to retrieve the user information. User access has expired.";
				break;
			case "MSC29126AERR":
				this.msgDesc = "Authentication error for this user has occurred. Please contact AD administrator.";
				break;
			case "MSC29129AERR":
				this.msgDesc = "Authentication error for this Toyota user has occurred. Please contact Help Desk.";
				break;
			case "MSC29122AERR":
				this.msgDesc = "User account locked. Incorrect password was entered more than 5 times. To unlock your account please contact Helpdesk.";
				break;
			case "MSC29003AERR":
				this.msgDesc = "User account locked. Incorrect password was entered more than 5 times.";
				break;
			default:
				this.msgDesc = "Unexpected error.";
				break;
			}
		}
	}
}
