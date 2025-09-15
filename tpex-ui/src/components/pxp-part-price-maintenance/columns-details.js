import { LABEL_CONST } from "../../constants/label.constant.en";

export const columns = [
    {
        "id": "effectiveFromMonth",
        "name": LABEL_CONST.EFFECTIVE_FROM_MONTH,
        "type": "date",
        "format": "yyyy/MM",
        "required": true,
        "editable": false,
        "unique": false,
        "link": false
    },
    {
        "id": "effectiveToMonth",
        "name": LABEL_CONST.EFFECTIVE_TO_MONTH,
        "type": "date",
        "format": "yyyy/MM",
        "required": true,
        "editable": true,
        "unique": false,
        "link": false
    },
    {
        "id": "partNo",
        "name": LABEL_CONST.PART_NO,
        "type": "string",
        "max": "14",
        "min": "14",
        "required": true,
        "editable": false,
        "unique": false,
        "link": false
    },
    {
        "id": "partName",
        "name": LABEL_CONST.PART_NAME,
        "type": "string",
        "max": "40",
        "min": "1",
        "required": true,
        "editable": false,
        "doNotEditInAdd": true,
        "unique": false,
        "link": false
    },
    {
        "id": "partPrice",
        "name": LABEL_CONST.PART_PRICE,
        "type": "string",
        "required": true,
        "editable": true,
        "unique": false,
        "link": false,
        "max": "14",
        "step": "0.01"
    },
    {
        "id": "currency",
        "name": LABEL_CONST.CURRENCY,
        "type": "dropdown",
        "suggestive": true,
        "required": true,
        "editable": true,
        "unique": false,
        "link": false
    }
];