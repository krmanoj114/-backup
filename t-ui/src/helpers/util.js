import { LABEL_CONST } from "../constants/label.constant.en";

export function getDate(defaultValue) {
    return defaultValue.toLocaleDateString();
};

export function formatedDate(d) {
    const date = new Date(d)
    if (date) {
        return ("0" + (date.getDate())).slice(-2) + '/' + ("0" + (Number(date.getMonth()) + 1)).slice(-2) + '/' + date.getFullYear();
    }
    return d;
}

export function formatedDateNumbers(d) {
    if (d) {
        return ("0" + (d.getDate())).slice(-2) + ("0" + (Number(d.getMonth()) + 1)).slice(-2) + d.getFullYear();
    }
    return d;
}

export function formatedDate_mm_yyyy(d) {
    if (d) {
        //  mm/yyyy
        return ("0" + (Number(d.getMonth()) + 1)).slice(-2) + '/' + d.getFullYear() ;
    }
    return d;
}

export function formatedDate_yyyy_mm(d) {
    if (d) {
        //    yyyymm
        return d.getFullYear() + ("0" + (Number(d.getMonth()) + 1)).slice(-2);
    }
    return d;
}

export function formatedDate_yyyymm(d) {
    if (d) {
        return d.getFullYear() + '/' + ("0" + (Number(d.getMonth()) + 1)).slice(-2);
    }
    return d;
}

export function getCurrent_yyyymm() {
    const d = new Date();   
    return d.getFullYear() + '/' + ("0" + (Number(d.getMonth()) + 1)).slice(-2);
}

export function getFilename (url) {

    if (
        url &&
        typeof(url) === 'string' &&
        url.length > 0 &&
        url?.indexOf('/') !== -1
    ) {
        return url.substring(url.lastIndexOf('/') + 1, url.length - 1);
    }

    return "";
}

export function getFileExtension(fileName) {
    return fileName.split('.').pop();
}

export function dateCompare(d1, d2) {
    let date1 = new Date(d1);
    let date2 = new Date(d2);
    let result;
    if (date1 < date2) {
        result = true;
    } else if (date1 > date2) {
        result = false;
    } else {
        result = true;
    }
    return result;
}

const parseDateMonthYear = s => {
    let [d, m, y] = s.split(/\D/);
    return new Date(y, m-1, d);
};

const getFutureDate = (sDate) => {
    const currentDate = new Date();
    let result;
    if (sDate.getTime() > currentDate.getTime()) {
        result = true;
    } else {
        result = false;
    }
    return result;
}
export function compareDates(startDate, endDate) {
    let sDate = parseDateMonthYear(startDate.toString());
    let futureDate = getFutureDate(sDate);
    let eDate = parseDateMonthYear(endDate.toString());
    
    const todaysDate = new Date();

    const now = new Date(
        todaysDate.getFullYear(),
        todaysDate.getMonth(),
        todaysDate.getDate()
    );
    const bookingStartDate = new Date(sDate);
    const bookingEndDate = new Date(eDate);

    return (
        futureDate ||
        (
            (now.getTime() >= bookingStartDate.getTime()) &&
            (now.getTime() <= bookingEndDate.getTime())
        )
        
    );
}

export function isPastDate (date) {
    let d1 = parseDateMonthYear(date.toString());
    const specificDate = new Date(d1);
    const today = new Date();

    const now = new Date(
        today.getFullYear(),
        today.getMonth(),
        today.getDate()
    );
    return specificDate < now;
}

export function getFormatedDate_Time(d) {
    if (d) {
        return ("0" + (d.getDate())).slice(-2) + ("0" + (Number(d.getMonth()) + 1)).slice(-2) + d.getFullYear()
            + '_' + ("0" + (d.getHours())).slice(-2) + ("0" + (d.getMinutes())).slice(-2) + ("0" + (d.getSeconds())).slice(-2);
    }
    return d;

}

//endDateTime: "10/03/2023_00:00"
export function getFormatedDateWithSlash_Time(d) {
    if (d) {
        return ("0" + (d.getDate())).slice(-2) + "/"+ ("0" + (Number(d.getMonth()) + 1)).slice(-2) + "/"+ d.getFullYear()
            + '_' + ("0" + (d.getHours())).slice(-2) +':'+ ("0" + (d.getMinutes())).slice(-2);
    }
    return d;

}

