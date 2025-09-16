import { LABEL_CONST } from "../../constants/label.constant.en";

export const columns = [
    {
        "id": "effectiveFromMonth",
        "name": "Effective From Month",
        "type": "date",
        "required": false,
        "editable": false,
        "unique": false,
        "link": false
    },
    {
        "id": "effectiveToMonth",
        "name": "Effective To Month",
        "type": "date",
        "required": false,
        "editable": false,
        "unique": false,
        "link": false
    },
    {
        "id": "lotCode",
        "name": "Lot Code",
        "type": "string",
        "max": "999999999999",
        "min": "1",
        "required": false,
        "editable": false,
        "unique": false,
        "link": false
    },
    {
        "id": "lotPrice",
        "name": "Lot Price",
        "type": "number",
        "max": "999999999999",
        "min": "1",
        "required": false,
        "editable": false,
        "unique": false,
        "link": false
    },
    {
        "id": "currency",
        "name": "Currency",
        "type": "string",
        "max": "999999999999",
        "min": "1",
        "required": false,
        "editable": false,
        "unique": false,
        "link": false
    }
];

export const columnsPopup = [
    {
        "id": "partNumber",
        "name": LABEL_CONST.PART_NO,
        "type": "string",
        "required": true,
        "editable": false,
        "unique": false,
        "link": false,
        "min": 1,
        "max": 50
    },
    {
        "id": "partName",
        "name": LABEL_CONST.PART_NAME,
        "type": "string",
        "required": true,
        "editable": true,
        "unique": false,
        "link": false,
        "min": 1,
        "max": 40
    },
    {
        "id": "partPrice",
        "name": LABEL_CONST.PART_PRICE,
        "type": "number",
        "required": true,
        "editable": true,
        "unique": false,
        "link": false,
        "min": 0.01,
        "max": 9999999999999.99,
        "step": "0.01"
    },
    {
        "id": "partUsage",
        "name": LABEL_CONST.PART_USAGE,
        "type": "number",
        "required": true,
        "editable": true,
        "unique": false,
        "link": false,
        "min": 1,
        "max": 999
    }
];