export function objectsAreSame(x, y) {
    let objectsAreSame = true;
    for (let propertyName in x) {
        if (x[propertyName] !== y[propertyName]) {
            objectsAreSame = false;
            break;
        }
    }
    return objectsAreSame;
}

export function deepEqual(object1, object2) {
    const keys1 = Object.keys(object1);
    const keys2 = Object.keys(object2);
    if (keys1.length !== keys2.length) {
        return false;
    }
    for (const key of keys1) {
        const val1 = object1[key];
        const val2 = object2[key];
        const areObjects = isObject(val1) && isObject(val2);
        if ((areObjects && !deepEqual(val1, val2)) || (!areObjects && val1 !== val2)) {
            return false;
        }
    }
    return true;
}

export function isObject(object) {
    return object != null && typeof object === 'object';
}

export function isObjectEmpty (obj) {
    return isObject(obj) && Object.keys(obj).length === 0;
}

export function ddmmTOmmddChange(d) {
    if (d) {
        let dateSplit = d.split('/');
        return dateSplit[1] + '/' + dateSplit[0] + '/' + dateSplit[2];
    }

}
export function dateValidationForSixMonth(k) {
    let result;
    let b = new Date(k); //your input date here

    let d = new Date(); // today date
    d.setMonth(d.getMonth() - 6);  //subtract 6 month from current date 

    if (b > d) {
        result = true;
    } else {
        result = false;
    }
    return result;
}
export function copyObject(obj) {
    return JSON.parse(JSON.stringify(obj))
}

export function createMessage(code) {
    return LABEL_CONST[code] ? code + " : " + LABEL_CONST[code] : code;
}


const incorporateUrl = (string, errObj) => {
    
    const columns = errObj.keyColumns;

    if (columns && columns.length > 0) {
        return string.replace("{keyColumns}.", "") +
            (columns.length <= 2 ? "Country Code " : "Country Name ") +
            columns.join(); 
    }

    return "";
};

const interpolateUrl = (string, errObj) => {
    let msg = string;

    for (const errKey in errObj) {
        const placeHolderText = `{${errKey}}`;
    
        if (msg.indexOf(placeHolderText) !== -1) {
        const placeHolderStartsAt = msg.indexOf(placeHolderText);
        const placeHolderEndsAt = placeHolderStartsAt + placeHolderText.length;
        msg = msg.substring(0, placeHolderStartsAt) + errObj[errKey] + msg.substring(placeHolderEndsAt, msg.length -1); 
        }
    }
  
    return msg;
};

export function createMesssageReplacer(paths, code, handleFromUI=false, pathName=null) {
    let path = LABEL_CONST[code];
    if (path) {
        const url = (pathName === 'codemaster') ? incorporateUrl(path, paths) : interpolateUrl(path, paths); 
        if(pathName === "pxp-part-price-maintenance" && code === "ERR_IN_3023"){
            
            return ` ${code} : Part No.: ${paths["Part No."].join(', ')} ${url}`;
        }
        if (handleFromUI) {
            return url;
        } else {
            if (code === 'ERR_CM_3029') {
                return `${code}: ${paths.keyColumns}`;
            }
            return `${code} : ${url}`;
        }

    } else {
        return code
    }
}

export function formatPartNo(label) {
    
    let l1 = "";
    let l2 = "";
    let l3 = "";
    
    if (label && label.length > 0) {
        l1 = label.substring(0, 5);
        l2 = label.substring(5, 10);
        l3 = label.substring(10, 12);

        const l3ToConcatenate = l3 !== "" ? `-${l3}` : "";

        return l1 + ((l2 !== "") ? `-${l2}${l3ToConcatenate}` : "");
    }

    return "";
}

/**
 * 
 * @param {String} d : Date for formatting 
 * @returns date in format yyyy-mm-dd Example:- 2023-08-01
 */
export function formatDateSlash(d) {
    const date = new Date(d)
    if (date) {
        return date.getFullYear() + '-' + ("0" + (Number(date.getMonth()) + 1)).slice(-2) + '-' + ("0" + (date.getDate())).slice(-2);
    }
    return d;
}
/**
 * 
 * @param {String} d : Date for formatting 
 * @returns date in format dd/mm/yyyy Example :- 01/08/2023
 */
export const formatedDate_ddmmyyyy = (d) => {
    const date = new Date(d)
    if (date) {
        return ("0" + (Number(date.getMonth()) + 1)).slice(-2) + '/' + ("0" + (date.getDate())).slice(-2) + '/' + date.getFullYear();
    }
    return d;
  }